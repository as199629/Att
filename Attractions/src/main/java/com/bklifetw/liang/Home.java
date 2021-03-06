package com.bklifetw.liang;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity implements View.OnClickListener {


    private List<String> permissionsList = new ArrayList<String>();
    public static String lang = "zh_tw";
    private static long lastClickTime;
    private static long FAST_CLICK_DELAY_TIME=1000;
    private RecyclerView recyclerView;
    private TextView mTxtResult;
    private TextView mDesc;
    private ArrayList<HashMap<String, String>> arrayList;
    private List<java.util.Map> mList;
    private SwipeRefreshLayout laySwipe;
    private int total;
    private int total_hotel;
    private TextView t_count;
    private TextView u_loading;
    private LinearLayout li01;
    private int nowposition = 0;
    private int t_total = 0;
    private String Description;
    private int opda_index=1;
    private TextView attractions_textLYT;
    private TextView attractions_text2LYT;
    private TextView hotel_textLYT;
    private TextView hotel_text2LYT;
    private com.bklifetw.liang.RecyclerAdapter adapter;
    static final ArrayList<Post> mData = new ArrayList<>();
    static final ArrayList<Post> mData_hotel = new ArrayList<>();
    private Dialog weatherDlg;
    private TextView weatherLat;
    private TextView weatherLon;
    private TextView weatherData;
    private TextView weatherPic;
    private ImageView weatherimg;
    private TextView weatherName;
    public static String BaseUrl = "https://api.openweathermap.org/";
    private String iconurl;
    public static String AppId = "fdc4017e4b347f6fb6c30881430e2e20";
    private String list_city;
    private double list_latitude;
    private double list_longitude;
    private TextView selete_text;
    private ProgressDialog progDlg;
    private Button go_map;



    //???????????????????????????
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private LinearLayout weather_li00;
    private TextView weather_ctiy;
    private TextView weather_temperature;
    private TextView weather_temperature_High;
    private TextView weather_temperature_Low;
    private TextView weather_humidity;
    private TextView weather_pressure;
    private TextView weather_situation;
    private ImageView img_home01;
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private TableRow table_name;
    private SearchView searchView;
    private boolean mData_flag=false;
    private boolean mData_hotel_flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkRequiredPermission(this);     //  ??????SDK??????, ????????????????????????.
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2206);
        setupViewComponent();
    }

    private void setupViewComponent() {
        ImageView imageViewtest2206=(ImageView)findViewById(R.id.imagetest2206);

        ImageView imageViewtest =(ImageView)findViewById(R.id.imagetest);
        Glide.with(this)
                .asBitmap()
                .load("https://www.weblink.idv.tw/wp-content/uploads/2017/10/%E7%B8%AE%E5%9C%96.jpg")
//                .error(R.drawable.no_result)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageViewtest2206.setImageBitmap(resource);
                        imageViewtest2206.buildDrawingCache();
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });



        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 5 / 100; // ??????ScrollView???????????????4/5

        selete_text=(TextView)findViewById(R.id.selete_text);
        li01 = (LinearLayout) findViewById(R.id.li01);
        li01.setVisibility(View.GONE);//???????????????
        weather_li00=(LinearLayout)findViewById(R.id.weather_li00);
        weather_li00.setVisibility(View.GONE);


        mTxtResult = findViewById(R.id.m2206_name);
        mDesc = findViewById(R.id.m2206_descr);
        //textview ????????????????????????
        mDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        mDesc.scrollTo(0, 0);//textview ?????????
        recyclerView = findViewById(R.id.recyclerView);
        t_count = findViewById(R.id.count);
        img_home01=(ImageView)findViewById(R.id.img_home01);


        attractions_textLYT=(TextView)findViewById(R.id.attractions_text);
        attractions_textLYT.setOnClickListener(this);
        attractions_text2LYT=(TextView)findViewById(R.id.attractions_text2);
        attractions_text2LYT.setOnClickListener(this);
        go_map=(Button)findViewById(R.id.go_map);
        go_map.setOnClickListener(this);


        weather_ctiy=(TextView)findViewById(R.id.weather_ctiy);//??????
        weather_temperature=(TextView)findViewById(R.id.weather_temperature);//??????
        weather_temperature_High=(TextView)findViewById(R.id.weather_temperature_High);//?????????
        weather_temperature_Low=(TextView)findViewById(R.id.weather_temperature_Low);//?????????
        weather_humidity=(TextView)findViewById(R.id.weather_humidity);//??????
        weather_pressure=(TextView)findViewById(R.id.weather_pressure);//??????
        weather_situation=(TextView)findViewById(R.id.weather_situation);//??????


        table_name=(TableRow)findViewById(R.id.table_name);


        hotel_textLYT=(TextView)findViewById(R.id.hotel_text);
        hotel_textLYT.setOnClickListener(this);
        hotel_text2LYT=(TextView)findViewById(R.id.hotel_text2);
        hotel_text2LYT.setOnClickListener(this);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                li01.setVisibility(View.GONE);
                weather_li00.setVisibility(View.GONE);
                table_name.setVisibility(View.VISIBLE);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //--------------???????????????-----------
        u_loading = (TextView)findViewById(R.id.u_loading);//??????????????????
        u_loading.setVisibility(View.GONE);
        //-------------------------------------
        laySwipe = (SwipeRefreshLayout)findViewById(R.id.laySwipe);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setSize(SwipeRefreshLayout.LARGE);
        // ????????????????????????????????????????????????
        laySwipe.setDistanceToTriggerSync(100);
        // ???????????????????????????
        laySwipe.setProgressBackgroundColorSchemeColor(getColor(android.R.color.tertiary_text_dark));
        // ??????????????????????????????????????????1????????????
        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_purple,
                android.R.color.holo_orange_dark);

