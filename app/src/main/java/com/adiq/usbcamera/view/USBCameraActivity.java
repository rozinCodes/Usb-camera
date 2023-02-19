package com.adiq.usbcamera.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adiq.usbcamera.R;
import com.adiq.usbcamera.UVCCameraHelper;
import com.adiq.usbcamera.application.ForegroundService;
import com.adiq.usbcamera.application.MyApplication;
import com.adiq.usbcamera.application.networking.ApiService;
import com.adiq.usbcamera.application.networking.uploadResponse;
import com.adiq.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class USBCameraActivity extends AppCompatActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {
    private static final String TAG = "Release";
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.camera_view)
    public View mTextureView;
    @SuppressLint("NonConstantResourceId")

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;

    private boolean isRequest;
    private boolean isPreview;

    @Override
    public void onBackPressed() {

    }

    private final UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

//        public void setRunnable(Runnable runnable) {
//            this.runnable = runnable;
//        }

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        public void onDetachDev(UsbDevice device) {

//            if (isRequest) {
//                isRequest = false;
//                mCameraHelper.closeCamera();
//                showShortMsg(device.getDeviceName() + " is out");
//            }
        }


        final Handler handler = new Handler();
        private Runnable runnable;

        @Override

        public void onConnectDev(UsbDevice device, boolean isConnected) {
//                    Log.d("hello", String.valueOf(device.getDeviceClass() == 239));
            if (!isConnected) {
//                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
//                    Log.d("hello", device.getDeviceName());
//                    Log.d("hello", device.getProductName());
//                    Log.d("hello", String.valueOf(device.getDeviceId()));
//                    Log.d("hello", String.valueOf(device.getConfigurationCount()));
//                    Log.d("hello", device.getManufacturerName());
//                if (device.describeContents() == 0) {
                    isPreview = true;
//                    Log.d("hello", device.getDeviceName());
//
                    recordVideo();
//                } else {
//                    isPreview = false;
//                }

//
//                StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
//                long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
//                long megAvailable = bytesAvailable / 1048576;

//                showShortMsg(String.valueOf(megAvailable));

//                String videoPath = UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME + "/"
//                        + "1667" + UVCCameraHelper.SUFFIX_MP4;
//
////                        UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME + "/" + "test_new";
//                RecordParams params = new RecordParams();
//                params.setRecordPath(videoPath);
//                params.setRecordDuration(20000);
//                showShortMsg(videoPath);


//                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://af63-103-169-159-101.in.ngrok.io/api/files/")
//                        .addConverterFactory(GsonConverterFactory.create()).build();
//                File file = new File(videoPath);
//
//                if(file.exists()) {
//                    showShortMsg("file exists");
//                }
//                else {
//                    showShortMsg("file does not exist");
//                }
//
//                showShortMsg(file.getName());
//
//
//
//                RequestBody requestFile = RequestBody.create(MediaType.parse("videos/mp4"), file);
//                MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestFile);
//
//
//                ApiService apiService = retrofit.create(ApiService.class);
//                Call<uploadResponse> call = apiService.uploadVideo(body);
//                call.enqueue(new Callback<uploadResponse>() {
//                    @Override
//                    public void onResponse(@NonNull Call<uploadResponse> call, @NonNull Response<uploadResponse> response) {
//                        if (response.isSuccessful()) {
//                            showShortMsg("Success");
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<uploadResponse> call, Throwable t) {
//                        Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
//                        showShortMsg("Failure");
//                    }
//                });
//                showShortMsg("connecting");
//                showShortMsg("start record...");
                // need to wait UVCCamera initialize over
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //call function after set duration
//
//                        FileUtils.releaseFile();
//                        mCameraHelper.stopPusher();
//                        showShortMsg("stop record...");
//
//
//                        handler.postDelayed(this, 30000);
//                    }
//                }, 30000);

//                new Thread(() -> {
//                    try {
//                        Thread.sleep(2500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    Looper.prepare();
//                    if (mCameraHelper != null) {
//                        mCameraHelper.isCameraOpened();
//                    }
//                    Looper.loop();
//                }).start();

            }
        }

        private void recordVideo() {

            final int delay = 300000; // 1000 milliseconds == 1 second

//            if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
//                showShortMsg("sorry,camera open failed");
//            }
//            if (!mCameraHelper.isPushing()) {
            String videoPath = UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME + "/" + System.currentTimeMillis();

            RecordParams params = new RecordParams();
            params.setRecordPath(videoPath);
            params.setVoiceClose(true);
//            params.setRecordDuration(10000);
            params.setSupportOverlay(true);
            mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                @Override
                public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                    if (type == 1) {
                        FileUtils.putFileStream(data, offset, length);
                    }
                    if (type == 0) {

                    }
                }

                @Override
                public void onRecordResult(String videoPath) {
                    if (TextUtils.isEmpty(videoPath)) {
                        return;
                    }
                    File file = new File(videoPath);
//                    new Handler(getMainLooper()).post(() -> Toast.makeText(USBCameraActivity.this, "save videoPath:" + videoPath, Toast.LENGTH_SHORT).show());
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://0300-103-169-159-101.in.ngrok.io/api/files/")
                            .addConverterFactory(GsonConverterFactory.create()).build();

                    RequestBody requestFile = RequestBody.create(MediaType.parse("videos/mp4"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestFile);


                    ApiService apiService = retrofit.create(ApiService.class);
                    Call<uploadResponse> call = apiService.uploadVideo(body);
                    call.enqueue(new Callback<uploadResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<uploadResponse> call, @NonNull Response<uploadResponse> response) {
                            if (response.isSuccessful()) {
                                showShortMsg("Success");


                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<uploadResponse> call, Throwable t) {
//                            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    });
                }
            });
//            } else {
            handler.postDelayed(new Runnable() {
                public void run() {
                    handler.removeCallbacksAndMessages(runnable);
                    handler.postDelayed(this, delay);
                    FileUtils.releaseFile();
                    mCameraHelper.stopPusher();
                    recordVideo();
                }
            }, delay);

        }
//        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
//            showShortMsg("disconnecting");
            System.exit(0);

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = new Intent(this, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            stopService(intent);
        }
        setContentView(R.layout.activity_usbcamera);
        ButterKnife.bind(this);
        initView();
        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        mCameraHelper.setOnPreviewFrameListener(nv21Yuv -> Log.d(TAG, "onPreviewResult: " + nv21Yuv.length));
    }

    private void initView() {
    }


    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_toobar, menu);
//        return true;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("Cancel the operation");
        }
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }
}
