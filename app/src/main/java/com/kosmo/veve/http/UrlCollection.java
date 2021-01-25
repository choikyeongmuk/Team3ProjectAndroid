package com.kosmo.veve.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class UrlCollection{
    public static String SERVER_URL = "http://192.168.219.184:8080/veve";

    public static String SIGNUP = SERVER_URL + "/member/signUp";
    public static String SIGNUPKAKAO = SERVER_URL + "/member/signUpKakao";
    public static String GALLERY_POST = SERVER_URL + "/gallary/post";
    public static String ANDROIDLIST = SERVER_URL + "/gallary/androidList";

    public static String MYPAGE = SERVER_URL +"/mypage";
    public static String MYPAGELIST = SERVER_URL + "/mypage/list";
    public static String MYPAGESCRAPLIST = SERVER_URL +"/mypage/scrapList";

}
