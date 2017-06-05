package com.icbcasia.icbcamvtmclientapp.frag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.esdk.cc.MobileCC;
import com.huawei.esdk.cc.common.BroadMsg;
import com.huawei.esdk.cc.common.NotifyMessage;
import com.icbcasia.icbcamvtmclientapp.R;
import com.icbcasia.icbcamvtmclientapp.model.FinalMsg;

public class FragDocs extends Fragment {
    private static final String TAG = "FragDocs";

    private LinearLayout ecc_doc;
    private TextView tv_no_doc;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frag_docs, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ecc_doc = (LinearLayout) getActivity().findViewById(R.id.ecc_doc);
        tv_no_doc = (TextView) getActivity().findViewById(R.id.tv_no_doc);

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(receiver, FinalMsg.getFilter());
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
            if (NotifyMessage.CALL_MSG_USER_RECEIVE_SHARED_DATA.equals(action)) {
                String recode = broadMsg.getRequestCode().getRetCode();
                String msg = broadMsg.getRequestInfo().getMsg();

                String sharedType = "2".equals(recode) ? "Application sharing" : "";
                String sharedState = "1".equals(msg) ? "begin !" : "end !";
                Log.e(TAG, sharedType + " - " + sharedState);

                tv_no_doc.setVisibility(View.GONE);
                MobileCC.getInstance().setDesktopShareContainer(getActivity(), ecc_doc); // 接收共享数据，设置显示容器
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
}
