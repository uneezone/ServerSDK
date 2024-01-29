package com.example.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class ResponseParams {

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
    @JsonProperty("RET_CD")
    private String RET_CD;
    @JsonProperty("VRF_VAL")
    private String VRF_VAL;
    @JsonProperty("ERR_MSG")
    private String ERR_MSG;
    @JsonProperty("RECV_TM")
    private String RECV_TM;
    @JsonProperty("RESP_TM")
    private String RESP_TM;
    @JsonProperty("PROC_TM")
    private String PROC_TM;
    @JsonProperty("FIRST_REGISTRY_PRT")
    private String FIRST_REGISTRY_PRT;

    public void setFILE_NM(String FILE_NM) {
        this.FILE_NM = FILE_NM;
    }

    public void setIMG_TYPE(String IMG_TYPE) {
        this.IMG_TYPE = IMG_TYPE;
    }

    public void setSTR_IMG(String STR_IMG) {
        this.STR_IMG = STR_IMG;
    }

    public void setMEM_CMP_CD(String MEM_CMP_CD) {
        this.MEM_CMP_CD = MEM_CMP_CD;
    }

    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public void setOS_INFO(String OS_INFO) {
        this.OS_INFO = OS_INFO;
    }

    public void setDEVICE_INFO(String DEVICE_INFO) {
        this.DEVICE_INFO = DEVICE_INFO;
    }

    public void setBROWSER_INFO(String BROWSER_INFO) {
        this.BROWSER_INFO = BROWSER_INFO;
    }

    public void setRET_CD(String RET_CD) {
        this.RET_CD = RET_CD;
    }

    public void setVRF_VAL(String VRF_VAL) {
        this.VRF_VAL = VRF_VAL;
    }

    public void setERR_MSG(String ERR_MSG) {
        this.ERR_MSG = ERR_MSG;
    }

    public void setRECV_TM(String RECV_TM) {
        this.RECV_TM = RECV_TM;
    }

    public void setRESP_TM(String RESP_TM) {
        this.RESP_TM = RESP_TM;
    }

    public void setPROC_TM(String PROC_TM) {
        this.PROC_TM = PROC_TM;
    }

    public void setFIRST_REGISTRY_PRT(String FIRST_REGISTRY_PRT) {
        this.FIRST_REGISTRY_PRT = FIRST_REGISTRY_PRT;
    }

    public void setResponseData(Map<String, Object> responseData) {
    }
}

