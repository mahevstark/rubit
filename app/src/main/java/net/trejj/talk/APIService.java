package net.trejj.talk;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {

    @POST("/verification.php")
    @FormUrlEncoded
    Call<Api> validation(@Field("transId") int transId);

}
