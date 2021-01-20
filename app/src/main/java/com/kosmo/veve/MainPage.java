package com.kosmo.veve;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPage extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private F1_Home f1Home = new F1_Home();
    private F2_Management f2Management = new F2_Management();
    private F3_Feed f3Feed = new F3_Feed();
    private F4_Search f4Search = new F4_Search();
    private F5_MyPage f5MyPage = new F5_MyPage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("data");
        if(msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, f1Home).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.home:
                    transaction.replace(R.id.frameLayout, f1Home).commitAllowingStateLoss();

                    break;
                case R.id.management:
                    transaction.replace(R.id.frameLayout, f2Management).commitAllowingStateLoss();
                    break;
                case R.id.feed:
                    transaction.replace(R.id.frameLayout, f3Feed).commitAllowingStateLoss();
                    break;
                case R.id.search:
                    transaction.replace(R.id.frameLayout, f4Search).commitAllowingStateLoss();
                    break;
                case R.id.myPage:
                    transaction.replace(R.id.frameLayout, f5MyPage).commitAllowingStateLoss();
                    break;
            }
            return true;
        }


    }
}