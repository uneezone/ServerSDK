package com.example.data;
import com.quram.mi.lib.engine.QuramOcrScanner.QuramOcrScannerFrameType;

public class RequestInfo {
    String req_id;
    QuramOcrScannerFrameType frame_type;
    String req_time;
    DeviceInfo device_info;
    String user_info;
    String user_agent;
    int card_type;
    IdDetail id_detail;

    public String getReq_id() {
        return req_id;
    }
    public void setReq_id(String req_id) {
        this.req_id = req_id;
    }
    public QuramOcrScannerFrameType getFrame_type() {
        return frame_type;
    }
    public void setFrame_type(QuramOcrScannerFrameType frame_type) {
        this.frame_type = frame_type;
    }
    public String getReq_time() {
        return req_time;
    }
    public void setReq_time(String req_time) {
        this.req_time = req_time;
    }
    public DeviceInfo getDevice_info() {
        return device_info;
    }
    public void setDevice_info(DeviceInfo device_info) {
        this.device_info = device_info;
    }
    public String getUser_info() {
        return user_info;
    }
    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }
    public String getUser_agent() {
        return user_agent;
    }
    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }
    public int getCard_type() {
        return card_type;
    }
    public void setCard_type(int card_type) {
        this.card_type = card_type;
    }
    public IdDetail getId_detail() {
        return id_detail;
    }
    public void setId_detail(IdDetail id_detail) {
        this.id_detail = id_detail;
    }
}

