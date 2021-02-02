package com.kosmo.veve;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Feed;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Nutrient;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Scrap;
import com.kosmo.veve.F5_MyPage_Fragment.FollowList;
import com.kosmo.veve.F5_MyPage_Fragment.FollowingList;
import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.MyPage;
import com.kosmo.veve.http.RequestHttpURLConnection;
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

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class OtherHomeActivity extends AppCompatActivity implements Runnable{

    private FragmentManager fragmentManager = getSupportFragmentManager();

    F5_MyPage_Feed f5_myPage_feed;
    F5_MyPage_Scrap f5_myPage_scrap;
    F5_MyPage_Nutrient f5_myPage_nutrient;

    private View view;
    private ImageView user_profile_img,my_feed,my_scrap,my_nutrient;
    private TextView posts,follower_count,following_count,fullname,bio;
    private Button follow_btn;

    private List<Fragment> fragments = new Vector<>();
    private String userId,otherId;
    private Intent intent;

    ArrayList<MyPage> myPages = new ArrayList<>();
    ArrayList<GallaryBoard> follow_list = new ArrayList<>();
    ArrayList<GallaryBoard> following_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_home);

        posts = findViewById(R.id.posts);
        follower_count = findViewById(R.id.follower_count);
        following_count = findViewById(R.id.following_count);
        fullname = findViewById(R.id.user_id);
        bio = findViewById(R.id.bio);
        follow_btn = findViewById(R.id.follow_btn);

        user_profile_img = findViewById(R.id.user_profile_img);

        intent = getIntent();
        userId = intent.getStringExtra("userID");

        SharedPreferences preferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        otherId = preferences.getString("userId",null);

        if(userId.equals(otherId)){
            follow_btn.setVisibility(View.INVISIBLE);
        }

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* DB 대조 */
                ContentValues values = new ContentValues();
                values.put("userID", userId);
                values.put("otherID", otherId);

                NetworkTask networkTask = new NetworkTask(UrlCollection.INSERTFOLLOW, values);
                networkTask.execute();
            }
        });

        fullname.setText(userId);

        /*SharedPreferences preferences2 = view.getContext().getSharedPreferences("postInfo", Context.MODE_PRIVATE);
        String postCount = preferences2.getString("postCount",null);
        String followCount = preferences2.getString("followCount",null);
        String followingCount = preferences2.getString("followingCount",null);
        String f_name = preferences2.getString("f_name",null);*/

        /*posts.setText(postCount);
        follower_count.setText(followCount);
        following_count.setText(followingCount);
        bio.setText("hello");*/

        my_feed = findViewById(R.id.my_feed);
        my_scrap = findViewById(R.id.my_scrap);
        my_nutrient = findViewById(R.id.my_nutrient);

        follower_count.setOnClickListener(listener);
        following_count.setOnClickListener(listener);
        my_feed.setOnClickListener(listener);
        my_scrap.setOnClickListener(listener);
        my_nutrient.setOnClickListener(listener);

        f5_myPage_feed = new F5_MyPage_Feed();
        f5_myPage_scrap = new F5_MyPage_Scrap();
        f5_myPage_nutrient = new F5_MyPage_Nutrient();

        Bundle bundle = new Bundle();
        bundle.putString("userID",userId);
        f5_myPage_feed.setArguments(bundle);
        f5_myPage_scrap.setArguments(bundle);
        f5_myPage_nutrient.setArguments(bundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mypage_view, f5_myPage_feed).commitAllowingStateLoss();

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.follower_count:
                    showFollower();
                    break;
                case R.id.following_count:
                    showFollowing();
                    break;
                case R.id.my_feed:
                    transaction.replace(R.id.mypage_view, f5_myPage_feed).commitAllowingStateLoss();
                    break;
                case R.id.my_scrap:
                    transaction.replace(R.id.mypage_view, f5_myPage_scrap).commitAllowingStateLoss();
                    break;
                case R.id.my_nutrient:
                    transaction.replace(R.id.mypage_view, f5_myPage_nutrient).commitAllowingStateLoss();
                    break;
            }
        }
    };

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.my_feed:
                    transaction.replace(R.id.mypage_view, f5_myPage_feed).commitAllowingStateLoss();

                    break;
                case R.id.my_scrap:
                    transaction.replace(R.id.mypage_view, f5_myPage_scrap).commitAllowingStateLoss();
                    break;
                case R.id.my_nutrient:
                    transaction.replace(R.id.mypage_view, f5_myPage_nutrient).commitAllowingStateLoss();
                    break;
            }
            return true;
        }


    }

    public void showFollower(){
        Intent intent = new Intent(this, FollowList.class);
        startActivity(intent);
    }

    public void showFollowing(){
        Intent intent = new Intent(this, FollowingList.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            // NameValuePair : 변수명과 값을 함께 저장하는 객체로 제공되는 객체이다.
            ArrayList<NameValuePair> postData = new ArrayList<>();
            // post 방식으로 전달할 값들을 postData 객체에 집어 넣는다.
            postData.add(new BasicNameValuePair("userID",userId));
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다.
            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData,"utf-8");
            HttpClient http = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(UrlCollection.MYPAGE);
            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(request);
            // post 방식으로 전송, 응답결과는 response로 넘어옴
            HttpResponse response = http.execute(httpPost);
            // response text를 스트링으로 변환
            String body = EntityUtils.toString(response.getEntity());
            // 스트링을 json으로 변환한다.
            JSONObject obj = new JSONObject(body);

            JSONObject JsonList = new JSONObject();
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);

            JSONArray jArray = (JSONArray) obj.get("sendData");

            for (int i = 0; i < 1; i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                MyPage myPage = new MyPage();

                myPage.setPostCount(row.getString("postCount"));
                myPage.setFollowCount(row.getString("followCount"));
                myPage.setFollowingCount(row.getString("followingCount"));
                myPage.setF_name(row.getString("f_name"));

                myPages.add(myPage);
                Log.d("파일 이름:",(row.getString("f_name")));
            }

            SharedPreferences preferences = this.getSharedPreferences("postInfo",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("postcount", myPages.get(0).getPostCount());
            editor.putString("followCount",myPages.get(0).getFollowCount());
            editor.putString("followingCount",myPages.get(0).getFollowingCount());
            editor.putString("f_name",myPages.get(0).getF_name());
            editor.commit();

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    posts.setText(myPages.get(0).getPostCount());
                    follower_count.setText(myPages.get(0).getFollowCount());
                    following_count.setText(myPages.get(0).getFollowingCount());
                    String image_url = myPages.get(0).getF_name();
                    loadImageTask imageTask = new loadImageTask(image_url);
                    imageTask.execute();
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class loadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {

        private String url;

        public loadImageTask(String url) {

            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            Bitmap imgBitmap = null;

            try {
                URL url1 = new URL(url);
                URLConnection conn = url1.openConnection();
                conn.connect();
                int nSize = conn.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
                imgBitmap = BitmapFactory.decodeStream(bis);
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imgBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bit) {
            super.onPostExecute(bit);
            user_profile_img.setImageBitmap(bit);
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String url;
        ContentValues values;

        NetworkTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }
}