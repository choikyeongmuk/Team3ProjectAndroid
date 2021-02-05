package com.kosmo.veve;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosmo.veve.dto.GallaryComment;
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

import java.util.ArrayList;
import java.util.List;

public class PostComment extends AppCompatActivity implements Runnable {

    private ImageView btn_back, comment;
    private EditText user_comment;
    private Button comment_btn;

    String userId, gallary_no;

    private RecyclerView recyclerView_comment;
    private CommentRecycleAdapter comment_adapter;

    public List<GallaryComment> gcList = new ArrayList<>();
    ;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        intent = getIntent();
        gallary_no = intent.getStringExtra("gallary_no");

        SharedPreferences preferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);

        initView();

        Thread thread = new Thread(this);
        thread.start();

        recyclerView_comment = findViewById(R.id.comment_recyclerview);
        recyclerView_comment.setLayoutManager(new LinearLayoutManager(this));

        initAdapter();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* DB 대조 */
                ContentValues values = new ContentValues();
                values.put("userID", userId);
                values.put("gallary_no", gallary_no);
                values.put("content",user_comment.getText().toString());

                NetworkTask networkTask = new NetworkTask(UrlCollection.COMMENT_POST, values);
                networkTask.execute();
            }
        });
    }

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        user_comment = (EditText) findViewById(R.id.userComment);
        comment_btn = findViewById(R.id.comment_btn);

    }

    private void initAdapter() {
        comment_adapter = new CommentRecycleAdapter(gcList);
        recyclerView_comment.setAdapter(comment_adapter);
    }

    public void idCheck(){

    }

    public void run() {
        try {
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