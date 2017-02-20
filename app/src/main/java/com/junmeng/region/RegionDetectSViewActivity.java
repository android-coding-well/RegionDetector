package com.junmeng.region;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.junmeng.rdetecte.utils.CommonUtil;
import com.junmeng.rdetecte.widget.RegionDetectSurfaceView;
import com.junmeng.region.databinding.ActivityRegionDetectSviewBinding;

import static com.junmeng.rdetecte.widget.RegionDetectSurfaceView.SCALE_ZOOMIN;

public class RegionDetectSViewActivity extends AppCompatActivity {
    private static final String TAG = "RegionDetectSViewActivi";
    int[] areaRes = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_xinjiang, R.string.china_fujian
            , R.string.china_gansu, R.string.china_zhejiang, R.string.china_yunnan
            , R.string.china_xizang, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };

    int[] areaRes2 = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_shanxi, R.string.china_neimenggu
            , R.string.china_fujian, R.string.china_gansu, R.string.china_zhejiang
            , R.string.china_yunnan, R.string.china_liaoning, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };

    ActivityRegionDetectSviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_region_detect_sview);

        binding.rdvDetect.setOnActivateRegionDetectListener(new RegionDetectSurfaceView.OnActivateRegionDetectListener() {
            @Override
            public void onActivateRegionDetect(String name) {
                binding.tvActivate.setText("高亮区域：" + name);
                binding.rdvDetect.setSelectedAreaOnlyCloseCenterLocation(name);
            }
        });
        binding.rdvDetect.setOnRegionDetectListener(new RegionDetectSurfaceView.OnRegionDetectListener() {
            @Override
            public void onRegionDetect(String name) {
                binding.tvDetect.setText("当前区域：" + name);
            }
        });

        binding.rdvDetect.setOnDoubleClickListener(new RegionDetectSurfaceView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(@RegionDetectSurfaceView.ScaleMode int scaleMode) {
                binding.tvZoom.setText("双击操作:" + (scaleMode == SCALE_ZOOMIN ? "放大" : "缩小"));
            }
        });

        //binding.rdvDetect.setCenterIcon(CommonUtil.getBitmapFromVectorDrawable(this,R.mipmap.ic_launcher));
        //binding.rdvDetect.setAllAreaActivateStatus(true);
        binding.rdvDetect.setAreaActivateStatus(areaRes, true);
        binding.rdvDetect.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
        binding.rdvDetect.setCenterIconVisibility(true);
        //binding.rdvDetect.setCenterIconLocationType(RegionDetectSurfaceView.CENTER_ICON_POSITION_CENTER);
        binding.rdvDetect.setDefaultNormalColor(0x8069BBA8);
        binding.rdvDetect.setDefaultActivateColor(0x802F8BBB);
        binding.rdvDetect.setDefaultHighlightColor(0x80BB945A);
        binding.rdvDetect.setBackgroundColor(0xFFdddddd);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "onContextItemSelected: ");
        switch (id) {
            case R.id.m_fitcenter:
                binding.rdvDetect.fitCenter();
                break;
            case R.id.m_change_icon:
                binding.rdvDetect.setCenterIcon(CommonUtil.getBitmapFromVectorDrawable(this,R.mipmap.ic_launcher));
                break;
            case R.id.m_toggle:
                binding.rdvDetect.setAreaMap(R.drawable.ic_china, 297, 297);
                binding.rdvDetect.setAreaActivateStatus(areaRes2, true);
                binding.rdvDetect.setAreaColor(R.string.china_guangdong, Color.YELLOW, -1, -1);
                binding.rdvDetect.setDefaultNormalColor(0x8069BBA8);
                binding.rdvDetect.setDefaultActivateColor(0x802F8BBB);
                binding.rdvDetect.setDefaultHighlightColor(0x80BB945A);
                binding.rdvDetect.setBackgroundColor(0xFFFFCAF3);
                break;
            case R.id.m_mode_center:
                binding.rdvDetect.setRegionDetectMode(RegionDetectSurfaceView.REGION_DETECT_MODE_CENTER);
                break;
            case R.id.m_mode_click:
                binding.rdvDetect.setRegionDetectMode(RegionDetectSurfaceView.REGION_DETECT_MODE_CLICK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
