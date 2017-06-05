package com.icbcasia.icbcamvtmclientapp.bak;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.esdk.cc.MobileCC;
import com.huawei.esdk.cc.common.BroadMsg;
import com.huawei.esdk.cc.common.NotifyMessage;
import com.icbcasia.icbcamvtmclientapp.R;
import com.icbcasia.icbcamvtmclientapp.frag.FragDocs;
import com.icbcasia.icbcamvtmclientapp.frag.FragMessage;
import com.icbcasia.icbcamvtmclientapp.frag.FragVideo;
import com.icbcasia.icbcamvtmclientapp.model.AuthVarify;
import com.icbcasia.icbcamvtmclientapp.model.FinalMsg;
import com.icbcasia.icbcamvtmclientapp.model.FragmentSwitchTool;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivityBak3 extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String LOGIN_ID = "mVTMAndroid";
    private static String HOST_IP = "14.136.198.49";
    private static String HOST_PORT = "8243";
    private static String SIP_IP = "14.136.198.49";
    private static String SIP_PORT = "5060";
    private static String CODE_VOICE = "6699";
    private static String CODE_TEXT = "6677";
    private boolean isLogin, isTexted, isCalled, isDismiss, isExit;

    private LinearLayout ll_video, ll_message, ll_docs, ll_exit;
    private ImageView iv_video, iv_message, iv_docs;
    private TextView tv_video, tv_message, tv_docs;
    private FragmentSwitchTool fragmentSwitchTool;

    private LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLocate();
        setContentView(R.layout.activity_main);
//        StatusBarUtil.setTransparent(MainActivity.this);
        StatusBarUtil.setTranslucent(MainActivityBak3.this, 50);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();

        localBroadcastManager.registerReceiver(receiver, FinalMsg.getFilter());

