package com.example.wisecheck;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface FileDownoloadClient {
    public void  path(String path);
    @GET
    Call<ResponseBody> downoloadFile(@Url String url);


}