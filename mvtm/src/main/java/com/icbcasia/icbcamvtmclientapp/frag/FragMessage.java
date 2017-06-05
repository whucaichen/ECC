package com.icbcasia.icbcamvtmclientapp.frag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.allenliu.badgeview.BadgeFactory;
import com.allenliu.badgeview.BadgeView;
import com.huawei.esdk.cc.MobileCC;
import com.huawei.esdk.cc.common.BroadMsg;
import com.huawei.esdk.cc.common.NotifyMessage;
import com.icbcasia.icbcamvtmclientapp.R;
import com.icbcasia.icbcamvtmclientapp.model.FinalMsg;
import com.icbcasia.icbcamvtmclientapp.model.MyChatAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FragMessage extends Fragment {
    private static final String TAG = "FragMessage";
    private static final int ME = 0;
    private static final int OTHER = 1;

    private ListView lv_message;
    private EditText et_message;
    private Button bt_send;
    private ImageView iv_message;

    private boolean isHide;
    private int countMsg = 0;
    private BadgeView badgeView;

    private LocalBroadcastManager localBroadcastManager;

    ArrayList<HashMap<String, Object>> chatList = new ArrayList();
    String[] from = {"image", "text"};
    int[] to = {R.id.iv_head_me, R.id.tv_msg_me, R.id.iv_head_other, R.id.tv_msg_other};
    int[] layout = {R.layout.msg_me, R.layout.msg_other};
    MyChatAdapter myChatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frag_message, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lv_message = (ListView) getActivity().findViewById(R.id.lv_message);
        et_message = (EditText) getActivity().findViewById(R.id.et_message);
        bt_send = (Button) getActivity().findViewById(R.id.bt_send);
        iv_message = (ImageView) getActivity().findViewById(R.id.iv_message);

        myChatAdapter = new MyChatAdapter(getActivity(), chatList, layout, from, to);
        lv_message.setAdapter(myChatAdapter);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myWord = et_message.getText().toString();
                if (myWord.length() == 0)
                    return;
                et_message.setText("");
                MobileCC.getInstance().sendMsg(myWord);
            }
        });
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(receiver, FinalMsg.getFilter());

        badgeView = BadgeFactory.create(getActivity())
                .setBadgeGravity(Gravity.RIGHT | Gravity.TOP)
                .setTextColor(Color.WHITE)
                .setBadgeBackground(Color.RED)
                .setShape(BadgeView.SHAPE_CIRCLE)
                .setWidthAndHeight(20, 20)
                .setTextSize(10);
    }

    private void addMsgToChatList(String text, int who) {
        HashMap<String, Object> map = new HashMap();
        map.put("person", who);
        map.put("image", who == ME ? R.drawable.ic_male : R.drawable.ic_female);
        map.put("text", text);
        chatList.add(map);
        myChatAdapter.notifyDataSetChanged();
        lv_message.setSelection(chatList.size() - 1);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
            if (NotifyMessage.CHAT_MSG_ON_SEND.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, getString(R.string.text_send_fail) + broadMsg.getRequestCode().getErrorCode());
                } else {
                    if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRetCode())) {
                        String content = broadMsg.getRequestInfo().getMsg();
                        if (!"".equals(content)) addMsgToChatList(content, ME);
                    } else {
                        Log.e(TAG, getString(R.string.text_send_fail));
//                        Toast.makeText(getActivity(), getString(R.string.text_send_fail) + broadMsg.getRequestCode().getRetCode(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (NotifyMessage.CHAT_MSG_ON_RECEIVE.equals(action)) {
                String receiveData = broadMsg.getRequestInfo().getMsg();
//                Log.e(TAG, "MSG ENCODING: " + getEncoding(receiveData));
//                Log.e(TAG, "MSG CONTENT: " + receiveData);
                addMsgToChatList(receiveData, OTHER);
                updateInfo();
            } else if (NotifyMessage.CALL_MSG_ON_CONNECTED.equals(action)) {
                if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getRequestInfo().getMsg())) {
                    addMsgToChatList(getString(R.string.ecc_msg_connected), OTHER);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHide = hidden;
        if (!hidden) {
            countMsg = 0;
            badgeView.unbind();
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
        }
    }

    private void updateInfo() {
        if (isHide) {
            Log.e(TAG, String.valueOf(countMsg));
            badgeView.setBadgeCount(++countMsg).bind(iv_message);
        }
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }
}
