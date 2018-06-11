package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class FilterGallery extends AppCompatActivity {
    private List<Filter> filterList = new ArrayList<Filter>();
    private FilterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_gallery);
        Intent intent = getIntent();
        RadioButton hotestButton = findViewById(R.id.hotestButton);
        RadioButton historyButton = findViewById(R.id.historyButton);
        RadioButton favorityButton = findViewById(R.id.favorityButton);
        int choose = intent.getIntExtra("choose",0);
        switch (choose){
            case 0:initHotest();hotestButton.setChecked(true);break;
            case 1:initHistory();historyButton.setChecked(true);break;
            case 2:initFavority();favorityButton.setChecked(true);break;
        }
        adapter = new FilterAdapter(FilterGallery.this,R.layout.filter_item,filterList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);/**/
        final RadioGroup chooseGroup = findViewById(R.id.chooseGroup);
        chooseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View view = chooseGroup.findViewById(i);
                if(!view.isPressed()) return;
                switch(i){
                    case R.id.hotestButton:initHotest(); adapter.notifyDataSetChanged(); break;
                    case R.id.historyButton:initHistory();adapter.notifyDataSetChanged();break;
                    case R.id.favorityButton:initFavority();adapter.notifyDataSetChanged();break;
                }
            }
        });
    }

    private void initFavority(){
        filterList.clear();
        Filter light = new Filter("Light",R.drawable.icons8_filter);
        filterList.add(light);
        Filter dark = new Filter("Dark",R.drawable.icons8_filter);
        filterList.add(dark);
    }

    private void initHistory(){
        filterList.clear();
        Filter light = new Filter("New",R.drawable.icons8_filter);
        filterList.add(light);
        Filter dark = new Filter("Old",R.drawable.icons8_filter);
        filterList.add(dark);
    }

    private void initHotest(){
        filterList.clear();
        Filter light = new Filter("Hot",R.drawable.icons8_filter);
        filterList.add(light);
        Filter dark = new Filter("Cold",R.drawable.icons8_filter);
        filterList.add(dark);
    }
}
