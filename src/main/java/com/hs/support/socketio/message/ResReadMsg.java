package com.hs.support.socketio.message;

import com.hs.socketio.support.ResponseMessage;

public class ResReadMsg extends ResponseMessage {
    private static final long serialVersionUID = 8514253612328928318L;

    public static final String EVENTNAME = "supportReadMsgConfirm";


    public ResReadMsg(String code,String error){
        this.code=code;
        this.error=error;
    }

    public ResReadMsg(){ }
}
