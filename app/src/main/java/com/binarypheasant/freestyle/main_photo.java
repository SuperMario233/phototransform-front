package com.binarypheasant.freestyle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class main_photo extends AppCompatActivity {
    private ImageView sign;
    private ImageView gallery;
    private ImageView takePhoto;
    private TextureView cameraView;
    private String mCameraId = "0";
    private ImageReader imageReader;
    private Size previewSize;
    private int width=0,height=0;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder mCaptureRequestBuilder,captureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mPreviewSession;
    private ImageView iv;
    private Context context=this;
    private Button tempButton;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2=7;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int CROP_PHOTO = 2;
    private static final int RESULT_CODE_CAMERA=1;
    private static final int REQUEST_PHOTO_RESULT=4;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            stopCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_photo);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        sign=(ImageView) findViewById(R.id.sign_in);
        gallery=(ImageView)findViewById(R.id.gallery);
        takePhoto=(ImageView)findViewById(R.id.photo_button);
        cameraView=(TextureView)findViewById(R.id.camera_in);
        iv=(ImageView)findViewById(R.id.photo_show);
        tempButton=(Button)findViewById(R.id.button);
        /**
         * ？？？为什么加了这个button就会报错？？？
         */
        /*
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(main_photo.this,prepublish.class);
                startActivity(intent);
            }
        });*/
        sign.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent SignInt=new Intent(main_photo.this,LoginActivity.class);
                startActivity(SignInt);
            }
                                }
        );
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                //ToDo:跳转页面，传输数据
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(main_photo.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                }else {
                    choosePhoto();
                }
            }
        });
    }
    private void openCamera(){
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
        setCameraCharacteristics(manager);
        try{
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                String[] perms={"android.permission.CAMERA"};
                ActivityCompat.requestPermissions(main_photo.this,perms,RESULT_CODE_CAMERA);
            }
            else{
                manager.openCamera(mCameraId, stateCallback,null);
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void setCameraCharacteristics(CameraManager manager){
        try {
            // 获取指定摄像头的特性
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(mCameraId);
            // 获取摄像头支持的配置属性
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 获取摄像头支持的最大尺寸
            Size largest= Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),new CompareSizesByArea());
            // 创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader=ImageReader.newInstance(largest.getWidth(),largest.getHeight(),ImageFormat.JPEG,2);
            //设置获取图片的监听
            imageReader.setOnImageAvailableListener(imageAvailableListener,null);
            //获取最佳预览尺寸
            previewSize=chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),width,height,largest);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }catch (NullPointerException e) {
        }
    }
    private static Size chooseOptimalSize(Size[] choices,int width,int height,Size aspectRatio){
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size>bigEnough=new ArrayList<>();
        int w=aspectRatio.getWidth();
        int h=aspectRatio.getHeight();
        for(Size option:choices){
            if(option.getHeight()==option.getWidth()* h / w && option.getWidth() >= width && option.getHeight()>=height){
                bigEnough.add(option);
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最小的
        if(bigEnough.size()>0){
            return Collections.min(bigEnough, new CompareSizesByArea());
        }
        else{
            return choices[0];
        }
    }
    //为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>{
        @Override
        public int compare(Size lhs,Size rhs){
            return Long.signum((long) lhs.getWidth()*lhs.getHeight()-(long)rhs.getWidth()*rhs.getHeight());
        }
    }
    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            main_photo.this.cameraDevice=cameraDevice;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            main_photo.this.cameraDevice.close();
            main_photo.this.cameraDevice=null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
        }
    };

    /**
     * 开始预览
     */
    private void takePreview(){
        SurfaceTexture mSurfaceTexture=cameraView.getSurfaceTexture();
        //设置TextureView的缓冲区大小
        mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(),previewSize.getHeight());
        //获取Surface显示预览数据
        Surface mSurface=new Surface(mSurfaceTexture);
        try{
            //创建预览请求
            mCaptureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置自动对焦模式
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //设置Surface作为预览数据的显示界面
            mCaptureRequestBuilder.addTarget(mSurface);
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            cameraDevice.createCaptureSession(Arrays.asList(mSurface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try{
                        //开始预览
                        mCaptureRequest=mCaptureRequestBuilder.build();
                        mPreviewSession=cameraCaptureSession;
                        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                        mPreviewSession.setRepeatingRequest(mCaptureRequest,null,null);
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            },null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    private void takePicture(){
        try{
            if(cameraDevice==null) {
                return;
            }
            // 创建拍照请求
            captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 设置自动对焦模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //将imageReader的surface设为目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            //获取设备方向
            int rotation=getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation) );
            //停止连续取景
            mPreviewSession.stopRepeating();
            //拍照
            CaptureRequest captureRequest=captureRequestBuilder.build();
            //设置拍照监听
            mPreviewSession.capture(captureRequest,captureCallback,null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }
    /**监听拍照结果*/
    private CameraCaptureSession.CaptureCallback captureCallback=new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            // 重设自动对焦模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            //设置自动曝光模式
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            try{
                //重新进行预览
                mPreviewSession.setRepeatingRequest(mCaptureRequest,null,null);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }
        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure){
            super.onCaptureFailed(session,request,failure);
        }
    };
    /**监听拍照的图片*/
    private ImageReader.OnImageAvailableListener imageAvailableListener=new ImageReader.OnImageAvailableListener() {
        // 当照片数据可用时激发该方法
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            //先验证手机是否有sdcard
            String status= Environment.getExternalStorageState();
            if(!status.equals(Environment.MEDIA_MOUNTED)){
                Toast.makeText(getApplicationContext(),"你的SD卡不可用",Toast.LENGTH_SHORT).show();
                return;
            }
            //获取捕获的照片数据
            Image image=imageReader.acquireNextImage();
            ByteBuffer buffer=image.getPlanes()[0].getBuffer();
            byte[] data=new byte[buffer.remaining()];
            buffer.get(data);

            //手机拍照都是存到这个路径
            String filePath=Environment.getExternalStorageDirectory().getPath()+ "/DCIM/Camera/";
            String picturePath=System.currentTimeMillis()+".jpg";
            File file=new File(filePath,picturePath);
            try{
                //存到本地相册
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.close();

                //显示图片
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inSampleSize=2;
                Bitmap bitmap=BitmapFactory.decodeByteArray(data,0,data.length,options);
                iv.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                image.close();
            }
        }
    };
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,String[] permissions,int[] grantResults){
        switch (permsRequestCode){
            case RESULT_CODE_CAMERA:
                boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
                    openCamera();
                }else {
                    //用户授权拒绝之后，友情提示一下就可以了
                    Toast.makeText(main_photo.this,"请开启应用拍照权限",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void startCamera(){
        if(cameraView.isAvailable()){
            if(cameraDevice==null){
                openCamera();
            }
        }else {
            cameraView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    /**
     * 停止拍照释放资源
     */
    private void stopCamera(){
        if(cameraDevice!=null){
            cameraDevice.close();
            cameraDevice=null;
        }
    }
//拍照部分终于结束了！



    void choosePhoto() {
            Intent chooseInt=new Intent(Intent.ACTION_PICK);
            chooseInt.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(chooseInt,REQUEST_CODE_PICK_IMAGE);
    }
    public void onActivityResult(int req,int res,Intent data) {
        if(req==0)
            return;
        if(data==null)
            return;
        if(req==REQUEST_CODE_PICK_IMAGE){
            startPhotoZoom(data.getData());
        }
        if(req==REQUEST_PHOTO_RESULT){
            Bundle extras=data.getExtras();
            if(extras!=null){
                Bitmap photo=extras.getParcelable("data");
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG,75,stream);// (0-100)压缩文件
                iv.setImageBitmap(photo);
            }
        }
        super.onActivityResult(req,res,data);
    }
    public void startPhotoZoom(Uri uri){
        Intent zoomInt=new Intent("com.android.camera.action.CROP");
        zoomInt.setDataAndType(uri,"image/*");
        /**
        *运行时会报错，据说是路径问题，还没解决
         */
        zoomInt.putExtra("crop","true");
        zoomInt.putExtra("aspectX",1);
        zoomInt.putExtra("aspectY",1);
        zoomInt.putExtra("outputX",300);
        zoomInt.putExtra("outputY",400);
        zoomInt.putExtra("return-data",true);

        startActivityForResult(zoomInt,REQUEST_PHOTO_RESULT);
    }
}
