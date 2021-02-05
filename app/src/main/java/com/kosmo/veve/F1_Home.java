package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.GallaryLike;
import com.kosmo.veve.dto.GallaryScrap;
import com.kosmo.veve.http.UrlCollection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class F1_Home extends Fragment implements Runnable {

    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    F1_RecyclerViewAdapter recyclerViewAdapter;

    ImageView userfile;

    ArrayList<GallaryBoard> gb_list = new ArrayList<>();
    ArrayList<GallaryLike> glb_list = new ArrayList<>();
    ArrayList<GallaryScrap> gs_list = new ArrayList<>();

    int check=0;
    private String login_id;


    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_home,container,false);

        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        login_id = preferences.getString("userId",null);

        if(check == 0){

            userfile = view.findViewById(R.id.bbs_file);


            Thread thread = new Thread(this);
            thread.start();

            recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            initAdapter();

            check++;

        }else{
            userfile = view.findViewById(R.id.bbs_file);

            recyclerView = view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            initAdapter();

        }

        return view;
    }


    private void initAdapter() {
        recyclerViewAdapter = new F1_RecyclerViewAdapter(gb_list,glb_list,gs_list,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void run() {
        try {
            JSONObject JsonList = new JSONObject();

            // http client 객체
            HttpClient http = new DefaultHttpClient();

            //주소설정
            HttpPost httpPost = new HttpPost(UrlCollection.ANDROIDLIST);

            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);

            //서버에서 중복된 아이디 찾기, excute는 한번만 하자
            HttpResponse response = http.execute(httpPost);

            String body = EntityUtils.toString(response.getEntity());

            JSONObject obj = new JSONObject(body);

            JSONArray jArray = (JSONArray) obj.get("sendData");

            for (int i = 0; i < jArray.length(); i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                GallaryBoard gb = new GallaryBoard();
                GallaryLike glb = new GallaryLike();
                GallaryScrap gs = new GallaryScrap();
                gb.setGallary_no(row.getString("gallary_no"));
                glb.setGallary_no(row.getString("gallary_no"));
                gs.setGallary_no(row.getString("gallary_no"));
                gb.setUserID(row.getString("userId"));
                glb.setUserID(row.getString("userId"));
                gs.setUserID(row.getString("userId"));
                gb.setTitle(row.getString("title"));
                gb.setContent(row.getString("content"));
                gb.setF_name(row.getString("f_name"));
                gb.setHeartCount(row.getInt("visitCount"));
                gb.setScrapCount(row.getInt("scrapCount"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일");
                String date = (String) formatter.format(new Timestamp(Long.parseLong(row.getString("postDate"))));
                gb.setPostDate(date);

                // ArrayList에 add
                gb_list.add(gb);
                glb_list.add(glb);
                gs_list.add(gs);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}