package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;

public class editprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        boolean sex = intent.getBooleanExtra("sex",true);
        String des = intent.getStringExtra("des");

        EditText usernameText = findViewById(R.id.username);
        usernameText.setText(nickname);
        EditText desText = findViewById(R.id.userdes);
        desText.setText(des);
        RadioButton manRadio = findViewById(R.id.man);
        RadioButton womanRadio = findViewById(R.id.woman);
        if(sex) manRadio.setChecked(true);
        else womanRadio.setChecked(true);
    }
}
