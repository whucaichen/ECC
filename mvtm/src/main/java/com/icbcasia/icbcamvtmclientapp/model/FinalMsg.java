package com.icbcasia.icbcamvtmclientapp.model;

import android.content.IntentFilter;

import com.huawei.esdk.cc.common.NotifyMessage;

/**
 * Created by Chance on 17/04/05.
 */
public class FinalMsg {
    public static final int MSG_LOGIN_DONE = 100;
    public static final int MSG_LOGIN_FAIL = 101;
    public static final int MSG_LOGIN_ANIM = 102;
    public static final int MSG_LOGOUT = 103;
    public static final int MSG_VERIFY_CODE = 104;

    public static final int MSG_MEET_START = 200;
    public static final int MSG_MEET_STOP = 201;

    public static final int MSG_CHAT_SENDED = 300;
    public static final int MSG_CHAT_RECEIVE = 301;

    public static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NotifyMessage.AUTH_MSG_ON_LOGIN);
        filter.addAction(NotifyMessage.AUTH_MSG_ON_LOGOUT);

        filter.addAction(NotifyMessage.CALL_MSG_ON_VERIFYCODE);
        filter.addAction(NotifyMessage.CALL_MSG_ON_POLL);
        filter.addAction(NotifyMessage.CALL_MSG_ON_NET_QUALITY_LEVEL);
        filter.addAction(NotifyMessage.CC_MSG_CONTENT);

        filter.addAction(NotifyMessage.CALL_MSG_ON_CONNECT);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CONNECTED);
        filter.addAction(NotifyMessage.CALL_MSG_ON_DISCONNECTED);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUING);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUE_INFO);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);
        filter.addAction(NotifyMessage.CALL_MSG_ON_QUEUE_TIMEOUT);

        filter.addAction(NotifyMessage.CHAT_MSG_ON_RECEIVE);
        filter.addAction(NotifyMessage.CHAT_MSG_ON_SEND);

        filter.addAction(NotifyMessage.CALL_MSG_USER_JOIN);
        filter.addAction(NotifyMessage.CALL_MSG_USER_END);
        filter.addAction(NotifyMessage.CALL_MSG_USER_STATUS);
        filter.addAction(NotifyMessage.CALL_MSG_USER_NETWORK_ERROR);
        filter.addAction(NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA);

        filter.addAction(NotifyMessage.CALL_MSG_REFRESH_LOCALVIEW);
        filter.addAction(NotifyMessage.CALL_MSG_REFRESH_REMOTEVIEW);
        filter.addAction(NotifyMessage.CALL_MSG_ON_APPLY_MEETING);
        filter.addAction(NotifyMessage.CALL_MSG_ON_STOP_MEETING);
        filter.addAction(NotifyMessage.CALL_MSG_GET_VIDEO_INFO);

        filter.addAction(NotifyMessage.CALL_MSG_ON_DROPCALL);
        filter.addAction(NotifyMessage.CALL_MSG_ON_CALL_END);
        filter.addAction(NotifyMessage.CALL_MSG_ON_SUCCESS);
        filter.addAction(NotifyMessage.CALL_MSG_ON_FAIL);

        return filter;
    }
}
