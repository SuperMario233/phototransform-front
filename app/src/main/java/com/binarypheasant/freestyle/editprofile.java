package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class editprofile extends AppCompatActivity {

    private String statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        boolean sex = intent.getBooleanExtra("sex",true);

        EditText usernameText = findViewById(R.id.username);
        usernameText.setText(nickname);
        RadioButton manRadio = findViewById(R.id.man);
        RadioButton womanRadio = findViewById(R.id.woman);
        if(sex) manRadio.setChecked(true);
        else womanRadio.setChecked(true);

        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nicknameText = findViewById(R.id.username);
                RadioGroup sexgroup = findViewById(R.id.sexGroup);
                String sex = sexgroup.getCheckedRadioButtonId() == 0 ? "M" : "F";
                String localNickname = nicknameText.getText().toString();
                SendToSever(localNickname,sex);
                Intent GotoProfile = new Intent(editprofile.this, profile_main.class);
                startActivity(GotoProfile);
            }
        });
    }

    public void SendToSever(String nickname,String sex){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29:8000/modify-user";
        //String renderURL = "http://47.92.69.29/render";
        JSONObject sign_inJSON = new JSONObject();
        try {
            sign_inJSON.put("nickName", nickname);
            sign_inJSON.put("sex", sex);
            sign_inJSON.put("portrait","");
            sign_inJSON.put("sessionKey",log_in.sessionKey);
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
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        //Toast.makeText(log_in.this, "statusCode:"+statusCode, Toast.LENGTH_LONG).show();
                        //SendRet = false;

                        if(statusCode.equals("401"))Toast.makeText(editprofile.this, "用户不存在", Toast.LENGTH_LONG).show();
                        else if (statusCode.equals("200")) Toast.makeText(editprofile.this, "修改成功", Toast.LENGTH_LONG).show();
                        else Toast.makeText(editprofile.this, "其他错误：返回值"+statusCode, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(editprofile.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

}
