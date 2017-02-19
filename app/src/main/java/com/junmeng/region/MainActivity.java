package com.junmeng.region;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.CompoundButton;

import com.junmeng.region.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    ActivityMainBinding binding;

    int[] areaRes = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_xinjiang, R.string.china_fujian
            , R.string.china_gansu, R.string.china_zhejiang,R.string.china_yunnan
            , R.string.china_xizang, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };

    int[] areaRes2 = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_shanxi,R.string.china_neimenggu
            , R.string.china_fujian, R.string.china_gansu, R.string.china_zhejiang
            ,R.string.china_yunnan, R.string.china_liaoning, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
       binding.sCenterLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    binding.btnMap.isOpenCenterLocation(isChecked);
           }
       });
       /* binding.btnMap.setOnActivateRegionDetecteListener(new RegionDetecteSurfaceView.OnActivateRegionDetecteListener() {
            @Override
            public void onActivateRegionDetecte(String name) {
                binding.tvActivate.setText("激活区域："+name);
                binding.btnMap.setSelectedAreaOnlyCloseCenterLocation(name);
            }
        });
        binding.btnMap.setOnRegionDetecteListener(new RegionDetecteSurfaceView.OnRegionDetecteListener() {
            @Override
            public void onRegionDetecte(String name) {
                binding.tvDetecte.setText(" 普通区域："+name);
            }
        });*/
        //binding.btnMap.setAllAreaActivateStatus(true);
        binding.btnMap.setAreaActivateStatus(areaRes, true);
        binding.btnMap.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
        binding.btnMap.setCenterIconVisibility(true);
        //binding.btnMap.setCenterIconPosition(MapButton.CENTER_ICON_POSITION_CENTER);
        binding.btnMap.setDefaultNormalColor(0x8069BBA8);
        binding.btnMap.setDefaultActivateColor(0x802F8BBB);
        binding.btnMap.setDefaultHighlightColor(0x80BB945A);
        binding.btnMap.setBackgroundColor(0xFFdddddd);


    }


    public void onClickFitCenter(View view) {
        binding.btnMap.fitCenter();
    }

    public void onClickToggle(View view) {
        binding.btnMap.setAreaMap(R.drawable.ic_china, 297, 297);
        //binding.btnMap.setAreaMap(R.drawable.ic_chinalow, 800, 600);

        //binding.btnMap.setAllAreaActivateStatus(true);
        binding.btnMap.setAreaActivateStatus(areaRes2, true);
        binding.btnMap.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
        binding.btnMap.setCenterIconVisibility(true);
        //binding.btnMap.setCenterIconPosition(MapButton.CENTER_ICON_POSITION_CENTER);
        binding.btnMap.setDefaultNormalColor(0x8069BBA8);
        binding.btnMap.setDefaultActivateColor(0x802F8BBB);
        binding.btnMap.setDefaultHighlightColor(0x80BB945A);
        binding.btnMap.setBackgroundColor(0xFFdddddd);
    }
}