//        new StartVerify().execute();
        permitLogin(LOGIN_ID);
    }

    private void initView() {
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);
        ll_docs = (LinearLayout) findViewById(R.id.ll_docs);
        ll_exit = (LinearLayout) findViewById(R.id.ll_exit);
        ll_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_exit.setSelected(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityBak3.this);
                builder.setMessage(R.string.ecc_exit_app);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ecc_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int which) {
                        isExit = true;
                        MainActivityBak3.this.finish();
                    }
                });
                builder.setNegativeButton(R.string.ecc_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int which) {
                        ll_exit.setSelected(false);
                    }
                });
                builder.create().show();
                return;
            }
        });

        iv_video = (ImageView) findViewById(R.id.iv_video);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        iv_docs = (ImageView) findViewById(R.id.iv_docs);

        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_docs = (TextView) findViewById(R.id.tv_docs);

        fragmentSwitchTool = new FragmentSwitchTool(getSupportFragmentManager(), R.id.fl_container);
        fragmentSwitchTool.setClickableViews(ll_video, ll_message, ll_docs);
        fragmentSwitchTool.addSelectedViews(new View[]{iv_video, tv_video})
                .addSelectedViews(new View[]{iv_message, tv_message})
                .addSelectedViews(new View[]{iv_docs, tv_docs});
        fragmentSwitchTool.setFragments(FragVideo.class, FragMessage.class, FragDocs.class);
        fragmentSwitchTool.initAll();
    }

    private class StartVerify extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                String authVersion = uri.getQueryParameter("authVersion");
                String authData = uri.getQueryParameter("authData");
                String callData = uri.getQueryParameter("callData");

                if (authVersion != null && authData != null && callData != null) {
                    boolean verify = AuthVarify.mVTMAuthVerifyData(authData, authVersion);
                    Log.e(TAG, "[Verify Result]:" + verify + " [Verify Info]:" + authData + " - " + authVersion);
                    if (verify) {
                        int p1 = callData.indexOf("_");
                        int p2 = callData.lastIndexOf("_");
                        LOGIN_ID = callData.substring(0, p1);
                        CODE_TEXT = callData.substring(p1 + 1, p2);
                        CODE_VOICE = callData.substring(p2 + 1);
                        return callData;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o == null) {
                Log.e(TAG, "URI不合法");
                MainActivityBak3.this.finish();
            } else
                permitLogin(LOGIN_ID);
        }
    }

    private void setLocate() {
        Uri uri = getIntent().getData();
        if (uri != null) {
            String lang = uri.getQueryParameter("lang");
            if (lang != null) {
                Resources RES = getApplicationContext().getResources();
                Log.e(TAG, "setLocate: " + lang);
                lang = lang.toLowerCase();
                Locale temp = null;
                if (lang.equals("en")) {
                    temp = Locale.ENGLISH;
                } else if (lang.equals("zh_cn")) {
                    temp = Locale.SIMPLIFIED_CHINESE;
                } else if (lang.equals("zh_hk")) {
                    temp = Locale.TRADITIONAL_CHINESE;
                }
                if (temp != null && !temp.equals(RES.getConfiguration().locale)) {
                    RES.getConfiguration().locale = temp;
                    Log.e(TAG, "语言设置已改变: " + RES.getConfiguration().locale);
                    RES.updateConfiguration(RES.getConfiguration(), RES.getDisplayMetrics());
                }
            }
        }
    }

    private void permitLogin(String loginId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//        if (PermissionChecker.checkPermission(this, Manifest.permission.CAMERA,Process.myPid(),Process.myUid(), getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M");
//            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Log.e(TAG, "android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M");
            if (isCameraCanUse())
                loginECC(loginId);
            else
                getAppDetailSettingIntent();
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }
    }

    private AlertDialog dialog = null;

    private void getAppDetailSettingIntent() {
        dialog = new AlertDialog.Builder(MainActivityBak3.this)
                .setMessage(getString(R.string.ecc_permission_deny))
                .setPositiveButton(getString(R.string.ecc_set_permission), null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//                        startActivity(localIntent);
                startActivityForResult(localIntent, 666);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666 && isCameraCanUse() && dialog != null) {
            dialog.dismiss();
            loginECC(LOGIN_ID);
        }
    }

    //    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loginECC(LOGIN_ID);
            } else {
                Toast.makeText(MainActivityBak3.this, getString(R.string.ecc_permission), Toast.LENGTH_SHORT).show();
//                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    private void loginECC(String loginId) {
//        String ipStr = "218.4.33.199";
        String ipStr = HOST_IP;
        String portStr = HOST_PORT;
        boolean isHttps = true;
        int retCode = MobileCC.getInstance().setHostAddress(ipStr, portStr, isHttps, MobileCC.SERVER_MS);
        if (retCode == 0) {
//            ipStr = "222.92.146.242";
            ipStr = SIP_IP;
            portStr = SIP_PORT;
            retCode = MobileCC.getInstance().setSIPServerAddress(ipStr, portStr);
            if (retCode == 0) {
//                MobileCC.getInstance().setTransportSecurity(false, false);
                loginId = (new Random().nextInt(10000) + "000").substring(0, 4) + loginId;
                Log.e(TAG, retCode + " loginName = " + loginId);
                if (MobileCC.getInstance().login("1", loginId) == 0) {
                    Log.e(TAG, getString(R.string.ecc_logining));
                }
            } else {
                Log.e(TAG, "setSIPServerAddress error");
            }
        } else {
            Log.e(TAG, "setHostAddress error");
        }
    }

    private void callECC() {
        MobileCC.getInstance().webChatCall(CODE_TEXT, "", "");
    }

    private void showExitDialog(String title) {
        if (isDismiss || isExit) return;
        isDismiss = true;
        LayoutInflater factory = LayoutInflater.from(MainActivityBak3.this);
        final View view = factory.inflate(R.layout.dialog_exit, null);
        Button bt_exit_second = (Button) view.findViewById(R.id.bt_exit_second);
        TextView tv_exit_second = (TextView) view.findViewById(R.id.tv_exit_second);
        if (title != null) tv_exit_second.setText(title);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivityBak3.this);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        bt_exit_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                MainActivityBak3.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCalled) MobileCC.getInstance().releaseCall();
        if (isTexted) MobileCC.getInstance().releaseWebChatCall();
        if (isLogin) MobileCC.getInstance().logout();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "onDestroy");
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 200);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BroadMsg broadMsg = (BroadMsg) intent
                    .getSerializableExtra(NotifyMessage.CC_MSG_CONTENT);
            if (NotifyMessage.AUTH_MSG_ON_LOGIN.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, getString(R.string.login_fail));
