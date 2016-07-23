package com.suspa.jjhu.contacts;

import cn.bmob.v3.BmobObject;

/**
 * Created by jjhu on 2016/6/29.
 */
public class Info extends BmobObject{
    int id;
    String myid;
    String name;
    String phone_num;
    String tel;
    String depart;
    String remark;
    String simplename;

    public String getSimplename() {
        return simplename;
    }

    public void setSimplename(String simplename) {
        this.simplename = simplename;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", tel='" + tel + '\'' +
                ", depart='" + depart + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
