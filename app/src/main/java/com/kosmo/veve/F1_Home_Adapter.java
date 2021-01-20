package com.kosmo.veve;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.Vector;

public class F1_Home_Adapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new Vector<>();

    public F1_Home_Adapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    //page의 개수를 반환
    @Override
    public int getCount() {
        return fragments.size();
    }
}
