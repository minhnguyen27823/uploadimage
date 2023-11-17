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
    //Link API : http://localhost/phpmyadmin/index.php?route=/database/structure&server=1&db=upload_image&table=avt
    public static final String DOMAIN ="http://localhost/phpmyadmin/index.php?route=/database/";

    Gson gson= new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    @Multipart
    @POST("structure&server=1&db=upload_image&table=avt")
    Call<User> registerAccount(@Part MultipartBody.Part avt);
}
