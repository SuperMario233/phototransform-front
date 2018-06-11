package com.binarypheasant.freestyle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FilterAdapter extends ArrayAdapter {
    private final int resourceId;

    public FilterAdapter(Context context, int textViewResourceId, List<Filter> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Filter fruit = (Filter) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView fruitImage = (ImageView) view.findViewById(R.id.filter_item);//获取该布局内的图片视图
        TextView fruitName = (TextView) view.findViewById(R.id.filter_name);//获取该布局内的文本视图
        fruitImage.setImageResource(fruit.getFilterImage());//为图片视图设置图片资源
        fruitName.setText(fruit.getFilterName());//为文本视图设置文本内容
        return view;
    }
}
