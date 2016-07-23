package com.suspa.jjhu.contacts;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    public Context ctx;
    public ArrayList<Info> infos;

    public MyAdapter(Context ctx, ArrayList<Info> infos) {
        this.ctx = ctx;
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int[] images = {R.mipmap.bohao, R.mipmap.linkman};
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.info_item_job, null);
            vh = new ViewHolder();
            vh.iv_icon = (ImageView) convertView.findViewById(R.id.imageView_icon);
            vh.tv_name = (TextView) convertView.findViewById(R.id.textView_name);
            vh.tv_phone_num = (TextView) convertView.findViewById(R.id.textView_phone_num);
            vh.tv_tel = (TextView) convertView.findViewById(R.id.textView_tel);
            vh.iv_call = (ImageView) convertView.findViewById(R.id.imageView_call);
            vh.tv_job = (TextView) convertView.findViewById(R.id.textView_job);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        Info info = infos.get(position);
        vh.iv_icon.setImageResource(images[1]);
        vh.tv_name.setText(info.getName());
        vh.tv_phone_num.setText(info.getPhone_num());
        vh.tv_tel.setText(info.getTel());
        vh.iv_call.setImageResource(images[0]);
        vh.tv_job.setText(info.getRemark());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_phone_num;
        TextView tv_tel;
        ImageView iv_call;
        TextView tv_job;
    }
}