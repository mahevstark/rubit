package net.trejj.talk;


public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://talk.trejj.net";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
