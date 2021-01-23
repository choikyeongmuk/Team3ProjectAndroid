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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Adapter;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Feed;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Nutrient;
import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Scrap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;


public class F5_MyPage extends Fragment {

    private View view;
    private ImageView user_profile_img;
    private TextView posts,follower_count,following_count,fullname,bio;
    private Button edit_profile;

    private TabLayout tabLayout;
    private ViewPager viewPager;
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

        String userId;
        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);
        fullname.setText(userId);

        posts.setText("5");
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

        /*tabLayout = view.findViewById(R.id.tabLayout);
        //viewPager = view.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));

        F5_MyPage_Feed tabContent1 = new F5_MyPage_Feed();
        fragments.add(tabContent1);
        F5_MyPage_Scrap tabContent2 = new F5_MyPage_Scrap();
        fragments.add(tabContent2);
        F5_MyPage_Nutrient tabContent3 = new F5_MyPage_Nutrient();
        fragments.add(tabContent3);

        F5_MyPage_Adapter myPageAdapter = new F5_MyPage_Adapter(getActivity().getSupportFragmentManager(),fragments);
        viewPager.setAdapter(myPageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
*/
        //task = new back();
        //task.execute(imgUrl+"test12.jpg");
        //btn_profile_edit.setOnClickListener(listener);

        return view;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*if(v.getId() == R.id.btn_edit_profile){
                Intent intent = new Intent(getActivity(),MyPage_Edit_Profile.class);
                startActivity(intent);
            }*/
        }
    };

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
