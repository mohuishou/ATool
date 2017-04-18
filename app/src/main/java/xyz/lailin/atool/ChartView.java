package xyz.lailin.atool;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by laili on 2017/4/18.
 * 图表相关
 */

public class ChartView {

    private LineChart chart;

    private ArrayList<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();


    /**
     * 构造函数
     * @param mChart 线性图表
     */
    ChartView(LineChart mChart){
        chart=mChart;
        init();
    }

    /**
     * 初始化X轴
     */
    private void initX(XAxis xAxis){
        //设置X轴显示的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴的最小值
        xAxis.setAxisMinimum(0f);
        //关闭X轴的网格线
        xAxis.setDrawGridLines(false);
    }

    /**
     * 初始化Y轴设置
     */
    private void initY(YAxis yAxis){

    }

    /**
     * 初始化设置
     */
    private void init(){
        //可拖拽
        chart.setDragEnabled(true);

        //可缩放
        chart.setScaleEnabled(true);

        //设置背景颜色，和APP背景融合
        chart.setBackgroundColor(Color.parseColor("#fafafa"));

        initX(chart.getXAxis());

        initY(chart.getAxisLeft());

        initLines();

    }

    /**
     * 初始化三条折线
     */
    public void initLines(){
        //X方向
        createNewLine("#607d8b");

        //Y方向
        createNewLine("#e91e63");

        //Z方向
        createNewLine("#673ab7");

        LineData data = new LineData(dataSets);

        data.setValueTextColor(Color.GRAY);

        chart.setData(data);

        //刷新图表
        chart.invalidate();
    }

    /**
     * 新建一条折线
     * @param lineColor 折线的背景颜色
     */
    public void createNewLine(String lineColor){
        ArrayList<Entry> yValue = new ArrayList<>();
        yValue.add(new Entry(0,0));
        final LineDataSet dataSet=new LineDataSet(yValue,"X");
        dataSet.setColor(Color.parseColor(lineColor));
        dataSet.setLineWidth(2f);
        dataSets.add(dataSet);
    }

    /**
     * 追加数据
     * @param val 数据
     * @param line 折线id
     */
    public void addData(float val,int line){

        //获取所有的图标数据
        LineData data =chart.getData();

        //获取指定折线id的数据
        ILineDataSet dataSet=data.getDataSetByIndex(line);

        //获取最后一个X轴的坐标
        int lastXIndex=dataSet.getEntryCount();

        //追加数据
        data.addEntry(new Entry(lastXIndex,val),line);

        //通知线性表数据已经更新
        chart.notifyDataSetChanged();

        //设置X轴的显示间隔，数据滚动更新
        chart.setVisibleXRange(4,8);

        //移动到最后一个X轴的位置
        chart.moveViewToX(lastXIndex);
    }

    public LineData getData(){
        return chart.getData();
    }

    public LineChart getChart(){
        return chart;
    }

    public ArrayList<ILineDataSet> getDataSets(){
        return dataSets;
    }
}
