package com.kosmo.veve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class F5_MyPage extends Fragment {

    private String userId;
    private String password;

    private View view;
    private Button btn_profile_edit,btn_bookmark;
    private ImageView profile_img;

    String imgUrl = "http://192.168.219.184:8080/veve/upload/";
    Bitmap bitmap;
    back task;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_page,container,false);

        btn_profile_edit = view.findViewById(R.id.btn_edit_profile);
        btn_bookmark = view.findViewById(R.id.btn_bookmark);

        profile_img = view.findViewById(R.id.profile_img);

        task = new back();
        task.execute(imgUrl+"test12.jpg");
        btn_profile_edit.setOnClickListener(listener);

        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_edit_profile){
                Intent intent = new Intent(getActivity(),MyPage_Edit_Profile.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_bookmark){
                //Intent intent = new Intent(getActivity(),)
            }
        }
    };

    private class back extends AsyncTask<String, Integer,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL myFileUrl = new URL("http://192.168.219.184:8080/veve/upload/test12.jpg");
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);


            }catch(IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap img){
            profile_img.setImageBitmap(bitmap);
        }

    }
}
