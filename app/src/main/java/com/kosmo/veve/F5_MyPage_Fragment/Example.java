package com.kosmo.veve.F5_MyPage_Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kosmo.veve.F1_Home;
import com.kosmo.veve.F1_RecyclerViewAdapter;
import com.kosmo.veve.F2_Management;
import com.kosmo.veve.F3_Feed;
import com.kosmo.veve.F4_Search;
import com.kosmo.veve.F5_MyPage;
import com.kosmo.veve.MainPage;
import com.kosmo.veve.R;

import java.net.URL;
import java.util.HashMap;

public class Example extends AppCompatActivity {

    private TextView userId;
    private ImageView bbsFile;
    private TextView bbsTitle;
    private TextView bbsContent;
    private TextView bbsPostdate;

    private Intent intent;

    private F1_Home f1Home = new F1_Home();
    private F2_Management f2Management = new F2_Management();
    private F3_Feed f3Feed = new F3_Feed();
    private F4_Search f4Search = new F4_Search();
    private F5_MyPage f5MyPage = new F5_MyPage();

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String postDate = intent.getStringExtra("postDate");
        String f_name = intent.getStringExtra("f_name");

        initView();
        userId.setText(userID);
        bbsTitle.setText(title);
        bbsContent.setText(content);
        bbsPostdate.setText(postDate);
        new DownloadFilesTask(f_name,bbsFile).execute();

    }



    private void initView() {
        userId = (TextView) findViewById(R.id.user_id);
        bbsFile = (ImageView) findViewById(R.id.bbs_file);
        bbsTitle = (TextView) findViewById(R.id.bbs_title);
        bbsContent = (TextView) findViewById(R.id.bbs_content);
        bbsPostdate = (TextView) findViewById(R.id.bbs_postdate);
    }

    private static class DownloadFilesTask extends AsyncTask<String,Void, Bitmap> {
        private String urlStr;
        private ImageView imageView;
        private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

        public DownloadFilesTask(String urlStr, ImageView imageView) {
            this.urlStr = urlStr;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... voids) {
            Bitmap bitmap = null;
            try {
                if (bitmapHash.containsKey(urlStr)) {
                    if(bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                URL url = new URL(urlStr);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                bitmapHash.put(urlStr,bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
    }
}