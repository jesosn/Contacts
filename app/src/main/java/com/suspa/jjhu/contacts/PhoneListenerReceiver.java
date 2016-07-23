package com.suspa.jjhu.contacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class PhoneListenerReceiver extends BroadcastReceiver {
    DbUtils db;
    public PhoneListenerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        db = DbUtils.create(context);
        //获取电话管理对象
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new MyPhoneStateListener(context),PhoneStateListener.LISTEN_CALL_STATE);
    }

    static WindowManager wm = null;
    private class MyPhoneStateListener extends PhoneStateListener{
        private Context context;
        TextView textView = null;

        public MyPhoneStateListener(Context context){
            this.context = context;
        }
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if(state == TelephonyManager.CALL_STATE_RINGING){
                wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                textView = new TextView(context);

                try {
                    Info info = db.findFirst(Selector.from(Info.class).where("phone_num","=",incomingNumber));
                    String call_name = info.getName();
                    System.out.println(call_name);
                    textView.setText("当前来电人为："+incomingNumber);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                 wm.addView(textView,params);
                //下面是挂机状态
            }else if(state==TelephonyManager.CALL_STATE_IDLE){
                if(wm!=null){
                    wm.removeViewImmediate(textView);
                    wm = null;
                }
            }
        }
    }
}
