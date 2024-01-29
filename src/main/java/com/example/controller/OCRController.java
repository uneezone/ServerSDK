package com.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.data.RequestParams;
import com.example.data.ResponseParams;
import com.example.logmanager.LogsManager;

import com.quram.mi.lib.data.result.QuramOcrScanResult;
import com.quram.mi.lib.engine.QuramOcrScanner;
import com.quram.mi.lib.engine.QuramOcrScanner.QuramOcrResultType;
import com.quram.mi.lib.engine.QuramOcrScanner.QuramOcrScannerFrameType;
import com.quram.mi.lib.engine.QuramOcrScanner.QuramOcrScannerResultCode;
import com.quram.mi.lib.engine.QuramOcrScanner.QuramOcrScannerType;
import com.quram.mi.lib.engine.ScanOptions;
import com.quram.mi.lib.exception.ScannerEngineVersionException;
import com.quram.mi.lib.exception.ScannerInitException;
import com.quram.mi.lib.exception.ScannerRuntimeException;
import com.quram.mi.lib.util.ResultSaver;

import com.quram.mi.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.data.FileMultipartFile.*;

@Controller
public class OCRController {

    @Value("${tess.path}")
    public static String _TESS_PATH;

    private static Logger resultLogger = LoggerFactory.getLogger("com.quram.mi.sample.log.result");
    private static Logger AppLogger = LoggerFactory.getLogger("com.quram.mi.sample.log");

    private static LogsManager mLogManager = new LogsManager();
    private static int tryCount=0;

    private static final String TESS_PATH = "tess.path";
    private static final String FACE_DATA_PATH = "facedata.path";
    private static Environment env;

    private static String PAGE_UPLOAD = "upload";
    private static String PAGE_CAPTURE = "capture";
    private static String PAGE_CAPTURE_AUTO = "capture_auto";

    @Autowired
    public OCRController(Environment environment) {
        env = environment;
    }

    @GetMapping("/")
    @ResponseBody
    public String healthCheck() {
        return "";
    }

