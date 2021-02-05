package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kosmo.veve.dto.GallaryComment;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PostDetailActivity extends AppCompatActivity implements Runnable{

    private TextView userId;
    private ImageView bbsFile;
    private TextView bbsTitle;
    private TextView bbsContent;
    private TextView bbsPostdate;
    private ImageView btn_menu,btn_comment,like;

    private Intent intent;

    private String login_id,gallary_no,userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        SharedPreferences preferences = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        login_id = preferences.getString("userId",null);

        intent = getIntent();
        gallary_no = intent.getStringExtra("gallary_no");
        userID = intent.getStringExtra("userID");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String postDate = intent.getStringExtra("postDate");
        String f_name = intent.getStringExtra("f_name");

        initView();

        if(gallary_no!=null)
            Log.d("gallary_no",gallary_no);
        Log.d("userID",userID);

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
                            Intent intent = new Intent(PostDetailActivity.this,PostUpdate.class);
                            intent.putExtra("gallary_no",gallary_no);
                            intent.putExtra("title",title);
                            intent.putExtra("content",content);
                            startActivity(intent);
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

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this,PostComment.class);
                intent.putExtra("gallary_no",gallary_no);
                startActivity(intent);
            }
        });
    }

    @Override
    public void run() {
        /*try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            // post 방식으로 전달할 값들을 postData 객체에 집어 넣는다.
            //postData.add(new BasicNameValuePair("userID",sessionID));
            postData.add(new BasicNameValuePair("gallary_no", gallary_no));
            //postData.add(new BasicNameValuePair("pw","패스워드"));
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다.
            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData, "utf-8");
            HttpClient http = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(UrlCollection.COMMENTLIST);
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
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);

            JSONArray jArray = (JSONArray) obj.get("sendData");
            for (int i = 0; i < jArray.length(); i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                GallaryComment gc = new GallaryComment();
                //GallaryBoard gb = new GallaryBoard();
                gc.setGallary_no(row.getString("gallary_no"));
                gc.setUserID(row.getString("userID"));
                gc.setContent(row.getString("content"));
                gc.setCPostdate(row.getString("postdate"));
                gc.setF_name(row.getString("f_name"));

                // ArrayList에 add
                //gbList.add(gb);
                gcList.add(gc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
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

    private void initView() {
        userId = (TextView) findViewById(R.id.user_id);
        bbsFile = (ImageView) findViewById(R.id.bbs_file);
        bbsTitle = (TextView) findViewById(R.id.bbs_title);
        bbsContent = (TextView) findViewById(R.id.bbs_content);
        bbsPostdate = (TextView) findViewById(R.id.bbs_postdate);
        btn_menu = (ImageView) findViewById(R.id.menu);
        btn_comment = (ImageView) findViewById(R.id.comment);
        like = (ImageView) findViewById(R.id.like);
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