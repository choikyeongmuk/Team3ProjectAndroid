package com.kosmo.veve.F5_MyPage_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.kosmo.veve.F5_RecyclerViewAdapter;
import com.kosmo.veve.R;
import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.http.UrlCollection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowList extends AppCompatActivity implements Runnable{

    private ImageView back;
    private ImageView user_profile;
    private TextView userId;
    private TextView follow_id;
    private RecyclerView recyclerViewFollow;
    Context mContext;

    private String userID;

    int check=0;

    RecyclerView recyclerView;
    FollowList_RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<GallaryBoard> follow_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);
        initView();

        SharedPreferences preferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userID = preferences.getString("userId",null);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(check == 0){

            Thread thread = new Thread(this);
            thread.start();

            recyclerView = findViewById(R.id.recycler_view_follow);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            initAdapter();
            check++;
        }else{
            user_profile = findViewById(R.id.user_profile);

            recyclerView = findViewById(R.id.recycler_view_follow);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            initAdapter();
        }

    }

    private void initAdapter() {
        recyclerViewAdapter = new FollowList_RecyclerViewAdapter(follow_list);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.btn_back);
        userId = (TextView) findViewById(R.id.user_id);
        recyclerViewFollow = (RecyclerView) findViewById(R.id.recycler_view_follow);
    }

    @Override
    public void run() {
        try {
            // NameValuePair : 변수명과 값을 함께 저장하는 객체로 제공되는 객체이다.
            ArrayList<NameValuePair> postData = new ArrayList<>();
            // post 방식으로 전달할 값들을 postData 객체에 집어 넣는다.
            postData.add(new BasicNameValuePair("userID", userID));
            //postData.add(new BasicNameValuePair("pw","패스워드"));
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다.
            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");
            HttpClient http = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(UrlCollection.FOLLOWLIST);
            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(request);
            // post 방식으로 전송, 응답결과는 response로 넘어옴
            HttpResponse response = http.execute(httpPost);
            // response text를 스트링으로 변환
            String body = EntityUtils.toString(response.getEntity());
            // 스트링을 json으로 변환한다.
            JSONObject obj = new JSONObject(body);

            // 스프링 컨트롤러에서 리턴해줄 때 저장했던 값을 꺼냄
            //String message = obj.getString("message");

            JSONObject JsonList = new JSONObject();
            // http client 객체
            //HttpClient http = new DefaultHttpClient();

            //주소설정
            //HttpPost httpPost = new HttpPost("http://192.168.219.184:8080/veve/Gallary/MyList");

            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);

            JSONArray jArray = (JSONArray) obj.get("sendData");
            Log.i("옴?","");

            for (int i = 0; i < jArray.length(); i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                GallaryBoard gb = new GallaryBoard();
                gb.setUserID(row.getString("userID"));
                gb.setF_name(row.getString("f_name"));
                follow_list.add(gb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}