package com.binarypheasant.freestyle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class choose_filter extends AppCompatActivity {

    private LinearLayout filterLayout;
    private int[] filters;
    private String[] filterNames;
    private LayoutInflater layoutInflater;
    private String photoPath;
    private Bitmap photo; // 应该是从相机或者相册中获得，考虑使用Intent传递？
    private Bitmap renderPhoto = null;
    private String renderURL = "http://47.92.69.29:8000/render2";
    private String filterStr = null;

    private static final int filters_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_filter);

        layoutInflater = LayoutInflater.from(this);
        initData();
        initView();
    }

    // for action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_choose_filter, menu);
        return true;
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            photoPath = intent.getStringExtra("imagePath");
            Toast.makeText(choose_filter.this, photoPath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(choose_filter.this, "无法获得intent", Toast.LENGTH_SHORT).show();
        }


        // 预定义滤镜
        filters = new int[] { R.drawable.f1, R.drawable.f2, R.drawable.f3, R.drawable.f4};
        filterNames = new String[] {"1", "2", "3", "4"};
    }

    private void initView() {
        // 照片
        if (photoPath != null) {
            photo = BitmapFactory.decodeFile(photoPath);
        } else {
            Toast.makeText(choose_filter.this, "读取照片失败！", Toast.LENGTH_LONG).show();
            photo = BitmapFactory.decodeResource(getResources(), R.drawable.selfie_maniac);
        }
        ImageView imageView = (ImageView) findViewById(R.id.photoShow);
        imageView.setImageBitmap(photo);

        // 滤镜
        filterLayout = (LinearLayout) findViewById(R.id.linearScroll);

        for (int i=0; i<filters.length; i++) {
            View view = layoutInflater.inflate(R.layout.scroll_filter_item, filterLayout, false);

            final ImageButton imageButton = (ImageButton) view.findViewById(R.id.filter_item);
            imageButton.setImageResource(filters[i]); // 设置图片
            imageButton.setBackgroundColor(0); // 透明背景
            imageButton.setTag(filterNames[i]); // 设置标签，后面可以用来知道本imageButton对应哪张图片
            // 点击样式和事件
            //final Drawable drawable = getResources().getDrawable(R.drawable.ic_done_white_48dp);
            //drawable.setAlpha(128);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //imageButton.setForeground(drawable);
                    //Integer resource = (Integer)imageButton.getTag();
                    //ImageView imageView = (ImageView) findViewById(R.id.photoShow);
                    //imageView.setImageResource(resource);
                    filterStr = (String)imageButton.getTag();
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
        progressDialog.setTitle("渲染");
        progressDialog.setMessage("上传中，请耐心等待...");
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
        photo.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imgBytes = baos.toByteArray();
        final String imgStr = Base64.encodeToString(imgBytes, Base64.DEFAULT);

        //sending image to server
        JSONObject uploadJson = new JSONObject();
        try {
            uploadJson.put("style", filterStr);
            uploadJson.put("content", imgStr);
            uploadJson.put("sessionKey", 123);
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
                            byte[] bitmapArray;
                            bitmapArray = Base64.decode(content, Base64.DEFAULT);
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
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();

                String body = null;
                if(volleyError.networkResponse.data != null) {
                    try {
                        body = new String(volleyError.networkResponse.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (body != null) {
                    Log.v("############", body);
                } else {
                    Log.v("############", "null");
                }

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

        // 后端处理时间较长，需要时间较长，先设为60s
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60*1000, 1, 1f));

        RequestQueue rQueue = Volley.newRequestQueue(choose_filter.this);
        rQueue.add(jsonRequest);

        progressDialog.setMessage("图片处理中，请耐心等待...");
    }

    public void onClickFinish(MenuItem item) {
        renderPhoto = photo;

        // save it to file
        if (renderPhoto != null) {
            String filePath = saveImgToFile(renderPhoto);

            // open the pre_publish activity
            if (filePath != null) {
                Intent filterIntent = new Intent(choose_filter.this, prepublish.class);
                filterIntent.putExtra("imagePath", filePath);
                startActivity(filterIntent);
            } else {
                Toast.makeText(choose_filter.this, "无法获得图片路径。", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(choose_filter.this, "图片还未渲染，无法进行下一步。", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImgToFile(Bitmap bitmap){

        File mediaStorageDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"FreeStyle");
        Log.v("yzjy",mediaStorageDir.toString());
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                return null;
            }
        }

        //
        String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "render_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, filePath);

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            filePath = null;
            e.printStackTrace();
            Toast.makeText(choose_filter.this, "图片无法保存。", Toast.LENGTH_SHORT).show();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);


        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(choose_filter.this, "文件流无法关闭。", Toast.LENGTH_SHORT).show();
        }

        return filePath;
    }

    public void onClickMoreFilters(View view) {
        Toast.makeText(choose_filter.this, "你点击了\"更多滤镜\"", Toast.LENGTH_SHORT).show();
        // ToDO: 打开滤镜画廊，获取滤镜后添加到filters[]中，然后重新加载滤镜显示栏，并把新的排到前面
        Intent intent = new Intent(choose_filter.this, FilterGallery.class);
        // check sessionKey first

        //
        intent.putExtra("choose",0);
        intent.putExtra("needResult",true);
        startActivityForResult(intent, filters_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.v("yzjy", "herhehrehrehrherhehr");
        if (requestCode == filters_REQUEST_CODE) {
            // 获取新的滤镜 Array
            int[] newFilters = null;
            String[] newFilterNames = null;
            if (intent != null) {
                String tmp = intent.getStringExtra("photo_url");
//                newFilters = intent.getIntArrayExtra("newFilters");
//                newFilterNames = intent.getStringArrayExtra("newFilterNames");
            } else {
                Toast.makeText(choose_filter.this, "无法获得intent", Toast.LENGTH_SHORT).show();
            }

            // 将两个filter数组合并
            // id or path?
            int[] combFilters = new int[filters.length + newFilters.length];
            System.arraycopy(newFilters, 0, combFilters, 0, newFilters.length);
            System.arraycopy(filters, 0, combFilters, newFilters.length, filters.length);
            filters = combFilters;
            // name
            String[] combFilterNames = new String[filterNames.length + newFilterNames.length];
            System.arraycopy(newFilterNames, 0, combFilterNames, 0, newFilterNames.length);
            System.arraycopy(filterNames, 0, combFilterNames, newFilterNames.length, filterNames.length);
            filterNames = combFilterNames;
            // 显示所有filter
            initView(); // ToDO: 得设置照片为renderPhoto
        } else {
            Toast.makeText(choose_filter.this, "请求码错误！", Toast.LENGTH_SHORT).show();
        }
    }


//    public void onClickEdit(View view) {
//        Toast.makeText(choose_filter.this, "你点击了\"编辑\"", Toast.LENGTH_SHORT).show();
//    }
//
//    public void onClickDIYFilter(View view) {
//        this.filterStr = "DIY";
//        Toast.makeText(choose_filter.this, "你点击了\"自定义滤镜\"", Toast.LENGTH_SHORT).show();
//    }
//
//    public void onClickCrop(View view) {
//        Toast.makeText(choose_filter.this, "你点击了\"选区\"", Toast.LENGTH_SHORT).show();
//    }


}

