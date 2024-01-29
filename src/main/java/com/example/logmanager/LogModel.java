package com.example.logmanager;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogModel {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public class Point2D {
        int x;
        int y;
        public Point2D( int xaxis, int yaxis ) {
            this.x = xaxis;
            this.y = yaxis;
        }
    }

    public class CardBoxInfo {
        Point2D TL;
        Point2D TR;
        Point2D BL;
        Point2D BR;

        public CardBoxInfo(int TLx, int TLy, int TRx, int TRy, int BLx, int BLy, int BRx, int BRy){
            TL = new Point2D( TLx, TLy );
            TR = new Point2D( TRx, TRy );
            BL = new Point2D( BLx, BLy );
            BR = new Point2D( BRx, BRy );
        }

        public String toString() {
            return "[ (" + TL.x + "," + TL.y + "),(" + TR.x + "," + TR.y + "),(" + BL.x + "," + BL.y + "),(" + BR.x + "," + BR.y + ")]";
        }
    }

    public enum GenderCode{
        UNKOWN(                     0, "UNKOWN"),
        MALE_KOR(                   1, "MALE_KOR"),
        FEMALE_KOR(                 2, "FEMALE_KOR"),
        MALE_KOR_AFTER21TH(         3, "MALE_KOR_AFTER21TH"),
        FEMALE_KOR_AFTER21TH(       4, "FEMALE_KOR_AFTER21TH"),
        MALE_FOREIGN(               5, "MALE_FOREIGN"),
        FEMALE_FOREIGN(             6, "FEMALE_FOREIGN"),
        MALE_FOREIGN_AFTER21TH(     7, "MALE_FOREIGN_AFTER21TH"),
        FEMALE_FOREIGN_AFTER21TH(   8, "FEMALE_FOREIGN_AFTER21TH");

        private final int enumNmber;
        private final String enumKey;

        GenderCode(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() {
            return enumNmber;
        }

        public String getStringValue() {
            return enumKey;
        }
    }

    public static GenderCode getGenderCodeFromIdNum( String idNum ) {
        int genderCodeIndex = idNum.lastIndexOf('-');
        if( idNum.length() < genderCodeIndex + 2 )
            return GenderCode.UNKOWN;

        int genderCode = Integer.parseInt(idNum.substring(genderCodeIndex + 1, genderCodeIndex + 2));
        return GenderCode.values()[genderCode];
    }

    public static int getBirthYearFromIdNum( String idNum ) {
        if( idNum.length() < 3 ) return 0;
        return Integer.parseInt(idNum.substring(0, 2));
    }

    /** app info **/
    private String userID = null;
    private String userHashID = null;
    private String transcationNumber = null;

    private String clientPlatform = null;
    private String clientPackage = null;
    private String clientVersion = null;

    public void setUserID(String id) { userID = id; }
    public String getUserID(){ return userID; };

    public void setUserHashID(String hashID) { userHashID = hashID; }
    public String getUserHashID(){ return userHashID; }

    public void setTranscationNumber(String tNumber) { transcationNumber = tNumber; }
    public String getTranscationNumber(){ return transcationNumber; }

    public void setClientPlatform(String platfrom) { clientPlatform = platfrom; }
    public String getClientPlatform(){
        if( clientPlatform == null ) {
            return "ServerOS_" + OS;
        }
        return clientPlatform;
    }

    public void setClientPackage(String pakcage) { clientPackage = pakcage; }
    public String getClientPackage() { return clientPackage; }

    public void setClientVersion(String version) { clientVersion = version; }
    public String getClientVersion(){ return clientVersion; }

    /** app extra result **/
    private float faceMatchingScore;
    public void setFaceMatchingScore(float score) { faceMatchingScore = score; }
    public float getFaceMatchingScore(){ return faceMatchingScore; }


    /** engine info **/
    private String engineInfo = null;
    private String idType = null;
    private String scanDate = null;
    private long scanTime = 0L;
    private boolean scanDone = false;
    private String scanState = null;

    public void setEngineInfo(String info) { engineInfo = info; }
    public String getEngineInfo(){ return engineInfo; }

    public void setIdType(String type){
        idType = type;
    }
    public String getIdType(){
        return idType;
    }

    public void setScanDate(String date) { scanDate = date; }
    public String getScanDate() {
        if( scanDate == null ) {
            return getDateTime();
        }
        return scanDate;
    }

    public void setScanTime(long time) { scanTime = time; }
    public long getScanTime(){ return scanTime; }

    public void setScanDone(boolean done) { scanDone = done; }
    public boolean setScanDone(){ return scanDone; }

    public void setScanState(String state) { scanState = state; }
    public String getScanState(){ return scanState; }

    /** scan result **/
    private boolean isScanResidentIdNumber = false;
    private boolean isScanName = false;
    private float faceScore = 0.0f;
    private float specularScore = 0.0f;
    private GenderCode genderCode = GenderCode.UNKOWN;
    private int birthYear = 0;
    private int issuingArea = 0; // to enum?
    private String issuingDate = null;
    private String driverLicenseType = null;
    private int overseas = 0;
    private int bwScan = 0;
    private String analysisInfo;

    public void setScanResidentIdNumber(boolean scan) { isScanResidentIdNumber = scan; }
    public boolean isScanResidentIdNumber(){ return isScanResidentIdNumber; }

    public void setScanName(boolean scan) { isScanName = scan; }
    public boolean isScanName(){ return isScanName; }

    public void setFaceScore(float score) { faceScore = score; }
    public float getFaceScore(){ return faceScore; }

    public void setSpecularScore(float score) { specularScore = score; }
    public float getSpecularScore() { return specularScore; }

    public void setGenderCode(GenderCode code) { genderCode = code; }
    public GenderCode getGenderCode(){ return genderCode; }

    public void setBirthYear(int year) { birthYear = year; }
    public int getBirthYear(){ return birthYear; }

    public void setRegion(int area) { issuingArea = area; }
    public int getRegion(){ return issuingArea; }

    public void setIssuingDate(String date) { issuingDate = date; }
    public String getIssuingDate(){ return issuingDate; }

    public void setDriverLicenseType(String type) { driverLicenseType = type; }
    public String getDriverLicenseType(){ return driverLicenseType; }

    public void setOverSeas(int flag) { overseas = flag; }
    public int getOverseas(){ return overseas; }

    public void setBWScan(int scan) { bwScan = scan; }
    public int getBwScan(){ return bwScan; }

    public void setAnalysisInfo(String analysisInfo) { this.analysisInfo = analysisInfo; }
    public String getAnalysisInfo() { return analysisInfo; }

    public String setAppName() {
        if (OS.indexOf("sunos") >= 0)
            return "ServerOS_"+ OS;
        else
            return "ServerOS_None";
    }

    public String getDateTime(){
        Date time = new Date();
        SimpleDateFormat currDateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.KOREA);
        return currDateTime.format(time).toString();
    }

}