/*        setProgressViewOffset : ?????????????????????????????????
        ?????????????????????????????????????????????
        ??????????????????????????????????????????????????????????????????
        ??????????????????????????????????????????????????????????????????*/
        laySwipe.setProgressViewOffset(true, 0, 500);
//=====================
        onSwipeToRefresh.onRefresh();  //????????????????????????
        //-------------------------




    }

    public ArrayList<Post> getmData(int opda_index)
    {
        //???????????? ????????? ??????????????????
        if (opda_index==1)
        {
            return this.mData;
        }
        else
        {
            return this.mData_hotel;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.attractions_text:
                if(adapter!=null)
                {
                    adapter.getFilter().filter("");//?????????????????????
                }
                opda_index=1;
                onSwipeToRefresh.onRefresh();  //????????????????????????

                break;
            case R.id.hotel_text:
                if(adapter!=null)
                {
                    adapter.getFilter().filter("");//?????????????????????
                }

                opda_index=2;
                onSwipeToRefresh.onRefresh();  //????????????????????????
                break;


            case R.id.attractions_text2:
                opda_index=1;
                adapter.getFilter().filter("");
                adapter = new com.bklifetw.liang.RecyclerAdapter(opda_index,this, mData);
                adapter_click_setAdap();
                break;
            case R.id.hotel_text2:
                opda_index=2;
                adapter.getFilter().filter("");
                adapter = new com.bklifetw.liang.RecyclerAdapter(opda_index,this, mData_hotel);
                adapter_click_setAdap();
                break;
            case R.id.go_map:
                Intent it = new Intent();
                it.setClass(com.bklifetw.liang.Home.this, com.bklifetw.liang.Map.class);
                Bundle bundle = new Bundle();
                bundle.putInt("OPDA_INDEX", opda_index);
                it.putExtras(bundle);
                startActivity(it);
                break;
        }

    }

    private final SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh=
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //??????????????? ???????????????
                    //-------------------------------------
                    mTxtResult.setText("");
                    com.bklifetw.liang.MyAlertDialog myAltDlg = new com.bklifetw.liang.MyAlertDialog(com.bklifetw.liang.Home.this);
                    if(opda_index==1)
                        myAltDlg.setTitle(getString(R.string.dialog_title01));
                    else if(opda_index==2)
                        myAltDlg.setTitle(getString(R.string.dialog_title02));

                    myAltDlg.setMessage(getString(R.string.dialog_t001) + getString(R.string.dialog_b001));
                    myAltDlg.setIcon(android.R.drawable.ic_menu_rotate);
                    myAltDlg.setCancelable(false);
                    myAltDlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_positive), altDlgOnClkPosiBtnLis);
                    myAltDlg.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_neutral), altDlgOnClkNeutBtnLis);
                    myAltDlg.show();
//------------------------------------
                }
            };
    private DialogInterface.OnClickListener altDlgOnClkPosiBtnLis=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            //-----------------??????????????????----------------
            laySwipe.setRefreshing(true);
            u_loading.setVisibility(View.VISIBLE);
            mTxtResult.setText(getString(R.string.m2206_name) + "");
            mDesc.setText("");
            mDesc.scrollTo(0, 0);//textview ?????????
            show_ProgDlg();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

