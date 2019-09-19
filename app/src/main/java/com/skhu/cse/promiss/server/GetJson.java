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

    //네이버 지도에서 쓰일 메소드
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
}