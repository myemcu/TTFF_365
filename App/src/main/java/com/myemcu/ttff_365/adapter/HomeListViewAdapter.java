package com.myemcu.ttff_365.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.javabean.HomeDataResult;

import java.util.List;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<HomeDataResult.DataBean.NewsListBean> datas;

    public HomeListViewAdapter(Context context, List<HomeDataResult.DataBean.NewsListBean> datas) {
        this.context=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        private TextView newsText,newsDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        // 加载视图(item)
        if (convertView==null) {
            convertView=View.inflate(context, R.layout.item_list_view, null);
            viewHolder.newsText = (TextView) convertView.findViewById(R.id.news_text);
            viewHolder.newsDate = (TextView) convertView.findViewById(R.id.news_date);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        // 设置数据(item)
        viewHolder.newsText.setText(datas.get(position).getTitle());        // 对应位置标题
        viewHolder.newsDate.setText(datas.get(position).getCreate_time());  // 对应位置数据

        return convertView;

    }
}
