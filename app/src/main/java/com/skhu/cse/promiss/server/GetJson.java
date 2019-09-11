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
                .url(URL) //통신하고자하는 url
                .post(body.build())
                .build();
        client.newCall(request).enqueue(callback); //통신후 콜백될 함수
    }
}