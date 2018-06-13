package com.binarypheasant.freestyle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.List;

public class FilterGallery extends AppCompatActivity {
    private List<Filter> filterList = new ArrayList<Filter>();
    private FilterAdapter adapter;
    private String hotString,statusCode;
    private int[] hotest_item;
    private int hotNum;
    private boolean needResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_gallery);
        Intent intent = getIntent();
        hotest_item = new int[200];
        RadioButton hotestButton = findViewById(R.id.hotestButton);
        RadioButton historyButton = findViewById(R.id.historyButton);
        RadioButton favorityButton = findViewById(R.id.favorityButton);
        int choose = intent.getIntExtra("choose",0);
        needResult = intent.getBooleanExtra("needResult",false);
        adapter = new FilterAdapter(FilterGallery.this,R.layout.filter_item,filterList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);/**/
        switch (choose){
            case 0:initHotest();hotestButton.setChecked(true);break;
            case 1:initHistory();historyButton.setChecked(true);break;
            case 2:initFavorite();favorityButton.setChecked(true);break;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Filter filter = (Filter) adapter.getItem(position);
                LayoutInflater inflater= LayoutInflater.from(FilterGallery.this);
                View myview = inflater.inflate(R.layout.dialog_filter, null);
                AlertDialog.Builder builder= new AlertDialog.Builder(FilterGallery.this);
                builder.setTitle(filter.getFilterName());
                builder.setView(myview);
                MyImageView filterImage = (MyImageView) myview.findViewById(R.id.filterImage);
                filterImage.setImageURL(filter.photo_url);
                TextView description = myview.findViewById(R.id.description);
                description.setText(filter.filterDes);
                //builder.setMessage(filter.filterDes);
                builder.setIcon(R.drawable.freestyle_icon);

                if(!isFavorite(filter.ID)){
                    builder.setPositiveButton("收藏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ChangeFavorite(filter.ID,true);
                        }
                    });
                }
                else{
                    builder.setPositiveButton("取消收藏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ChangeFavorite(filter.ID,false);
                        }
                    });
                }
                if(needResult){
                    builder.setNegativeButton("选择", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent1 = new Intent();
                            intent1.putExtra("photo_url",filter.photo_url);
                            setResult(filter.ID,intent1);
                            finish();
                        }
                    });
                }
                builder.show();
            }
        });
        final RadioGroup chooseGroup = findViewById(R.id.chooseGroup);
        chooseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View view = chooseGroup.findViewById(i);
                if(!view.isPressed()) return;
                switch(i){
                    case R.id.hotestButton:initHotest();break;
                    case R.id.historyButton:initHistory();break;
                    case R.id.favorityButton:initFavorite();break;
                }
            }
        });
    }

    private boolean isFavorite(int filterID){
        for(int i=0;i<profile_main.favoriteNum;++i)
            if(profile_main.favorite_item[i] == filterID)
                return true;
        return false;
    }

    private void ChangeFavorite(int filterID,boolean star){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = star ? "http://47.92.69.29:8000/star-filter" : "http://47.92.69.29:8000/star-remove";
        final String label = star ? "favorite" : "remove";
        JSONObject sign_inJSON = new JSONObject();
        try {
            sign_inJSON.put("sessionKey",log_in.sessionKey);
            sign_inJSON.put(label,String.valueOf(filterID));
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
                            if(statusCode.equals("200")){
                                profile_main.favoriteNum = GetID(response.getString(label),profile_main.favorite_item);
                                if(label == "favorite") Toast.makeText(FilterGallery.this, "收藏成功", Toast.LENGTH_LONG).show();
                                else Toast.makeText(FilterGallery.this, "取消收藏", Toast.LENGTH_LONG).show();
                            }
                            else if(statusCode.equals("400")) Toast.makeText(FilterGallery.this, "滤镜不存在", Toast.LENGTH_LONG).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FilterGallery.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );
        queue.add(jsonRequest);
    }

    private void initFavorite(){
        filterList.clear();
        for(int i=0;i<profile_main.favoriteNum;++i){
            GetFilter(profile_main.favorite_item[i]);
        }
        adapter.notifyDataSetChanged();
    }

    private void initHistory(){
        filterList.clear();
        for(int i=0;i<profile_main.historyNum;++i){
            GetFilter(profile_main.history_item[i]);
        }
        adapter.notifyDataSetChanged();
    }

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

    private void GetFilter(final int filterID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29:8000/get-filter";
        JSONObject sign_inJSON = new JSONObject();
        try {
            sign_inJSON.put("filterID",String.valueOf(filterID));
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
                            String name = response.getString("name");
                            String like = response.getString("like");
                            String tag = response.getString("tag");
                            String photo_url = response.getString("photo_url");
                            String description = response.getString("description");
                            statusCode = response.getString("statusCode");
                            if(statusCode.equals("200")){
                                Filter filter = new Filter(name,R.drawable.icons8_filter,description,photo_url,"\t\t\t热度："+like+"\n标签:"+tag,filterID);
                                filterList.add(filter);
                                adapter.notifyDataSetChanged();
                            }
                            else if(statusCode.equals("400")) Toast.makeText(FilterGallery.this, "滤镜"+filterID+"不存在", Toast.LENGTH_LONG).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FilterGallery.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );
        queue.add(jsonRequest);
    }

    private void initHotest(){
        filterList.clear();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://47.92.69.29:8000/get-hotest";
        JSONObject sign_inJSON = new JSONObject();
        try {
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
                            hotString = response.getString("hotest");
                            if(statusCode.equals("400")) Toast.makeText(FilterGallery.this,"最热列表不存在",Toast.LENGTH_LONG).show();
                            else if(!statusCode.equals("200")) Toast.makeText(FilterGallery.this,"状态码："+statusCode,Toast.LENGTH_LONG).show();
                            else{
                                hotNum = GetID(hotString,hotest_item);
                                for(int i=0;i<hotNum;++i){
                                    GetFilter(hotest_item[i]);
                                }
                                //adapter.notifyDataSetChanged();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FilterGallery.this, "Error "+error, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }
        );
        queue.add(jsonRequest);
    }

}
