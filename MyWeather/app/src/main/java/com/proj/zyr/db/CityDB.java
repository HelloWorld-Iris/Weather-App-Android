package com.proj.zyr.db;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.proj.zyr.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IrisZYR on 2018/10/17
 */
public class CityDB {
    public static final String CITY_DB_NAME="city.db";
    private static final String CITY_TABLE_NAME="city";
    private SQLiteDatabase db;

    public CityDB(Context context,String path){//Context是个抽象类，通过类的结构可以看到：Activity、Service、Application都是Context的子类
        db=context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);//在这个路径下打开数据库

    }

    public List<City> getAllCity(){
        List<City> list=new ArrayList<City>();
        Cursor c=db.rawQuery("SELECT * from "+CITY_TABLE_NAME,null);
        while(c.moveToNext()){
            String province=c.getString(c.getColumnIndex("province"));
            String city=c.getString(c.getColumnIndex("city"));
            String number=c.getString(c.getColumnIndex("number"));
            String allPY=c.getString(c.getColumnIndex("allpy"));
            String allFirstPY=c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY=c.getString(c.getColumnIndex("firstpy"));
            City item=new City(province,city,number,firstPY,allPY,allFirstPY);
            list.add(item);
        }
        return list;
    }
}
