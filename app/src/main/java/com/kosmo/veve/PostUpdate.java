package com.kosmo.veve;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.kosmo.veve.http.UrlCollection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostUpdate extends AppCompatActivity {

    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;

    private ImageView close;
    private TextView POST;
    private ImageView imageAdded;
    private EditText edtTitle;
    private EditText edtContent;

    String photoImagePath;
    String userId;
    String gallary_no;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);

        initView();
        intent = getIntent();
        gallary_no = intent.getStringExtra("gallary_no");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String f_name = intent.getStringExtra("f_name");

        SharedPreferences preferences = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);

        edtTitle.setText(title);
        edtContent.setText(content);
        new PostUpdate.DownloadFilesTask(f_name,imageAdded).execute();

        imageAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    private void initView() {
        close = (ImageView) findViewById(R.id.close);
        POST = (TextView) findViewById(R.id.POST);
        imageAdded = (ImageView) findViewById(R.id.image_added);
        edtTitle = (EditText) findViewById(R.id.edt_title);
        edtContent = (EditText) findViewById(R.id.edt_content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            //갤러리에 있는 이미지를 이미지뷰에 표시하기
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, "이미지를 가져올수 없어요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri selectedImageUri = data.getData();
                //선택된 이미지의 Uri로 이미지뷰에 표시
                imageAdded.setImageURI(selectedImageUri);
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Log.d("",file.toString());
                file.mkdirs();
                photoImagePath = file.getAbsolutePath() + File.separator + edtTitle.getText().toString() + ".jpg";

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

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);//이미지가 용량이 클 경우
                        //OutOfMemoryException 발생할수 있음.그래서 압축
                        //사진을 앨범에 보이도록 갤러리앱에 방송을 보내기
                        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        bos.flush();
                        bos.close();
                        //https://square.github.io/okhttp/
                        //1.그레이들에 okhttp3라이브러리 추가
                        //서버로 전송하기
                        sendImageToServer(file);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void sendImageToServer(File file){
        try{

            /*SimpleDateFormat format = new SimpleDateFormat( "yyyy년 MM월dd일");
            Date date = new Date();
            String postDate = format.format(date);*/

            //요청바디 설정
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    //파라미터명은 picture
                    //.addFormDataPart("userID", userId)
                    .addFormDataPart("gallary_no",gallary_no)
                    .addFormDataPart("title", edtTitle.getText().toString())
                    .addFormDataPart("content", edtContent.getText().toString())
                    //.addFormDataPart("postdate",postDate)
                    .addFormDataPart("upload", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file))
                    .build();
            //요청 객체 생성
            Request request = new Request.Builder()
                    .url(UrlCollection.GALLERY_UPDATE)
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
                    Intent intent = new Intent(PostUpdate.this, MainPage.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),edtId.getText().toString()+"님 회원가입이 완료되었습니다.",Toast.LENGTH_SHORT);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
