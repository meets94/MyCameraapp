package com.example.manmeet.mycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CamActivity extends Activity implements SurfaceHolder.Callback,View.OnClickListener {
    Button capture,camera_change,flash;
    SurfaceView cameraSurface;
    SurfaceHolder surfaceHolder;
    Camera camera;
    int noOfCameras;
    Camera.CameraInfo cameraInfo;
    int backCamId, frontCamId;
    Camera.Parameters params;
    Camera.PictureCallback jpegCallback;

    @Override
  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        capture= (Button)findViewById(R.id.Camera_capture);
        cameraSurface=(SurfaceView)findViewById(R.id.Camera_surface);
        camera_change = (Button)findViewById(R.id.camer_change);
        // flash= (Button)findViewById(R.id.flash);


        surfaceHolder= cameraSurface.getHolder();
        surfaceHolder.addCallback(this);

        noOfCameras = Camera.getNumberOfCameras();
        cameraInfo = new Camera.CameraInfo();
        for ( int i = 0 ; i < noOfCameras; i++) {
            Camera.getCameraInfo(i,cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                backCamId = i;
            }
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                frontCamId = i;
            }
        }

        capture.setOnClickListener(this);


        jpegCallback = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {

                FileOutputStream outStream = null;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "JPEG_" + timeStamp + ".jpg";

                try {
                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                    dir.mkdirs();

                    File outFile = new File(dir, fileName);

                    outStream = new FileOutputStream(outFile);
                    outStream.write(data);
                    outStream.flush();
                    outStream.close();

                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                }

                Toast.makeText(getApplicationContext(), "Picture Saved on phone/Testcam",Toast.LENGTH_LONG).show();

                refreshCamera();

            }

        };
        Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        };
    }

    public void refreshCamera() {

        if (surfaceHolder.getSurface() == null) {
            return;

        }
        try {
            camera.stopPreview();
        } catch (Exception e) {

        }
        try {
            camera.setPreviewDisplay(surfaceHolder);

            camera.startPreview();
        } catch (Exception e) {
        }
    }

public void captureImage(){
          camera.takePicture(null, null, jpegCallback);
}

@Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{

            camera = Camera.open(frontCamId);
            if(camera_change.isPressed()) {
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    refreshCamera();
                    try {
                        camera = Camera.open(frontCamId);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                        camera.setDisplayOrientation(90);
                    }
                    catch (Exception e){

                    }
                    params=camera.getParameters();
                    List<String> sceneModes = params.getSupportedSceneModes();
                    if(sceneModes.contains(Camera.Parameters.SCENE_MODE_PARTY)){
                        params.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
                    }
                    camera.setParameters(params);
                } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    refreshCamera();
                    try {
                        camera = Camera.open(backCamId);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                        camera.setDisplayOrientation(90);
                    }
                    catch (Exception e){

                    }
                    params=camera.getParameters();
                    List<String> sceneModes = params.getSupportedSceneModes();
                    if(sceneModes.contains(Camera.Parameters.SCENE_MODE_PARTY)){
                        params.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
                    }
                    camera.setParameters(params);
            }}
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setDisplayOrientation(90);

        }catch(Exception e){

        }

        params=camera.getParameters();
     List<String> sceneModes = params.getSupportedSceneModes();
        if(sceneModes.contains(Camera.Parameters.SCENE_MODE_PARTY)){
            params.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
        }
      camera.setParameters(params);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width, int height) {
        holder.setFixedSize(300, 400);
        holder.setFormat(format);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.Camera_capture:
                captureImage();
                break;
            case R.id.camer_change:


        }}
}

