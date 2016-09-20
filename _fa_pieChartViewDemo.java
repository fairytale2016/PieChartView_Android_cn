package com.fairytale110.mycustomviews.WidgetDemo;

import android.os.Bundle;
import android.widget.TextView;

import com.fairytale110.mycustomviews.BaseActivity;
import com.fairytale110.mycustomviews.R;
import com.fairytale110.mycustomviews.Widget._fa_pieChartView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by   fairytale110
 * Creat date   2016/9/14 16:18
 * Copy Right   www.xkyiliao.com
 * function
 */
public class _fa_pieChartViewDemo extends BaseActivity {
    @Bind(R.id.piechart)
    _fa_pieChartView piechart;
    Random random;
    @Bind(R.id.txt_data)
    TextView txtData;
    private int max, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fa_piechartviewdemo);
        ButterKnife.bind(this);
        max = 360;
        min = 0;
        random = new Random();
        Float[] datas = {73.54F,80.01F,90.00F,103F,120F};
        piechart.setDatas(datas);

        piechart.setOnItemChangedListener(new _fa_pieChartView.OnItemChangedListener() {
            @Override
            public void onItemChanged(int index, float value) {
                txtData.setText("index: " +index+ " value: "+value );
            }
        });

    }
}
