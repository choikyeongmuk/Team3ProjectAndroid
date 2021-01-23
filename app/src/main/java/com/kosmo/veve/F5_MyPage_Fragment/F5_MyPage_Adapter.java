package com.kosmo.veve.F5_MyPage_Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.Vector;

public class F5_MyPage_Adapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new Vector<>();

    public F5_MyPage_Adapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:return new F5_MyPage_Feed();
            case 1:return new F5_MyPage_Scrap();
            default:return new F5_MyPage_Nutrient();
        }
    }
    //page의 개수를 반환
    @Override
    public int getCount() {
        return fragments.size();
    }
}
