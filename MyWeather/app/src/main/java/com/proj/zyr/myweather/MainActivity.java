package com.proj.zyr.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.zyr.bean.TodayWeather;
import com.proj.zyr.util.Netutil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener {    //构建一个MainActivity类，继承自Activity类，用于与用户交互，
                                                                                   // 且要实现一个监听器，用来监听按键是否被触发

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn; //定义ImageView控件

    private ImageView mCityState;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;  //定义TestView控件
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {  //实例化一个Handler类型的变量，并重写了handleMessage方法
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj); //调用updateTodayWeather方法来更新数据
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {  //重写onCreate方法
        super.onCreate(savedInstanceState); //调用基类中的onCreate方法，savedInstanceState是保存当前Activity的状态信息
        setContentView(R.layout.weather_info);  //设置所用的布局是weather_info

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);  //mUpdateBtn这个控件作为一个按钮通过id找到所用的布局中的图，且此控件用于点击刷新
        mUpdateBtn.setOnClickListener(this);  //设置所在的MainActivity类为此控件的监听器

        mCityState=(ImageView)findViewById(R.id.title_city_manager);  //选择城市的控件通过id找到所用的图
        mCityState.setOnClickListener(this); //设置监听器为本类

        if (Netutil.getNetworkState(this) != Netutil.NETWORN_NONE) {//调用Netutil类查看网络状况
            Log.d("myWeather", "Network OK");
            Toast.makeText(MainActivity.this, "Network OK!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "No Netowrk!");
            Toast.makeText(MainActivity.this, "No Network!", Toast.LENGTH_LONG).show();
        }



        initView();
    }

    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_date);
        pmDataTv = (TextView) findViewById(R.id.pm_date);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.tempreture);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

    }

    public void onClick(View view) { //按钮出发后自动调用此方法

        if (view.getId()==R.id.title_city_manager){//如果所触发的事件是点击了选择城市的button
            Intent i=new Intent(this,SelectCity.class); //就创建一个Intent用于本类和SelectCity类之间的相互传递信息
            startActivityForResult(i,1); //i用于携带将跳转至下一个界面中使用的数据， 第二个参数：如果> = 0,当Activity结束时requestCode将归还在onActivityResult()中。以便确定返回的数据是从哪个Activity中返回，用来标识目标activity。
             //发起方回收数据
        }

        if (view.getId() == R.id.title_update_btn) {  //如果所触发的事件是点击了更新页面的button
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);//获得一个SharedPreferences对象，第一个参数为对象文件的名字， 第二个参数为对此对象的操作权限，MODE_PRIVATE权限是指只能够被本应用所读写。
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");//从文件中获取main_city_code的值，如果文件中没有，就直接赋值为后面的101010100
            Log.d("myWeather", cityCode);

            if (Netutil.getNetworkState(this) != Netutil.NETWORN_NONE) {  //检查网络是否畅通
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);//如果网络畅通，就调用queryWeatherCode方法，获取城市代码为cityCode的天气xml信息
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected  void onActivityResult(int requestCode,int resultCode,Intent data){//Intent发起方实现方法回收数据，自动执行
        if (requestCode==1 && resultCode==RESULT_OK){
            String newCityCode=data.getStringExtra("cityCode"); //将data中名字为“cityCode”的信息取出来
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if (Netutil.getNetworkState(this)!=Netutil.NETWORN_NONE){
                Log.d("myWeather","Network ok~");
                queryWeatherCode(newCityCode);//调用这个方法，使用新的城市编码，重新刷新天气信息
            }else{
                Log.d("myWeather","No Netowrk Avilable");
                Toast.makeText(MainActivity.this,"no network",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() { //创建一个子线程
            @Override
            public void run() {

                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address); //定义URL地址
                    con = (HttpURLConnection) url.openConnection(); //通过地址打开连接
                    con.setRequestMethod("GET"); //设置获取方式为“GET”
                    con.setConnectTimeout(8000); //设置连接超时时间为8000毫秒
                    con.setReadTimeout(8000);  //设置读取超时时间为8000毫秒
                    InputStream in = con.getInputStream();//得到网络返回的输入流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));//定义BufferReader，为其它in提供缓冲功能
                    StringBuilder response = new StringBuilder();//StringBuilder是一个可变字符序列
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);//字符串连接
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString(); //构建一个与缓冲器内容相同的字符串
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr); //调用parseXML方法来解析xml信息
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();  //定义一个信息msg用于子线程与主线程通信
                        msg.what = UPDATE_TODAY_WEATHER; //传递的信息代码，即告诉主线程要更新天气信息
                        msg.obj = todayWeather; //传递的对象，即将更新的信息传递给主线程
                        mHandler.sendMessage(msg); //发送给mHandler信息msg

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();

    }

    private TodayWeather parseXML(String xmldata) { //解析xml数据
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance(); //创建生产XML的pull解析器的工厂
            XmlPullParser xmlPullParser = fac.newPullParser(); //使用工厂获取pull解析器
            xmlPullParser.setInput(new StringReader(xmldata)); //使用解析器读取当前的xml流
            int eventType = xmlPullParser.getEventType();//获取当前事件的状态
            Log.d("myWeather", "paseXML");
            /**
               * 我们知道pull解析是以事件为单位解析的因此我们要获取一开始的解析标记type，之后通过type判断循环来读取文档
               * 注意：当解析器开始读取xmldata的时候已经开始了，指针type在xml的第一行开始。pull解析是指针从第一行开始读取到最后一行以事件为单位读取的解析方式
               */
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) { //回应？？
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {//获取第一次出现fengxiang标签的值
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli")&& fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());//substring指截掉首字母起长度为2的字母，trim指去掉空格
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) { //获取更新的天气数据，并且赋值给各种文字控件
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText("空气质量"+todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "-" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力" + todayWeather.getFengli());
        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
    }
}
