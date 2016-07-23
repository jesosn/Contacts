package com.suspa.jjhu.contacts;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class ManageUser extends BmobUser{
    BmobFile bmobFile;

    public BmobFile getBmobFile() {
        return bmobFile;
    }

    public void setBmobFile(BmobFile bmobFile) {
        this.bmobFile = bmobFile;
    }
}
