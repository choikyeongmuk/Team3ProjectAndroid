package com.kosmo.veve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kosmo.veve.http.UrlCollection;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private EditText edtId;
    private EditText edtPwd;
    private Button findBtn;
    private Button btnLogin;
    private ImageButton btn_kakao_login;
    private ImageButton btn_naver_login;
    private ImageButton btn_email_login;

    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    private WebView mWebView;
    private WebSettings mWebSettings;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("signup");
        if(msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }

        if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
            String notificationData = intent.getStringExtra("test");
            if(notificationData != null)
                Log.d("FCM_TEST", notificationData);
        }

        btn_kakao_login = (ImageButton)findViewById(R.id.btn_kakao_login);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        btn_kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.open(AuthType.KAKAO_LOGIN_ALL, Login.this);
                session.addCallback(sessionCallback);
                session.checkAndImplicitOpen();
            }
        });

        /*mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(UrlCollection.SERVER_URL+"/Member/Auth/Login.do");
        String url = mWebView.getUrl();

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(url);

        Connection.Response re = Jsoup.connect("http://192.168.219.184:8080/veve/Member/MemberDiet.do")
                .header("Cookie", cookies)//쿠키값 전달
                .execute();
*/
        initView();
    }

    private class SessionCallback implements ISessionCallback {

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                        }

                        @Override
                        public void onSuccess(MeV2Response result) {
                            Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {

                                // 이메일
                                String email = kakaoAccount.getEmail();

                                if (email != null) {
                                    Log.i("KAKAO_API", "email: " + email);

                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 이메일 획득 가능
                                    // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                                } else {
                                    // 이메일 획득 불가
                                }

                                // 프로필
                                Profile profile = kakaoAccount.getProfile();

                                if (profile != null) {
                                    Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                    Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());
                                    Log.d("KAKAO_API","gender: "+kakaoAccount.getGender().toString());
                                    Log.d("KAKAO_API","age: "+kakaoAccount.getAgeRange().toString().split("_")[2]);
                                    Log.d("KAKAO_API","profile_image: "+kakaoAccount.getProfile().getProfileImageUrl());

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 프로필 정보 획득 가능

                                } else {
                                    // 프로필 획득 불가
                                }
                            }
                            try{
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("userID", kakaoAccount.getEmail())
                                        .addFormDataPart("password", "1")
                                        .addFormDataPart("nickname", kakaoAccount.getProfile().getNickname())
                                        .addFormDataPart("name", kakaoAccount.getProfile().getNickname())
                                        .addFormDataPart("gender", kakaoAccount.getGender().toString())
                                        .addFormDataPart("vg_level", "비건레벨 임의 값")
                                        .addFormDataPart("addr", "주소 임의 값")
                                        .addFormDataPart("selfintro", "자기소개 임의 값")
                                        .addFormDataPart("age", kakaoAccount.getAgeRange().toString().split("_")[2])
                                        .addFormDataPart("k1n0", "1")
                                        .addFormDataPart("f_name", kakaoAccount.getProfile().getProfileImageUrl())
                                        .build();
                                //요청 객체 생성
                                Request request = new Request.Builder()
                                        .url(UrlCollection.SIGNUPKAKAO)
                                        .post(requestBody)
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                //비동기로 요청 보내기
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        e.printStackTrace();
                                        Log.d("카카오 회원가입 오류","??");
                                    }

                                    //서버로부터 응답받는 경우
                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        //Log.d("com.kosmo.veve", response.body().string());
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(getApplicationContext(),MainPage.class);
                            intent.putExtra("data",kakaoAccount.getEmail()+"님 회원가입이 완료되었습니다.");
                            startActivity(intent);
                        }
                    });
        }
    }



        @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        edtId = (EditText) findViewById(R.id.edt_id);
        edtPwd = (EditText) findViewById(R.id.edt_pwd);
        findBtn = (Button) findViewById(R.id.find_btn);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btn_kakao_login = (ImageButton) findViewById(R.id.btn_kakao_login);
        btn_naver_login = (ImageButton) findViewById(R.id.btn_naver_login);
        btn_email_login = (ImageButton) findViewById(R.id.btn_email_login);

        btn_email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginAsyncTask().execute(
                        UrlCollection.LOGIN,
                        edtId.getText().toString(),
                        edtPwd.getText().toString());

            }
        });
    }

    private class LoginAsyncTask extends AsyncTask<String,Void,String>{

        private AlertDialog progressDialog;
        ProgressDialog asyncDialog = new ProgressDialog(Login.this);
        @Override
        protected void onPreExecute() {
            //프로그래스바용 다이얼로그 생성]
            //빌더 생성 및 다이얼로그창 설정
            /*AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setCancelable(false);
            //builder.setView(R.layout.progress);
            builder.setIcon(android.R.drawable.ic_menu_compass);
            builder.setTitle("로그인");*/
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로그인중입니다...");

            asyncDialog.show();

            //빌더로 다이얼로그창 생성
            //progressDialog = builder.create();
            //progressDialog.show();
        }///////////onPreExecute

        @Override
        protected String doInBackground(String... params) {
            StringBuffer buf = new StringBuffer();
            try {
                URL url = new URL(String.format("%s?userID=%s&password=%s",params[0],params[1],params[2]));
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

            //서버로부터 받은 데이타(JSON형식) 파싱
            //회원이 아닌 경우 빈 문자열
            if(result !=null && result.length()!=0) {//회원인 경우
                try {

                    JSONObject json = new JSONObject(result);
                    String name = json.getString("name");
                    Intent intent = new Intent(Login.this,MainPage.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    //finish()불필요-NO_HISTORY로 설정했기때문에(매니페스트에서)
                    //아이디 비번저장
                    SharedPreferences preferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor =preferences.edit();
                    editor.putString("userId",edtId.getText().toString());
                    editor.putString("password",edtPwd.getText().toString());
                    editor.commit();

                }
                catch(Exception e){e.printStackTrace();}

            }
            else{//회원이 아닌 경우
                Toast.makeText(Login.this,"아이디와 비번이 일치하지 않아요",Toast.LENGTH_SHORT).show();
            }

            /*//다이얼로그 닫기
            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();*/
            asyncDialog.dismiss();

        }
    }///////////////LoginAsyncTask

}
