package com.proj.zyr.LocationListener;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.proj.zyr.bean.TodayWeather;
import com.proj.zyr.myweather.MainActivity;

/**
 * Created by IrisZYR on 2018/11/9
 */
public class MyLocationListener implements BDLocationListener {
    @Override
    public void onReceiveLocation(final BDLocation bdLocation) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        String addr = bdLocation.getAddrStr();    //获取详细地址信息
        String country = bdLocation.getCountry();    //获取国家
        String province = bdLocation.getProvince();    //获取省份
        String city = bdLocation.getCity();    //获取城市
        String district = bdLocation.getDistrict();    //获取区县
        String street = bdLocation.getStreet();    //获取街道信

    }

//    public String getDistrict(){
//        return district;
//    }

}
