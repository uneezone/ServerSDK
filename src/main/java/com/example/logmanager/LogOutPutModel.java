package com.example.logmanager;


import java.util.Locale;

public class LogOutPutModel {

    private String rawDataObject="";
    private static final String separator = ";";

    public LogOutPutModel()
    {

    }

    private static String addTokenData( String token ){
        return token + separator;
    }

    public static String getLogString(LogModel logModel) {
        String log = "";

        // App Info
        log += addTokenData( logModel.getUserID() );
        log += addTokenData( logModel.getUserHashID() );
        log += addTokenData( logModel.getTranscationNumber() );
        log += addTokenData( logModel.getClientPlatform() );
        log += addTokenData( logModel.getClientPackage() );
        log += addTokenData( logModel.getClientVersion() );

        log += " | ";

        // engine data
        log += addTokenData( logModel.getEngineInfo() );
        log += addTokenData( logModel.getScanDate() );
        log += addTokenData( String.format( Locale.getDefault(), "%5d ms", logModel.getScanTime() ) );
        log += addTokenData( logModel.getScanState() );

        // scan data
        log += addTokenData( logModel.getIdType() );
        log += addTokenData( String.valueOf( logModel.isScanName() ) );
        log += addTokenData( String.valueOf( logModel.isScanResidentIdNumber() ) );
        log += addTokenData( String.format( Locale.getDefault(), "%2.2f", logModel.getFaceScore() ) );
        log += addTokenData( String.format( Locale.getDefault(), "%2.2f", logModel.getSpecularScore() ) );

        // server matching score
        log += addTokenData( String.format( Locale.getDefault(), "%2.2f", logModel.getFaceMatchingScore() ) );

        log += addTokenData( logModel.getGenderCode().getStringValue() );
        log += addTokenData( String.format( Locale.getDefault(), "%2d", logModel.getBirthYear() ) );
        log += addTokenData( String.format( Locale.getDefault(), "%5d", logModel.getRegion() ) );
        log += addTokenData( logModel.getIssuingDate() );
        log += addTokenData( logModel.getDriverLicenseType() );
        log += addTokenData( String.format( Locale.getDefault(), "%1d", logModel.getOverseas() ) );
        log += addTokenData( String.format( Locale.getDefault(), "%1d", logModel.getBwScan() ) );
        log += addTokenData( logModel.getAnalysisInfo() );

        return log;
    }
}

