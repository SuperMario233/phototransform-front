package com.binarypheasant.freestyle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

//import com.umeng.socialize.UMShareConfig;


public class prepublish extends AppCompatActivity {
    private ImageView back;
    private ImageView share;
    private ImageView mainImage;
    private ImageView edit;
    private ImageView save;
    private ImageView filterPub;
    boolean isSaved=false;
    private Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepublish);
        back=(ImageView)findViewById(R.id.back);
        share=(ImageView)findViewById(R.id.share);
        mainImage=(ImageView)findViewById(R.id.main_image);//ToDo
        edit=(ImageView)findViewById(R.id.edit);
        save=(ImageView)findViewById(R.id.save);
        filterPub=(ImageView)findViewById(R.id.filter_pub);
        //UMConfigure.init(this,"5a12384aa40fa3551f0001d1"
        //        ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("123","321");
                if(isSaved) {
                    prepublish.this.finish();
                }
                else {
                    AlertDialog.Builder querySave=new AlertDialog.Builder(context);
                    querySave.setMessage("您尚未保存图片，是否确认退出？");
                    querySave.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    querySave.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            prepublish.this.finish();
                        }
                    });
                    querySave.show();
                    //ToDo:弹窗提示未保存，询问是否退出
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {//ToDo
            @Override
            public void onClick(View view) {

            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo 还没有相关功能的activity，功能：跳转到编辑界面，传输数据：图片
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaved=true;
                AlertDialog.Builder savedInform=new AlertDialog.Builder(context);
                savedInform.setMessage("已保存！");
                savedInform.show();
            }
        });
        filterPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo 还没有相关功能的activity，功能：跳转到滤镜发布界面，传输数据：滤镜图片（所以应该把choose_filter界面的filter也接收到这个activity里然后再传下去？）
            }
        });
    }
}
