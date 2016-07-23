package com.suspa.jjhu.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ChangePassword extends AppCompatActivity {
    private EditText useremail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");
        useremail = (EditText) findViewById(R.id.useremail);
    }

    public void resetPassword(View view) {
        final String email = useremail.getText().toString();
            BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setTitle("提示");
                        builder.setMessage("请到邮箱中完成重置密码");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
                }
            });
        }
}

