package com.kosmo.veve;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class F1_Home extends Fragment implements Runnable {

    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    ImageView user_file;
    Bitmap bitmap;

    boolean isLoading = false;

    ArrayList<GallaryBoard> gb_all_list = new ArrayList<>();
    ArrayList<GallaryBoard> gb_list = new ArrayList<>();

    private List<String> userID = new ArrayList<>();
    private List<String> gallary_no = new ArrayList<>();
    private List<String> f_name = new ArrayList<>();
    private List<String> title = new ArrayList<>();
    private List<String> content = new ArrayList<>();
    private List<String> postdate = new ArrayList<>();



    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home,container,false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        user_file = getView().findViewById(R.id.bbs_file);

        Thread thread = new Thread(this);
        thread.start();

        recyclerView = getView().findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initAdapter();


    }


    private void firstData() {
        for (int a=0; a<gb_all_list.size(); a++) {
            gb_all_list.add(gb_all_list.get(a));
        }
        // 총 아이템에서 10개를 받아옴
        for (int i=0; i<10; i++) {
            gb_list.add(gb_list.get(i));
        }
    }

    private void dataMore() {
        Log.d(TAG, "dataMore: ");
        gb_list.add(null);
        recyclerViewAdapter.notifyItemInserted(gb_list.size() -1 );

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gb_list.remove(gb_list.size() -1 );
                int scrollPosition = gb_list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                for (int i=currentSize; i<nextLimit; i++) {
                    if (i == gb_all_list.size()) {
                        return;
                    }
                    gb_list.add(gb_all_list.get(i));
                }

                isLoading = false;
            }
        }, 2000);

    }


    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(gb_list);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // 리싸이클러뷰 이벤트시
    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: ");
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: ");

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == gb_list.size() -1 ) {
                        dataMore();
                        isLoading = true;
                        Toast.makeText(getContext(), "스크롤감지", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void getData() {
        // 임의의 데이터입니다.
        for(int i=0; i<gb_list.size(); i++){
            gallary_no.add(gb_list.get(i).getGallary_no());
            userID.add(gb_list.get(i).getUserID());
            title.add(gb_list.get(i).getTitle());
            content.add(gb_list.get(i).getContent());
            postdate.add(gb_list.get(i).getPostDate());
            gb_all_list.add(gb_list.get(i));
        }

        for (int i = 0; i < title.size(); i++) {
            // 각 List의 값들을 data 객체에 set
            GallaryBoard data = new GallaryBoard();
            data.setUserID(userID.get(i));
            data.setTitle(title.get(i));
            data.setContent(content.get(i));
            data.setPostDate(postdate.get(i));
            data.setGallary_no(gallary_no.get(i));
            // 각 값이 들어간 data를 adapter에 추가
            recyclerViewAdapter.addItem(data);
        }


    }

    public void run() {
        try {
            JSONObject JsonList = new JSONObject();

            // http client 객체
            HttpClient http = new DefaultHttpClient();

            //주소설정
            HttpPost httpPost = new HttpPost("http://192.168.219.184:8080/veve/Gallary/AndroidList");

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
                gb.setUserID(row.getString("userId"));
                gb.setF_name(row.getString("f_name"));
                gb.setTitle(row.getString("title"));
                gb.setContent(row.getString("content"));
                gb.setPostDate(row.getString("postDate"));

                // ArrayList에 add
                gb_list.add(gb);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}