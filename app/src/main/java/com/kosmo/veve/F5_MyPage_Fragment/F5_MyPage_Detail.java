package com.kosmo.veve.F5_MyPage_Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosmo.veve.R;

import java.util.ArrayList;


public class F5_MyPage_Detail extends Fragment {

    private String postId;
    private View view;
    private RecyclerView recyclerView;
    //private PostAdapter postAdapter;
    //private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_f5__my_page__detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        //postId = pre;

        recyclerView = view.findViewById(R.id.recycler_view_detail);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //postList = new ArrayList<>();
        //postAdapter = new PostAdapter(getContext(),postList);
        //recyclerView.setAdapter(postAdapter);

        readPost();

        return view;
    }

    private void readPost(){

    }
}