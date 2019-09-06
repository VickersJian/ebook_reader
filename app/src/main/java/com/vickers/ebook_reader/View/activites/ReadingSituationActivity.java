package com.vickers.ebook_reader.View.activites;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.data.dao.UserLibraryBookEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.entity.UserLibraryBookEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadingSituationActivity extends mBaseActivity {

    private static final String TAG = "ReadingSituation";

    private PieChart pieChart;
    private BarChart barChart;
    private Toolbar toolbar;
    private float read;
    private float unRead;

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        setContentView(R.layout.activity_reading_situation);
    }

    @Override
    protected void bindView() {
        pieChart = findById(R.id.pie_chart);
        barChart = findById(R.id.bar_chart);
        toolbar = findById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        UserEntity user = UserEntityDao.findUserByUserId(intent.getStringExtra("userid"));
        LibraryBookEntity book = LibraryBookEntityDao.findBook(intent.getStringExtra("bookurl"));
        UserLibraryBookEntity userLibraryBookEntity = UserLibraryBookEntityDao.findUserLibraryBookEntity(user, book);
        read = userLibraryBookEntity.getBookRateOfProgress();
        unRead = 100 - read;
    }

    @Override
    protected void bindEvent() {
        initPieChart();
        initBarChart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reading_stuation_chart, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.pie_chart: {
                pieChart.setVisibility(View.VISIBLE);
                pieChart.animateY(1400, Easing.EaseInOutQuad);
                barChart.setVisibility(View.GONE);
            }
            break;
            case R.id.bar_chart: {
                barChart.setVisibility(View.VISIBLE);
                barChart.animateY(1400, Easing.EaseInOutQuad);
                pieChart.setVisibility(View.GONE);
            }
            break;
            default:
                return false;
        }
        return true;
    }

    private void initPieChart() {
        //数据
        List<PieEntry> pieEntryList = new ArrayList<>();
        if (read != 0) {
            pieEntryList.add(new PieEntry(read, "已读"));
        }
        if (unRead != 0) {
            pieEntryList.add(new PieEntry(unRead, "未读"));
        }
        showPieChart(pieChart, pieEntryList, "阅读情况");
    }

    private void showPieChart(PieChart pieChart, List<PieEntry> pieList, String descriptionText) {
        PieDataSet dataSet = new PieDataSet(pieList, "Label");

        // 设置颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);

        // 设置描述
        Description description = new Description();
        description.setText(descriptionText);
        description.setTextSize(15f);
        pieChart.setDescription(description);

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1OffsetPercentage(80f);

        //设置连接线的颜色
        dataSet.setValueLineColor(Color.BLUE);

        // 连接线设置
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.3f);

        // 设置饼块之间的间隔
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);
        // 不显示图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // 和四周相隔一段距离,显示数据
        pieChart.setExtraOffsets(30, 8, 30, 8);

        // 设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(true);
        // 设置pieChart图表展示动画效果，动画运行1.4秒结束
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        //是否绘制PieChart内部中心文本
        pieChart.setDrawCenterText(false);
        // 绘制内容value，设置字体颜色大小
        pieData.setDrawValues(true);
        //格式化成百分比数据
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        //设置数据显示大小
        pieData.setValueTextSize(15f);
        //数据字体颜色
        pieData.setValueTextColor(Color.DKGRAY);

        //设置半透明圆环的半径, 0为透明
        pieChart.setTransparentCircleRadius(0f);
        //设置空洞颜色
        pieChart.setHoleColor(Color.TRANSPARENT);
        //设置初始旋转角度
        pieChart.setRotationAngle(-40);
        //设置百分比显示数据
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        // 更新 piechart 视图
        pieChart.postInvalidate();
    }

    private void initBarChart() {
        //不显示描述
        Description description = barChart.getDescription();
        description.setEnabled(false);
        // 不显示图例
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
        //禁用触摸
        barChart.setTouchEnabled(false);
        //数据设置
        ArrayList<BarEntry> barEntryList = new ArrayList<>();
        barEntryList.add(new BarEntry(1f, read));
        barEntryList.add(new BarEntry(2f, unRead));
        BarDataSet barDataSet = new BarDataSet(barEntryList, "");
        //颜色设置
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));
        barDataSet.setColors(colors);
        //柱形图设置
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(true);//是否显示柱子的数值
        barData.setValueTextSize(10f);//柱子上面标注的数值的字体大小
        barData.setBarWidth(0.3f);//每个柱子的宽度
        barData.setValueFormatter(new PercentFormatter());//数值按百分比显示
        barChart.setData(barData);
        //x轴标签设置
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawLabels(true);//是否显示x坐标的数据
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x坐标数据的位置
        xAxis.setDrawGridLines(false);//是否显示网格线中与x轴垂直的网格线
        xAxis.setGranularity(0.5f);
        xAxis.setLabelCount(2);
        final List<String> xValue = new ArrayList<>();
        xValue.add("");//index = 0 的位置的数据在IndexAxisValueFormatter中时不显示的。
        xValue.add("已读");
        xValue.add("未读");
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));
        //y轴标签设置
        YAxis rightYAxis = barChart.getAxisRight();
        YAxis leftYAxis = barChart.getAxisLeft();
        rightYAxis.setEnabled(false);//设置右侧的y轴是否显示。包括y轴的那一条线和上面的标签都不显示
        rightYAxis.setDrawAxisLine(false);//这个方法就是专门控制坐标轴线的
        rightYAxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(false);//只有左右y轴标签都设置不显示水平网格线，图形才不会显示网格线
        leftYAxis.setEnabled(true);
        leftYAxis.setDrawLabels(true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置标签位置
        leftYAxis.setAxisMinimum(0f);//设置左轴最小值的数值。
        leftYAxis.setValueFormatter(new IndexAxisValueFormatter() {//让y轴标签按百分比显示
            @Override
            public String getFormattedValue(float value) {
                return value + "%";
            }
        });
    }
}
