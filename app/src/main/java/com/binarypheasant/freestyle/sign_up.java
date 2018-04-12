package com.binarypheasant.freestyle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class sign_up extends AppCompatActivity {
    private String phone_region = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // set up spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner_phoneRegion);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] phone_regions = getResources().getStringArray(R.array.phone_region);
                phone_region = phone_regions[pos];
                Toast.makeText(sign_up.this, "你选择的地区为"+ phone_region, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }
}
