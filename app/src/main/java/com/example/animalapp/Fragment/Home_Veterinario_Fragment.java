package com.example.animalapp.Fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.animalapp.Adapter.My_Adapter;

import com.example.animalapp.R;
import com.google.android.material.tabs.TabLayout;

public class Home_Veterinario_Fragment extends Fragment {

    TabLayout tabLayoutVeterinario;
    ViewPager2 viewPager2Veterinario;
    My_Adapter myViewPagerAdapterVeterinario;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_veterinario, container, false);
        tabLayoutVeterinario  = view.findViewById(R.id.tab_layout_veterinario);
        viewPager2Veterinario = view.findViewById(R.id.view_pager_veterinario);
        myViewPagerAdapterVeterinario = new My_Adapter(this);
        viewPager2Veterinario.setAdapter(myViewPagerAdapterVeterinario);

        tabLayoutVeterinario.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2Veterinario.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2Veterinario.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabLayoutVeterinario.getTabAt(position).select();
            }
        });
        return inflater.inflate(R.layout.fragment_home_veterinario, container, false);
    }
}