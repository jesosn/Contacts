package com.suspa.jjhu.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity{
    ArrayList<Info> infos = new ArrayList<>();
    MyAdapter myAdapter;
    PullToRefreshListView listView;
    private EditText editText_search;
    private DbUtils db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddde");
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        editText_search = (EditText) findViewById(R.id.editText_search);
        db = DbUtils.create(this);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new LoadDataAsyncTask(MainActivity.this).execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            PopupMenu popupMenu;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                view.setFocusable(true);
//                view.setFocusableInTouchMode(true);
                View myview = view.findViewById(R.id.imageView_call);
                final Info info = infos.get(position - 1);
                 popupMenu = new PopupMenu(MainActivity.this,myview);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.pop_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        switch (item.getItemId()){
                            case R.id.call_someone:
                                builder.setTitle("提示");
                                builder.setMessage("确定要给" + info.getName() + "拨打电话？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        callPhoneNumber(info.getPhone_num());
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            break;
                            case R.id.send_sms:
                                builder.setTitle("提示");
                                builder.setMessage("确定要给" + info.getName() + "发送短信？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendSms(info.getPhone_num());
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                                break;
                            case R.id.view_detail:
                                Intent intent = new Intent(MainActivity.this,ShowInfo.class);
                                intent.putExtra("user",info);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


//        try {
//            db.createTableIfNotExist(Info.class);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key =  editText_search.getText().toString();
//                if(key!=null && !"".equals(key.trim())){
                if(TextUtils.isEmpty(key)){
                    loadLocalAllData();
                }
                else if(key.equals("管理")){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else if(key.equals("退出")){
                    finish();
                }else{
                    loadCompeleteMatchData();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        try {
            if(db.tableIsExist(Info.class)){
                loadLocalAllData();
            }else {
                syncAllDataToLocal();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("初次打开请等待5秒左右，待数据同步完成后，点击确定。再次进入即可使用");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadLocalAllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void callPhoneNumber(String phone_num) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        String number = phone_num.trim();
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }
    public void sendSms(String phone_num){
            Uri smsToUri = Uri.parse("smsto:" + phone_num);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", "");
            startActivity(intent);
    }

    public void saveOneUserData(String objectId,String name,String phone_num,String tel,String dep,String remark,String simple_name){
        DbUtils db = DbUtils.create(this);
        Info info = new Info(); //这里需要注意的是User对象必须有id属性，或者有通过@ID注解的属性
        info.setMyid(objectId);
        info.setName(name);
        info.setPhone_num(phone_num);
        info.setTel(tel);
        info.setDepart(dep);
        info.setRemark(remark);
        info.setSimplename(simple_name);
        try {
            db.save(info); // 使用saveBindingId保存实体时会为实体的id赋值
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void callPhoneNumber() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        String number = editText_search.getText().toString();
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }



    public void loadCompeleteMatchData() {
        String searchInfo = editText_search.getText().toString().trim();
        try {
            List<Info> list1 =db.findAll(Selector.from(Info.class).where("name","like","%" + searchInfo + "%"));
            List<Info> list2 =db.findAll(Selector.from(Info.class).where("simplename","like","%" + searchInfo + "%"));
            ArrayList<Info> mySearchInfos = null;
            for(int i=0;i<list1.size();i++){
                list2.add(list1.get(i));
            }
            infos = (ArrayList<Info>) list2;
            myAdapter = new MyAdapter(this,infos);
            listView.setAdapter(myAdapter);
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
                        if(db.tableIsExist(Info.class)){
                            db.deleteAll(Info.class);
                            for(int i=0;i<infos.size();i++){
                                saveOneUserData(lists.get(i).getObjectId(),infos.get(i).getName(),
                                        infos.get(i).getPhone_num(),infos.get(i).getTel(),infos
                                                .get(i).getDepart(),infos.get(i).getRemark(),lists.get(i).getSimplename());
                                //所有数据均保存到本地
                                //提示保存到本地成功
                            }
                            Toast.makeText(MainActivity.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                        }else {
                            //这里开始同步到本地
                            for(int i=0;i<infos.size();i++){
                                saveOneUserData(infos.get(i).getObjectId(),infos.get(i).getName(),
                                        infos.get(i).getPhone_num(),infos.get(i).getTel(),infos
                                                .get(i).getDepart(),infos.get(i).getRemark(),lists.get(i).getSimplename());
                                //所有数据均保存到本地
                                //提示保存到本地成功
                            }
                            Toast.makeText(MainActivity.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                    myAdapter = new MyAdapter(MainActivity.this, infos);
                    listView.setAdapter(myAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "同步数据失败，请检查网络，并尝试刷新", Toast.LENGTH_LONG).show();
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void loadLocalAllData(){
        try {
            List<Info> list = db.findAll(Info.class);//通过类型查找
            infos = (ArrayList<Info>) list;
            myAdapter = new MyAdapter(MainActivity.this,infos);
            listView.setAdapter(myAdapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void loadAllData() {
        BmobQuery<Info> query = new BmobQuery<Info>();
        query.setLimit(500);
//执行查询方法
        query.findObjects(new FindListener<Info>() {
            @Override
            public void done(List<Info> lists, BmobException e) {
                if (e == null) {
                    infos = (ArrayList<Info>) lists;
                    myAdapter = new MyAdapter(MainActivity.this, infos);
                    listView.setAdapter(myAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "查询失败，请检查网络", Toast.LENGTH_LONG).show();
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.exit_app:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("提示");
                builder2.setMessage("真的要退出吗？");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder2.setCancelable(false);
                builder2.show();
                break;
            case R.id.shengming_app:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("作者声明");
                builder1.setMessage("1.本APP无毒无害，不会窃取手机里的任何个人信息，倘若觉得有安全隐患，可不必安装。\n" +
                        "\n" +
                        "2.此APP仅供SUSPA员工使用，如遇员工离职或泄密该APP，作者有权停止其使用。"+"\n"+"\n"+
                        "作者：Jesson Hu");
                builder1.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder1.setCancelable(false);
                builder1.show();

                break;
            case R.id.about_app:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("关于");
                builder.setMessage("技术支持 By Jesson Hu" + "\n" + "Email:jjhu@cn.suspa.com" + "\n" + "\n" + "苏世博（南京）减振系统有限公司");
                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(false);
                builder.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {
        private MainActivity mainActivity;

        public LoadDataAsyncTask(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                mainActivity.syncAllDataToLocal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "success";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if("success".equals(s)){
                mainActivity.myAdapter.notifyDataSetChanged();
                mainActivity.listView.onRefreshComplete();
            }
        }
    }
}

