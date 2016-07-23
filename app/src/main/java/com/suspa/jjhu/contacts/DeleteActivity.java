package com.suspa.jjhu.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import cn.bmob.v3.listener.UpdateListener;

public class DeleteActivity extends AppCompatActivity{
    ArrayList<Info> infos = new ArrayList<>();
    MyDeleteAdapter myAdapter;
    PullToRefreshListView listView;
    private EditText editText_search;
    private DbUtils db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
        setContentView(R.layout.delete_activity);
        Bmob.initialize(this, "cf0b6e9741668da391f960b21b5b9ddd");
        listView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        editText_search = (EditText) findViewById(R.id.editText_search);
        db = DbUtils.create(this);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new LoadDataAsyncTask(DeleteActivity.this).execute();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                view.setFocusable(true);
//                view.setFocusableInTouchMode(true);
                View myview = view.findViewById(R.id.imageView_call);
                final Info info = infos.get(position - 1);
                PopupMenu popupMenu = new PopupMenu(DeleteActivity.this, myview);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.pop_menu_manage, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this);
                        switch (item.getItemId()) {
                            case R.id.user_modify:
                                try {
                                    final Info deleteInfo = db.findFirst(Selector.from(Info.class).where("myid", "=", info.getMyid()));
                                    String mydeleteid = deleteInfo.getMyid();
                                    info.setObjectId(mydeleteid);
                                    builder.setTitle("提示");
                                    builder.setMessage("确定要修改" + info.getName() + "的信息吗？");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //这里启动修改的activity，传递对象过去
                                            Intent intent = new Intent(DeleteActivity.this, ModifyInfo.class);
                                            intent.putExtra("modify", info);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                                break;
                            case R.id.user_delete:
                                builder.setTitle("提示");
                                builder.setMessage("确定要删除" + info.getName() + "的通讯录信息？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //获取对象的值 从本地查询
                                        try {
                                            Info deleteInfo = db.findFirst(Selector.from(Info.class).where("myid", "=", info.getMyid()));
                                            Info gameScore = new Info();
                                            gameScore.setObjectId(deleteInfo.getMyid());
                                            gameScore.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if (e == null) {
                                                        Toast.makeText(DeleteActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                                                        syncAllDataToLocal();
                                                    }
//                                                else {
//                                                    Toast.makeText(DeleteActivity.this,"删除失败，请重试"+e.toString(),Toast.LENGTH_LONG).show();
//                                                }
                                                }
                                            });
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
//
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
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

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = editText_search.getText().toString();
//                if(key!=null && !"".equals(key.trim())){
                if (TextUtils.isEmpty(key)) {
                    loadLocalAllData();
                } else if (key.equals("119")) {
                    finish();
                } else {
                    loadCompeleteMatchData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        try {
            if(db.tableIsExist(Info.class)){
                loadLocalAllData();
            }else {
                syncAllDataToLocal();
//                loadLocalAllData();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
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
            myAdapter = new MyDeleteAdapter(this,infos);
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
                            Toast.makeText(DeleteActivity.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                            loadLocalAllData();
                        }else {
                            //这里开始同步到本地
                            for(int i=0;i<infos.size();i++){
                                saveOneUserData(lists.get(i).getObjectId(),infos.get(i).getName(),
                                        infos.get(i).getPhone_num(),infos.get(i).getTel(),infos
                                                .get(i).getDepart(),infos.get(i).getRemark(),lists.get(i).getSimplename());
                                //所有数据均保存到本地
                                //提示保存到本地成功
                            }
                            Toast.makeText(DeleteActivity.this, "已成功同步云端最新数据", Toast.LENGTH_LONG).show();
                            loadLocalAllData();
                        }
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Toast.makeText(DeleteActivity.this, "同步数据失败，请检查网络，并尝试刷新", Toast.LENGTH_LONG).show();
                    loadLocalAllData();
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocalAllData();
        myAdapter = new MyDeleteAdapter(DeleteActivity.this,infos);
        listView.setAdapter(myAdapter);
    }

    public void loadLocalAllData(){
        try {
            List<Info> list = db.findAll(Info.class);//通过类型查找
            infos = (ArrayList<Info>) list;
            myAdapter = new MyDeleteAdapter(DeleteActivity.this,infos);
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
                    myAdapter = new MyDeleteAdapter(DeleteActivity.this, infos);
                    listView.setAdapter(myAdapter);
                } else {
                    Toast.makeText(DeleteActivity.this, "查询失败，请检查网络", Toast.LENGTH_LONG).show();
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {
        private DeleteActivity mainActivity;

        public LoadDataAsyncTask(DeleteActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(20);
                //这里开始同步数据到本地数据库中
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

