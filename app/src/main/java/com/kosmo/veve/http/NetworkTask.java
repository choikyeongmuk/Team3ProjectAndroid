package com.kosmo.veve.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    public static String SERVER_URL = "http://192.168.219.184:8080/veve";
    public static String SIGNUP = SERVER_URL + "/member/signUp";
    public static String SIGNUPKAKAO = SERVER_URL + "/member/signUpKakao";
    public static String GALLERY_POST = SERVER_URL + "/gallery/post";

    private String url;
    private ContentValues values;
    private String _method="POST";

    public NetworkTask(String url, ContentValues values) {
        this.url = url;
        this.values = values;
        this._method ="POST";
    }

    public NetworkTask(String url, ContentValues values, String _me) {
        this.url = url;
        this.values = values;
        this._method =_me;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        result = requestHttpURLConnection.request(url, values,_method); // 해당 URL로 부터 결과물을 얻어온다.

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        Log.d("결과값===",s+"");
    }
}
