package com.proj.zyr.myweather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.proj.zyr.Adapter.ViewPagerAdapter;
import com.proj.zyr.LocationListener.MyLocationListener;
import com.proj.zyr.app.MyApplication;
import com.proj.zyr.bean.City;
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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {    //构建一个MainActivity类，继承自Activity类，用于与用户交互，
                                                                                   // 且要实现一个监听器，用来监听按键是否被触发

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ViewPagerAdapter vpAdapter;
    private ViewPager mvp;
    private List<View> views;

    private ImageView dots[];
    private int iddot[]={R.id.dot1,R.id.dot2};

    private ImageView mUpdateBtn; //定义ImageView控件
    private ImageView mLationBtn;

    private TextView otherweather[];
    private TextView otherday[];
    private TextView otherwind[];
    private TextView otherclimate[];
    private ImageView otherweaimg[];
    private int weather[]={R.id.weather,R.id.weather1,R.id.weather2,R.id.weather3};
    private int dat[]={R.id.weekday,R.id.weekday1,R.id.weekday2,R.id.weekday3};
    private int wind[]={R.id.wind_info,R.id.wind_info1,R.id.wind_info2,R.id.wind_info3};
    private int climate[]={R.id.degreeC,R.id.degreeC1,R.id.degreeC2,R.id.degreeC3};
    private int weaimg[]={R.id.weather_otherday,R.id.weather_otherday1,R.id.weather_otherday2,R.id.weather_otherday3};

    private ImageView mCityState;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;  //定义TestView控件
    private ImageView weatherImg, pmImg;
    private ProgressBar mprogressbar;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private String locdistrict;
    private List<City> data=new ArrayList<City>();


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

        mprogressbar=(ProgressBar)findViewById(R.id.title_update_bar);

        mLationBtn=(ImageView)findViewById(R.id.title_location);
        mLationBtn.setOnClickListener(this);

        mLocationClient=new LocationClient(getApplicationContext());

        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            requestLocation();
        }

        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                    locdistrict=bdLocation.getDistrict();
                }else if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                    locdistrict=bdLocation.getDistrict();
                }else{
                    locdistrict=String.valueOf(bdLocation.getLocType());
                }

            }
        });

        if (Netutil.getNetworkState(this) != Netutil.NETWORN_NONE) {//调用Netutil类查看网络状况
            Log.d("myWeather", "Network OK");
            Toast.makeText(MainActivity.this, "Network OK!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "No Netowrk!");
            Toast.makeText(MainActivity.this, "No Network!", Toast.LENGTH_LONG).show();
        }

        initView();
        initVP();
        initDots();
        initFutureWeather();
    }

    private void initVP(){//用于动态加载viewpager中的视图
        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<View>();
        views.add(inflater.inflate(R.layout.vp1,null));
        views.add(inflater.inflate(R.layout.vp2,null));
        //views.add(inflater.inflate(R.layout.vp3,null));
        vpAdapter=new ViewPagerAdapter(views,this);
        mvp=(ViewPager) findViewById(R.id.viewpager);
        mvp.setAdapter(vpAdapter);
        mvp.setOnPageChangeListener(this);
    }

    void initDots(){
        dots=new ImageView[views.size()];
        for (int i=0;i<views.size();i++){
            dots[i]=(ImageView) findViewById(iddot[i]);
        }
    }

    void initFutureWeather(){
        SharedPreferences weatherinfo=(SharedPreferences) getSharedPreferences("shared",MODE_PRIVATE);
        otherweather=new TextView[4];
        otherday=new TextView[4];
        otherweaimg=new ImageView[4];
        otherclimate=new TextView[4];
        otherwind=new TextView[4];

        for(int a=0;a<weaimg.length;a++){
            if(a<2){
                otherweather[a]=(TextView)views.get(0).findViewById(weather[a]);
                otherday[a]=(TextView)views.get(0).findViewById(dat[a]);
                otherclimate[a]=(TextView)views.get(0).findViewById(climate[a]);
                otherwind[a]=(TextView)views.get(0).findViewById(wind[a]);
                otherweaimg[a]=(ImageView)views.get(0).findViewById(weaimg[a]);
                otherweather[a].setText(weatherinfo.getString("futurehigh"+a,"")+"-"+weatherinfo.getString("futurelow"+a,""));
                otherday[a].setText(weatherinfo.getString("futuredate"+a,""));
                otherwind[a].setText(weatherinfo.getString("futurefengxiang"+a,""));
                otherclimate[a].setText(weatherinfo.getString("futuretype"+a,""));
                otherweaimg[a].setImageResource(weatherinfo.getInt("otherweaing"+a,R.drawable.biz_plugin_weather_qing));

            }else{
                otherweather[a]=(TextView)views.get(1).findViewById(weather[a]);
                otherday[a]=(TextView)views.get(1).findViewById(dat[a]);
                otherclimate[a]=(TextView)views.get(1).findViewById(climate[a]);
                otherwind[a]=(TextView)views.get(1).findViewById(wind[a]);
                otherweaimg[a]=(ImageView)views.get(1).findViewById(weaimg[a]);
                otherweather[a].setText(weatherinfo.getString("futurehigh"+a,"")+"-"+weatherinfo.getString("futurelow"+a,""));
                otherday[a].setText(weatherinfo.getString("futuredate"+a,""));
                otherwind[a].setText(weatherinfo.getString("futurefengxiang"+a,""));
                otherclimate[a].setText(weatherinfo.getString("futuretype"+a,""));
                otherweaimg[a].setImageResource(weatherinfo.getInt("otherweaing"+a,R.drawable.biz_plugin_weather_qing));
            }


        }
    }

    void initView() {
        SharedPreferences weatherinfo=(SharedPreferences) getSharedPreferences("shared",MODE_PRIVATE);

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

        int id=weatherinfo.getInt("wetherImg",R.drawable.biz_plugin_weather_qing);
        weatherImg.setImageResource(id);
        int idpm=weatherinfo.getInt("pmImg",R.drawable.biz_plugin_weather_0_50);
        pmImg.setImageResource(idpm);
        city_name_Tv.setText(weatherinfo.getString("city","")+"天气");
        cityTv.setText(weatherinfo.getString("city",""));
        timeTv.setText(weatherinfo.getString("updatetime",""));
        humidityTv.setText(weatherinfo.getString("shidu",""));
        pmDataTv.setText(weatherinfo.getString("pm25",""));
        weekTv.setText(weatherinfo.getString("date",""));
        temperatureTv.setText(weatherinfo.getString("high","")+"-"+weatherinfo.getString("low",""));
        climateTv.setText(weatherinfo.getString("type",""));
        windTv.setText(weatherinfo.getString("fengli",""));
        pmQualityTv.setText(weatherinfo.getString("quality",""));



    }

    private void requestLocation(){
        mLocationClient.start();
        Toast.makeText(this,locdistrict,Toast.LENGTH_SHORT).show();

    }

    void updateBar(boolean update){
        if(update==true){
            mprogressbar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);
        }else if (update==false){
            mprogressbar.setVisibility(View.INVISIBLE);
            mUpdateBtn.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) { //按钮出发后自动调用此方法

        if (view.getId()==R.id.title_city_manager){//如果所触发的事件是点击了选择城市的button

            Intent i=new Intent(this,SelectCity.class); //就创建一个Intent用于本类和SelectCity类之间的相互传递信息
            i.putExtra("cityname",city_name_Tv.getText().toString().substring(0,city_name_Tv.getText().toString().length()-2));
            startActivityForResult(i,1); //i用于携带将跳转至下一个界面中使用的数据， 第二个参数：如果> = 0,当Activity结束时requestCode将归还在onActivityResult()中。以便确定返回的数据是从哪个Activity中返回，用来标识目标activity。
             //发起方回收数据



        }

        if (view.getId() == R.id.title_update_btn) {  //如果所触发的事件是点击了更新页shared面的button
            boolean update=true;
            updateBar(update);
            SharedPreferences sharedPreferences = getSharedPreferences("", MODE_PRIVATE);//获得一个SharedPreferences对象，第一个参数为对象文件的名字， 第二个参数为对此对象的操作权限，MODE_PRIVATE权限是指只能够被本应用所读写。
            String city = sharedPreferences.getString("city", "北京");//从文件中获取main_city_code的值，如果文件中没有，就直接赋值为后面的101010100
            String cityCode=findCityCode(city);
            Log.d("myWeather", cityCode);

            if (Netutil.getNetworkState(this) != Netutil.NETWORN_NONE) {  //检查网络是否畅通
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);//如果网络畅通，就调用queryWeatherCode方法，获取城市代码为cityCode的天气xml信息
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
            }
        }

        if (view.getId()==R.id.title_location){
            requestLocation();
            boolean update=true;
            updateBar(update);
            String cityCode="";
            if (locdistrict==null){
                cityCode=findCityCode("北京");
            }else{
                cityCode=findCityCode(locdistrict.substring(0,locdistrict.length()-1));
            }


            if (Netutil.getNetworkState(this) != Netutil.NETWORN_NONE) {  //检查网络是否畅通
                Log.d("myWeather", "网络OK");

                queryWeatherCode(cityCode);


                if (locdistrict==null){
                    Log.d("asdasd","kongkongkon");
                }

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

    private void SharePreference(TodayWeather todayWeather1){
        SharedPreferences weatherdatas=(SharedPreferences)getSharedPreferences("shared",MODE_PRIVATE);
        SharedPreferences.Editor editor=weatherdatas.edit();

        editor.putString("city",todayWeather1.getCity());
        editor.putString("updatetime",todayWeather1.getUpdatetime());
        editor.putString("wendu",todayWeather1.getWendu());
        editor.putString("shidu",todayWeather1.getShidu());
        editor.putString("pm25",todayWeather1.getPm25());
        editor.putString("quality",todayWeather1.getQuality());
        editor.putString("fengxiang",todayWeather1.getFengxiang());
        editor.putString("fengli",todayWeather1.getFengli());
        editor.putString("date",todayWeather1.getDate());
        editor.putString("high",todayWeather1.getHigh());
        editor.putString("low",todayWeather1.getLow());
        editor.putString("type",todayWeather1.getType());

        String[] futurefengxiang=todayWeather1.getFuturefengxiang();
        String[] futuredate=todayWeather1.getFuturedate();
        String[] futurehigh=todayWeather1.getFuturehigh();
        String[] futurelow=todayWeather1.getFuturelow();
        String[] futuretype=todayWeather1.getFuturetype();
        for (int a=0;a<4;a++){
           editor.putString("futurefengxiang"+a,futurefengxiang[a]);
           editor.putString("futuredate"+a,futuredate[a]);
           editor.putString("futurehigh"+a,futurehigh[a]);
           editor.putString("futurelow"+a,futurelow[a]);
           editor.putString("futuretype"+a,futuretype[a]);
        }

        editor.commit();
    }

    private String findCityCode(String cityname){
        String cityCode="";
        MyApplication myApplication=(MyApplication) getApplication();
        data=myApplication.getmCityList();
        for(City city : data){
            if(city.getCity().equals(cityname)){
                cityCode=city.getNumber();

            }
        }
        return cityCode;
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
        String ffxiang[],fhigh[],flow[],ftype[],fdate[];
        ffxiang=new String[4];
        fhigh=new String[4];
        flow=new String[4];
        ftype=new String[4];
        fdate=new String[4];

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
                        Log.d("start","start!!!");
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
                            } else if (xmlPullParser.getName().equals("fengxiang") ) {//获取第一次出现fengxiang标签的值
                                eventType = xmlPullParser.next();
                                if(fengxiangCount==0){
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                }else{
                                    ffxiang[fengliCount-1]=xmlPullParser.getText();
                                }
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli")&& fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                eventType = xmlPullParser.next();
                                if(dateCount==0){
                                    todayWeather.setDate(xmlPullParser.getText());
                                }else{
                                    fdate[dateCount-1]=xmlPullParser.getText();
                                }
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                if(highCount==0){
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());//substring指截掉首字母起长度为2的字母，trim指去掉空格
                                }else{
                                    fhigh[highCount-1]=xmlPullParser.getText().substring(2).trim();
                                }
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")) {
                                eventType = xmlPullParser.next();
                                if(lowCount==0){
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                }else{
                                    flow[lowCount-1]=xmlPullParser.getText().substring(2).trim();
                                    Log.d("low!!!","ok");
                                }
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type")&&typeCount<=8) {
                                eventType = xmlPullParser.next();
                                if(typeCount==0){
                                    todayWeather.setType(xmlPullParser.getText());
                                }else if(typeCount>=2){
                                    ftype[(typeCount/2-1)]=xmlPullParser.getText();
                                }
                                typeCount++;
                            }
                        }
                        todayWeather.setFuturefengxiang(ffxiang);
                        todayWeather.setFuturedate(fdate);
                        todayWeather.setFuturehigh(fhigh);
                        todayWeather.setFuturelow(flow);
                        todayWeather.setFuturetype(ftype);
                        SharePreference(todayWeather);
                        Log.d("SSSAVE","Saved!!!!!!!!");
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
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "-" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力" + todayWeather.getFengli());

        SharedPreferences weatherdatas=(SharedPreferences)getSharedPreferences("shared",MODE_PRIVATE);
        SharedPreferences.Editor editor=weatherdatas.edit();

        String fwind[]=todayWeather.getFuturefengxiang();
        String fdate[]=todayWeather.getFuturedate();
        String fhigh[]=todayWeather.getFuturehigh();
        String flow[]=todayWeather.getFuturelow();
        String ftype[]=todayWeather.getFuturetype();

        for(int b=0;b<4;b++){
            otherwind[b].setText(fwind[b]);
            otherday[b].setText(fdate[b]);
            otherweather[b].setText(ftype[b]);
            otherclimate[b].setText(fhigh[b]+"-"+flow[b]);

            if (ftype[b].equals(null)){
                Log.d("FTYPE###","nulllnulllnulll");
            }
            if(ftype[b].equals("暴雪")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_baoxue);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_baoxue);
            }
            if(ftype[b].equals("暴雨")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_baoyu);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_baoyu);
            }
            if(ftype[b].equals("大暴雨")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if(ftype[b].equals("大雪")){
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_daxue);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_daxue);
            }
            if(ftype[b].equals("多云")){
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_duoyun);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_duoyun);
            }
            if(ftype[b].equals("雷阵雨")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if(ftype[b].equals("雷阵雨冰雹")){
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if(ftype[b].equals("晴")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_qing);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_qing);
            }
            if(ftype[b].equals("沙尘暴")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_shachenbao);
            }
            if(ftype[b].equals("特大暴雨")){
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if(ftype[b].equals("雾")) {
                otherweaimg[b].setImageResource(R.drawable.biz_plugin_weather_wu);
                otherweaimg[b].setTag(R.drawable.biz_plugin_weather_wu);
            }

            editor.putInt("otherweaing"+b,(int)otherweaimg[b].getTag());

        }


        if(todayWeather.getPm25()!=null){
            int pm2_5=Integer.parseInt(todayWeather.getPm25());
            if(pm2_5<=50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
                pmImg.setTag(R.drawable.biz_plugin_weather_0_50);
            }
            if(pm2_5>50&&pm2_5<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
                pmImg.setTag(R.drawable.biz_plugin_weather_51_100);
            }
            if(pm2_5>100&&pm2_5<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
                pmImg.setTag(R.drawable.biz_plugin_weather_101_150);
            }
            if(pm2_5>150&&pm2_5<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
                pmImg.setTag(R.drawable.biz_plugin_weather_151_200);
            }
            if(pm2_5>200&&pm2_5<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                pmImg.setTag(R.drawable.biz_plugin_weather_201_300);
            }
            if(pm2_5>300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                pmImg.setTag(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        String climate=todayWeather.getType();
        if(climate.equals("暴雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            weatherImg.setTag(R.drawable.biz_plugin_weather_baoxue);
        }
        if(climate.equals("暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            weatherImg.setTag(R.drawable.biz_plugin_weather_baoyu);
        }
        if(climate.equals("大暴雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            weatherImg.setTag(R.drawable.biz_plugin_weather_dabaoyu);
        }
        if(climate.equals("大雪")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            weatherImg.setTag(R.drawable.biz_plugin_weather_daxue);
        }
        if(climate.equals("多云")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            weatherImg.setTag(R.drawable.biz_plugin_weather_duoyun);
        }
        if(climate.equals("雷阵雨")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            weatherImg.setTag(R.drawable.biz_plugin_weather_leizhenyu);
        }
        if(climate.equals("雷阵雨冰雹")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            weatherImg.setTag(R.drawable.biz_plugin_weather_leizhenyubingbao);
        }
        if(climate.equals("晴")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            weatherImg.setTag(R.drawable.biz_plugin_weather_qing);
        }
        if(climate.equals("沙尘暴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            weatherImg.setTag(R.drawable.biz_plugin_weather_shachenbao);
        }
        if(climate.equals("特大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            weatherImg.setTag(R.drawable.biz_plugin_weather_tedabaoyu);
        }
        if(climate.equals("雾")) {
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            Log.d("tagtagtag",String.valueOf(R.drawable.biz_plugin_weather_wu));
            weatherImg.setTag(R.drawable.biz_plugin_weather_wu);
        }

        if (pmImg.getTag()==null){
            editor.putInt("pmImg",R.drawable.biz_plugin_weather_0_50);
        }else{
            editor.putInt("pmImg",(int)pmImg.getTag());
        }
        if (weatherImg.getTag()==null){
            editor.putInt("wetherImg",R.drawable.biz_plugin_weather_daxue);
        }else{
            editor.putInt("wetherImg",(int)weatherImg.getTag());
        }


        editor.commit();






        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();

        boolean update=false;
        updateBar(update);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        for (int a=0; a<iddot.length;a++){
            if(a==i){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
