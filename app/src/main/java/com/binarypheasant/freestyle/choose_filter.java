package com.binarypheasant.freestyle;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class choose_filter extends AppCompatActivity {

    private LinearLayout filterLayout;
    private int[] filters;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_filter);

        layoutInflater = LayoutInflater.from(this);
        initData();
        initView();
    }

    private void initData() {
        filters = new int[] { R.drawable.ic_alarm_on_black_48dp, R.drawable.ic_check_circle_black_48dp,
                R.drawable.ic_done_all_black_48dp, R.drawable.ic_done_black_48dp};
    }

    private void initView() {
        filterLayout = (LinearLayout) findViewById(R.id.linearScroll);

        for (int i=0; i<filters.length; i++) {
            View view = layoutInflater.inflate(R.layout.scroll_filter_item, filterLayout, false);

            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.filter_item);
            imageButton.setImageResource(filters[i]); // 设置图片
            imageButton.setBackgroundColor(0); // 透明背景
            imageButton.setTag(filters[i]); // 设置标签，后面可以用来知道本imageButton对应哪张图片
            // 点击样式和事件
            //final Drawable drawable = getResources().getDrawable(R.drawable.ic_done_white_48dp);
            //drawable.setAlpha(128);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //imageButton.setForeground(drawable);
                    Integer resource = (Integer)imageButton.getTag();
                    ImageView imageView = (ImageView) findViewById(R.id.photoShow);
                    imageView.setImageResource(resource);
                }
            });

            filterLayout.addView(view);
        }

    }

    public void onClickMoreFilters(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"更多滤镜\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickEdit(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"编辑\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickDIYFilter(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"自定义滤镜\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickCrop(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"选区\"", Toast.LENGTH_SHORT).show();
    }

}
