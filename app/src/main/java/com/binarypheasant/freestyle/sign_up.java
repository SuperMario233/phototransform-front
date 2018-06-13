package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class sign_up extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


    }

    public void login(View view) {
        // 调到登录页面
        Intent loginIntent = new Intent(sign_up.this, log_in.class);
        startActivity(loginIntent);
    }

    public void createUser(View view) {
        // Get the user info stored in JSONObject
        JSONObject userInfo = this.getUserJson();

        if (userInfo!=null) {

            // The agreement checkbox should be checked
            final CheckBox agreement = (CheckBox) findViewById(R.id.agreementCheckbox);
            if (!agreement.isChecked()) {
                Toast.makeText(sign_up.this, "请先同意用户协议及隐私条款", Toast.LENGTH_LONG).show();
                return;
            }

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://47.92.69.29/sign-up";

            // Request a string response from the provided URL.
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, userInfo,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // 需要判断返回码

                            //// parse the response
                            Toast.makeText(sign_up.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(sign_up.this, "网络错误", Toast.LENGTH_LONG).show();
                }
            }
            );

            // Add the request to the RequestQueue.
            queue.add(jsonRequest);
        }
        else {
            return;
        }
    }

    private JSONObject getUserJson() {
        // Get the input
        final EditText email = (EditText) findViewById(R.id.getEmail);
        final EditText password = (EditText) findViewById(R.id.getPassword);
        final EditText confirmPassword = (EditText) findViewById(R.id.repeatPassword);
        final EditText phoneNumber = (EditText) findViewById(R.id.getPhoneNumber);
//        final Spinner phoneRegion = (Spinner) findViewById(R.id.spinner_phoneRegion);
//        final EditText veriCode = (EditText) findViewById(R.id.getVeriCode);

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();
        String phoneNumberStr = phoneNumber.getText().toString();
//        String veriCodeStr = veriCode.getText().toString();

        // Check the inputs
        if (!isEmail(emailStr)) {
            email.setError("请输入有效邮箱地址！");
            return null;
        }
        if (isEmpty(passwordStr)) {
            password.setError("密码不能为空！");
            return null;
        }
        if (!passwordStr.equals(confirmPasswordStr)) {
            confirmPassword.setError("两次输入密码不一致。");
            return null;
        }
//        if (isEmpty(veriCodeStr)) {
//            veriCode.setError("验证码不能为空！");
//            return null;
//        }

        // Construct the json obkect
        JSONObject userInfo = new JSONObject();
        try {
//            userInfo.put("account", emailStr);
//            userInfo.put("password", Encrypt.encrypt(passwordStr)); // password encrypted
//            userInfo.put("mobile", phoneNumberStr);
//            userInfo.put("authCode", veriCodeStr);
            userInfo.put("userName", emailStr);
            userInfo.put("pwd", Encrypt.encrypt(passwordStr));
            userInfo.put("nickName", "null");
            userInfo.put("sex", "male");
            userInfo.put("mobile", phoneNumberStr);
            userInfo.put("birthday", "null");
            userInfo.put("portrait", "null");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userInfo;
    }

    // Check user's input
    private boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    private boolean isEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean isPhoneNumber(String phoneNumber) {
        /*
            "[1]"代表第1位为数字1
            "[3578]"代表第二位可以为3、5、8中的一个
            "\\d{9}"代表后面是可以是0～9的数字，有9位。
        */
        String phoneRegex = "[1][3578]\\d{9}";
        if (TextUtils.isEmpty(phoneNumber))
            return false;
        else
            return phoneNumber.matches(phoneRegex);
    }

    // 获取验证码，已经放弃
    //    public void getAuthCode(View view) {
//        // First check the phone number
//        EditText phoneNumber = (EditText) findViewById(R.id.getPhoneNumber);
//        Spinner phoneRegion = (Spinner) findViewById(R.id.spinner_phoneRegion);
//        if (isPhoneNumber(phoneNumber.getText().toString())) {
//            // Instantiate the RequestQueue.
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String url = "http://47.92.69.29/authCode";
//
//            // Construct the json object
//            String phoneNumberStr = phoneRegion.getSelectedItem().toString() + phoneNumber.getText().toString();
//            JSONObject phoneJSON = new JSONObject();
//            try {
//                phoneJSON.put("mobile", phoneNumberStr);
//            } catch (JSONException e){
//                e.printStackTrace();
//            }
//
//            // Request a string response from the provided URL.
//            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, phoneJSON,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // 需要判断返回码
//                            //// parse the response
//                            //ret = response.getString();
//                            Toast.makeText(sign_up.this, "验证码已发送", Toast.LENGTH_LONG).show();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(sign_up.this, "网络错误", Toast.LENGTH_LONG).show();
//                    error.printStackTrace();
//                }
//            }
//            );
//
//            // Add the request to the RequestQueue.
//            queue.add(jsonRequest);
//            //return ret;
//        }
//        else {
//            phoneNumber.setError("请输入合法的手机号。");
//        }
//
//    }

}
