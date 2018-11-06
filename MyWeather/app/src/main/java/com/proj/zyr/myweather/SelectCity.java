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
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private List<Map<String,Object>> data2=new ArrayList<Map<String, Object>>();
    private List<Map<String,Object>> searchResult=new ArrayList<Map<String,Object>>();

    TextWatcher mTextWatcher=new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //AdapterView<String> aAdapter=new ArrayAdapter<String>(this,R.layout.select_city_item,searchResult);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp=s;
            if(s.length()==1){

                Log.d("myapp","ontextchanged"+temp);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //mEdittext.setOnClickListener(new AdapterView.OnItemClickListener(){});

        }
    };

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city); //布局文件为select_city.xml

        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);//设置监听器为本类

        titleName=(TextView)findViewById(R.id.title_name);

        mListView=(ListView)findViewById(R.id.title_list);

        mEdittext=(EditText)findViewById(R.id.title_search);

        mEdittext.addTextChangedListener(mTextWatcher);
        initViews();
        initSearch();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this,"你单击了"+position,Toast.LENGTH_SHORT).show();
                Map<String,Object> map=data.get(position);
                String newcityCode=map.get("number").toString();
                titleName.setText("当前城市："+map.get("city").toString());
                Intent i=new Intent();
                i.putExtra("cityCode",newcityCode); //i中保存cityCode的值为101160101
                setResult(RESULT_OK,i);//在意图跳转的目的地界面调用这个方法把Activity想要返回的数据返回到主Activity

                Log.d("chengshi bianma ",newcityCode);
            }
        });

//        searchCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            //当点击搜索按钮时触发该方法
//            public boolean onQueryTextSubmit(String query) {
//                return false;
////            }
//
//            @Override
//            //当搜索内容改变时触发该方法
//            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText)){
//                    mListView.setFilterText(newText);
//
//                }else{
//                    mListView.clearTextFilter();
//                }
//                return false;
//            }
//        });




    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                //Intent i=new Intent();
                //i.putExtra("cityCode","101160101"); //i中保存cityCode的值为101160101
                //setResult(RESULT_OK,i);//在意图跳转的目的地界面调用这个方法把Activity想要返回的数据返回到主Activity
                finish();
                break;
            default:
                break;
        }
    }

    private void initViews(){
        MyApplication myApplication=(MyApplication) getApplication();
        List<City> cityList=myApplication.getmCityList();

        for(City city:cityList){   //遍历City类型的数组mCityList，并且把每次遍历的内容赋值给City类型的city中
            Map<String,Object> item=new HashMap<String, Object>();
            String cityName=city.getCity();
            item.put("city",cityName);
            String cityCode=city.getNumber();
            item.put("number",cityCode);
            data.add(item);


        }
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(myApplication.gentleInstance(),android.R.layout.simple_list_item_1,a);
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.select_city_item,new String[]{"city"},new int[]{R.id.city_name});
        mListView.setAdapter(adapter);
    }

    private void initSearch(){
        MyApplication myApplication=(MyApplication) getApplication();
        List<City> cityList=myApplication.getmCityList();

        for(City city:cityList){   //遍历City类型的数组mCityList，并且把每次遍历的内容赋值给City类型的city中
            Map<String,Object> item2=new HashMap<String, Object>();
            String cityFPY=city.getFirstPY();
            String cityAFPY=city.getAllFristPY();
            String cityPY=city.getAllPY();
            String cityName=city.getCity();
            item2.put("city",cityName);
            item2.put("firstPY",cityFPY);
            item2.put("firstAllPY",cityAFPY);
            item2.put("AllPY",cityPY);
            data2.add(item2);

        }

        SimpleAdapter adapter2=new SimpleAdapter(this,data2,R.layout.select_city_item,new String[]{"city","firstPY","firstAllPY","AllPY" },new int[] {R.id.city_name});
        mListView.setAdapter(adapter2);
    }

}
