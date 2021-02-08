package net.trejj.talk;


import com.limecoders.quizapp.Pojo.Api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {

    @POST("/api/v1//eq_signUp.php")
    @FormUrlEncoded
    Call<Api> createEQUser(@Field("fullname") String fullanme,
                           @Field("email") String email,
                           @Field("password") String password,
                           @Field("coins") String coins,
                           @Field("token") String token,
                           @Field("created_at") String created_at,
                           @Field("refer_code") String refer_code);

    @POST("/api/v1/eq_login.php")
    @FormUrlEncoded
    Call<Api> eqUserLogin(@Field("email") String email,
                          @Field("password") String password);

    @POST("/api/v1/Api.php?apicall=sendEmail")
    @FormUrlEncoded
    Call<Api> sendEmail(@Field("email") String email);

    @POST("/api/v1/Api.php?apicall=updateEQPassword")
    @FormUrlEncoded
    Call<Api> updateEQPassword(@Field("email") String email,
                               @Field("password") String pass);

    @GET("/api/v1/Api.php?apicall=getEQCat")
//    @FormUrlEncoded
    Call<Api> getEQCat();


    @POST("/api/v1/Api.php?apicall=getEQQuestions")
    @FormUrlEncoded
    Call<Api> getEQQuestions(@Field("cat_id") int cat_id);
}
