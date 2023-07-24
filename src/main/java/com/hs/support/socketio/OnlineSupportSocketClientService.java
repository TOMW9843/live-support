package com.hs.support.socketio;

import com.hs.support.socketio.message.ReqReadMsg;
import com.hs.support.socketio.message.ReqSendMsg;

public interface OnlineSupportSocketClientService {

    public void send(ReqSendMsg message);


    public void read(ReqReadMsg message);
}
