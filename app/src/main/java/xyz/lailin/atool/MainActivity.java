package xyz.lailin.atool;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import xyz.lailin.atool.Storage.FileStorage;

import static android.R.attr.max;
import static android.R.attr.value;


public class MainActivity extends AppCompatActivity {

    private TextView textValX;
    private TextView  textValY;
    private TextView  textValZ;

    private RatingBar rate;

    private ChartView chart;
    LineChart mLineChart;

    private List<String> accData=new ArrayList<String>();

    private boolean isSaveData=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textValX=(TextView)findViewById(R.id.valueX);
        textValY=(TextView)findViewById(R.id.valueY);
        textValZ=(TextView)findViewById(R.id.valueZ);

        mLineChart=(LineChart)findViewById(R.id.chart);

        chart=new ChartView(mLineChart);

        //加速度传感器初始化
        final Acceleration acceleration=new Acceleration();

        //文件保存
        final FileStorage fileStorage = new FileStorage("ATool");

        //获取switch btn
        final Switch switchButton=(Switch) findViewById(R.id.switchButton);

        //通过switch btn的状态设置是否监听
        switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    acceleration.register();
                    toast("测试开始！");
                }else {
                    acceleration.unRegister();
                    if (isSaveData){
                        fileStorage.addData(accData);
                        toast("测试结束！文件已保存在："+fileStorage.getFile().getPath());
                    }else {
                        toast("测试结束！");
                    }
                }
                switchButton.setChecked(isChecked);
            }
        });


        final Switch isSaveSwitch=(Switch) findViewById(R.id.isSave);
        isSaveSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSaveData=isChecked;
            }
        });

        //设置曲线是否显示
        Switch switchX=(Switch) findViewById(R.id.switchX);
        Switch switchY=(Switch) findViewById(R.id.switchY);
        Switch switchZ=(Switch) findViewById(R.id.switchZ);
        lineControl(switchX,0);
        lineControl(switchY,1);
        lineControl(switchZ,2);

        //舒适度设置
        rate=(RatingBar) findViewById(R.id.rate);

    }

    public void restart(View v){
        accData=new ArrayList<>();
        chart.reset();
        chart=null;
        chart=new ChartView(mLineChart);
    }

    private void lineControl(Switch mSwitch, final int id){
        mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chart.getDataSets().get(id).setVisible(isChecked);
            }
        });
    }

    private void toast(String string){
        Toast toast = Toast.makeText(MainActivity.this, string, Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * 加速度传感器的相关设置，以及监听
     */
    public class Acceleration {

        /**
         * 上一次的值
         */
        private float[] prevData=new float[3];

        private float comfort=0.0f;

        /**
         * 传感器控制
         */
        private SensorManager mSensorManager=null;

        /**
         * 传感器
         */
        private Sensor mSensor=null;

        /**
         * 构造函数，传感器初始化
         */
        Acceleration(){

            //获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象
            mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

            //通过SensorManager获取相应的（加速度感应器）Sensor类型对象
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        /**
         * 注册加速度传感器
         */
        void register(){
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        /**
         * 取消注册加速度传感器
         */
        void unRegister(){
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        }

        /**
         * 加速度传感器监听方法
         */
        private final SensorEventListener mSensorEventListener=new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    float x=event.values[0];
                    float y=event.values[1];
                    float z=event.values[2];

                    if (prevData!=null){
                        comfort=0f;
                        for (int i = 0; i < 3; i++) {
                            if(Math.abs(prevData[i]-event.values[i])>comfort){
                                comfort=Math.abs(prevData[i]-event.values[i]);
                            }
                        }
                        Log.i("test:",(-comfort+5f)+"");
                        setComfortRate(rate);
                    }

                    System.arraycopy(event.values, 0, prevData, 0, 3);


                /*显示左右、前后、垂直方向加速度*/
                    DecimalFormat decimalFormat=new DecimalFormat("00.00");
                    accData.add(decimalFormat.format(x)+"\t"+decimalFormat.format(y)+"\t"+decimalFormat.format(z));
                    textValX.setText(decimalFormat.format(x));
                    textValY.setText(decimalFormat.format(y));
                    textValZ.setText(decimalFormat.format(z));

                    chart.addData(x,0);
                    chart.addData(y,1);
                    chart.addData(z,2);




                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }
        };


        /**
         * 获取当前的舒适状况
         */
        void  setComfortRate(RatingBar rate){
            float r=0f;
            if(-(comfort-5f)>0){
                r=-comfort+5f;
            }
            rate.setRating(r);
        }



    }
}
