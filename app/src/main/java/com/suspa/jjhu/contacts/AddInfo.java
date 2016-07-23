package com.suspa.jjhu.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddInfo extends AppCompatActivity {
    private EditText editText_name;
    private EditText editText_phone_num;
    private EditText editText_remark;
    private EditText editText_tel;
    private EditText editText_part;
    private EditText editText_simeplename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
        setContentView(R.layout.activity_manage_info);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_phone_num = (EditText) findViewById(R.id.editText_phone_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);
        editText_tel = (EditText) findViewById(R.id.editText_tel);
        editText_part = (EditText) findViewById(R.id.editText_part);
        editText_simeplename = (EditText) findViewById(R.id.editText_simplename);
    }

    public void addOneTestData(View view) {
       final  Info p2 = new Info();
        p2.setName(editText_name.getText().toString());
        p2.setPhone_num(editText_phone_num.getText().toString());
        p2.setDepart(editText_part.getText().toString());
        p2.setRemark(editText_remark.getText().toString());
        p2.setTel(editText_tel.getText().toString());
        p2.setSimplename(editText_simeplename.getText().toString());
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddInfo.this);
                    builder.setTitle("系统消息");
                    builder.setMessage("创建"+p2.getName()+"成功");
                    builder.setCancelable(false);
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                    editText_name.setText("");
                    editText_phone_num.setText("");
                    editText_part.setText("");
                    editText_remark.setText("");
                    editText_tel.setText("");
                    editText_simeplename.setText("");

//                    toast("添加数据成功，返回objectId为："+objectId);
                }else{
//                    toast("创建数据失败：" + e.getMessage());
                }
            }
        });
    }

    public void queryAllDatas(View view) {
        Intent intent = new Intent(AddInfo.this,DeleteActivity.class);
        startActivity(intent);
    }
}
