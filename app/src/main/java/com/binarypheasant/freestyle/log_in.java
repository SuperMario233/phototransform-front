package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


public class log_in extends AppCompatActivity {

    // UI references.
    private Tencent mTencent;

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
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            /*
             * 下面隐藏的是用户登录成功后 登录用户数据的获取的方法
             * 共分为两种  一种是简单的信息的获取,另一种是通过UserInfo类获取用户较为详细的信息
             *有需要看看
             * */
          /*  try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------"+response.toString());
                openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);

                mTencent.setAccessToken(((JSONObject) response).getString("access_token"),((JSONObject) response).getString("expires_in"));


                Log.v("TAG", "-------------"+openidString);
                //access_token= ((JSONObject) response).getString("access_token");              //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
          /*

            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);

            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了

                    try {

                        Toast.makeText(getApplicationContext(), ((JSONObject) o).getString("nickname")+((JSONObject) o).getString("gender"), Toast.LENGTH_SHORT).show();
                        Log.v("UserInfo",o.toString());
                        Intent intent1 = new Intent(log_in.this,MainActivity.class);
                        startActivity(intent1);
                        finish();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo","onError");
                }

                @Override
                public void onCancel() {
                    Log.v("UserInfo","onCancel");
                }
            });*/
                Intent GotoProfile = new Intent(log_in.this, profile.class);
                //add info
                startActivity(GotoProfile);
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
                Intent GotoProfile = new Intent(log_in.this, profile.class);
                EditText user_emailText = findViewById(R.id.emailText);
                EditText user_passwordText = findViewById(R.id.passwordText);
                String user_email = user_emailText.getText().toString();
                String user_password = user_passwordText.getText().toString();
                boolean result = SendToSever(user_email,user_password);
                //send email and password to somewhere
                if (result){
                    GotoProfile.putExtra("UserMessage",user_email);
                    startActivity(GotoProfile);
                }
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
        Button QQButton = (Button) findViewById(R.id.qqloginButton);
        QQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTencent.login(log_in.this,"all",new log_in.BaseUiListener());
            }
        });
    }

    public boolean SendToSever(String email,String password){
        return true;
    }

}
