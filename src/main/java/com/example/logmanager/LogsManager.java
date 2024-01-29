package com.example.logmanager;

import com.quram.mi.lib.data.result.QuramOcrCommonIDScanResult;
import com.quram.mi.lib.data.result.QuramOcrDriverLicenseScanResult;
import com.quram.mi.lib.data.result.QuramOcrResidentRegistrationScanResult;
import com.quram.mi.lib.data.result.QuramOcrScanResult;
import com.quram.mi.lib.engine.QuramOcrScanner;

public class LogsManager {

    private String TAG = "QuramOcrLogDebugging";

    private LogModel logModel;
    private LogOutPutModel outPut;
    private String mStrResult;

    private String appInfo = null;

    public LogsManager() {
        if (this.logModel == null || this.outPut == null) {
            this.logModel = new LogModel();
            this.outPut = new LogOutPutModel();
        }
    }

    public String getAppLogString() {
        return LogOutPutModel.getLogString(logModel);
    }

    public void setAppInfo(String userID, String userHashID, String transactionNumber,
                           String clientPlatform, String clientPackage, String clientVersion) {
        logModel.setUserID(userID);
        logModel.setUserHashID(userHashID);
        logModel.setTranscationNumber(transactionNumber);
        logModel.setClientPlatform(clientPlatform);
        logModel.setClientPackage(clientPackage);
        logModel.setClientVersion(clientVersion);
    }

    public void setFaceMatchingScore(float score) {
        logModel.setFaceMatchingScore(score);
    }

    public void setAppLogString(String appinfo) {
        appInfo = appinfo;
    }

    public void setEngineInfo(String engineInfo, String idType, long scanTime, boolean scanDone, String scanState) {
        logModel.setEngineInfo(engineInfo);
        logModel.setIdType(idType);
        logModel.setScanTime(scanTime);
        logModel.setScanDone(scanDone);
        logModel.setScanState(scanState);
    }

    public void setAppScanInfo(boolean isScanResidentIdNumber, boolean isScanName,
                               float faceScore, float specularScore,
                               int genderCode,
                               int birthYear,
                               int issuingArea,
                               String issuingDate,
                               String driverLicenseType,
                               int overseas,
                               int bwScan) {
        logModel.setScanResidentIdNumber(isScanResidentIdNumber);
        logModel.setScanName(isScanName);
        logModel.setFaceScore(faceScore);
        logModel.setSpecularScore(specularScore);
        logModel.setGenderCode(LogModel.GenderCode.values()[genderCode]);
        logModel.setBirthYear(birthYear);
        logModel.setIssuingDate(issuingDate);
        logModel.setRegion(issuingArea);
        logModel.setDriverLicenseType(driverLicenseType);
        logModel.setOverSeas(overseas);
        logModel.setBWScan(bwScan);
    }

    private int getRegionCode(QuramOcrScanResult scanResult) {
        int code = 0;
        if(scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.RESIDENT_REGISTRATION) {
            QuramOcrCommonIDScanResult id = scanResult.getId();
            if(id != null) {
                code = RegionCode.findRegionCode(id.getRegion());
            }
        } else if(scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.DRIVER_LICENSE) {
            QuramOcrCommonIDScanResult id = scanResult.getId();
            if(id != null) {
                QuramOcrDriverLicenseScanResult dlsr = id.getDriverLicense();
                if(dlsr != null) {
                    String driverNumber = dlsr.getDriverNumber();
                    if(driverNumber == null || driverNumber.length() < 2) {
                        return code;
                    }
                    String dnSplit[] = driverNumber.split("-");
                    try {
                        if(dnSplit.length == 3) { // old type
                            String spaceSplit[] = dnSplit[0].split(" ");
                            if(spaceSplit.length > 1) {
                                String region = spaceSplit[0].substring(0, 2);
                                code = RegionCode.findOldDriverLicenseCode(region);
                            }
                        } else if(dnSplit.length == 4) { // new type
                            code = Integer.parseInt(driverNumber.substring(0, 2));
                        } else { // unknown
                            return code;
                        }
                    } catch (NumberFormatException e) {
                        return code;
                    }
                }
            }
        }

        return code;
    }

    public void setAppScanInfo(QuramOcrScanResult scanResult) {
        if( scanResult == null ) {
            return;
        }

        if( scanResult.isComplete() ) {
            if(scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.RESIDENT_REGISTRATION ||
                    scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.DRIVER_LICENSE ||
                    scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.PASSPORT ||
                    scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.ALIEN_REGISTRATION) {
                QuramOcrCommonIDScanResult id = scanResult.getId();
                if (id != null) {
                    String jumin = id.getJumin();
                    if (jumin != null && jumin.length() > 0) {
                        logModel.setScanResidentIdNumber(true);
                        LogModel.GenderCode code = LogModel.getGenderCodeFromIdNum(jumin);
                        logModel.setGenderCode(code);
                        logModel.setBirthYear( LogModel.getBirthYearFromIdNum(jumin) );
                    }

                    if(id.getName() != null && id.getName().length() > 0) {
                        logModel.setScanName(true);
                    }

                    logModel.setFaceScore(id.getFoundFace());
                    logModel.setSpecularScore(id.getSpecularRatio());

                    logModel.setIssuingDate(id.getIssuedDate());
                    logModel.setRegion(getRegionCode(scanResult));

                    if(scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.DRIVER_LICENSE) {
                        QuramOcrDriverLicenseScanResult dlsr = id.getDriverLicense();
                        if(dlsr != null) {
                            logModel.setDriverLicenseType(dlsr.getDriverType());
                        }
                    } else if(scanResult.getResultScanType() == QuramOcrScanner.QuramOcrResultType.DRIVER_LICENSE) {
                        QuramOcrResidentRegistrationScanResult rrsr = id.getResidentRegistration();
                        if(rrsr != null) {
                            if(rrsr.getExpatriate() != null && rrsr.getExpatriate().length() > 0) {
                                logModel.setOverSeas(1);
                            }
                        }
                    }
                }
            }

            logModel.setBWScan(scanResult.isColor() ? 1 : 0);
        }

        logModel.setScanState(scanResult.getResultCode());
        logModel.setScanTime( scanResult.getRecogTime() );
        logModel.setIdType( scanResult.getResultScanType().name() );

        logModel.setEngineInfo(QuramOcrScanner.getVersionInfo());

        logModel.setAnalysisInfo(scanResult.getAnalysisInfo());
    }
}

