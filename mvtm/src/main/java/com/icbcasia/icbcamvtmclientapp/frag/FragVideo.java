package com.icbcasia.icbcamvtmclientapp.frag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.esdk.cc.MobileCC;
import com.huawei.esdk.cc.common.BroadMsg;
import com.huawei.esdk.cc.common.NotifyMessage;
import com.icbcasia.icbcamvtmclientapp.R;
import com.icbcasia.icbcamvtmclientapp.model.FinalMsg;

public class FragVideo extends Fragment implements View.OnTouchListener {
    private static final String TAG = "FragVideo";
    private boolean isVideo;
    private boolean isHide;
//    private PopupWindow popupWindow;

    private RelativeLayout ecc_video_large;
    private RelativeLayout ecc_video_small;
    private RelativeLayout ecc_remoteview;
    private FrameLayout ecc_localview;
    private FrameLayout ecc_shelter;
    private ImageView iv_call_anim;
    private TextView tv_call_anim;
    private AnimationDrawable animationDrawable;
    private RelativeLayout.LayoutParams rlayoutParams = new RelativeLayout
            .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frag_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ecc_remoteview = (RelativeLayout) getActivity().findViewById(R.id.ecc_remoteview);
        ecc_video_large = (RelativeLayout) getActivity().findViewById(R.id.ecc_video_large);
        ecc_video_small = (RelativeLayout) getActivity().findViewById(R.id.ecc_video_small);
        ecc_shelter = (FrameLayout) getActivity().findViewById(R.id.ecc_shelter);
        iv_call_anim = (ImageView) getActivity().findViewById(R.id.iv_call_anim);
        tv_call_anim = (TextView) getActivity().findViewById(R.id.tv_call_anim);

        ecc_localview = new FrameLayout(getActivity());
        ecc_video_small.addView(ecc_localview, rlayoutParams);

        iv_call_anim.setImageResource(R.drawable.anim_call);
        animationDrawable = (AnimationDrawable) iv_call_anim.getDrawable();
        animationDrawable.start();

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(receiver, FinalMsg.getFilter());

        getActivity().findViewById(R.id.ll_video).setOnTouchListener(this);
        getActivity().findViewById(R.id.ll_message).setOnTouchListener(this);
        getActivity().findViewById(R.id.ll_docs).setOnTouchListener(this);
    }

//    Handler handler = new Handler();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent.getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
            if (NotifyMessage.CALL_MSG_USER_JOIN.equals(action)) {
                Log.e(TAG, "join conf.");
                isVideo = true;
                animationDrawable.stop();
//                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.loading);
//                iv_call_anim.setImageResource(R.drawable.anim_loading);
//                iv_call_anim.startAnimation(animation);
////                tv_call_anim.setText(R.string.ecc_handle);
//                Log.e(TAG, getString(R.string.ecc_handle));
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        iv_call_anim.clearAnimation();
                        ecc_remoteview.removeAllViews();
                        MobileCC.getInstance().setVideoContainer(getActivity(), ecc_localview, ecc_remoteview);
                        showPromte(1);
//                    }
//                }, 500);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MobileCC.getInstance().switchCamera();
//                    }
//                }, 1100);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MobileCC.getInstance().switchCamera();
//                    }
//                }, 1700);
            }
//            else if (NotifyMessage.CALL_MSG_GET_VIDEO_INFO.equals(action)) {
//            } else if (NotifyMessage.AUTH_MSG_ON_LOGIN.equals(action)) {
//                if (("0").equals(broadMsg.getRequestCode().getRetCode())) {
////                    tv_call_anim.setText(getString(R.string.ecc_calling));
////                    Log.e(TAG, getString(R.string.ecc_calling));
//                }
//            } else if (NotifyMessage.CALL_MSG_ON_CONNECTED.equals(action)) {
//                if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getRequestInfo().getMsg())) {
////                    tv_call_anim.setText(R.string.ecc_audio_calling);
////                    Log.e(TAG, getString(R.string.ecc_audio_calling));
//                } else if (MobileCC.MESSAGE_TYPE_AUDIO.equals(broadMsg.getRequestInfo().getMsg())) {
////                    tv_call_anim.setText(R.string.ecc_update_video);
////                    Log.e(TAG, getString(R.string.ecc_update_video));
//                } else if (MobileCC.VIDEO_CALL.equals(broadMsg.getRequestInfo().getMsg())) {
//                    Log.e(TAG, "MobileCC.VIDEO_CALL");
//                }
//            } else if (NotifyMessage.CALL_MSG_ON_QUEUING.equals(action)) {
////                tv_call_anim.setText(R.string.ecc_queuing);
////                Log.e(TAG, getString(R.string.ecc_queuing));
//            }
        }
    };

    private void showPromte(int choice) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View view = factory.inflate(R.layout.shelter, null);
        TextView tv_shelter = (TextView) view.findViewById(R.id.tv_shelter);
        final Button bt_shelter = (Button) view.findViewById(R.id.bt_shelter);
        bt_shelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                popupWindow.dismiss();
                ecc_shelter.setVisibility(View.GONE);
            }
        });
        if (choice == 0) {
            bt_shelter.setVisibility(View.GONE);
            tv_shelter.setText(R.string.ecc_permission_deny);
        }
        ecc_shelter.setVisibility(View.VISIBLE);
        ecc_shelter.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

//        int dpi = App.DM.densityDpi;
//        popupWindow = new PopupWindow(view, 350 * dpi / 160, 350 * dpi / 160, false);
//        popupWindow.showAtLocation(ecc_video_large, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.ll_message:
            case R.id.ll_docs:
                if (isHide) return false;
                ecc_video_small.removeAllViews();
                ecc_video_small.addView(ecc_localview, new RelativeLayout.LayoutParams(1, 1));
                isHide = true;
                break;
            case R.id.ll_video:
                if (!isHide) return false;
                ecc_video_small.removeAllViews();
                ecc_video_small.addView(ecc_localview, rlayoutParams);
                isHide = false;
                break;
            default:
                break;
        }
        return false;
    }
}
