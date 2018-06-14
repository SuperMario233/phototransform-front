package com.binarypheasant.freestyle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;

import java.io.File;

import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;


public class prepublish extends AppCompatActivity {
    private ImageView back;
    private ImageView share;
    private ImageView mainImage;
    private ImageView edit;
    private ImageView save;
    private ImageView filterPub;
    boolean isSaved=false;
    private Context context=this;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepublish_new);
        back=(ImageView)findViewById(R.id.back);
        share=(ImageView)findViewById(R.id.share);
        mainImage=(ImageView)findViewById(R.id.main_image);//ToDo
        edit=(ImageView)findViewById(R.id.edit);
        save=(ImageView)findViewById(R.id.save);
        filterPub=(ImageView)findViewById(R.id.filter_pub);
        UMConfigure.init(this,"5b08fb298f4a9d6c18000253"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin("wxdc1e388c3822c80b", "3baf1193c85774b3fd9d18447d76cab0");
        PlatformConfig.setSinaWeibo("975220766", "3db78fd6875be7770fba3237208762e3","https://api.weibo.com/oauth2/default.html");
        PlatformConfig.setQQZone("1106748887", "a8KJgGiMruNMgi7x");
        //52a3b473aeb9e9d42fc42070d30bb8c6签名
        final Bitmap tempphoto = BitmapFactory.decodeResource(getResources(), R.drawable.selfie_maniac);
        Intent getImageInt=getIntent();
        imagePath=getImageInt.getStringExtra("imagePath");
        //mainImage.setImageBitmap(tempphoto);
        if(imagePath!=null) {
            Bitmap imageGet = BitmapFactory.decodeFile(imagePath);
            mainImage.setImageBitmap(imageGet);
        }
        else{
            Toast.makeText(prepublish.this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
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
                            File file=new File(imagePath);
                            file.delete();
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
                if(imagePath!=null) {
                    File imageFile = new File(imagePath);
                    UMImage image = new UMImage(prepublish.this, imageFile);
                    UMImage tempimage = new UMImage(prepublish.this, tempphoto);
                    Log.v("", "startshare");
                    new ShareAction(prepublish.this).withText("FreeStyle").withMedia(tempimage).setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener).open();
                }
                else {
                    Toast.makeText(prepublish.this,"分享失败",Toast.LENGTH_SHORT);
                }
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
    private UMShareListener umShareListener=new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(prepublish.this,"分享成功！",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