//                    =================================
                    setDatatolist();
//                  =================================
//----------SwipeLayout ?????? --------
//???????????????????????? u_importopendata()
                    if(opda_index==1)
                    {
                        if(mData.size()!=0)
                        {
                            attractions_textLYT.setVisibility(View.GONE);
                            attractions_text2LYT.setVisibility(View.VISIBLE);
                            go_map.setVisibility(View.VISIBLE);
                            selete_text.setVisibility(View.GONE);
                            img_home01.setVisibility(View.GONE);
                        }

                    }
                    else
                    {
                        if(mData_hotel.size()!=0)
                        {
                            hotel_textLYT.setVisibility(View.GONE);
                            hotel_text2LYT.setVisibility(View.VISIBLE);
                            go_map.setVisibility(View.VISIBLE);
                            selete_text.setVisibility(View.GONE);
                            img_home01.setVisibility(View.GONE);
                        }

                    }


                    laySwipe.setVisibility(View.VISIBLE);
                    u_loading.setVisibility(View.GONE);


                    laySwipe.setRefreshing(false);

                    progDlg.cancel();
                    Toast.makeText(getApplicationContext(), getString(R.string.loadover), Toast.LENGTH_SHORT).show();
                }
            }, 1000);  //10???
        }
    };
    private void show_ProgDlg() {
        progDlg = new ProgressDialog(this);
        progDlg.setTitle("?????????");
        progDlg.setMessage("???????????????");
        progDlg.setIcon(android.R.drawable.presence_away);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.setMax(100);
        progDlg.show();
    }

    private void setDatatolist() {
        //==================================
        u_importopendata();  //??????Opendata
        //???mysql??????
        //==================================
        //??????Adapter to ????????? view recyleview scrollview ??????
        //???post ???????????????
        //latout ?????????
        //Rrcycler  ViewHolder ?????????????????????
             for (java.util.Map m : mList) {
                if (m != null) {

                    String Name = m.get("Name").toString().trim(); //??????
                    String Add = m.get("Add").toString().trim(); //??????
                    String Picture1 = m.get("Picture1").toString().trim(); //??????
                    if (Picture1.isEmpty() || Picture1.length() < 1) { //???????????????
                        Picture1 = "https://bklifetw.com/img/nopic1.jpg";
                    }
                    String Description = m.get("Description").toString().trim(); //??????
                    String Zipcode = m.get("Zipcode").toString().trim(); //??????
                    String Latitude = m.get("lat").toString().trim();;//??????
                    String Longitude= m.get("lon").toString().trim();;//??????
                    String Ticketinfo= m.get("Ticketinfo").toString().trim();//????????????
                    if (Ticketinfo.isEmpty() || Ticketinfo.length() < 1) { //????????????
                        Ticketinfo = "???";
                    }
                    String Opentime= m.get("Opentime").toString().trim();;//????????????
                    if (Opentime.isEmpty() || Opentime.length() < 1) { //????????????
                        Opentime = "???";
                    }


                    String Picdescribe1= m.get("Picdescribe1").toString().trim();;//????????????
                    String Tel= m.get("Tel").toString().trim();;//??????





    //************************************************************
                    if(opda_index==1 && !mData_flag)
                    {

                        mData.add(new Post(Name, Picture1, Add, Description, Zipcode,
                                Latitude,Longitude,Ticketinfo,Opentime,Picdescribe1,Tel));
                    }
                    else if(opda_index==2 && !mData_hotel_flag)
                    {
                        mData_hotel.add(new Post(Name, Picture1, Add, Description, Zipcode,
                                Latitude,Longitude,Ticketinfo,Opentime,Picdescribe1,Tel));
                    }


    //************************************************************
                } else {
                    return;
                }
            }

        if(opda_index==1)
        {
            adapter = new com.bklifetw.liang.RecyclerAdapter(opda_index,this, mData);
            mData_flag=true;
        }
        else if(opda_index==2)
        {
            adapter = new com.bklifetw.liang.RecyclerAdapter(opda_index,this, mData_hotel);
            mData_hotel_flag=true;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter_click_setAdap();
    }
    //???????????? ?????? ?????????
    private void adapter_click_setAdap()
    {
        //???????????? ??????
        adapter.setOnItemClickListener(new com.bklifetw.liang.RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                li01.setVisibility(View.VISIBLE);
                weather_li00.setVisibility(View.VISIBLE);
                table_name.setVisibility(View.GONE);


                if(opda_index==1){
                    mTxtResult.setText(getString(R.string.m2206_name) + mData.get(position).Name);
                    mDesc.setText(mData.get(position).Content);
                }
                else if(opda_index==2){
                    mTxtResult.setText(getString(R.string.m2206_name) + mData_hotel.get(position).Name);
                    mDesc.setText(mData_hotel.get(position).Content);
                }


                mDesc.scrollTo(0, 0); //textview ?????????



                nowposition = position;



                if(isFastClick())
                {
                    show_weather(nowposition);//?????????
                }
            }
        });
