package com.binarypheasant.freestyle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class profile_main extends AppCompatActivity {

    public String nickname,content,statusCode;
    public String historyString,favoriteString;
    public Bitmap Photo;
    public String userName;
    public boolean sex;
    static int historyNum,favoriteNum;
    static int[] favorite_item,history_item;
    private String hotString,filter_name,filter_description,filter_url;

    private int GetID(String str,int[] list){
        int count = 0,length = str.length(),i,num;

        for(i=0;i<length;++i){
            num = 0;
            while(i<length && str.charAt(i) !='\t'){
                num = num*10 + str.charAt(i)-'0';
                ++i;
            }
            if(num!=0) list[count++] = num;
        }
        return count;
    }

    private void GetUserInfo(){
        favorite_item = new int[200];
        history_item = new int[200];
        content = null;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29:8000/get-userinfo";
        JSONObject infoJSON = new JSONObject();
        try {
            infoJSON.put("sessionKey", log_in.sessionKey);
        } catch (JSONException e){
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, infoJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 需要判断返回码
                        //// parse the response
                        try{
                            statusCode = response.getString("statusCode");
                            nickname = response.getString("nickName");
                            content = response.getString("portrait");
                            sex = response.getString("sex") == "M";
                            historyString = response.getString("history");
                            favoriteString = response.getString("favorite");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        if (statusCode.equals("401")) return;//Toast.makeText(profile_main.this, "用户不存在", Toast.LENGTH_LONG).show();
                        else if(statusCode.equals("200")){
                            //Toast.makeText(profile_main.this, "用户信息获取成功", Toast.LENGTH_LONG).show();
                            TextView nicknameView = findViewById(R.id.nickname);
                            TextView myfavorite = findViewById(R.id.myfavorite);
                            TextView myhistory = findViewById(R.id.myhistory);
                            nicknameView.setText(nickname);//设置昵称
                            historyNum = GetID(historyString,history_item);
                            favoriteNum = GetID(favoriteString,favorite_item);
                            historyString = historyNum + "\n使用历史";
                            favoriteString = favoriteNum + "\n我的收藏";
                            myhistory.setText(historyString);//设置历史记录
                            myfavorite.setText(favoriteString);//设置我的收藏
                            /*
                            if(content != null) {//显示头像
                                byte[] bitmapArray;
                                bitmapArray= Base64.decode(content, Base64.DEFAULT);
                                Photo = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                                // display in the imageView
                                ImageView imageView = (ImageView) findViewById(R.id.usericon);
                                imageView.setImageBitmap(Photo);
                            }*/
                        }
                        else Toast.makeText(profile_main.this, "错误码："+statusCode, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(profile_main.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        GetUserInfo();//获取用户信息

        TextView myfavorite = findViewById(R.id.myfavorite);
        TextView myhistory = findViewById(R.id.myhistory);

        ImageView filterView = (ImageView) findViewById(R.id.filter);
        filterView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent Gotofilterlist = new Intent(profile_main.this, FilterGallery.class);
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
                logout();
                Intent Gotolog_in = new Intent(profile_main.this, log_in.class);
                startActivity(Gotolog_in);
            }
        });
        ImageView cameraView = (ImageView) findViewById(R.id.camera);
        cameraView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent Gotomain_photo = new Intent(profile_main.this, main_photo.class);
                startActivity(Gotomain_photo);
            }
        });
        ImageView logoutView = (ImageView) findViewById(R.id.logout);
        logoutView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logout();
                Intent Gotomain_photo = new Intent(profile_main.this, main_photo.class);
                startActivity(Gotomain_photo);
            }
        });

        myfavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent Gotofilterlist = new Intent(profile_main.this, FilterGallery.class);
                Gotofilterlist.putExtra("choose",2);
                startActivity(Gotofilterlist);
            }
        });

        myhistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent Gotofilterlist = new Intent(profile_main.this, FilterGallery.class);
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
                startActivity(GotoEdit);
            }
        });
    }
    private void logout(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29:8000/log-out";
        JSONObject sign_inJSON = new JSONObject();
        try {
            sign_inJSON.put("sessionKey", log_in.sessionKey);
        } catch (JSONException e){
            e.printStackTrace();
        }

        log_in.sessionKey = "";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, sign_inJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(profile_main.this, "退出登录", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(profile_main.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );
        queue.add(jsonRequest);
    }
}
