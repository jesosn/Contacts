package com.suspa.jjhu.contacts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Register extends AppCompatActivity {
    private EditText editText_reg_username,editText_reg_passwd,editText_reg_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editText_reg_username = (EditText) findViewById(R.id.editText_reg_username);
        editText_reg_passwd = (EditText) findViewById(R.id.editText_reg_passwd);
        editText_reg_email = (EditText) findViewById(R.id.editText_reg_email);

        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");
    }


    public void buttonRegYesClick(View view) {
        final String username = editText_reg_username.getText().toString();
        final String password = editText_reg_passwd.getText().toString();
        final String email = editText_reg_email.getText().toString();

        BmobUser bu = new BmobUser();
        bu.setUsername(username);
        bu.setPassword(password);
        bu.setEmail(email);
        bu.signUp(new SaveListener<ManageUser>() {
            @Override
            public void done(ManageUser s, BmobException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                if (e == null) {
                    builder.setTitle("提示");
                    builder.setMessage("注册成功了，请到邮件中验证用户信息，登陆后即可成为管理成员");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    builder.setTitle("提示");
                    builder.setMessage("注册失败，请重试" + e.toString());
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //啥也不干
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    public void buttonCancelYesClick(View view) {
        finish();
    }
}
