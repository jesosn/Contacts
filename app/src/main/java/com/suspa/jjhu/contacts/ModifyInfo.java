package com.suspa.jjhu.contacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyInfo extends AppCompatActivity {
    private EditText editText_name;
    private EditText editText_phone_num;
    private EditText editText_remark;
    private EditText editText_tel;
    private EditText editText_part;
    private EditText editText_simplename;
    private MyAdapter myAdapter;
    Info info;
    private DbUtils db;
    private ArrayList<Info> infos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_infos);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");
        db = DbUtils.create(this);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_phone_num = (EditText) findViewById(R.id.editText_phone_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);
        editText_tel = (EditText) findViewById(R.id.editText_tel);
        editText_part = (EditText) findViewById(R.id.editText_part);
        editText_simplename = (EditText) findViewById(R.id.editText_simplename);
        Intent intent = getIntent();
        info = (Info) intent.getSerializableExtra("modify");
        editText_name.setText(info.getName());
        editText_phone_num.setText(info.getPhone_num());
        editText_remark.setText(info.getRemark());
        editText_part.setText(info.getDepart());
        editText_tel.setText(info.getTel());
        editText_simplename.setText(info.getSimplename());
    }

    public void modifyYesClick(View view) {
        Info gameScore = new Info();
        gameScore.setName(editText_name.getText().toString());
        gameScore.setTel(editText_tel.getText().toString());
        gameScore.setPhone_num(editText_phone_num.getText().toString());
        gameScore.setRemark(editText_remark.getText().toString());
        gameScore.setDepart(editText_part.getText().toString());
        gameScore.setSimplename(editText_simplename.getText().toString());
        String objectIdUpdate = info.getMyid();
        gameScore.update(objectIdUpdate, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
//                    Log.i("bmob","更新成功");
                    Toast.makeText(ModifyInfo.this, "修改成功", Toast.LENGTH_SHORT).show();
                    syncAllDataToLocal();
//                    finish();
                    //触发同步数据的方法
                } else {
//                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                    Toast.makeText(ModifyInfo.this, "修改失败，请重试" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveOneUserData(String objectId,String name,String phone_num,String tel,String dep,String remark,String simplename){
        DbUtils db = DbUtils.create(this);
        Info info = new Info(); //这里需要注意的是User对象必须有id属性，或者有通过@ID注解的属性
        info.setMyid(objectId);
        info.setName(name);
        info.setPhone_num(phone_num);
        info.setTel(tel);
        info.setDepart(dep);
        info.setRemark(remark);
        info.setSimplename(simplename);
        try {
            db.save(info); // 使用saveBindingId保存实体时会为实体的id赋值
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void syncAllDataToLocal(){
        BmobQuery<Info> query = new BmobQuery<Info>();
        query.setLimit(500);
        query.findObjects(new FindListener<Info>() {
            @Override
            public void done(List<Info> lists, BmobException e) {
                if (e == null) {
                    //先从云端获取所有数据
                    infos = (ArrayList<Info>) lists;
                    try {
                        //先把本地的数据全部删除
                        //判断本地有没有数据库，有的话先删除，再赋值，没有的话则直接拉数据
                        if (db.tableIsExist(Info.class)) {
                            db.deleteAll(Info.class);
                            for (int i = 0; i < infos.size(); i++) {
                                saveOneUserData(lists.get(i).getObjectId(), infos.get(i).getName(),
                                        infos.get(i).getPhone_num(), infos.get(i).getTel(), infos
                                                .get(i).getDepart(), infos.get(i).getRemark(),infos.get(i).getSimplename());
                                //所有数据均保存到本地
                                //提示保存到本地成功
                            }
                            Toast.makeText(ModifyInfo.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                        } else {
                            //这里开始同步到本地
                            for (int i = 0; i < infos.size(); i++) {
                                saveOneUserData(lists.get(i).getObjectId(), infos.get(i).getName(),
                                        infos.get(i).getPhone_num(), infos.get(i).getTel(), infos
                                                .get(i).getDepart(), infos.get(i).getRemark(),infos.get(i).getSimplename());
                                //所有数据均保存到本地
                                //提示保存到本地成功
                            }
                            Toast.makeText(ModifyInfo.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Toast.makeText(ModifyInfo.this, "同步数据失败，请检查网络，并尝试刷新", Toast.LENGTH_LONG).show();
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void exitClick(View view) {
        finish();
    }
}
