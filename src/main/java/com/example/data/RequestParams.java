package com.example.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

public class RequestParams {

    private static MultipartFile[] files;
    private static String image_base64;
    private Boolean debug;


    @JsonProperty("FILE_NM")
    private String FILE_NM;
    @JsonProperty("IMG_TYPE")
    private String IMG_TYPE;
    @JsonProperty("STR_IMG")
    private String STR_IMG;
    @JsonProperty("MEM_CMP_CD")
    private String MEM_CMP_CD;
    @JsonProperty("CHANNEL_ID")
    private String CHANNEL_ID;
    @JsonProperty("CUST_ID")
    private String CUST_ID;
    @JsonProperty("OS_INFO")
    private String OS_INFO;
    @JsonProperty("DEVICE_INFO")
    private String DEVICE_INFO;
    @JsonProperty("BROWSER_INFO")
    private String BROWSER_INFO;

    public String getFILE_NM() {
        return FILE_NM;
    }

    public void setFILE_NM(String FILE_NM) {
        this.FILE_NM = FILE_NM;
    }

    public String getIMG_TYPE() {
        return IMG_TYPE;
    }

    public void setIMG_TYPE(String IMG_TYPE) {
        this.IMG_TYPE = IMG_TYPE;
    }

    public String getSTR_IMG() {
        return STR_IMG;
    }

    public void setSTR_IMG(String STR_IMG) {
        this.STR_IMG = STR_IMG;
    }

    public String getMEM_CMP_CD() {
        return MEM_CMP_CD;
    }

    public void setMEM_CMP_CD(String MEM_CMP_CD) {
        this.MEM_CMP_CD = MEM_CMP_CD;
    }

    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getOS_INFO() {
        return OS_INFO;
    }

    public void setOS_INFO(String OS_INFO) {
        this.OS_INFO = OS_INFO;
    }

    public String getDEVICE_INFO() {
        return DEVICE_INFO;
    }

    public void setDEVICE_INFO(String DEVICE_INFO) {
        this.DEVICE_INFO = DEVICE_INFO;
    }

    public String getBROWSER_INFO() {
        return BROWSER_INFO;
    }

    public void setBROWSER_INFO(String BROWSER_INFO) {
        this.BROWSER_INFO = BROWSER_INFO;
    }

    public static MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public static String getImage_base64() {
        return image_base64;
    }

    public void setImage_base64(String image_base64) {
        this.image_base64 = image_base64;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
}

