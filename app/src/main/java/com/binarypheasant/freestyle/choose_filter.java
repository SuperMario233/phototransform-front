package com.binarypheasant.freestyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class choose_filter extends AppCompatActivity {

    private LinearLayout filterLayout;
    private int[] filters;
    private LayoutInflater layoutInflater;
    private Bitmap photo; // 应该是从相机或者相册中获得，考虑使用Intent传递？
    private Bitmap renderPhoto = null;
    private String renderURL = "http://47.92.69.29/render";
    private String filterStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_filter);

        layoutInflater = LayoutInflater.from(this);
        initData();
        initView();
    }

    private void initData() {
        Intent tmpIntent =getIntent();
        if(tmpIntent!=null){
            String photoPath=tmpIntent.getStringExtra("imagePath");
            Toast.makeText(choose_filter.this,photoPath,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(choose_filter.this,"no Intent",Toast.LENGTH_SHORT).show();
        }
        // 照片
        photo = BitmapFactory.decodeResource(getResources(), R.drawable.selfie_maniac);
        // 预定义滤镜
        filters = new int[] { R.drawable.ic_alarm_on_black_48dp, R.drawable.ic_check_circle_black_48dp,
                R.drawable.ic_done_all_black_48dp, R.drawable.ic_done_black_48dp};
    }

    private void initView() {
        filterLayout = (LinearLayout) findViewById(R.id.linearScroll);

        for (int i=0; i<filters.length; i++) {
            View view = layoutInflater.inflate(R.layout.scroll_filter_item, filterLayout, false);

            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.filter_item);
            imageButton.setImageResource(filters[i]); // 设置图片
            imageButton.setBackgroundColor(0); // 透明背景
            imageButton.setTag(filters[i]); // 设置标签，后面可以用来知道本imageButton对应哪张图片
            // 点击样式和事件
            //final Drawable drawable = getResources().getDrawable(R.drawable.ic_done_white_48dp);
            //drawable.setAlpha(128);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //imageButton.setForeground(drawable);
                    Integer resource = (Integer)imageButton.getTag();
                    //ImageView imageView = (ImageView) findViewById(R.id.photoShow);
                    //imageView.setImageResource(resource);
                    filterStr = resource.toString();
                    onRender();
                }
            });
            filterLayout.addView(view);
        }

    }

    // Send choosed image and filter to server
    public void onRender() {
        // progress bar
        final ProgressDialog progressDialog = new ProgressDialog(choose_filter.this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();


        // get the chose filter
        if (filterStr == null) {
            Toast.makeText(choose_filter.this, "请选择一个滤镜", Toast.LENGTH_SHORT).show();
        }
        else if (filterStr.equals("DIY")) {
            filterStr = null;// 滤镜图片转的Base64 string
        }

        // convert image to Base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgBytes = baos.toByteArray();
        final String imgStr = Base64.encodeToString(imgBytes, Base64.DEFAULT);

        //sending image to server
        JSONObject uploadJson = new JSONObject();
        try {
            uploadJson.put("style", filterStr);
            uploadJson.put("content", imgStr);
            uploadJson.put("sessionKey", new Random().nextInt(1024));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, renderURL, uploadJson,
                new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                String code = null;
                String content = null;
                try {
                    code = response.getString("statusCode");
                    content = response.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(choose_filter.this, "Parse JSON error", Toast.LENGTH_SHORT).show();
                }
                if(code.equals("200")){
                    Toast.makeText(choose_filter.this, "渲染成功！", Toast.LENGTH_SHORT).show();
                }
                else if (code.equals("401")) {
                    Toast.makeText(choose_filter.this, "Internal Error", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(choose_filter.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

                if(content != null) {
                    byte[]bitmapArray;
                    bitmapArray=Base64.decode(content, Base64.DEFAULT);
                    renderPhoto = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                    // display in the imageView
                    ImageView imageView = (ImageView) findViewById(R.id.photoShow);
                    imageView.setImageBitmap(renderPhoto);

                    Toast.makeText(choose_filter.this, "图片已显示", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(choose_filter.this, "Content null!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(choose_filter.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("image", imgStr);
                return parameters;
            }
        };

        RequestQueue rQueue = Volley.newRequestQueue(choose_filter.this);
        rQueue.add(jsonRequest);

    }

    public void onClickMoreFilters(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"更多滤镜\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickEdit(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"编辑\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickDIYFilter(View view) {
        this.filterStr = "DIY";
        Toast.makeText(choose_filter.this, "你点击了\"自定义滤镜\"", Toast.LENGTH_SHORT).show();
    }

    public void onClickCrop(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"选区\"", Toast.LENGTH_SHORT).show();
    }

}
