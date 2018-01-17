package com.example.slidedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.slidedemo.view.SlideMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SlideMenu slideMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.ic_back).setOnClickListener(this);
        slideMenu= findViewById(R.id.sm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ic_back:
                slideMenu.switchState();
                break;
        }
    }
}
