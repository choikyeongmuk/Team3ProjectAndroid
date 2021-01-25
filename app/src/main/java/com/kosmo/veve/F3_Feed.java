package com.kosmo.veve;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kosmo.veve.http.UrlCollection;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class F3_Feed extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=1;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;

    String photoImagePath;
    private Context context;

    private ImageView close, image_added;
    private TextView post;
    private EditText edt_title,edt_content;

    private View view;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed,container,false);

        close = view.findViewById(R.id.close);
        image_added = view.findViewById(R.id.image_added);
        post = view.findViewById(R.id.POST);
        edt_title = view.findViewById(R.id.edt_title);
        edt_content = view.findViewById(R.id.edt_content);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MainPage.class));
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+edt_title.getText()+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendImageToServer(file);
            }
        });

        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);

        return view;
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
                image_added.setImageBitmap(bmp);

                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Log.d("",file.toString());
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+edt_title.getText()+".jpg";

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
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    bos.flush();
                    bos.close();
                    //https://square.github.io/okhttp/
                    //1.그레이들에 okhttp3라이브러리 추가
                    //서버로 전송하기
                    //sendImageToServer(file);

                }
                catch(Exception e){e.printStackTrace();}
                Log.i("com.kosmo.veve",photoImagePath);
            }
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
                    .addFormDataPart("userID", userId)
                    .addFormDataPart("title", edt_title.getText().toString())
                    .addFormDataPart("content", edt_content.getText().toString())
                    //.addFormDataPart("postdate",postDate)
                    .addFormDataPart("upload", file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file))
                    .build();
            //요청 객체 생성
            Request request = new Request.Builder()
                    .url(UrlCollection.GALLERY_POST)
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
                    Intent intent = new Intent(getActivity(), MainPage.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),edtId.getText().toString()+"님 회원가입이 완료되었습니다.",Toast.LENGTH_SHORT);
                }
            });
        }catch (Exception e){
        e.printStackTrace();
    }

    }
}