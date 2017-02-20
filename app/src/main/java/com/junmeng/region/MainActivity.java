package com.junmeng.region;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.junmeng.region.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }


    public void onClickRDSV(View view) {
        Intent intent = new Intent(this, RegionDetectSViewActivity.class);
        startActivity(intent);
    }

    public void onClickRDV(View view) {
    }
}
