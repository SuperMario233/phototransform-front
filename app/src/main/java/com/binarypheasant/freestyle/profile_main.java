package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class profile_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        ImageView filterView = (ImageView) findViewById(R.id.filter);
        filterView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent Gotofilterlist = new Intent(profile_main.this, filterlist.class);
                Gotofilterlist.putExtra("choose",0);
                startActivity(Gotofilterlist);
            }
        });
        ImageView aboutView = (ImageView) findViewById(R.id.about);
        aboutView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(profile_main.this);
                builder.setTitle("关于我们");
                builder.setMessage("本软件由北京大学2018软件工程第六组成员制作");
                builder.setIcon(R.drawable.freestyle_icon);
                builder.show();
            }
        });
        ImageView loginView = (ImageView) findViewById(R.id.login);
        loginView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //退出当前用户申请
                Intent Gotolog_in = new Intent(profile_main.this, log_in.class);
                startActivity(Gotolog_in);
            }
        });
        ImageView logoutView = (ImageView) findViewById(R.id.logout);
        logoutView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //退出当前用户申请
                Intent Gotomain_photo = new Intent(profile_main.this, main_photo.class);
                startActivity(Gotomain_photo);
            }
        });
        TextView myfavorite = (TextView) findViewById(R.id.myfavorite);
        myfavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //退出当前用户申请
                Intent Gotofilterlist = new Intent(profile_main.this, filterlist.class);
                Gotofilterlist.putExtra("choose",2);
                startActivity(Gotofilterlist);
            }
        });
        TextView myhistory = (TextView) findViewById(R.id.myhistory);
        myhistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //退出当前用户申请
                Intent Gotofilterlist = new Intent(profile_main.this, filterlist.class);
                Gotofilterlist.putExtra("choose",1);
                startActivity(Gotofilterlist);
            }
        });
        TextView mypublish = (TextView) findViewById(R.id.mypublish);
        mypublish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(profile_main.this);
                builder.setTitle("我的发布");
                builder.setMessage("敬请期待");
                builder.setIcon(R.drawable.freestyle_icon);
                builder.show();
            }
        });
    }
}
