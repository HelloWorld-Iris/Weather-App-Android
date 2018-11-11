package com.proj.zyr.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.proj.zyr.bean.City;
import com.proj.zyr.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IrisZYR on 2018/10/17
 */
public class MyApplication extends Application{
    private static final String TAG="MyAPP";
    private static MyApplication mApplication;
    private CityDB mCityDB;

    private List<City> mCityList;

    public void onCreate(){
        super.onCreate(); //调用基类的onCreate方法
        Log.d(TAG,"MyApplication->Oncreate");
        mApplication=this;

        mCityDB=openCityDB(); //调用openCityDB方法
        initCityList();

    }

    private void initCityList(){
        mCityList=new ArrayList<City>();//实例化变量
        new Thread(new Runnable() {//创建子线程
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList(){
        mCityList=mCityDB.getAllCity();
        int i=0;
        for(City city:mCityList){   //遍历City类型的数组mCityList，并且把每次遍历的内容赋值给City类型的city中
            i++;
            String cityName=city.getCity();
            String cityCode=city.getNumber();
           // Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }

    public List<City> getmCityList(){
        return mCityList;
    }

    public static MyApplication gentleInstance(){
        return mApplication;
    }

    private CityDB openCityDB(){
        String path="/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator+getPackageName()
                +File.separator+"databases1"
                +File.separator
                +CityDB.CITY_DB_NAME;  //数据库的路径

        File db=new File(path); //打开路径下的文件
        Log.d(TAG,path);
        if (!db.exists()){
            String pathfolder="/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator+getPackageName()
                    +File.separator+"databases1"
                    +File.separator;  //如果路径不存在就打开上一级路径
            File dirFirstFolder=new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs(); //如果上一级路径还不存在就创建一个目录
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db does not exists");
            try {
                InputStream is=getAssets().open("city.db");//打开数据库文件
                FileOutputStream fos=new FileOutputStream(db); //在所在的路径下创建一个数据库文件
                int len=-1;
                byte[] buffer=new byte[1024];
                while ((len=is.read(buffer))!=-1){
                    fos.write(buffer,0,len);//将city.db中的内容写入到路径下新创建的数据库文件中
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }


        return  new CityDB(this,path);//实例化一个匿名变量并且调用构造方法
        //Context是个抽象类，通过类的结构可以看到：Activity、Service、Application都是Context的子类
    }
}
