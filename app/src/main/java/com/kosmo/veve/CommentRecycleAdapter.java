package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.GallaryComment;
import com.kosmo.veve.http.UrlCollection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<GallaryComment> gcList;
    public List<GallaryBoard> gbList;

    public CommentRecycleAdapter(List<GallaryComment> gc_list) {
        gcList = gc_list;
    }
    public void addItem(GallaryComment data) {
        // 외부에서 item을 추가시킬 함수입니다.
        gcList.add(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_recycler, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        populateItemRows((ItemViewHolder) viewHolder, position);
    }

    @Override
    public int getItemCount() {

        return gcList == null ? 0 : gcList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return gcList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView userID,content;
        ImageView userFile;

        Button comment_btn;


        String sessionID;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            SharedPreferences preferences = itemView.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
            this.sessionID = preferences.getString("userID",null);
            this.userID = itemView.findViewById(R.id.user_id);
            this.content = itemView.findViewById(R.id.user_comment);
            this.userFile = itemView.findViewById(R.id.user_profile);
            this.comment_btn = itemView.findViewById(R.id.comment_btn);

        }
    }



    public void populateItemRows(ItemViewHolder viewHolder, int position) {

        //GallaryBoard gb_list = gbList.get(position);
        GallaryComment gc_list = gcList.get(position);
        viewHolder.userID.setText(gc_list.getUserID());
        viewHolder.content.setText(gc_list.getContent());

        /*viewHolder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToComment(gc_list.getGallary_no(),viewHolder.sessionID,viewHolder.content.getText().toString());
            }
        });*/

        new DownloadFilesTask(gc_list.getF_name(),viewHolder.userFile).execute();
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

    private void sendToComment(String gallary_no,String sessionID, String content) {

        //요청바디 설정
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //파라미터명은 picture
                .addFormDataPart("userID",sessionID)
                .addFormDataPart("gallary_no",gallary_no)
                .addFormDataPart("content",content)
                .build();
        //요청 객체 생성
        Request request = new Request.Builder()
                .url(UrlCollection.COMMENTLIST)
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

            }
        });

    }

}