//********************************* ****
        recyclerView.setAdapter(adapter);
    }
    //????????????
    public static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = true;//??????1000?????? true
        }
        lastClickTime = currentClickTime;
        return flag;
    }
    private void show_weather(int position) {
        //?????????????????????
        if(opda_index==1)
        {
            list_city=mData.get(position).Name;
            list_latitude = Double.parseDouble(mData.get(position).Latitude);
            list_longitude = Double.parseDouble(mData.get(position).Longitude);
        }
        else if(opda_index==2)
        {
            list_city=mData_hotel.get(position).Name;
            list_latitude = Double.parseDouble(mData_hotel.get(position).Latitude);
            list_longitude = Double.parseDouble(mData_hotel.get(position).Longitude);

        }

//                            String aa=location_to_address(list_latitude,list_longitude);
        weatherDlg = new Dialog(com.bklifetw.liang.Home.this);
        weatherDlg.setTitle("test");
        weatherDlg.setCancelable(true);
        weatherDlg.setContentView(R.layout.home_weather_dlg);
        weatherLat = (TextView)findViewById(R.id.weather_lat);
        weatherLon = (TextView)findViewById(R.id.weather_lon);
        weatherData = (TextView)findViewById(R.id.weather_status);
        weatherPic = (TextView)findViewById(R.id.show_pic);
//        Button gomap=(Button)findViewById(R.id.home_dlg_go_map);
//        gomap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent it = new Intent();
//                it.setClass(M2206.this, M1908.class);
//
//                Bundle bundle = new Bundle();
//
//                bundle.putDouble("HOME_LAT", list_latitude);
//                bundle.putDouble("HOME_LON", list_longitude);
//
//                it.putExtras(bundle);
//
//                startActivity(it);
//            }
//        });

//        Button close = (Button) weatherDlg.findViewById(R.id.home_dlg_close);
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weatherDlg.cancel();
//            }
//        });
        weatherimg = (ImageView)findViewById(R.id.weather_img);
        weatherName = (TextView)findViewById(R.id.weather_name);

        getCurrentData();
    }

    private void getCurrentData() {
//        ProgressDialog pd = new ProgressDialog(M2206.this);
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setTitle("Internet");
//        pd.setMessage("Loading.........");
//        pd.setIndeterminate(false);
//        pd.show();
//***************************************************************
/*
Retrofit ???????????? Square ????????????????????? RESTful API ???????????????????????????????????????
???????????????????????? okHttp ???okHttp ???????????? okHttp ?????????
Retrofit ???????????????????????????????????? API ???????????? JSON?????????????????????????????????????????? Converter???
?????????????????? Server ????????????????????????????????? okHttpClient ?????? Interceptor???
??? Retrofit 1.9.0 ??? Interceptor ?????????????????????
*/
//***************************************************************
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.bklifetw.liang.WeatherService service = retrofit.create(com.bklifetw.liang.WeatherService.class);

//        retrofit2.Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, lang, AppId);
        retrofit2.Call<WeatherResponse> call = service.getCurrentWeatherData(list_latitude+"", list_longitude+"", lang, AppId);

        call.enqueue(new retrofit2.Callback<WeatherResponse>() {
            @Override
            public void onResponse(retrofit2.Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    //********??????????????????????????????*****************************



//                    weatherDlg.show();
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    weather_ctiy.setText(list_city);//??????
                    weather_temperature.setText((int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) + "C??");//??????
                    weather_temperature_High.setText((int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15)+ "C??");//?????????
                    weather_temperature_Low.setText((int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) + "C??");//?????????
                    weather_humidity.setText(weatherResponse.main.humidity+"");//??????
                    weather_pressure.setText(weatherResponse.main.pressure+"");//??????
                    weather_situation.setText(weatherResponse.weather.get(0).description);//??????
//                    String stringBuilder =
//                            getString(R.string.weather_name) +list_city+
//                                    "\n" +
////                            getString(R.string.areaname) + weatherResponse.name +
////                            "\n" +
//                                    getString(R.string.Temperature) +
//// --------------- K?????????????????C??-------------------
//                                    (int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) + "C??" +
//                                    "\n" +
//                                    getString(R.string.Temperature_Max) + (int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15) + "C??" +
//                                    "\n" +
//                                    getString(R.string.Temperature_Min) + (int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) + "C??" +
//                                    "\n" +
//                                    getString(R.string.Humidity) +
//                                    weatherResponse.main.humidity +
//                                    "\n" +
//                                    getString(R.string.Pressure) +
//                                    weatherResponse.main.pressure;
//                    weatherData.setText(stringBuilder); //??????
////====????????????==============
//                    weatherLat.setText(getString(R.string.weather_lat) + (list_latitude));
//                    weatherLon.setText(getString(R.string.weather_lon) + (list_longitude));
                    //====??????????????????==============

//                    weatherName.setText(getString(R.string.weather_name) + tranlocationName(lat, lon));
//======?????? Internet ??????==================
                    int b_id = weatherResponse.weather.get(0).id;
                    String b_main = weatherResponse.weather.get(0).main;
                    String b_description = weatherResponse.weather.get(0).description;
                    String b_icon = weatherResponse.weather.get(0).icon;
                    iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";  //icon?????????
// iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";
// https://openweathermap.org/img/wn/50n@2x.png
                    int cc = 1;
//                    String weather = "\n" +
//                            getString(R.string.w_description) + b_description;
////                            "\n" ;
////                            getString(R.string.w_icon) +
////                            "\n" +
////                            iconurl;
////=========================
//                    weatherData.append(weather);

                    Glide.with(com.bklifetw.liang.Home.this)
                            .load(iconurl)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                            .override(200, 200)
//                            .transition(withCrossFade())
//                            .error(
//                                    Glide.with(mContext)
//                                            .load("https://bklifetw.com/img/nopic1.jpg"))
                            .into(weatherimg);
//                    pd.cancel();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {

            }
        });


    }


    //==========================================================
    private void u_importopendata() { //??????Opendata
        try {
            String Task_opendata="";
            if(opda_index==1)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/scenic_spot_C_f.json").get();   //???????????? - ??? ??????????????????
            }
            else if(opda_index==2)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/hotel_C_f.json").get();   //???????????? - ??? ??????????????????
            }

