package com.proj.zyr.myweather;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.MenuPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.zyr.Adapter.SearchAdapter;
import com.proj.zyr.Adapter.cityAdapter;
import com.proj.zyr.app.MyApplication;
import com.proj.zyr.bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IrisZYR on 2018/10/17
 */
public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private TextView titleName;
    private ListView mListView;
    private EditText mEdittext;
    private cityAdapter cityadapter;
    private SearchAdapter msearchCityAdapter;
    private List<City> data=new ArrayList<City>();


    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city); //布局文件为select_city.xml

        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);//设置监听器为本类

        titleName=(TextView)findViewById(R.id.title_name);

        mListView=(ListView)findViewById(R.id.title_list);

        mEdittext=(EditText)findViewById(R.id.title_search);

        Intent j=getIntent();
        titleName.setText("当前城市："+j.getStringExtra("cityname"));

        initViews();


        cityadapter=new cityAdapter(SelectCity.this,R.layout.select_city_item,data);

        mListView.setAdapter(cityadapter);

        mEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                msearchCityAdapter=new SearchAdapter(SelectCity.this,data);
                mListView.setTextFilterEnabled(true);
                if(data.size()<1||TextUtils.isEmpty(s)){
                    mListView.setAdapter(cityadapter);
                }else{
                    mListView.setAdapter(msearchCityAdapter);
                    msearchCityAdapter.getFilter().filter(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city;
                if(msearchCityAdapter!=null){
                    city=(City) msearchCityAdapter.getItem(position);

                }else{
                    //Toast.makeText(SelectCity.this,"你单击了"+position,Toast.LENGTH_SHORT).show();
                    city=data.get(position);
                }

                titleName.setText("当前城市："+city.getCity());
                Intent i=new Intent();
                i.putExtra("cityCode",city.getNumber()); //i中保存cityCode的值为101160101
                setResult(RESULT_OK,i);//在意图跳转的目的地界面调用这个方法把Activity想要返回的数据返回到主Activity

                Log.d("chengshi bianma ",city.getNumber());
            }
        });

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                //Intent i=new Intent();
                //i.putExtra("cityCode","101160101"); //i中保存cityCode的值为101160101
                //setResult(RESULT_OK,i);//在意图跳转的目的地界面调用这个方法把Activity想要返回的数据返回到主Activity
                finish();//结束当前activity的生命周期
                break;
            default:
                break;
        }
    }



    private void initViews(){
        MyApplication myApplication=(MyApplication) getApplication();
        data=myApplication.getmCityList();

    }


}
