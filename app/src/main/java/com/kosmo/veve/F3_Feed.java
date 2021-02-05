package com.kosmo.veve;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.GallaryComment;
import com.kosmo.veve.dto.MyFeed;
import com.kosmo.veve.http.UrlCollection;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class F3_Feed extends Fragment implements Runnable {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=1;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE=2;

    String photoImagePath;
    private Context context;

    private ImageView close, image_added,user_profile;
    private TextView post,user_ID;
    private EditText edt_title,edt_content;

    private View view;
    private String userId;

    ArrayList<MyFeed> myFeeds = new ArrayList<>();

    UUID uuid = UUID.randomUUID();
    String fileName = uuid.toString();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed,container,false);


        SharedPreferences preferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        userId = preferences.getString("userId",null);


        SharedPreferences preferences2 = view.getContext().getSharedPreferences("postInfo", Context.MODE_PRIVATE);
        String f_name = preferences2.getString("f_name",null);

        close = view.findViewById(R.id.close);
        image_added = view.findViewById(R.id.image_added);
        post = view.findViewById(R.id.POST);
        edt_title = view.findViewById(R.id.edt_title);
        edt_content = view.findViewById(R.id.edt_content);
        user_ID = view.findViewById(R.id.user_id);
        user_profile = view.findViewById(R.id.user_profile);

        user_ID.setText(userId);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MainPage.class));
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                file.mkdirs();
                photoImagePath=file.getAbsolutePath()+File.separator+fileName+".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendImageToServer(file);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            // NameValuePair : 변수명과 값을 함께 저장하는 객체로 제공되는 객체이다.
            ArrayList<NameValuePair> postData = new ArrayList<>();
            // post 방식으로 전달할 값들을 postData 객체에 집어 넣는다.
            postData.add(new BasicNameValuePair("userID",userId));
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다.
            UrlEncodedFormEntity request = new UrlEncodedFormEntity(postData,"utf-8");
            HttpClient http = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(UrlCollection.MYFEED);
            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(request);
            // post 방식으로 전송, 응답결과는 response로 넘어옴
            HttpResponse response = http.execute(httpPost);
            // response text를 스트링으로 변환
            String body = EntityUtils.toString(response.getEntity());
            // 스트링을 json으로 변환한다.
            JSONObject obj = new JSONObject(body);

            JSONObject JsonList = new JSONObject();
            // url encoding이 필요한 값들(한글, 특수문자) : 한글은 인코딩안해주면 깨짐으로 인코딩을 한다. 고쳐봐야함
            StringEntity params = new StringEntity(JsonList.toString(), HTTP.UTF_8);

            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/JSON"));

            // post 방식으로 전달할 데이터 설정
            httpPost.setEntity(params);

            JSONArray jArray = (JSONArray) obj.get("sendData");

            for (int i = 0; i < 1; i++) {
                // json배열.getJSONObject(인덱스)
                JSONObject row = jArray.getJSONObject(i);
                MyFeed myFeed = new MyFeed();


                myFeed.setF_name(row.getString("f_name"));

                myFeeds.add(myFeed);
                Log.d("파일 이름:",(row.getString("f_name")));
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("postInfo",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit();

            editor.putString("f_name",myFeeds.get(0).getF_name());
            editor.commit();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String image_url = myFeeds.get(0).getF_name();
                    loadImageTask imageTask = new loadImageTask(image_url);
                    imageTask.execute();
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class loadImageTask extends AsyncTask<Bitmap, Void, Bitmap> {

        private String url;

        public loadImageTask(String url) {

            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            Bitmap imgBitmap = null;

            try {
                URL url1 = new URL(url);
                URLConnection conn = url1.openConnection();
                conn.connect();
                int nSize = conn.getContentLength();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
                imgBitmap = BitmapFactory.decodeStream(bis);
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return imgBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bit) {
            super.onPostExecute(bit);
            user_profile.setImageBitmap(bit);
        }
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
                photoImagePath=file.getAbsolutePath()+File.separator+fileName+".jpg";

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
        }else if(requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            //갤러리에 있는 이미지를 이미지뷰에 표시하기
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    Toast.makeText(context, "이미지를 가져올수 없어요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri selectedImageUri = data.getData();
                //선택된 이미지의 Uri로 이미지뷰에 표시
                image_added.setImageURI(selectedImageUri);
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //Log.d("",file.toString());
                file.mkdirs();
                photoImagePath = file.getAbsolutePath() + File.separator + edt_title.getText().toString() + ".jpg";

                file = new File(photoImagePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    BufferedOutputStream bos = null;
                    try {
                        bos = new BufferedOutputStream(
                                new FileOutputStream(file));

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);//이미지가 용량이 클 경우
                        //OutOfMemoryException 발생할수 있음.그래서 압축
                        //사진을 앨범에 보이도록 갤러리앱에 방송을 보내기
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
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