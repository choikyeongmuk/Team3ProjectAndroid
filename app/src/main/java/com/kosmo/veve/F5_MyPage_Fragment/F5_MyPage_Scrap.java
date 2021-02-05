package com.kosmo.veve.F5_MyPage_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosmo.veve.F5_RecyclerViewAdapter;
import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.R;
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

public class F5_MyPage_Scrap extends Fragment implements Runnable{

    private View view;
    private String userId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_f5__my_page__scrap,container,false);

        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);

        return view;
    }

    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    F5_RecyclerViewAdapter recyclerViewAdapter;

    ImageView user_file;
    Bitmap bitmap;

    boolean isLoading = false;

    ArrayList<GallaryBoard> gb_all_list = new ArrayList<>();
    ArrayList<GallaryBoard> gb_list = new ArrayList<>();

    int check=0;

    @Override
    public void onStart() {
        super.onStart();

        if(check == 0){
            Thread thread = new Thread(this);
            thread.start();

            recyclerView = getView().findViewById(R.id.recycler_view_scrap);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            initAdapter();
            check++;
        }else{
            user_file = getView().findViewById(R.id.my_file);
            recyclerView = getView().findViewById(R.id.recycler_view_scrap);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

            initAdapter();
        }

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
        recyclerViewAdapter = new F5_RecyclerViewAdapter(gb_list);
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

    /*private void getData() {
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


    }*/

    public void run() {
        try {
            // NameValuePair : 변수명과 값을 함께 저장하는 객체로 제공되는 객체이다.
            ArrayList<NameValuePair> postData = new ArrayList<>();
            // post 방식으로 전달할 값들을 postData 객체에 집어 넣는다.
            postData.add(new BasicNameValuePair("userID",userId));
            //postData.add(new BasicNameValuePair("pw","패스워드"));
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다.
            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData,"utf-8");
            HttpClient http = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(UrlCollection.MYPAGESCRAPLIST);
            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(request);
            // post 방식으로 전송, 응답결과는 response로 넘어옴
            HttpResponse response = http.execute(httpPost);
            // response text를 스트링으로 변환
            String body = EntityUtils.toString(response.getEntity());
            //Log.d("???",EntityUtils.toString(response.getEntity()));
            // 스트링을 json으로 변환한다.
            JSONObject obj = new JSONObject(body);

            // 스프링 컨트롤러에서 리턴해줄 때 저장했던 값을 꺼냄
            //String message = obj.getString("message");

            JSONObject JsonList = new JSONObject();

            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);
            JSONArray jArray = (JSONArray) obj.get("sendData");

            for (int i = 0; i < jArray.length(); i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                GallaryBoard gb = new GallaryBoard();
                gb.setGallary_no(row.getString("gallary_no"));
                gb.setUserID(row.getString("userID"));
                gb.setTitle(row.getString("title"));
                gb.setContent(row.getString("content"));
                gb.setF_name(row.getString("f_name"));
                gb_list.add(gb);
                Log.d("나와",(row.getString("f_name")));
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}