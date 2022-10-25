package com.emi.systemconfiguration;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {

    @Multipart
    @POST("UploadFile")
    Call<RequestBody> uplpadImage(@Part MultipartBody.Part part, @Part("apikey") RequestBody apikey, @Part("no-cache") RequestBody cache);

}
