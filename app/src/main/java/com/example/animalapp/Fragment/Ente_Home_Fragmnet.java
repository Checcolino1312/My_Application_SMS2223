package com.example.animalapp.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.animalapp.Adapter.My_Adapter;
import com.example.animalapp.R;
import com.google.android.material.tabs.TabLayout;
public class Ente_Home_Fragmnet extends Fragment {
    public  Home_Ente_Fragment() {
        // Required empty public constructor
    }



    public static  Home_Ente_Fragment newInstance(String param1, String param2) {

        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myViewPagerAdapterEnte = new My_Adapter(this);
        viewPager2Ente.setAdapter(myViewPagerAdapterEnte);

    }



    TabLayout tabLayoutEnte;
    ViewPager2 viewPager2Ente;
    My_Adapter myViewPagerAdapterEnte;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_ente, container, false);
        tabLayoutEnte  = view.findViewById(R.id.tab_layout_ente);
        viewPager2Ente = view.findViewById(R.id.view_pager_ente);

        tabLayoutEnte.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2Ente.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2Ente.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabLayoutEnte.getTabAt(position).select();
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_ente, container, false);
    }
}
