package com.adiq.usbcamera.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
        Runnable runnable;

        @Override

        public void onConnectDev(UsbDevice device, boolean isConnected) {
//                    Log.d("hello", String.valueOf(device.getDeviceClass() == 239));
            if (!isConnected) {
                showShortMsg("Failed to connect to camera, please restart the application");
                isPreview = false;
            } else {
                isPreview = true;
                recordVideo();


            }
        }

        private void recordVideo() {

            final int delay = 100000; // 1000 milliseconds == 1 second

            String videoPath = UVCCameraHelper.ROOT_PATH + MyApplication.DIRECTORY_NAME + "/" + Math.random();

            RecordParams params = new RecordParams();
            params.setRecordPath(videoPath);
            params.setVoiceClose(true);
            params.setSupportOverlay(true);
            mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                @Override
                public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                    if (type == 1) {
                        FileUtils.putFileStream(data, offset, length);
                    }
                }

                @Override
                public void onRecordResult(String videoPath) {
                    recordVideo();
                    File file = new File(videoPath);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://d44e-103-169-159-101.in.ngrok.io/api/files/").addConverterFactory(GsonConverterFactory.create()).build();

                    RequestBody requestFile = RequestBody.create(MediaType.parse("videos/mp4"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), requestFile);


                    ApiService apiService = retrofit.create(ApiService.class);
                    Call<uploadResponse> call = apiService.uploadVideo(body);
                    call.enqueue(new Callback<uploadResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<uploadResponse> call, @NonNull Response<uploadResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<uploadResponse> call, @NonNull Throwable t) {
//                            Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                            if (file.exists()) {
                                file.delete();
//                                showShortMsg("Failed to send video");
                            }
                        }
                    });
                }
            });
            handler.postDelayed(new Runnable() {
                public void run() {
                    handler.removeCallbacksAndMessages(runnable);
                    handler.postDelayed(this, delay);
                    FileUtils.releaseFile();
                    mCameraHelper.stopPusher();
                }
            }, delay);

        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("Camera disconnected");
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
