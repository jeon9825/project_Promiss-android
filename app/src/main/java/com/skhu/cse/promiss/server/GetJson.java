package com.skhu.cse.promiss.server;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GetJson {

    private OkHttpClient client;
    private static GetJson instance = new GetJson();

    private String url = "http://106.10.54.206/";
    public static GetJson getInstance() {
        return instance;
    }

    private GetJson() {
        this.client = new OkHttpClient();
    }

    /**
     * 웹 서버로 요청을 한다.
     */

    public void PostSpeekRequest(String Text, int Id, Callback callback) {

        FormBody body = new FormBody.Builder()
                .add("message", Text)
                .add("id", "" + Id)
                .build();

        Request request = new Request.Builder()
                .url(url + "api/promiss/ai")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public void requestGet(String parameter, Callback callback) {

        Request request = new Request.Builder()
                .url(url) //통신하고자하는 url
                .build();
        client.newCall(request).enqueue(callback); //통신후 콜백될 함수
    }

    public void requestPost(String api, Callback callback, String ... parameter) {

        String URL = url+api;
        FormBody.Builder body = new FormBody.Builder();
        for (int i = 0; i<parameter.length;i=i+2){
            body.add(parameter[i],parameter[i+1]);
        }

        Request request = new Request.Builder()
                .url(URL) //통신하고자하는 u
                .post(body.build())
                .build();
        client.newCall(request).enqueue(callback); //통신후 콜백될 함수
    }

    //지도 주소 검색
    public void requestMapApi(Callback callback, String ...parameter){
        String URL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?"; //네이버 지도 API 주소

        URL +="query="+parameter[0]+"&coordinate="+parameter[1]+","+parameter[2];

        Request request = new Request.Builder()
                .url(URL) //통신하고자하는 url
                .addHeader("X-NCP-APIGW-API-KEY-ID","ke4acfug28")
                .addHeader("X-NCP-APIGW-API-KEY","Q2CkzTpLxk7XKxEKn6GVIUPH1HTYsl8cKj7l15tn")
                .build();
        client.newCall(request).enqueue(callback); //통신후 콜백될 함수
    }

    //지도로부터 위도 경도 얻어오기
    public void requestLO(Callback callback, String ...parameter){
        String URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?"; //네이버 지도 API 주소

        URL +="coords="+parameter[0]+"&output=json";

        Request request = new Request.Builder()
                .url(URL) //통신하고자하는 url
                .addHeader("X-NCP-APIGW-API-KEY-ID","ke4acfug28")
                .addHeader("X-NCP-APIGW-API-KEY","Q2CkzTpLxk7XKxEKn6GVIUPH1HTYsl8cKj7l15tn")
                .build();
        client.newCall(request).enqueue(callback); //통신후 콜백될 함수
    }


}