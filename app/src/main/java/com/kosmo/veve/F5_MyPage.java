package com.kosmo.veve;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kakao.util.helper.log.Tag;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Detail;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Feed;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Nutrient;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Scrap;
import com.kosmo.veve.F5_MyPage_Fragment.FollowList;
import com.kosmo.veve.F5_MyPage_Fragment.FollowingList;
import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.MyPage;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class F5_MyPage extends Fragment implements Runnable{

    FragmentManager fm;
    FragmentTransaction tran;

    F5_MyPage_Feed f5_myPage_feed;
    F5_MyPage_Scrap f5_myPage_scrap;
    F5_MyPage_Nutrient f5_myPage_nutrient;
    F5_MyPage_Detail f5_myPage_detail;

    private View view;
    private ImageView user_profile_img,my_feed,my_scrap,my_nutrient;
    private TextView posts,follower_count,following_count,fullname,bio;
    private Button edit_profile;

    private List<Fragment> fragments = new Vector<>();
    private String userId;

    ArrayList<MyPage> myPages = new ArrayList<>();
    ArrayList<GallaryBoard> follow_list = new ArrayList<>();
    ArrayList<GallaryBoard> following_list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_page,container,false);

        posts = view.findViewById(R.id.posts);
        follower_count = view.findViewById(R.id.follower_count);
        following_count = view.findViewById(R.id.following_count);
        fullname = view.findViewById(R.id.user_id);
        bio = view.findViewById(R.id.bio);
        edit_profile = view.findViewById(R.id.edit_profile);

        user_profile_img = view.findViewById(R.id.user_profile_img);

        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);
        fullname.setText(userId);

        SharedPreferences preferences2 = view.getContext().getSharedPreferences("postInfo", Context.MODE_PRIVATE);
        String postCount = preferences2.getString("postCount",null);
        String followCount = preferences2.getString("followCount",null);
        String followingCount = preferences2.getString("followingCount",null);
        String f_name = preferences2.getString("f_name",null);

        posts.setText(postCount);
        follower_count.setText(followCount);
        following_count.setText(followingCount);
        bio.setText("hello");


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("Edit Profile")){

                }
            }
        });

        my_feed = view.findViewById(R.id.my_feed);
        my_scrap = view.findViewById(R.id.my_scrap);
        my_nutrient = view.findViewById(R.id.my_nutrient);

        follower_count.setOnClickListener(listener);
        following_count.setOnClickListener(listener);
        my_feed.setOnClickListener(listener);
        my_scrap.setOnClickListener(listener);
        my_nutrient.setOnClickListener(listener);

        f5_myPage_feed = new F5_MyPage_Feed();
        f5_myPage_scrap = new F5_MyPage_Scrap();
        f5_myPage_nutrient = new F5_MyPage_Nutrient();

        setFrag(0);

        //task = new back();
        //task.execute(imgUrl+"test12.jpg");
        //btn_profile_edit.setOnClickListener(listener);

        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.my_feed:
                    setFrag(0);
                    break;
                case R.id.my_scrap:
                    setFrag(1);
                    break;
                case R.id.my_nutrient:
                    setFrag(2);
                    break;
                case R.id.follower_count:
                    showFollower();
                    break;
                case R.id.following_count:
                    showFollowing();
                    break;
            }
        }
    };

    public void showFollower(){
        Intent intent = new Intent(getActivity(), FollowList.class);
        startActivity(intent);
    }

    public void showFollowing(){
        Intent intent = new Intent(getActivity(), FollowingList.class);
        startActivity(intent);
    }

    public void setFrag(int n){
        fm = getFragmentManager();
        tran = fm.beginTransaction();
        switch (n){
            case 0:
                tran.replace(R.id.mypage_view, f5_myPage_feed);
                tran.commit();
                break;
            case 1:
                tran.replace(R.id.mypage_view, f5_myPage_scrap);
                tran.commit();
                break;
            case 2:
                tran.replace(R.id.mypage_view, f5_myPage_nutrient);
                tran.commit();
                break;
        }
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

            SharedPreferences preferences = getActivity().getSharedPreferences("postInfo",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("postcount", myPages.get(0).getPostCount());
            editor.putString("followCount",myPages.get(0).getFollowCount());
            editor.putString("followingCount",myPages.get(0).getFollowingCount());
            editor.putString("f_name",myPages.get(0).getF_name());
            editor.commit();

            getActivity().runOnUiThread(new Runnable() {
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
}
