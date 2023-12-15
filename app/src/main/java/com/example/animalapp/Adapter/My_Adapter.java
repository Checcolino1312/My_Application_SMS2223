package com.example.animalapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.animalapp.Fragment.Home_Veterinario_Fragment;
import com.example.animalapp.Fragment.InCarico_Fragment;
import com.example.animalapp.Fragment.Veterinario_Fragment;

public class My_Adapter  extends FragmentStateAdapter {
    public My_Adapter(@NonNull Home_Veterinario_Fragment fragmentActivity) {
        super(fragmentActivity);
    }
    public My_Adapter(@NonNull Ente_Home_Fragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Veterinario_Fragment();
            case 1 :
                return new InCarico_Fragment();
            default:
                return new Veterinario_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
