package com.adiq.usbcamera.application.networking;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("local_upload")
    Call<uploadResponse> uploadVideo(@Part MultipartBody.Part video);
}
