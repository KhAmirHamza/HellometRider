package com.hellomet.rider.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    List<String> titleList;
    List<Fragment> fragmentList;

    public ViewPagerAdapter(@NonNull FragmentManager fm, Context context, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        this.context = context;
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public int getItemPosition(Object item) {
        fragmentList.indexOf(item);
        int position = fragmentList.indexOf(item);

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
