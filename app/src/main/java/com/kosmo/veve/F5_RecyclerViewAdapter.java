package com.kosmo.veve;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.kosmo.veve.F5_MyPage_Fragment.F5_MyPage_Detail;
import com.kosmo.veve.dto.GallaryBoard;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class F5_RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    ImageView userFile;

    FragmentManager fm;
    FragmentTransaction tran;
    Context mContext;

    F5_MyPage_Detail f5_myPage_detail;
    F5_MyPage f5_myPage;
    Fragment fragment;

    public List<GallaryBoard> gbList;


    public F5_RecyclerViewAdapter(List<GallaryBoard> gb_List ) {
        gbList = gb_List;
    }

    public F5_RecyclerViewAdapter(Context context,List<GallaryBoard> gb_List){
        this.gbList = gb_List;
        this.mContext = context;
    }

    public void addItem(GallaryBoard data) {
        // 외부에서 item을 추가시킬 함수입니다.
        gbList.add(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.f5_item_recycler, parent, false);
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
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {
        return gbList == null ? 0 : gbList.size();

    }


    @Override
    public int getItemViewType(int position) {
        return gbList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            userFile = itemView.findViewById(R.id.my_file);
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
                    /*F5_MyPage f5_myPage = new F5_MyPage();
                    F5_MyPage_Feed f5_myPage_feed = new F5_MyPage_Feed();
                    F5_MyPage_Detail f5_myPage_detail = new F5_MyPage_Detail();
                    FragmentTransaction transaction = f5_myPage_feed.getChildFragmentManager().beginTransaction();
                    transaction.replace(R.id.mypage_view, f5_myPage_detail).commit();*/

                    //moveToDetail();

                }
            });


        }
    }

    /*private void moveToDetail(){
        FragmentTransaction fragmentTransaction = (f5_myPage.getChildFragmentManager()).beginTransaction();
        fragmentTransaction.replace(R.id.mypage_view, f5_myPage_detail);
        //fragmentTransaction.addToBackStack("fragment");
        fragmentTransaction.commit();
    }*/




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

        GallaryBoard item = gbList.get(position);
        new DownloadFilesTask(item.getF_name(),userFile).execute();
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