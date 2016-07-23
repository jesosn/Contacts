package com.suspa.jjhu.contacts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ShowInfo extends AppCompatActivity {
    private TextView tv_name;
    private TextView tv_phone_num;
    private TextView tv_tel;
    private TextView tv_dep;
    private TextView tv_remark;
//    private TextView tv_simplename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        tv_name = (TextView) findViewById(R.id.textView_name_show);
        tv_phone_num = (TextView) findViewById(R.id.textView_num_show);
        tv_tel = (TextView) findViewById(R.id.textView_tel_show);
        tv_dep = (TextView) findViewById(R.id.textView_part_show);
        tv_remark = (TextView) findViewById(R.id.textView_remark_show);
//        tv_simplename = (TextView) findViewById(R.id.editText_simplename);
        Intent intent = getIntent();
        Info info = (Info) intent.getSerializableExtra("user");
        tv_name.setText(info.getName());
        tv_phone_num.setText(info.getPhone_num());
        tv_tel.setText(info.getTel());
        tv_dep.setText(info.getDepart());
        tv_remark.setText(info.getRemark());
//        tv_simplename.setText(info.getSimplename());
    }
}
