package com.binarypheasant.freestyle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class profile_main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        ImageView filterView = (ImageView) findViewById(R.id.filter);
        filterView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent Gotofilterlist = new Intent(profile_main.this, filterlist.class);
                startActivity(Gotofilterlist);
            }
        });
    }
}
