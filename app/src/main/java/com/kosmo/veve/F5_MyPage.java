package com.kosmo.veve;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Feed;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Nutrient;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Scrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;


public class F5_MyPage extends Fragment {

    FragmentManager fm;
    FragmentTransaction tran;

    F5_MyPage_Feed f5_myPage_feed;
    F5_MyPage_Scrap f5_myPage_scrap;
    F5_MyPage_Nutrient f5_myPage_nutrient;

    private View view;
    private ImageView user_profile_img,my_feed,my_scrap,my_nutrient;
    private TextView posts,follower_count,following_count,fullname,bio;
    private Button edit_profile;

    private List<Fragment> fragments = new Vector<>();

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
        String userId = preferences.getString("userId",null);
        fullname.setText(userId);

        SharedPreferences preferences2 = view.getContext().getSharedPreferences("postInfo", Context.MODE_PRIVATE);
        String postcount = preferences2.getString("postcount",null);
        posts.setText(postcount);
        follower_count.setText("5");
        following_count.setText("5");
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
            }
        }
    };

    public void setFrag(int n){    //프래그먼트를 교체하는 작업을 하는 메소드를 만들었습니다
        fm = getFragmentManager();
        tran = fm.beginTransaction();
        switch (n){
            case 0:
                tran.replace(R.id.mypage_view, f5_myPage_feed);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
            case 1:
                tran.replace(R.id.mypage_view, f5_myPage_scrap);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
            case 2:
                tran.replace(R.id.mypage_view, f5_myPage_nutrient);  //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
                tran.commit();
                break;
        }
    }

    private class back extends AsyncTask<String, Integer,Bitmap> {
        Bitmap bitmap ;

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{

                URL myFileUrl = new URL("http://192.168.219.184:8080/veve/upload/test12.jpg");
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                posts.setText("가져온 값 셋팅");
                follower_count.setText("가져온 값 셋팅");
                following_count.setText("가져온 값 셋팅");


            }catch(IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap img){
            user_profile_img.setImageBitmap(bitmap);
        }

    }
}
