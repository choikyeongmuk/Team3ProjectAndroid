package com.kosmo.veve;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public List<GallaryBoard> gbList;


    public RecyclerViewAdapter(List<GallaryBoard> gb_List ) {
        gbList = gb_List;
    }
    public void addItem(GallaryBoard data) {
        // 외부에서 item을 추가시킬 함수입니다.
        gbList.add(data);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
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

        return gbList == null ? 0 : gbList.size() ;

    }


    @Override
    public int getItemViewType(int position) {
        return gbList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView userID,title,postdate,user_content;
        ImageView userFile;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            userID = itemView.findViewById(R.id.user_id);
            title = itemView.findViewById(R.id.bbs_title);
            postdate = itemView.findViewById(R.id.bbs_postdate);
            user_content = itemView.findViewById(R.id.bbs_content);
            userFile = itemView.findViewById(R.id.bbs_file);

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

    private void populateItemRows(ItemViewHolder viewHolder, int position) {


        GallaryBoard item = gbList.get(position);
        viewHolder.userID.setText(item.getUserID());
        viewHolder.title.setText(item.getTitle());
        viewHolder.postdate.setText(item.getPostDate());
        viewHolder.user_content.setText(item.getContent());

    }


}