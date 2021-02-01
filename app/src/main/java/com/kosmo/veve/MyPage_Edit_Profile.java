package com.kosmo.veve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kosmo.veve.http.UrlCollection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPage_Edit_Profile extends AppCompatActivity {

    private ImageView btn_back;
    private ImageView profileImg;
    private EditText updateNickname;
    private EditText updatePwd;
    private EditText updatePwdCheck;
    private Button btn_update;

    String photoImagePath,userId;

    UUID uuid = UUID.randomUUID();
    String fileName = uuid.toString();

    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page__edit__profile);

        SharedPreferences preferences = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);

        initView();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+fileName+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateInfoToServer(file);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE){
            //갤러리에 있는 이미지를 이미지뷰에 표시하기
            if(resultCode== Activity.RESULT_OK){
                if(data== null){
                    return;
                }

                Uri selectedImageUri=data.getData();
                //선택된 이미지의 Uri로 이미지뷰에 표시
                profileImg.setImageURI(selectedImageUri);
                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Log.d("",file.toString());
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+fileName+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    BufferedOutputStream bos = null;
                    try {
                        bos = new BufferedOutputStream(
                                new FileOutputStream(file));

                        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);//이미지가 용량이 클 경우
                        //OutOfMemoryException 발생할수 있음.그래서 압축
                        //사진을 앨범에 보이도록 갤러리앱에 방송을 보내기
                        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        bos.flush();
                        bos.close();
                        //https://square.github.io/okhttp/
                        //1.그레이들에 okhttp3라이브러리 추가
                        //서버로 전송하기
                        //sendImageToServer(file);
                        //updateInfoToServer(file);
                    }
                    catch(Exception e){e.printStackTrace();}
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }///////////////

    private void initView() {
        btn_back = (ImageView) findViewById(R.id.btn_back);
        profileImg = (ImageView) findViewById(R.id.profile_img);
        updateNickname = (EditText) findViewById(R.id.update_nickname);
        updatePwd = (EditText) findViewById(R.id.update_pwd);
        updatePwdCheck = (EditText) findViewById(R.id.update_pwd_check);
        btn_update = (Button) findViewById(R.id.btn_update);
    }

    private void updateInfoToServer(File file){

        try {
            if (updateNickname.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                updateNickname.requestFocus();
            } else if (updatePwd.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                updatePwd.requestFocus();
            } else if (updatePwd.getText().toString().length() < 8 || updatePwd.getText().toString().length() > 16) {
                Toast.makeText(getApplicationContext(), "비밀번호는 8~16자의 영문 소문자, 숫자와 특수기호(-,_)만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                updatePwd.requestFocus();
            } else if (updatePwdCheck.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                updatePwdCheck.requestFocus();
            } else if (!updatePwdCheck.getText().toString().equalsIgnoreCase(updatePwdCheck.getText().toString())) {
                Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                updatePwdCheck.requestFocus();
            }else {

                //요청바디 설정
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        //파라미터명은 picture
                        .addFormDataPart("userID",userId)
                        .addFormDataPart("nickname", updateNickname.getText().toString())
                        .addFormDataPart("password", updatePwd.getText().toString())
                        .addFormDataPart("upload", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file))
                        .build();
                //요청 객체 생성
                Request request = new Request.Builder()
                        .url(UrlCollection.UPDATE_INFO)
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
                        Intent intent = new Intent(MyPage_Edit_Profile.this, MainPage.class);
                        //intent.putExtra("signup","회원가입이 완료되었습니다.");
                        startActivity(intent);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}