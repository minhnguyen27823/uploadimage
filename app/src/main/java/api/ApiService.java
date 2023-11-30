package api;

import com.example.uploadimage.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    //Link API : http://127.0.0.1:5000/post
    public static final String DOMAIN ="http://192.168.1.3:5000/";

    Gson gson= new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    @Multipart
    @POST("post")
    Call<User> registerAccount(@Part MultipartBody.Part image);
}
