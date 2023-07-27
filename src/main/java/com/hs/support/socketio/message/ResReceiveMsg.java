package com.hs.support.socketio.message;

import com.hs.socketio.support.ResponseMessage;

import java.util.List;

public class ResReceiveMsg extends ResponseMessage {

    private static final long serialVersionUID = 5214860430245125117L;

    public static final String EVENTNAME = "supportReceiveMsg";


    public ResReceiveMsg(String code,String error){
        this.code=code;
        this.error=error;
    }

    public ResReceiveMsg(){ }


    private List<ReceiveMsg> msgList;

    private ReceiveMsg msg;


    public List<ReceiveMsg> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<ReceiveMsg> msgList) {
        this.msgList = msgList;
    }

    public ReceiveMsg getMsg() {
        return msg;
    }

    public void setMsg(ReceiveMsg msg) {
        this.msg = msg;
    }
}
