package com.example.oopapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Preview extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mtextureview = (TextureView) findViewById(R.id.textureView);
    }
    @Override
    protected void onResume(){
        super.onResume();
        startbackgroundthread();
        if(mtextureview.isAvailable()){
            setupcamera(mtextureview.getWidth(),mtextureview.getHeight());
            connectcamera();
        }
        else{
            mtextureview.setSurfaceTextureListener(msurfacetextruelistener);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        stopbackgroundthread();
        closeCamera();
    }

    private TextureView mtextureview;
    private CameraDevice mcameradevice;
    private String mcameraid;
    private String flen;
    private String rlen;
    private HandlerThread mbackgroundthread;
    private Handler mbackgroundhandler;
    private Size mPreviewSize;
    private CaptureRequest.Builder mcapturerequestbuilder;
    private static final int REQUESTCAMERARESULT=0;
    public static int a=500;
    public static int b=750;


    //temporary display
    public void DrawRect(View view){
        View v = (View)findViewById(R.id.rect);
        if(v.getVisibility()==View.VISIBLE){
            v.setVisibility(v.INVISIBLE);
        }else{
            v.setVisibility(v.VISIBLE);
            AbsoluteLayout.LayoutParams params = ((AbsoluteLayout.LayoutParams) v.getLayoutParams());
            params.x = a;
            params.y = b;
            v.setLayoutParams(params);
        }
    }

    //switch len
    public void switchlen(View v){
        if(mcameraid==flen){
            mcameraid=rlen;
            closeCamera();
            connectcamera();
        }else{
            mcameraid=flen;
            closeCamera();
            connectcamera();
        }
    }
    //close camera
    private void closeCamera(){
        if(mcameradevice!=null){
            mcameradevice.close();
            mcameradevice=null;
        }
    }
    //set up the camera
    private void setupcamera(int height, int width){
        CameraManager cameramanager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String camerid : cameramanager.getCameraIdList()){
                CameraCharacteristics mcameracharacteristics = cameramanager.getCameraCharacteristics(camerid);
                if(mcameracharacteristics.get(CameraCharacteristics.LENS_FACING)!=CameraCharacteristics.LENS_FACING_FRONT){
                    rlen=camerid;
                    continue;
                }
                StreamConfigurationMap map = mcameracharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize=chooseOptimalsSize(map.getOutputSizes(SurfaceTexture.class),width,height);
                flen=camerid;
                mcameraid=rlen;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //setup backgroundthread
    private void startbackgroundthread(){
        mbackgroundthread = new HandlerThread("videoing");
        mbackgroundthread.start();
        mbackgroundhandler = new Handler(mbackgroundthread.getLooper());
    }

    //stop backgroundthread
    private void stopbackgroundthread(){
        mbackgroundthread.quitSafely();
        try {
            mbackgroundthread.join();
            mbackgroundthread=null;
            mbackgroundhandler=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //match the size of the textureview
    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size size, Size t1) {
            return Long.signum((long) size.getWidth()* size.getHeight()/ (long) t1.getWidth()*t1.getHeight());
        }
    }

    //previewsize calculating
    private static Size chooseOptimalsSize(Size[] choices, int width, int height){
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    //connect camera
    public void connectcamera(){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==
                        PackageManager.PERMISSION_GRANTED){
                    cameraManager.openCamera(mcameraid, mcamerdevicestatecallback, mbackgroundhandler);
                } else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        Toast.makeText(this,"video required camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[] {Manifest.permission.CAMERA},REQUESTCAMERARESULT);
                }
            }else{
                cameraManager.openCamera(mcameraid, mcamerdevicestatecallback, mbackgroundhandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
//start preview
    public void startPreview(){
        SurfaceTexture msurfacetexture = mtextureview.getSurfaceTexture();
        msurfacetexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
        Surface previewsurface = new Surface(msurfacetexture);
        try {
            mcapturerequestbuilder = mcameradevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mcapturerequestbuilder.addTarget(previewsurface);
            mcameradevice.createCaptureSession(Arrays.asList(previewsurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            try{
                                cameraCaptureSession.setRepeatingRequest(mcapturerequestbuilder.build(),
                                        null, mbackgroundhandler);
                            }catch(CameraAccessException e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getApplicationContext(),"FAILED",Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //set up listener to listen the cameradevice
    private CameraDevice.StateCallback mcamerdevicestatecallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mcameradevice = cameraDevice;
            startPreview();
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mcameradevice = null;
        }
        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            mcameradevice = null;
        }
    };

    //set up for surfacetexture listener to listen the textureview of system loading
    private TextureView.SurfaceTextureListener msurfacetextruelistener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            setupcamera(i,i1);
            connectcamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        }
        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
            //System.out.println ("ADDING FROM USER");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUESTCAMERARESULT) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
        }
    }
}