    @GetMapping(value = "upload")
    public ModelAndView UploadImage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(PAGE_UPLOAD);
        return mav;
    }

    @GetMapping(value = "webrtc-example")
    public ModelAndView cameraCapture(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(PAGE_CAPTURE);
        mav.addObject("maxFrame", 1);

        return mav;
    }


    @GetMapping(value = "webrtc-auto")
    public ModelAndView cameraCaptureAuto(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(PAGE_CAPTURE_AUTO);
        mav.addObject("maxFrame", 1);

        return mav;
    }

    public static String getTessPath() {
        AppLogger.info("tesseract data path : " + Paths.get(env.getProperty(TESS_PATH)).toAbsolutePath().normalize());
        return env.getProperty(TESS_PATH);
    }

    public static String getFaceDataPath() {
        String ret = null;
        String facePathProp = env.getProperty(FACE_DATA_PATH);
        if(facePathProp == null || "".equals(facePathProp)) {
            AppLogger.info("face data path is empty.");
            return null;
        }
        String path = Paths.get(facePathProp).toAbsolutePath().normalize().toString();
        AppLogger.info("face data path : " + path);

        try {
            File f = new File(path);
            if (f != null && f.exists()) {
                if(f.isFile()) {
                    if(path.matches("(?i).*\\.xml$")) {
                        ret = path;
                    }
                } else {
                    ret = Paths.get(path, "haarcascade_frontalface_alt.xml").toAbsolutePath().normalize().toString();
                }
            }
        } catch(Exception e) {
            AppLogger.error("Failed to get face data path", e);
            return null;
        }
        return ret;
    }

    /*
     * Scan ID Card (citizen registration & driver license)
     */

    private boolean isValidImageBase64(String base64Input) {
        // Check if the base64 input starts with '/9' (JPEG marker) or 'iV' (PNG marker)
        // return base64Input.startsWith("/9") || base64Input.startsWith("iV");
        return base64Input.startsWith("/9");
    }

    @PostMapping(value = "scan/{type}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String scanFrameByWeb(
            @PathVariable String type,
            @RequestParam("files") MultipartFile[] uploadedFiles,
            @RequestParam(value = "config", required = false, defaultValue = "") String in_config
    ) {

        return scanFrame(type, uploadedFiles, in_config, false);
    }

    @PostMapping(value = "auto/{type}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String scanFrameByWebAuto(
            @PathVariable String type,
            @RequestParam("files") MultipartFile[] uploadedFiles,
            @RequestParam(value = "config", required = false, defaultValue = "") String in_config
    ) {

        return scanFrame(type, uploadedFiles, in_config, true);
    }

    @PostMapping(value = "webrtc-example/scan/{type}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String scanFrameByWebRtc(
            @PathVariable String type,
            @RequestParam("files") MultipartFile[] uploadedFiles,
            @RequestParam(value = "config", required = false, defaultValue = "") String in_config
    ) {

        return scanFrame(type, uploadedFiles, in_config, false);
    }

    @PostMapping(value = "upload/scan/{type}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String scanFrameByWebUpload(
            @PathVariable String type,
            @RequestParam("files") MultipartFile[] uploadedFiles,
            @RequestParam(value = "config", required = false, defaultValue = "") String in_config
    ) {
        return scanFrame(type, uploadedFiles, in_config , false);
    }


    public String scanFrame(
            String type,
            MultipartFile[] uploadedFiles, String in_config, boolean autoMode
    ) {
        /*
         * 하나은행 지원되는 카드 종류
         * 1. 신분증(운전면허증, 주민등록증)
         * 2. 외국인등록증
         * 3. 여권
         * 4. 지로
         */
        boolean onlySSa = false;
        long totalST = System.currentTimeMillis();

        String retJSON = "";
        String resultJsonStr = "";

        long st = 0, t = 0;
        st = System.currentTimeMillis();
        if (type == null) {
            // 기본 모드 설정
            type = "id-auto";
        }
        // 1) 디폴트 스캐너 타입설정
        QuramOcrScannerType scannerType = QuramOcrScannerType.IDCARD_AUTO;

        type = type.toLowerCase();
        // 각 모드에 따라 스캐너 타입을 별도로 설정해야 함.
        if (type.equals("id-auto")) {
            scannerType = QuramOcrScannerType.IDCARD_AUTO;
        } else if (type.equals("id-alien")) {
            scannerType = QuramOcrScannerType.ID_ALIEN_REGISTRATION;
        } else if(type.equals("id-alien-back")) {
            scannerType = QuramOcrScannerType.ID_ALIEN_BACK;
        } else if (type.equals("passport")) {
            scannerType = QuramOcrScannerType.PASSPORT;
        } else if (type.equals("giro")) {
            scannerType = QuramOcrScannerType.GIRO;
        } else if (type.equals("ssa")) {
            AppLogger.info("[scanFrame SSA]");
            onlySSa = true;
        }
        /*
         * 스캔 옵션 예시
         *  - 기본값이 설정되어 있으며 scannerType 값 외에는 ScanOptions 인스턴스 생성한 그대로 사용해도 충분함.
         */
        ScanOptions scanOpts = new ScanOptions();
        if(onlySSa)
        {
            scanOpts.onlySsa = true;
        }
        scanOpts.scannerType = scannerType; // (기본값: QuramOcrScannerType.ID_AUTO), 스캐너 타입 명시적으로 지정할 것
        // 필수 스캔 옵션 (기본값: true)
        scanOpts.scanLicenseNumber = true; // 운전면허증 번호 스캔 여부
        scanOpts.scanIssueDate = true; // 발급일자 스캔 여부
        // 부가적인 스캔 옵션 (기본값: false)
        scanOpts.scanRegion = true; // 발행처 스캔 여부
        scanOpts.scanLicenseSerial = true; // 운전면허증 시리얼번호 스캔 여부
        scanOpts.scanLicenseType = true; // 운전면허증 종류 스캔 여부
        scanOpts.findFace = true; // 얼굴 찾기 여부 (기본값: false)
        scanOpts.faceDataPath = getFaceDataPath(); // 얼굴 데이터 파일 경로, findFace가 true일 때 해당 경로가 비정상적이면 ScannerInitException 발생
        scanOpts.tryColorTest = true; // 입력 프레임이 색깔인지 흑백인지 확인 (기본값: false)
        // 부가적인 인식 동작 옵션 예시
        scanOpts.frameType = QuramOcrScannerFrameType.SINGLE; // 단일 프레임 인식 모드
        scanOpts.numOfCardbox = 10; // 카드 영역 찾는 프레임 개수
        scanOpts.limitAngle = true; // 카드의 각도가 회전되어 있을 때 카드를 찾을 수 있을지 제한하는 옵션 (기본값: 고정)
        // limitAngle = false로 설정 시 인식률 저하될 수 있음.
        scanOpts.autoMode = autoMode;

        if (in_config.equals("") || (in_config.contains("OCR") == false && in_config.contains("SSA") == false )) {
            // config가 비어있는 경우 OCR 만 수행하고, SSA는 수행하지 않음
            scanOpts.doOCR = true;
            scanOpts.doSSA = true;
        }
        else if ((in_config.contains("OCR") == true) && (in_config.contains("SSA") == false)) {
            // config에 "OCR"이 포함되어 있는 경우 OCR 수행
            scanOpts.doOCR = true;
            scanOpts.doSSA = false;
        }
        else if ((in_config.contains("OCR") == false) && (in_config.contains("SSA") == true)) {
            // config에 "SSA"이 포함되어 있는 경우 SSA 수행
            scanOpts.doOCR = false;
            scanOpts.doSSA = true;
        }
        else {
            scanOpts.doOCR = true;
            scanOpts.doSSA = true;
        }

        if(onlySSa)
        {
            scanOpts.doOCR = true;
            scanOpts.doSSA = true;
        }

        scanOpts.threshFD = 0.5f;

        // 인식 시간 저장에 따른 시간 리스트 설정
        ArrayList<Long> recogTimeList = new ArrayList<Long>();
        // 웹 모드는 한장의 이미지로 처리함으로 첫번째 이미지 파일의 리스트를 멀티파트 파일에 추가

        MultipartFile mf = uploadedFiles[0];
        String contentType = mf.getContentType();
        String orgFileName = mf.getOriginalFilename();

        mLogManager.setAppLogString("QuramMI");

        t = System.currentTimeMillis() - st;
        AppLogger.debug(String.format("[TIME] %s time : %d", "controller prepare", t));

        // 입력된 이미지는 반드시 jpg 나 jpeg로 입력되어야 함.
        if (contentType.equals("image/jpg") || contentType.equals("image/jpeg")) {
            AppLogger.info("uploaded file : " + orgFileName);

            System.out.println(orgFileName);

            try {
                // 파일의 정보를 바이트로 변환
                byte[] frameBytes = mf.getBytes();

                ArrayList<byte[]> frameList = new ArrayList<byte[]>();
                // 프레임 리스트에 추가
                frameList.add(frameBytes);

                // 인식 시간 시작 정보 입력
                long recogST = System.currentTimeMillis();

                // 현재 날짜(일/시간/분/초) 단위 및 랜덤값 5자리로 동시 동시 처리 시간에 대한 중복을 시간과 랜덤으로 구분
                // 랜덤 + 시간 정보 순서임..
                Date scanTime = new Date();
                Random rand = new Random();
                SimpleDateFormat scanTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss_SSS");

                String scanTimeDir = "web_" + scanTimeFormat.format(scanTime);

                // 라이브러리 호출 (QuramOcrScanner.scanIDCard)
                // 인식 엔진에 스캔 결과 구분 문구, 스캔 옵션, 프레임리스트, 학습데이터 정보
                String keyword = type + "_" + scanTimeDir;
                AppLogger.info("[scanFrame scanIDCard start]");
                QuramOcrScanResult scanResult = QuramOcrScanner.scanIDCard(keyword, scanOpts,
                        frameList, getTessPath());
                AppLogger.info("[scanFrame scanIDCard end]");
                long recogET = System.currentTimeMillis();
                long recogTime = recogET - recogST;
                // 인식 전체 동작 시간 정보 추가
                // 라이브러리 호출 (전체 인식 시간 정보 업데이트)
                AppLogger.debug(String.format("[TIME] %s time : %d", "jar api call", recogTime));
                scanResult.setRecogTime(recogTime);
                recogTimeList.add(recogTime);

                st = System.currentTimeMillis();

                // 기본 저장 디렉토리
                // String defaultStorageDir = "./result";
                // String defaultStorageDir = "/home/chae/quram2/result";
                String defaultStorageDir = System.getProperty("result.path");
                Path saveDir = Paths.get(defaultStorageDir, scanTimeDir).normalize().toAbsolutePath();

                // 인식이 정상인지 또는 인식이 안된 경우를 분리하여 내부 저장하도록 디렉토리 정보 구분
                if(onlySSa)
                {
                    saveDir = null;
                }
                else if(scanResult.getResultCode().contains(QuramOcrScannerResultCode.SUCCESS.name())) {
                    QuramOcrResultType idType = scanResult.getResultScanType();

                    saveDir = Paths.get(saveDir.toAbsolutePath().toString(), idType.toString()).normalize();

                    // 신분증 사진을 추출할 수 있는 종류에서만 이미지를 추출하고 저장한다.
                    if(scanResult.getResultScanType() == QuramOcrResultType.DRIVER_LICENSE ||
                            scanResult.getResultScanType() == QuramOcrResultType.RESIDENT_REGISTRATION ||
                            scanResult.getResultScanType() == QuramOcrResultType.PASSPORT ||
                            scanResult.getResultScanType() == QuramOcrResultType.ALIEN_REGISTRATION) {

                        // 라이브러리 호출 (얼굴사진, 마스킹된 이미지, 마스킹되지 않는 이미지 )
                        // 신분증의 각 이미지를 byte[] 버퍼로 추출한다. JPG, PNG 포맷의 버퍼로 추출된다.
                        final ResultSaver.ImageFormat format = ResultSaver.ImageFormat.JPG;
                        byte pBuf[] = ResultSaver.getPortraitBuf(scanResult, format);
                        byte mBuf[] = ResultSaver.getMarkedBuf(scanResult, format);
                        byte uBuf[] = ResultSaver.getUnmarkedBuf(scanResult, format);
                        byte mfBuf[] = ResultSaver.getMarkedFrameBuf(scanResult, format);
                        byte ufBuf[] = ResultSaver.getUnmarkedFrameBuf(scanResult, format);
                        // 신분증 이미지 버퍼 파일로 저장
                        String pSavePath = ResultSaver.saveBuffer(pBuf, saveDir,"portrait", format);
                        String mSavePath = ResultSaver.saveBuffer(mBuf, saveDir,"marked_image", format);
                        String uSavePath = ResultSaver.saveBuffer(uBuf, saveDir,"unmarked_image", format);
                        String mfSavePath = ResultSaver.saveBuffer(mfBuf, saveDir,"marked_frame", format);
                        String ufSavePath = ResultSaver.saveBuffer(ufBuf, saveDir,"unmarked_frame", format);
                        // 파일로 저장된 신분증 이미지 경로를 결과 객체에 설정
                        scanResult.setIdImagePath(pSavePath, ResultSaver.ImageType.PORTRAIT);
                        scanResult.setIdImagePath(mSavePath, ResultSaver.ImageType.MARKED_CARD);
                        scanResult.setIdImagePath(uSavePath, ResultSaver.ImageType.UNMARKED_CARD);
                        scanResult.setIdImagePath(mfSavePath, ResultSaver.ImageType.MARKED_FRAME);
                        scanResult.setIdImagePath(ufSavePath, ResultSaver.ImageType.UNMARKED_FRAME);
                    }
                } else {
                    if(autoMode)
                    {
                        saveDir = null;
                    }
                    else
                    {
                        saveDir = Paths.get(saveDir.toAbsolutePath().toString(), "errorRecog_"+type).   normalize();
                    }

                }

                if(saveDir != null)
                {
                    // 입력 이미지 저장
                    String oSavePath = ResultSaver.saveBuffer(frameBytes, saveDir, "original", ResultSaver.ImageFormat.JPG);
                    scanResult.setIdImagePath(oSavePath, ResultSaver.ImageType.ORG_FRAME);
                    // 로그 매니저의 로그 파일로 저장
                    mLogManager.setAppScanInfo(scanResult);
                    String logString = mLogManager.getAppLogString();
                    ResultSaver.saveTextToFile(logString, "result.txt", saveDir);
                }
                // 라이브러리 호출 (이미지 결과를 사전에 호출하면 이미지 저장 경로 포함, 만약 단독 호출하면 이미지 경로 미포함됨)
                if(!onlySSa)
                {
                    scanResult.encryptResult();
                }
                resultJsonStr = ResultSaver.getResultJson(scanResult);
                if(saveDir != null)
                {
                    // 인식 결과 json 저장
                    ResultSaver.saveScanResultToJson(scanResult, saveDir);
                }
                // 라이브러리 호출 (ResultSaver.save)
                // 위의 getResultJson + getProtraitBuf + getMarkedBuf + getUnmarkedBuf 를 동시 처리함.
//                ResultSaver.save (scanResult, saveDir.toString(), keyword, false, true);

                if(!onlySSa)
                {
                    String tmp = new String(resultJsonStr);
                    String jumin = "jumin";
                    int index = tmp.indexOf(jumin);

                    if (index != -1) {
                        char[] charArray = tmp.toCharArray();
                        int startIndex = index + jumin.length() + 5;
                        int indexToChange = index + jumin.length() + 5 + 6 + 1;
                        if (charArray[startIndex] != '"' && startIndex < charArray.length) {
                            for(int i = 0; i < 7; i++)
                            {
                                if (indexToChange >= 0 && indexToChange < charArray.length) {
                                    if(charArray[indexToChange]  == '"')
                                    {
                                        break;
                                    }

                                    charArray[indexToChange] = 'x';
                                }
                                indexToChange++;
                            }
                            tmp = new String(charArray);
                        }
                    }

                    String driver_number = "driver_number";
                    index = tmp.indexOf(driver_number);

                    if (index != -1) {
                        char[] charArray = tmp.toCharArray();
                        int startIndex = index + jumin.length() + 5;
                        int indexToChange = index + driver_number.length() + 5 + 6;
                        if (charArray[startIndex] != '"' && startIndex < charArray.length) {
                            for(int i = 0; i < 6; i++)
                            {
                                if (indexToChange >= 0 && indexToChange < charArray.length) {
                                    if(charArray[indexToChange]  == '"')
                                    {
                                        break;
                                    }

                                    charArray[indexToChange] = 'x';
                                }
                                indexToChange++;
                            }
                            tmp = new String(charArray);
                        }
                    }
                    resultLogger.info(String.format("Result: %s", tmp));
                    AppLogger.info("[scanFrame scanIDCard resultJsonStr]" + tmp);
                }
                else {
                    resultLogger.info(String.format("Result: %s", resultJsonStr));
                    AppLogger.info("[scanFrame scanIDCard resultJsonStr]" + resultJsonStr);
                }


                // String 으로 전환된 JSON 값 리턴 전달
                retJSON = resultJsonStr;
            } catch (Exception e) {
                AppLogger.error("not finalized result with JSON output", e);
                retJSON = ResultSaver.getScanResultStringIfExceptionOccur(e);
            }
        } else {
            retJSON = "Input image file type error";
        }

        long totalET = System.currentTimeMillis();
        long totalTime = totalET - totalST;

        AppLogger.info("[Result of scanned by Web]");
        AppLogger.info("Version: "+ QuramOcrScanner.getVersionInfo()); // 자바 라이브러리, 네이티브 라이브러리, 릴리즈 날짜 정보 전체 출력
        if(!onlySSa)
        {
            String tmp = new String(retJSON);
            String jumin = "jumin";
            int index = tmp.indexOf(jumin);

            if (index != -1) {
                char[] charArray = tmp.toCharArray();
                int startIndex = index + jumin.length() + 5;
                int indexToChange = index + jumin.length() + 5 + 6 + 1;
                if (charArray[startIndex] != '"' && startIndex < charArray.length) {
                    for(int i = 0; i < 7; i++)
                    {
                        if (indexToChange >= 0 && indexToChange < charArray.length) {
                            if(charArray[indexToChange]  == '"')
                            {
                                break;
                            }
                            charArray[indexToChange] = 'x';
                        }
                        indexToChange++;
                    }
                    tmp = new String(charArray);
                }
            }


            String driver_number = "driver_number";
            index = tmp.indexOf(driver_number);

            if (index != -1) {
                char[] charArray = tmp.toCharArray();
                int startIndex = index + jumin.length() + 5;
                int indexToChange = index + driver_number.length() + 5 + 6;
                if (charArray[startIndex] != '"' && startIndex < charArray.length) {
                    for(int i = 0; i < 6; i++)
                    {
                        if (indexToChange >= 0 && indexToChange < charArray.length) {
                            if(charArray[indexToChange]  == '"')
                            {
                                break;
                            }
                            charArray[indexToChange] = 'x';
                        }
                        indexToChange++;
                    }
                    tmp = new String(charArray);
                }
            }
            AppLogger.info(tmp);
        }
        AppLogger.info(String.format("Total Recog. Time: %d", totalTime));
        String recogTimeListStr = "recog time of each frame : ";

        for (long time : recogTimeList) {
            recogTimeListStr += time + " ";
        }
        AppLogger.info(recogTimeListStr);
        AppLogger.info("scan done.");

        return retJSON;
    }

    @PostMapping(value="scan_app", produces="application/json;charset=UTF-8")
    @ResponseBody
    public String scanFrameByApp(@RequestParam("quram_mi_request_json") String json,
                                 @RequestParam(value="quram_mi_request_photo", required=false) MultipartFile photoFile,
                                 @RequestParam(value="quram_mi_request_unmarked_image", required=false) MultipartFile unmarkFile,
                                 @RequestParam(value="quram_mi_request_marked_image", required=false) MultipartFile markFile,
                                 @RequestParam(value="quram_mi_request_frame_image") MultipartFile[] uploadedFiles) {
        String retJSON = "";

        final String userInfoKey = "user_info";
        final String originKey = "origination";
        final String scannerTypeKey = "scanner_type";

        String userInfo = null;
        QuramOcrScannerFrameType frameType = QuramOcrScannerFrameType.SINGLE;
        QuramOcrScannerType scannerType = QuramOcrScannerType.IDCARD_AUTO;

        Map<String, String> infoMap = Util.getMapFromJson(json);

        if(infoMap != null) {
            if(infoMap.containsKey(userInfoKey)) {
                userInfo = infoMap.get(userInfoKey);
            }
            if(infoMap.containsKey(originKey)) {
                String origin = infoMap.get(originKey);
                if(origin != null && !"".equals(origin)) {
                    if (origin.equalsIgnoreCase("app")) {
                        frameType = QuramOcrScannerFrameType.MULTIPLE;
                    } else if (origin.equalsIgnoreCase("web")) {
                        frameType = QuramOcrScannerFrameType.SINGLE;
                    }
                }
            }
            if(infoMap.containsKey(scannerTypeKey)) {
                String type = infoMap.get(scannerTypeKey);
                for(QuramOcrScannerType _type : QuramOcrScannerType.values()) {
                    if(_type.getTypeName().equalsIgnoreCase(type)) {
                        scannerType = _type;
                        break;
                    }
                }
            }
        }

//		AppLogger.info("user info : " + userInfo);
//		AppLogger.info("frame type : " + frameType.name());
//		AppLogger.info("card type : " + scannerType);

        if(userInfo != null) {
            userInfo = userInfo.replace(' ', '_')
                    .replace(":", "")
                    .replace("+", "");
        }

//        ArrayList<InputData> inputList = new ArrayList<>();

        /*
         * 스캔 옵션 예시
         *  - 기본값이 설정되어 있으며 scannerType 값 외에는 ScanOptions 인스턴스 생성한 그대로 사용해도 충분함.
         */
        ScanOptions scanOpts = new ScanOptions();
        scanOpts.scannerType = scannerType; // (기본값: FingramScanner.ScannerType.ID_AUTO), 스캐너 타입 명시적으로 지정할 것
        // 필수 스캔 옵션 (기본값: true)
        scanOpts.scanLicenseNumber = true; // 운전면허증 번호 스캔 여부
        scanOpts.scanIssueDate = true; // 발급일자 스캔 여부
        // 부가적인 스캔 옵션 (기본값: false)
        scanOpts.scanRegion = true; // 발행처 스캔 여부
        scanOpts.scanLicenseSerial = true; // 운전면허증 시리얼번호 스캔 여부
        scanOpts.scanLicenseType = true; // 운전면허증 종류 스캔 여부
        scanOpts.findFace = true; // 얼굴 찾기 여부 (기본값: false)
        scanOpts.faceDataPath = getFaceDataPath(); // 얼굴 데이터 파일 경로, findFace가 true일 때 해당 경로가 비정상적이면 ScannerInitException 발생
        scanOpts.tryColorTest = true; // 입력 프레임이 색깔인지 흑백인지 확인 (기본값: false)
        // 부가적인 인식 동작 옵션 예시
        scanOpts.frameType = frameType; // 단일 프레임 인식 모드
        scanOpts.findEdge = false;

        ArrayList<byte[]> frameList = new ArrayList<byte[]>();

        for(MultipartFile mf : uploadedFiles) {
            String contentType = mf.getContentType();
            String orgFileName = mf.getOriginalFilename();
            if(contentType.equals("image/jpg") || contentType.equals("image/jpeg")) {
                AppLogger.info("uploaded file : "+orgFileName);
                try {
                    byte[] frameBytes = mf.getBytes();

                    // 프레임 리스트에 추가
                    frameList.add(frameBytes);
                } catch (Exception e) {
                    AppLogger.error("", e);
                }
            } else {
                retJSON += Util.getJSONString("{'image':'"+orgFileName+"', 'result':'The uploaded file is not image/jpg content type.'}");
            }
        }

        try {
            QuramOcrScanResult scanResult = QuramOcrScanner.scanIDCard(userInfo, scanOpts, frameList, getTessPath());

            String resultJsonStr = Util.getJSONString(scanResult);

            if (scanResult.getResultCode().contains(QuramOcrScannerResultCode.SUCCESS.name())) {
                resultLogger.info(String.format("success, result: %s", resultJsonStr));
            } else {
                resultLogger.info(String.format("fail, result code: %s", scanResult.getResultCode()));
            }

            retJSON += resultJsonStr;
        } catch(ScannerInitException | ScannerRuntimeException | ScannerEngineVersionException e) {
            AppLogger.error("exception occurs in recognition :", e);
        }

        AppLogger.info("[Result of scanned by App]");
        AppLogger.info(retJSON);
        AppLogger.info("scan done.");

        return retJSON;
    }

    @RequestMapping(value = "ocr_recog/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        return "HELLO!";
    }
}

