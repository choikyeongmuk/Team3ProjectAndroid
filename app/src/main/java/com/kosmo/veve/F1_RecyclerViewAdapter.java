package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.kosmo.veve.dto.GallaryBoard;
import com.kosmo.veve.dto.GallaryLike;
import com.kosmo.veve.dto.GallaryScrap;
import com.kosmo.veve.http.UrlCollection;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class F1_RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    Context context;
    public List<GallaryBoard> gbList;
    public List<GallaryLike> glbList;
    public List<GallaryScrap> gsList;


    public F1_RecyclerViewAdapter(List<GallaryBoard> gb_List,List<GallaryLike> glb_list,List<GallaryScrap> gs_List,Context context ) {
        gbList = gb_List;
        glbList = glb_list;
        gsList = gs_List;
        this.context = context;
    }
    public void addItem(GallaryBoard data) {
        // 외부에서 item을 추가시킬 함수입니다.
        gbList.add(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.f1_item_recycler, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
            F1_ViewPagerAdapter viewPagerAdapter = new F1_ViewPagerAdapter(gbList,context);
            ((ItemViewHolder) viewHolder).userFile.setAdapter(viewPagerAdapter);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {

        return gbList == null ? 0 : gbList.size() ;

    }


    @Override
    public int getItemViewType(int position) {
        return gbList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView userID,title,postdate,user_content,heart_count;
        ImageView heart,comment,scrap;


        ViewPager userFile;


        String sessionID;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            SharedPreferences preferences = itemView.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
            this.sessionID = preferences.getString("userId",null);
            this.userID = itemView.findViewById(R.id.user_id);
            this.title = itemView.findViewById(R.id.bbs_title);
            this.postdate = itemView.findViewById(R.id.bbs_postdate);
            this.user_content = itemView.findViewById(R.id.bbs_content);
            this.userFile = itemView.findViewById(R.id.bbs_file);
            this.heart_count = itemView.findViewById(R.id.heart_count);
            this.heart = itemView.findViewById(R.id._heart);
            this.comment = itemView.findViewById(R.id._comment);
            this.scrap = itemView.findViewById(R.id._scrap);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
                    intent.putExtra("gallary_no",gbList.get(position).getGallary_no());
                    intent.putExtra("userID",gbList.get(position).getUserID());
                    intent.putExtra("title",gbList.get(position).getTitle());
                    intent.putExtra("content",gbList.get(position).getContent());
                    intent.putExtra("postDate",gbList.get(position).getPostDate());
                    intent.putExtra("f_name",gbList.get(position).getF_name());
                    v.getContext().startActivity(intent);
                    Toast.makeText(v.getContext(), String.valueOf(position), Toast.LENGTH_LONG).show();

                }
            });

        }
    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //
    }

    public void populateItemRows(ItemViewHolder viewHolder, int position) {


        GallaryBoard gb_list = gbList.get(position);
        GallaryLike glb_list = glbList.get(position);
        GallaryScrap gs_list = gsList.get(position);
        viewHolder.userID.setText(gb_list.getUserID());
        viewHolder.title.setText(gb_list.getTitle());
        viewHolder.postdate.setText(gb_list.getPostDate());
        viewHolder.user_content.setText(gb_list.getContent());

        viewHolder.user_content.setText(gb_list.getContent());
        viewHolder.heart_count.setText("좋아요 " + gb_list.getHeartCount() + "개");
        if (gb_list.getHeartCount() > 0) {
            viewHolder.heart.setBackgroundResource(R.drawable.heart);
            viewHolder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (glb_list.isHeartCheck() == false) {
                        viewHolder.heart.setBackgroundResource(R.drawable.no_heart);
                        int likeCount = gb_list.getHeartCount() - 1;
                        viewHolder.heart_count.setText("좋아요 " + likeCount + "개");
                        sendToHeart(gb_list.getGallary_no(), glb_list.isHeartCheck(), viewHolder.sessionID);
                        glb_list.setHearCheck(true);
                    } else {
                        viewHolder.heart.setBackgroundResource(R.drawable.heart);
                        int likeCount = gb_list.getHeartCount();
                        viewHolder.heart_count.setText("좋아요 " + likeCount + "개");
                        sendToHeart(gb_list.getGallary_no(), glb_list.isHeartCheck(), viewHolder.sessionID);
                        glb_list.setHearCheck(false);
                    }
                }
            });
        } else {
            viewHolder.heart.setBackgroundResource(R.drawable.no_heart);
            viewHolder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (glb_list.isHeartCheck() == true) {
                        viewHolder.heart.setBackgroundResource(R.drawable.heart);
                        int likeCount = gb_list.getHeartCount() + 1;
                        viewHolder.heart_count.setText("좋아요 " + likeCount + "개");
                        sendToHeart(gb_list.getGallary_no(), glb_list.isHeartCheck(), viewHolder.sessionID);
                        glb_list.setHearCheck(false);
                    } else {
                        viewHolder.heart.setBackgroundResource(R.drawable.no_heart);
                        int likeCount = gb_list.getHeartCount();
                        viewHolder.heart_count.setText("좋아요 " + likeCount + "개");
                        sendToHeart(gb_list.getGallary_no(), glb_list.isHeartCheck(), viewHolder.sessionID);
                        glb_list.setHearCheck(true);
                    }
                }
            });
        }


        if (gb_list.getScrapCount() > 0) {
            viewHolder.scrap.setBackgroundResource(R.drawable.scrap);
            viewHolder.scrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gs_list.isScrapCheck() == false) {
                        viewHolder.scrap.setBackgroundResource(R.drawable.no_scrap);
                        sendToScrap(gs_list.getGallary_no(), gs_list.isScrapCheck(), viewHolder.sessionID);
                        gs_list.setScrapCheck(true);
                    } else {
                        viewHolder.scrap.setBackgroundResource(R.drawable.scrap);
                        sendToScrap(gs_list.getGallary_no(), gs_list.isScrapCheck(), viewHolder.sessionID);
                        gs_list.setScrapCheck(false);
                    }
                }
            });
        } else {
            viewHolder.scrap.setBackgroundResource(R.drawable.no_scrap);
            viewHolder.scrap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gs_list.isScrapCheck() == true) {
                        viewHolder.scrap.setBackgroundResource(R.drawable.scrap);
                        sendToScrap(gs_list.getGallary_no(), gs_list.isScrapCheck(), viewHolder.sessionID);
                        gs_list.setScrapCheck(false);
                    } else {
                        viewHolder.scrap.setBackgroundResource(R.drawable.no_scrap);
                        sendToScrap(gs_list.getGallary_no(), gs_list.isScrapCheck(), viewHolder.sessionID);
                        gs_list.setScrapCheck(true);
                    }
                }
            });
        }

        viewHolder.userID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),OtherHomeActivity.class);
                intent.putExtra("userID",gbList.get(position).getUserID());
                v.getContext().startActivity(intent);
            }
        });
    }


    private void sendToHeart (String gallary_no,boolean heartCheck, String sessionID){

        //요청바디 설정
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //파라미터명은 picture
                .addFormDataPart("userID", sessionID)
                .addFormDataPart("gallary_no", gallary_no)
                .addFormDataPart("heartcheck", String.valueOf(heartCheck))
                .build();
        //요청 객체 생성
        Request request = new Request.Builder()
                .url(UrlCollection.GALLERY_LIKE)
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

    private void sendToScrap (String gallary_no,boolean scrapCheck, String sessionID){

        //요청바디 설정
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //파라미터명은 picture
                .addFormDataPart("userID", sessionID)
                .addFormDataPart("gallary_no", gallary_no)
                .addFormDataPart("scrapCheck", String.valueOf(scrapCheck))
                .build();
        //요청 객체 생성
        Request request = new Request.Builder()
                .url(UrlCollection.GALLERY_SCRAP)
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