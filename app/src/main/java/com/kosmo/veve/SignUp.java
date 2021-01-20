package com.kosmo.veve;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kosmo.veve.http.NetworkTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=1;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;

    Spinner spinner;
    private ImageView btn_back;
    private EditText edtId;
    private EditText edtPwd;
    private EditText edtPwdCheck;
    private EditText edtBirth;
    private EditText edtNickname;
    private EditText edtIntro;
    private Button btn_id_check;
    private Button btn_signUp;
    private RadioGroup radio_gender;
    private RadioButton radioMan;
    private RadioButton radioWoman;
    private ImageView userImage;
    private Button btn_camera;
    private Button btn_gallery;

    String photoImagePath;

    private Context context;

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Log.i("com.kosmo.veve","onAttach:3");
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        edtId = (EditText) findViewById(R.id.edt_id);
        edtPwd = (EditText) findViewById(R.id.edt_pwd);
        edtPwdCheck = (EditText) findViewById(R.id.edt_pwd_check);
        edtBirth = (EditText) findViewById(R.id.edt_birth);
        edtNickname = (EditText) findViewById(R.id.edt_nickname);
        edtIntro = (EditText) findViewById(R.id.edt_intro);
        btn_id_check = (Button) findViewById(R.id.btn_id_check);
        btn_signUp = (Button) findViewById(R.id.btn_signUp);
        radio_gender = (RadioGroup) findViewById(R.id.radio_gender);
        radioMan = (RadioButton) findViewById(R.id.radio_man);
        radioWoman = (RadioButton) findViewById(R.id.radio_woman);
        userImage = (ImageView) findViewById(R.id.user_image);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_gallery = (Button) findViewById(R.id.btn_gallery);

        edtId.setFocusableInTouchMode(true);
        edtPwd.setFocusableInTouchMode(true);
        edtPwdCheck.setFocusableInTouchMode(true);
        edtNickname.setFocusableInTouchMode(true);
        edtBirth.setFocusableInTouchMode(true);
        edtIntro.setFocusableInTouchMode(true);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idcheck();
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        //갤러리 버튼
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        //회원가입 버튼
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+edtId.getText().toString()+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendImageToServer(file);
            }
        });

        spinner = findViewById(R.id.spinner);

        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.spinnerArray, android.R.layout.simple_spinner_dropdown_item);
        //R.array.test는 저희가 정의해놓은 1월~12월 / android.R.layout.simple_spinner_dropdown_item은 기본으로 제공해주는 형식입니다.
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(monthAdapter); //어댑터에 연결해줍니다.


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            } //이 오버라이드 메소드에서 position은 몇번째 값이 클릭됬는지 알 수 있습니다.
            //getItemAtPosition(position)를 통해서 해당 값을 받아올수있습니다.

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                if(data== null){
                    Toast.makeText(context,"카메라로 사진 찍기 실패",Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bmp=(Bitmap)data.getExtras().get("data");
                /*
                아래는 용량이 클 경우 OutOfMemoryException 발생이 예상되어 압축
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                //압축된 이미지를 이미지뷰에표시
                imageView.setImageBitmap(bitmap);
                 */
                //압축이 안된 이미지를 이미지뷰에 표시
                userImage.setImageBitmap(bmp);

                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Log.d("",file.toString());
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+edtId.getText().toString()+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ///갤러리에 촬영한 사진 추가하기
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(
                            new FileOutputStream(file));

                    bmp.compress(Bitmap.CompressFormat.PNG,100,bos);//이미지가 용량이 클 경우
                    //OutOfMemoryException 발생할수 있음.그래서 압축
                    //사진을 앨범에 보이도록 갤러리앱에 방송을 보내기
                    this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    bos.flush();
                    bos.close();
                    //https://square.github.io/okhttp/
                    //1.그레이들에 okhttp3라이브러리 추가
                    //서버로 전송하기
                    sendImageToServer(file);

                }
                catch(Exception e){e.printStackTrace();}
                Log.i("com.kosmo.veve",photoImagePath);
            }
        }
        else if(requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE){
            //갤러리에 있는 이미지를 이미지뷰에 표시하기
            if(resultCode== Activity.RESULT_OK){
                if(data== null){
                    Toast.makeText(context,"이미지를 가져올수 없어요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri selectedImageUri=data.getData();
                //선택된 이미지의 Uri로 이미지뷰에 표시
                userImage.setImageURI(selectedImageUri);


                /*String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String imgPath = "/storage/emulated/0/Pictures/";
                String imgName = edtId.getText().toString()+".jpg";

                File file = new File(imgPath+imgName);
                Log.d("imgpath",imgPath);

                sendImageToServer(file);*/


            }
        }
    }///////////////

    private void idcheck(){

    }


    private void sendImageToServer(File file){

        try {
            if (edtId.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                edtId.requestFocus();
            } else if (edtId.getText().toString().length() < 5 || edtId.getText().toString().length() > 20) {
                Toast.makeText(getApplicationContext(), "아아디는 5~20자의 영문 소문자, 숫자와 특수기호(-,_)만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                edtId.requestFocus();
            } else if (edtPwd.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                edtPwd.requestFocus();
            } else if (edtPwd.getText().toString().length() < 8 || edtId.getText().toString().length() > 16) {
                Toast.makeText(getApplicationContext(), "비밀번호는 8~16자의 영문 소문자, 숫자와 특수기호(-,_)만 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                edtId.requestFocus();
            } else if (edtPwdCheck.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                edtPwdCheck.requestFocus();
            } else if (!edtPwd.getText().toString().equalsIgnoreCase(edtPwdCheck.getText().toString())) {
                Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                edtPwd.requestFocus();
            } else if (edtNickname.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "별명을 입력하세요.", Toast.LENGTH_SHORT).show();
                edtNickname.requestFocus();
            } else if (radio_gender.getCheckedRadioButtonId() != R.id.radio_man && radio_gender.getCheckedRadioButtonId() != R.id.radio_woman) {
                Toast.makeText(getApplicationContext(), "성별을 체크하세요.", Toast.LENGTH_SHORT).show();
            } else if (edtBirth.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
                edtBirth.requestFocus();
            } else if (spinner.getSelectedItem().toString().equalsIgnoreCase("레벨을 선택하세요.")) {
                Toast.makeText(getApplicationContext(), "비건 레벨을 선택하세요.", Toast.LENGTH_SHORT).show();
            } else if (edtIntro.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getApplicationContext(), "자기소개를 입력하세요.", Toast.LENGTH_SHORT).show();
                edtIntro.requestFocus();
            }else {
                String gender = "";
                int gender_checked = radio_gender.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(gender_checked);
                if (radio_gender.getCheckedRadioButtonId() != R.id.radio_man && radio_gender.getCheckedRadioButtonId() != R.id.radio_woman) {
                    //Toast.makeText(getApplicationContext(), "성별을 체크하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    gender = rb.getText().toString();
                }

                //요청바디 설정
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        //파라미터명은 picture
                        .addFormDataPart("userID", edtId.getText().toString())
                        .addFormDataPart("password", edtPwd.getText().toString())
                        .addFormDataPart("nickname", edtNickname.getText().toString())
                        .addFormDataPart("name", edtNickname.getText().toString())
                        .addFormDataPart("gender", gender)
                        .addFormDataPart("vg_level", spinner.getSelectedItem().toString())
                        .addFormDataPart("addr", "서울시")
                        .addFormDataPart("selfintro", edtIntro.getText().toString())
                        .addFormDataPart("age", edtBirth.getText().toString())
                        .addFormDataPart("k1n0", "0")
                        .addFormDataPart("upload", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file))
                        .build();
                //요청 객체 생성
                Request request = new Request.Builder()
                        .url(NetworkTask.SIGNUP)
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                //비동기로 요청 보내기
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Toast.makeText(context,"회원가입 실패",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    //서버로부터 응답받는 경우
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d("com.kosmo.veve", response.body().string());
                        Intent intent = new Intent(SignUp.this, Login.class);
                        intent.putExtra("signup","회원가입이 완료되었습니다.");
                        startActivity(intent);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}