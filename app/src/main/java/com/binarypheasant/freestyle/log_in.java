package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;


public class log_in extends AppCompatActivity {

    // UI references.
    private Tencent mTencent;
    public String sessionKey,statusCode;
    private boolean SendRet;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Tencent.onActivityResultData(requestCode, resultCode, data, new log_in.BaseUiListener());

        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new log_in.BaseUiListener());
            }
        }

    }

    private class BaseUiListener implements IUiListener {
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            String openidString = null;
            Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            /*
             * 下面隐藏的是用户登录成功后 登录用户数据的获取的方法
             * 共分为两种  一种是简单的信息的获取,另一种是通过UserInfo类获取用户较为详细的信息
             *有需要看看
             * */
           try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                openidString = ((JSONObject) response).getString("openid");
                } catch (JSONException e) {
                e.printStackTrace();
            }
           boolean result = SendToSever(openidString,"");
           if (true){
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                Intent GotoProfile = new Intent(log_in.this, profile_main.class);
                startActivity(GotoProfile);
           }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //创建QQ登录实例
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance("1106748997", this.getApplicationContext());

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GotoProfile = new Intent(log_in.this, profile_main.class);
                EditText user_emailText = findViewById(R.id.emailText);
                EditText user_passwordText = findViewById(R.id.passwordText);
                String user_email = user_emailText.getText().toString();
                String user_password = user_passwordText.getText().toString();
                boolean result = SendToSever(user_email,user_password);
                //send email and password to somewhere
                if (result) startActivity(GotoProfile);
            }
        });
        Button logupButton = (Button) findViewById(R.id.logupButton);
        logupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GotoSignUp = new Intent(log_in.this, sign_up.class);
                startActivity(GotoSignUp);
            }
        });

        ImageView qqView = (ImageView) findViewById(R.id.qqView);
        qqView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mTencent.login(log_in.this,"all",new log_in.BaseUiListener());
            }
        });
    }

    public boolean SendToSever(String email,String password){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29/sign-in";
        JSONObject sign_inJSON = new JSONObject();
        try {
            sign_inJSON.put("userName", email);
            sign_inJSON.put("pwd", password);
        } catch (JSONException e){
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, sign_inJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 需要判断返回码
                        //// parse the response
                        try{
                            statusCode = response.getString("statusCode");
                            sessionKey = response.getString("sessionKey");
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        if(statusCode.equals("401")){
                            Toast.makeText(log_in.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                            SendRet = false;
                        }
                        else SendRet = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(log_in.this, "网络错误", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                SendRet = false;
            }
        }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
        return SendRet;
    }

}
