package com.kosmo.veve;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kosmo.veve.http.UrlCollection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class F2_Management extends Fragment {

    private String userId;
    private String password;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("com.kosmo.kosmoapp","onAttach:4");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);

        userId=preferences.getString("userId",null);
        password=preferences.getString("password",null);
        Log.i("com.kosmo.kosmoapp",userId+":"+password);
    }

    //2]onCreateView()오버 라이딩
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("com.kosmo.kosmoapp","onCreateView:4");

        //프래그먼트 레이아웃 전개
        View view=inflater.inflate(R.layout.fragment_management,null,false);
        //웹뷰 얻기]
        WebView webView = view.findViewById(R.id.webview);
        //WebView설정]
        //1]WebView의 getSettings()메소드로 WebSettings객체
        WebSettings settings=webView.getSettings();
        //자스가 실행되도록 설정- 기본적으로 웹뷰는 자스를 지원하지 않음]
        settings.setJavaScriptEnabled(true);//필수 설정
        // 아래부분 생략시 웹뷰가 전체 레이아웃을 차지함(사이트 로드시)]
        webView.setWebViewClient(new CustomWebViewClient());
        //자스의 alert()모양을 Toast 로 변경
        webView.setWebChromeClient(new CustomWebChromeClient());
        //get요청
        //webView.loadUrl("http://hwanyhee.iptime.org:8080/onememo/");
        //post요청
        try {
            String params = "userId=" + URLEncoder.encode(userId, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            //webView.postUrl("http://192.168.219.184:8080/veve/Member/Auth/Login.do", params.getBytes());
            webView.postUrl("http://192.168.0.104:8080/veve/Member/Auth/Login2.do", params.getBytes());
            //webView.loadUrl("http://192.168.0.104:8080/veve/Member/MemberDiet.do");
        }
        catch(UnsupportedEncodingException e){e.printStackTrace();}

        return view;
    }/////////
    //WebViewClient상속]-웹 페이지 로딩 담당
    private class CustomWebViewClient extends WebViewClient{
        //오버라이딩만하면 된다]
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }////////////
    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //경고 메시지를 Toast로 보여주기
            Toast.makeText(view.getContext(),message,Toast.LENGTH_SHORT).show();
            //자바스크립트 경고창의 확인버튼을 클릭한것으로 처리하도록 호출
            //해야한다 alert()는 모달이라 클릭한 것으로 처리안하면
            //다른 메뉴를 클릭 할 수 없다
            result.confirm();
            return true;
        }
    }
}