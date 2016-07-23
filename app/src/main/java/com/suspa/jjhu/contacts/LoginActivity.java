package com.suspa.jjhu.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private CheckBox checkbox;
    SharedPreferences sp;
    EditText editText_username,editText_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //写入保存信息
                    saveSharedPreference();
                }else
                {
                    deleteSharedPreference();
                    //删除保存信息
                }
            }
        });
        sp = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");

    }

    private void deleteSharedPreference() {
        SharedPreferences.Editor editor  = sp.edit();
        editor.clear();
        editor.commit();
    }

    private void saveSharedPreference() {
            String username = editText_username.getText().toString();
            String passwd = editText_password.getText().toString();
            if(TextUtils.isEmpty(username)|| TextUtils.isEmpty(passwd)){
                return;
            }
            //开始写入
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username",username);
            editor.putString("passwd",passwd);
            editor.putBoolean("flag", true);
            editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editText_username.setText(sp.getString("username", ""));
        editText_password.setText(sp.getString("passwd",""));
        checkbox.setChecked(sp.getBoolean("flag",false));
    }
    @Override
    protected void onPause() {
        super.onPause();

    }


    public void loginClick(View view) {
        String username = editText_username.getText().toString();
        String password = editText_password.getText().toString();
        BmobUser bu2 = new BmobUser();
        bu2.setUsername(username);
        bu2.setPassword(password);
        bu2.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    if (bmobUser.getEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, AddInfo.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage("请完成邮箱验证，方可管理用户");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    }
//                    toast("登录成功:");
                    //通过BmobUser user = BmobUser.getCurrentUser(context)获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(context,MyUser.class)获取自定义用户信息
                } else {
                    Toast.makeText(LoginActivity.this, "登陆失败失败" + e.toString(), Toast.LENGTH_LONG).show();
//                    loge(e);
                }
            }
        });
    }

    public void registerClick(View view) {
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }

    public void showChangePasswdActivity(View view) {
        String username = editText_username.getText().toString();
        Intent intent = new Intent(LoginActivity.this,ChangePassword.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }
}