//-------?????? json   ??????????????????-------------
            mList = new ArrayList<java.util.Map>();
            JSONObject json_obj1 = new JSONObject(Task_opendata);
            JSONObject json_obj2 = json_obj1.getJSONObject("XML_Head");
            JSONObject infos = json_obj2.getJSONObject("Infos");
            JSONArray info = infos.getJSONArray("Info");
//            total = 0;
//            total_hotel=0;
            t_total = info.length(); //?????????
//------JSON ??????----------------------------------------
            info = sortJsonArray(info);
//            total = info.length(); //????????????
            t_count.setText(getString(R.string.ncount) + total + "/" + t_total);
//----------------------------------------------------------
            //-----??????????????????-----
            if(opda_index==1){
                total = info.length();
            }else if(opda_index==2){
                total_hotel = info.length();
            }

            t_count.setText(getString(R.string.ncount) + total);
            for (int i = 0; i < info.length(); i++) {
                java.util.Map item = new HashMap<String, Object>();
                String Name = info.getJSONObject(i).getString("Name");
                if(opda_index==1)
                {
                    if(info.getJSONObject(i).getString("Toldescribe").trim()==info.getJSONObject(i).getString("Description"))
                    {
                        Description = info.getJSONObject(i).getString("Toldescribe");
                    }
                    else
                    {
                        Description = info.getJSONObject(i).getString("Toldescribe")+info.getJSONObject(i).getString("Description");
                    }
                }
                else if(opda_index==2)
                {
                    Description = info.getJSONObject(i).getString("Description");
                }

                String Ticketinfo="";
                try {
                    Ticketinfo=info.getJSONObject(i).getString("Ticketinfo");//????????????

                }catch (Exception e){

                }


                String Opentime="";
                try {
                    Opentime=info.getJSONObject(i).getString("Opentime");//????????????
                }catch (Exception e){

                }

//                String Description = info.getJSONObject(i).getString("Description");
                String Add = info.getJSONObject(i).getString("Add");
                String Picture1 = info.getJSONObject(i).getString("Picture1");
                String Zipcode = info.getJSONObject(i).getString("Zipcode"); //????????????,
                String lat=info.getJSONObject(i).getString("Py");//??????
                String lon=info.getJSONObject(i).getString("Px");//??????

                String Picdescribe1=info.getJSONObject(i).getString("Picdescribe1");//????????????
                String Tel=info.getJSONObject(i).getString("Tel");//??????




                item.put("Name", Name);
                item.put("Description", Description);
                item.put("Add", Add);
                item.put("Picture1", Picture1);
                item.put("Zipcode", Zipcode);
                item.put("lat", lat);
                item.put("lon", lon);

                item.put("Ticketinfo", Ticketinfo);
                item.put("Opentime", Opentime);
                item.put("Picdescribe1", Picdescribe1);
                item.put("Tel", Tel);


                mList.add(item);
//-------------------
            }
            t_count.setText(getString(R.string.ncount) + total + "/" + t_total);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//----------SwipeLayout ?????? --------
    }

    //-----------------------------
    private JSONArray sortJsonArray(JSONArray jsonArray) {//County??????????????????Method
        final ArrayList<JSONObject> json = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {  //???????????????ArrayList json???
            try {

                //============================
//                Zipcode????????????,Picture1??????????????????
                if (
                        //??????????????????????????????
//                        jsonArray.getJSONObject(i).getString("Zipcode").trim().length() > 0 //????????????
                                    jsonArray.getJSONObject(i).getString("Picture1").trim().length() > 0  //?????????????????????
                                &&    !jsonArray.getJSONObject(i).getString("Picture1").trim().trim().equals("null") //???????????????????????????
                ) {
                    json.add(jsonArray.getJSONObject(i));
                }
                //============================
                //??????????????????????????????
//                json.add(jsonArray.getJSONObject(i));//????????????
            } catch (JSONException jsone) {
                jsone.printStackTrace();
            }
        }
        //---------------------------------------------------------------
        Collections.sort(json, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonOb1, JSONObject jsonOb2) {
                        String lidZipcode = "", ridZipcode = "";
                        try {
                            lidZipcode = jsonOb1.getString("Zipcode");
                            ridZipcode = jsonOb2.getString("Zipcode");
                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }
                        return lidZipcode.compareTo(ridZipcode);
                    }
                }
        );

        return new JSONArray(json);//????????????????????????array
    }

    private DialogInterface.OnClickListener altDlgOnClkNeutBtnLis=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                //?????? ?????????????????????
            if(opda_index==1)
            {
                opda_index=2;
            }
            else if(opda_index==2)
            {
                opda_index=1;
            }
            u_loading.setVisibility(View.GONE);
            laySwipe.setRefreshing(false);
        }
    };

    private void checkRequiredPermission(Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }

    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {//????????????layout?????????
        getMenuInflater().inflate(R.menu.main, menu);



        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    /**??????RecyclerView??????Filter??????*/
                    /**SearchView???????????????????????????????????????*/
                    if(adapter!=null)
                    {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                    return false;
                }
            });






        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                this.finish();



                break;
            case R.id.menu_top:
                nowposition = 0;
                recyclerView.scrollToPosition(nowposition);
                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                break;

            case R.id.menu_next:
                nowposition = nowposition + 100;
                if (nowposition > total - 1) {
                    nowposition = total - 1;
                }
                recyclerView.scrollToPosition(nowposition);
                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                break;

            case R.id.menu_back:
                nowposition = nowposition - 100;
                if (nowposition < 0) {
                    nowposition = 0;
                }
                recyclerView.scrollToPosition(nowposition);
                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                break;

            case R.id.menu_end:
                if(opda_index==1){
                    nowposition = total - 1;
                }else if(opda_index==2){
                    nowposition = total_hotel - 1;
                }

                recyclerView.scrollToPosition(nowposition);
//                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                break;
            case R.id.menu_load:
                onSwipeToRefresh.onRefresh();  //????????????????????????
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    //*********************************************************************
    private class TransTask extends AsyncTask<String, Void, String> {
        String ans;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();

                //??????????????????????????? if url ???

                String aa = line;
                Integer bb=0;
                while (line != null) {
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ans = sb.toString();
            //------------
            return ans;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseJson(s);
        }

        private void parseJson(String s) {
        }
    }
}