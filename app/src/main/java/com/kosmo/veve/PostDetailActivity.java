package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kosmo.veve.http.UrlCollection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PostDetailActivity extends AppCompatActivity {

    private TextView userId;
    private ImageView bbsFile;
    private TextView bbsTitle;
    private TextView bbsContent;
    private TextView bbsPostdate;
    private ImageView btn_menu;

    private Intent intent;

    private String login_id;
    private String gallary_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        SharedPreferences preferences = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        login_id = preferences.getString("userId",null);

        intent = getIntent();
        gallary_no = intent.getStringExtra("gallary_no");
        String userID = intent.getStringExtra("userID");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String postDate = intent.getStringExtra("postDate");
        String f_name = intent.getStringExtra("f_name");

        initView();

        if(!userID.equals(login_id)) {
            btn_menu.setVisibility(View.INVISIBLE);
        }

        userId.setText(userID);
        bbsTitle.setText(title);
        bbsContent.setText(content);
        bbsPostdate.setText(postDate);
        new DownloadFilesTask(f_name,bbsFile).execute();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(),v);
                getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1){

                        }else if (menuItem.getItemId() == R.id.action_menu2){
                            new DeleteAsyncTask().execute(
                                    UrlCollection.GALLERY_DELETE,
                                    gallary_no
                            );
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private class DeleteAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute() {

        }///////////onPreExecute

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buf = new StringBuffer();
            try {
                URL url = new URL(String.format("%s?gallary_no=%s",params[0],params[1]));
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                //서버에 요청 및 응답코드 받기
                int responseCode=conn.getResponseCode();
                if(responseCode ==HttpURLConnection.HTTP_OK){
                    //연결된 커넥션에서 서버에서 보낸 데이타 읽기
                    BufferedReader br =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String line;
                    while((line=br.readLine())!=null){
                        buf.append(line);
                    }
                    br.close();
                }
            }
            catch(Exception e){e.printStackTrace();}

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return buf.toString();
        }///////////doInBackground

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(PostDetailActivity.this,MainPage.class);
            startActivity(intent);

        }
    }///////////////LoginAsyncTask

    /*private void delete(){
        try{

            //요청바디 설정
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    //파라미터명은 picture
                    .addFormDataPart("gallry_no", gallary_no)
                    .build();
            //요청 객체 생성
            Request request = new Request.Builder()
                    .url(UrlCollection.GALLERY_DELETE)
                    .post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            //비동기로 요청 보내기
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                //서버로부터 응답받는 경우
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.d("com.kosmo.veve", response.body().string());
                    Intent intent = new Intent(Example.this, MainPage.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),edtId.getText().toString()+"님 회원가입이 완료되었습니다.",Toast.LENGTH_SHORT);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/



    private void initView() {
        userId = (TextView) findViewById(R.id.user_id);
        bbsFile = (ImageView) findViewById(R.id.bbs_file);
        bbsTitle = (TextView) findViewById(R.id.bbs_title);
        bbsContent = (TextView) findViewById(R.id.bbs_content);
        bbsPostdate = (TextView) findViewById(R.id.bbs_postdate);
        btn_menu = (ImageView) findViewById(R.id.menu);
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