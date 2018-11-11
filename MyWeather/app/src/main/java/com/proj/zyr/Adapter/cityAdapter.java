package com.proj.zyr.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.proj.zyr.bean.City;
import com.proj.zyr.myweather.R;

import java.util.List;

/**
 * Created by IrisZYR on 2018/11/8
 */
public class cityAdapter extends ArrayAdapter<City> {
    private int resourceID;
    public cityAdapter(Context context, int textViewResourceID, List<City> objects){
        super(context,textViewResourceID,objects);
        resourceID=textViewResourceID;
    }

    public View getView(int position, View convertview, ViewGroup parent){
        City city=getItem(position);
        View view;
        if(convertview==null){
            view= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        }else {
            view=convertview;
        }
        TextView province=(TextView) view.findViewById(R.id.province);
        TextView cityname=(TextView)view.findViewById(R.id.city_name);
        province.setText(city.getProvince());
        cityname.setText(city.getCity());
        return view;
    }
}