//                    Toast.makeText(MainActivity.this, getString(R.string.login_fail) + broadMsg.getRequestCode().getErrorCode(), Toast.LENGTH_SHORT).show();
                    showExitDialog(getString(R.string.login_fail));
                } else {
                    if (("0").equals(broadMsg.getRequestCode().getRetCode())) {
                        Log.e(TAG, getString(R.string.login_success));
//                        Toast.makeText(MainActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        isLogin = true;
                        callECC();
                    } else {
                        Log.e(TAG, getString(R.string.login_fail) + broadMsg.getRequestCode().getRetCode());
                        Log.e(TAG, getString(R.string.login_fail));
//                        Toast.makeText(MainActivity.this, getString(R.string.login_fail) + broadMsg.getRequestCode().getRetCode(), Toast.LENGTH_SHORT).show();
                        showExitDialog(getString(R.string.login_fail));
                    }
                }
            } else if (NotifyMessage.AUTH_MSG_ON_LOGOUT.equals(action)) {
            } else if (NotifyMessage.CALL_MSG_ON_QUEUE_INFO.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, NotifyMessage.CALL_MSG_ON_QUEUE_INFO + broadMsg.getRequestCode().getErrorCode());
                } else {
                    if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRetCode())) {
                        long position = broadMsg.getQueueInfo().getPosition();
                        Log.e(TAG, "queuing , position =" + position);
                    } else {
                        Log.e(TAG, NotifyMessage.CALL_MSG_ON_QUEUE_INFO + broadMsg.getRequestCode().getRetCode());
                    }
                }
            } else if (NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE.equals(action)) {
                Log.e(TAG, NotifyMessage.CALL_MSG_ON_CANCEL_QUEUE);
            } else if (NotifyMessage.CALL_MSG_ON_NET_QUALITY_LEVEL.equals(action)) {
            } else if (NotifyMessage.CALL_MSG_ON_CONNECT.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, getString(R.string.connect_fail) + broadMsg.getRequestCode().getErrorCode());
                } else {
                    if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRetCode())) {
                        if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getType())) {
                            Log.e(TAG, "webChatCall --->get callId success");
                        } else {
                            Log.e(TAG, "get audio ablity success");
                        }
                    } else {
                        Log.e(TAG, getString(R.string.connect_fail) + broadMsg.getRequestCode().getRetCode());
                        Log.e(TAG, getString(R.string.connect_fail));
//                        Toast.makeText(MainActivity.this, getString(R.string.connect_fail) + broadMsg.getRequestCode().getRetCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (NotifyMessage.CALL_MSG_ON_CONNECTED.equals(action)) {//与坐席连接成功
                Log.e(TAG, NotifyMessage.CALL_MSG_ON_CONNECTED);
                if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getRequestInfo().getMsg())) {
                    Log.e(TAG, getString(R.string.ecc_text_connect));
//                    Toast.makeText(MainActivity.this, getString(R.string.ecc_text_connect), Toast.LENGTH_SHORT).show();
                    MobileCC.getInstance().makeCall(CODE_VOICE, MobileCC.AUDIO_CALL, "", "");
                    isTexted = true;
                } else if (MobileCC.MESSAGE_TYPE_AUDIO.equals(broadMsg.getRequestInfo().getMsg())) {
                    Log.e(TAG, "MESSAGE_TYPE_AUDIO");
//                    if (MobileCC.getInstance().updateToVideo() != 0) {
//                        Log.e(TAG, getString(R.string.ecc_update_error));
//                        showExitDialog(getString(R.string.ecc_update_error));
//                    }
//                    isCalled = true;
                }
            } else if (NotifyMessage.CALL_MSG_ON_SUCCESS.equals(action)) {
                Log.e(TAG, NotifyMessage.CALL_MSG_ON_SUCCESS + new Date().toLocaleString());
                setAudioMode();
                if (MobileCC.getInstance().updateToVideo() != 0) {
                    Log.e(TAG, getString(R.string.ecc_update_error));
                    showExitDialog(getString(R.string.ecc_update_error));
                }
                isCalled = true;
            } else if (NotifyMessage.CALL_MSG_ON_DISCONNECTED.equals(action)) {
                if (MobileCC.MESSAGE_TYPE_TEXT.equals(broadMsg.getRequestInfo().getMsg())) {
                    Log.e(TAG, getString(R.string.text_disconnect));
                } else if (MobileCC.MESSAGE_TYPE_AUDIO.equals(broadMsg.getRequestInfo().getMsg())) {
                    Log.e(TAG, getString(R.string.audio_call_disconnect));
                }
            } else if (NotifyMessage.CALL_MSG_ON_APPLY_MEETING.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, "申请会议失败: " + broadMsg.getRequestCode().getErrorCode());
                } else {
                    if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRetCode())) {
                        Log.e(TAG, "申请会议成功");
                    } else {
                        Log.e(TAG, "申请会议失败: " + broadMsg.getRequestCode().getRetCode());
                    }
                }
            } else if (NotifyMessage.CALL_MSG_USER_STATUS.equals(action)) {
                Log.e(TAG, "MS: get conf info, into ConferenceActivity");
            } else if (NotifyMessage.CALL_MSG_ON_DROPCALL.equals(action)) {
                if (null == broadMsg.getRequestCode().getRetCode()) {
                    Log.e(TAG, "dropcall_fail: " + broadMsg.getRequestCode().getErrorCode());
                } else {
                    if (MobileCC.MESSAGE_OK.equals(broadMsg.getRequestCode().getRetCode())) {
                        Log.e(TAG, "drop call success");
                    } else {
                        Log.e(TAG, "dropcall_fail: " + broadMsg.getRequestCode().getRetCode());
                    }
                }
                showExitDialog(getString(R.string.ecc_service_end));
            } else if (NotifyMessage.CALL_MSG_ON_QUEUE_TIMEOUT.equals(action)) {
                Log.e(TAG, getString(R.string.queue_timeout));
//                Toast.makeText(MainActivity.this, getString(R.string.queue_timeout), Toast.LENGTH_SHORT).show();
                showExitDialog(getString(R.string.ecc_agent_busy));
            } else if (NotifyMessage.CALL_MSG_ON_FAIL.equals(action)) {
                Log.e(TAG, getString(R.string.call_fail_return));
//                Toast.makeText(MainActivity.this, getString(R.string.call_fail_return), Toast.LENGTH_SHORT).show();
                showExitDialog(getString(R.string.call_fail_return));
            } else if (NotifyMessage.CALL_MSG_ON_CALL_END.equals(action)) {
                Log.e(TAG, getString(R.string.call_end));
//                Toast.makeText(MainActivity.this, getString(R.string.call_end), Toast.LENGTH_SHORT).show();
                showExitDialog(getString(R.string.ecc_service_end));
            } else if (NotifyMessage.CALL_MSG_ON_POLL.equals(action)) {
                if ("-5".equals(broadMsg.getRequestCode().getErrorCode())) {
//                    Toast.makeText(MainActivity.this, getString(R.string.net_loop_fail), Toast.LENGTH_SHORT).show();
                    showExitDialog(getString(R.string.net_loop_fail));
                }
            } else if (NotifyMessage.CALL_MSG_USER_END.equals(action)) {
                Log.e(TAG, NotifyMessage.CALL_MSG_USER_END);
            } else if (NotifyMessage.CALL_MSG_USER_NETWORK_ERROR.equals(action)) {
                Log.e(TAG, getString(R.string.ecc_network_error));
//                Toast.makeText(MainActivity.this, R.string.ecc_network_error, Toast.LENGTH_SHORT).show();
            } else if (NotifyMessage.CALL_MSG_ON_STOP_MEETING.equals(action)) {
                Log.e(TAG, getString(R.string.exit_meeting));
//                Toast.makeText(MainActivity.this, R.string.exit_meeting, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            Class<?> clazz = Class.forName("android.support.v7.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.action_speaker:
//                    Toast.makeText(this, R.string.ecc_action_speaker, Toast.LENGTH_SHORT).show();
                    MobileCC.getInstance().changeAudioRoute(MobileCC.AUDIO_ROUTE_SPEAKER);
                    return true;
                case R.id.action_earphone:
//                    Toast.makeText(this, R.string.ecc_action_earphone, Toast.LENGTH_SHORT).show();
                    MobileCC.getInstance().changeAudioRoute(MobileCC.AUDIO_ROUTE_RECEIVER);
                    return true;
                case R.id.action_camera:
//                    Toast.makeText(this, R.string.switch_camera, Toast.LENGTH_SHORT).show();
                    MobileCC.getInstance().switchCamera();
                    return true;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobileCC.getInstance().videoOperate(MobileCC.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobileCC.getInstance().videoOperate(MobileCC.STOP);
    }

    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

    private void setAudioMode() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        boolean isHeadsetOn = audioManager.isWiredHeadsetOn();
        if (isHeadsetOn) {
            Log.e(TAG, "setAudioMode " + isHeadsetOn);
            MobileCC.getInstance().changeAudioRoute(MobileCC.AUDIO_ROUTE_RECEIVER);
        } else {
            Log.e(TAG, "setAudioMode " + isHeadsetOn);
        }
    }
}
