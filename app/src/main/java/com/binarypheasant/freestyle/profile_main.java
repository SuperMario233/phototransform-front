package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class profile_main extends AppCompatActivity {

    public String nickname;
    public String des,historyString,favoriteString;
    public boolean sex;
    public int historyNum,favoriteNum;

    private void GetUserInfo(){
        nickname = "BinaryPheasant";
        des = "Hello World!";
        sex = true;
        historyNum = 2;
        favoriteNum = 5;
        historyString = historyNum + "\n使用历史";
        favoriteString = favoriteNum + "\n我的收藏";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        GetUserInfo();

        TextView nicknameView = findViewById(R.id.nickname);
        TextView myfavorite = findViewById(R.id.myfavorite);
        TextView myhistory = findViewById(R.id.myhistory);
        nicknameView.setText(nickname);
        myhistory.setText(historyString);
        myfavorite.setText(favoriteString);

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

        myfavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //退出当前用户申请
                Intent Gotofilterlist = new Intent(profile_main.this, filterlist.class);
                Gotofilterlist.putExtra("choose",2);
                startActivity(Gotofilterlist);
            }
        });

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
        TextView editview = (TextView) findViewById(R.id.edit);
        editview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent GotoEdit = new Intent(profile_main.this, editprofile.class);
                GotoEdit.putExtra("nickname",nickname);
                GotoEdit.putExtra("sex",sex);
                GotoEdit.putExtra("des",des);
                startActivity(GotoEdit);
            }
        });
    }
}
