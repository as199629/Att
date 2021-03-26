package com.bklifetw.liang;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap map;
    private Context mContext;

    static LatLng VGPS = new LatLng(24.172127, 120.610313);
    float currentZoom = 8;
    private static String[][] locations = {
            {"我的位置", "24.172127,120.610313"},
            {"中區職訓", "24.172127,120.610313"},
            {"東海大學路思義教堂", "24.179051,120.600610"},
            {"台中公園湖心亭", "24.144671,120.683981"},
            {"秋紅谷", "24.1674900,120.6398902"},
            {"台中火車站", "24.136829,120.685011"},
            {"國立科學博物館", "24.1579361,120.6659828"}};

    private static String[] mapType = {
            "街道圖",
            "衛星圖",
            "地形圖",
            "混合圖",
            "開啟路況",
            "關閉路況"};

    private Spinner mSpnLocation, mSpnMapType;
    double dLat, dLon;
    double latitude;
    double longitude;
    private BitmapDescriptor image_des;// 圖標顯示
    private int icosel = 0; //圖示旗標
    //----GPS------------
    private TextView txtOutput;
    private TextView tmsg;
    private Marker markerMe;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private LocationManager locationManager;
    private Location currentLocation;
    private String provider = null; // 提供資料
    long minTime = 5000;// ms
    float minDist = 1.0f;// meter
    private float Anchor_x=0.7f;
    private float Anchor_y=0.6f;


    private final String TAG = "oldpa=>";
    //-----------------所需要申請的權限數組---------------

    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Menu menu;
    private MenuItem m_traceon;
    private MenuItem m_traceoff;
    private int tracesel = 0;
    private HashMap<String, Object> hashMapMarker = new HashMap<String, Object>();
    private int location_no = 0;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    private ArrayList<LatLng> mytrace;// 追蹤我的位置
    private CheckBox checkBox;
    private ScrollView controlScroll;
    private int resID1;
    private int i=0;
    private int position=0;



    private ImageView img1908;
    private Bitmap[] bitmap;
    private int ii=0;
    BitmapDescriptor[] aaa = new BitmapDescriptor[1];
    private ArrayList<BitmapDescriptor> bitmapDescriptorArrayList;
    private RequestOptions options;
    private int sleep_millis=2000;
    private ArrayList<Post> mdata;
    private String ans_Url;
    private ImageView img;
    private int a;
    private int opda_index;

    //----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.m1908);

            //------------設定MapFragment-----------------------------------
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //-------------------------------------------------------

            u_checkgps();  //檢查GPS是否開啟
            setupViewComponent();
        }catch (Exception e){
            finish();
        }

    }



    //--------------------
    private void setupViewComponent() {

        mContext = getApplicationContext();
        mSpnLocation = (Spinner) this.findViewById(R.id.spnLocation);
        mSpnMapType = (Spinner) this.findViewById(R.id.spnMapType);
        // -----------------------------------------------------------------
        txtOutput = (TextView) findViewById(R.id.txtOutput);
        tmsg = (TextView) findViewById(R.id.msg);
        img1908 = (ImageView)findViewById(R.id.imagetest);

        // ----------------------------------------------------------------
        checkBox = (CheckBox) this.findViewById(R.id.checkcontrol);
        controlScroll = (ScrollView) this.findViewById(R.id.Scroll01);
        checkBox.setOnCheckedChangeListener(chklistener);
        controlScroll.setVisibility(View.INVISIBLE);



        Bundle bundle = this.getIntent().getExtras();
        opda_index=bundle.getInt("OPDA_INDEX");//取得首頁是景點還是旅館
        Home home=new Home();//new一個 首頁的物件
        mdata=home.getmData(opda_index);//呼叫 home的方法  傳值給mdata



//        icosel = 0;  //設定圖示初始值
        // ---------------
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item);

        for (int i = 0; i < locations.length; i++)
            adapter.add(locations[i][0]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLocation.setAdapter(adapter);
        // 指定事件處理物件
        mSpnLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//               Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_LONG).show();

                map.clear();
//                mytrace = null; ////若要點景點就清除軌跡圖寫此在處
                showloc();
//=====================================
//                if (position > 0) setMapLocation();  //218
//=====================================
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // ---------------
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < mapType.length; i++)
            adapter.add(mapType[i]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnMapType.setAdapter(adapter);
        //-----------設定ARGB 透明度----
//        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x00FFFFFF)); //全透明
        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x80FFFFFF)); //50%透明
        mSpnLocation.setPopupBackgroundDrawable(new ColorDrawable(0x80FFFFFF)); //50%透明
//        # ARGB依次代表透明度（alpha）、紅色(red)、綠色(green)、藍色(blue)
//        100% — FF       95% — F2        90% — E6        85% — D9
//        80% — CC        75% — BF        70% — B3        65% — A6
//        60% — 99        55% — 8C        50% — 80        45% — 73
//        40% — 66        35% — 59        30% — 4D        25% — 40
//        20% — 33        15% — 26        10% — 1A         5% — 0D         0% — 00
        //---------------
        mSpnMapType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 道路地圖。
                        break;
                    case 1:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // 衛星空照圖
                        break;
                    case 2:
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // 地形圖
                        break;
                    case 3:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 道路地圖混合空照圖
                        break;
                    case 4:
                        map.setTrafficEnabled(true); //開啟路況
                        break;
                    case 5:
                        map.setTrafficEnabled(false); //關閉路況
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private CheckBox.OnCheckedChangeListener chklistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checkBox.isChecked()) {
                controlScroll.setVisibility(View.VISIBLE);
            } else {
                controlScroll.setVisibility(View.GONE);
            }
        }
    };


    private void setMapLocation() {
        showloc(); //刷新所有景點
        int iSelect = mSpnLocation.getSelectedItemPosition();
        String[] sLocation = locations[iSelect][1].split(",");
        double dLat = Double.parseDouble(sLocation[0]);    // 南北緯
        double dLon = Double.parseDouble(sLocation[1]);    // 東西經
        String vtitle = locations[iSelect][0];
        //--- 設定所選位置之當地圖示 ---//
        image_des = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN); //使用系統水滴
        VGPS = new LatLng(dLat, dLon);
        map.addMarker(new MarkerOptions()
                .position(VGPS)
                .title(vtitle)
                .snippet("座標:" + dLat + "," + dLon)
                .icon(image_des));// 顯示圖標文字
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        onCameraChange(map.getCameraPosition());//把鏡頭的中間已到我點的地方
    }

    private void onCameraChange(CameraPosition cameraPosition) {
        //  目前地圖的倍數 zoom 儲存
        tmsg.setText("目前的zoom" + map.getCameraPosition().zoom);

    }

    private void showloc() {
        if (map != null) map.clear();  //218
        bitmap=new Bitmap[mdata.size()-1];
        Handler handler=new Handler();
        // 將所有景點位置顯示
//        for (int i = 1; i < ArrList_lat.size(); i++) {
//            dLat = Double.parseDouble(ArrList_lat.get(i)); // 南北緯
//            dLon = Double.parseDouble(ArrList_lon.get(i)); // 東西經
//            String vtitle = locations[i][0];



//            vtitle=vtitle+"#"+resID1; //案水滴閃退這邊錯
            if(icosel==0)
            {

                for (int i = 1; i < mdata.size(); i++) {
                   dLat = Double.parseDouble(mdata.get(i).Latitude); // 南北緯
                   dLon = Double.parseDouble(mdata.get(i).Longitude); // 東西經
                   VGPS = new LatLng(dLat, dLon);// 更新成欲顯示的地圖座標
                   map.addMarker(new MarkerOptions()
                                   .position(VGPS)
                                   .alpha(0.9f)
                                   .title("." + "vtitle")
                                   .snippet("緯度:" + String.valueOf(dLat) + "\n經度:" + String.valueOf(dLon))
//                            .infoWindowAnchor(0.5f, 0.9f)
                                   .infoWindowAnchor(Anchor_x, Anchor_y)
                                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) // 顯示圖標文字
                   );
               }

            }
            else if(icosel==1)
            {

                //變圓形
                options = new RequestOptions()
                        .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .priority(Priority.NORMAL);

                    i=0;
                    handler.post(updata_map);



            //--------------------使用自定義式窗-------------------------------------------------------



        }
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//外圓內方


    }
    private Runnable updata_map=new Runnable() {
        @Override
        public void run() {
            double dLat = Double.parseDouble(mdata.get(i).Latitude);    // 南北緯
            double dLon = Double.parseDouble(mdata.get(i).Longitude);    // 東西經



            String ans_Url = mdata.get(i).posterThumbnailUrl;
            if (ans_Url.getBytes().length == ans_Url.length() ||
                    ans_Url.getBytes().length > 100) {
                ans_Url = ans_Url;//不包含中文，不做處理
            } else {
//    ans_Url = utf8Togb2312(post.posterThumbnailUrl);
                ans_Url = utf8Togb2312(ans_Url).replace("http://", "https://");
            }

            options = new RequestOptions()
                    .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                    .placeholder(R.mipmap.ic_launcher)
//                    .error(R.drawable.error_img)
                    .priority(Priority.NORMAL);

//            VGPS = new LatLng(dLat, dLon);// 更新成欲顯示的地圖座標
//            map.addMarker(new MarkerOptions()
//                            .position(VGPS)
//                            .alpha(0.9f)
//                            .title("." + "vtitle")
//                            .snippet("緯度:" + String.valueOf(dLat) + "\n經度:" + String.valueOf(dLon))
//                            .infoWindowAnchor(Anchor_x, Anchor_y)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))); // 顯示圖標文字



            Glide.with(mContext)
                    .asBitmap()
                    .load(ans_Url)
                    .override(50,50)
                    .error(R.drawable.error_img)
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if(resource.equals(""))
                            {
                                //error的話就不會跑這裡
                            }
                            VGPS = new LatLng(dLat, dLon);// 更新成欲顯示的地圖座標
                            map.addMarker(new MarkerOptions()
                                            .position(VGPS)
                                            .alpha(0.9f)
                                            .title("." + "vtitle")
                                            .snippet("緯度:" + String.valueOf(dLat) + "\n經度:" + String.valueOf(dLon))
                                            .infoWindowAnchor(Anchor_x, Anchor_y)
                                            .icon(BitmapDescriptorFactory.fromBitmap(resource)) // 顯示圖標文字
                            );
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            int a=0;
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);

                        }


                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }

                    });

            i++;
            if(i<mdata.size())
                hander.postDelayed(updata_map,0);
            if(i==mdata.size()-1)
            {

                hander.removeCallbacks(updata_map);
            }

        }

    };

    private Handler hander=new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    break;
                default:
                    //其他想做的事情
                    break;
            }
        }
    };

    //--------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        mUiSettings = map.getUiSettings();//
//        開啟 Google Map 拖曳功能
        map.getUiSettings().setScrollGesturesEnabled(true);
//        右下角的導覽及開啟 Google Map功能
        map.getUiSettings().setMapToolbarEnabled(true);
//        左上角顯示指北針，要兩指旋轉才會出現
        map.getUiSettings().setCompassEnabled(true);
//        右下角顯示縮放按鈕的放大縮小功能
        map.getUiSettings().setZoomControlsEnabled(true);
        // --------------------------------
        map.addMarker(new MarkerOptions().position(VGPS).title("中區職訓"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        //----------取得定位許可-----------------------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //----顯示我的位置ICO-------
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "GPS定位權限未允許 將無法使用地圖 \n如果你同意後沒反應 請在試一次", Toast.LENGTH_LONG).show();


        }


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //典籍 window時
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                img1908.setImageDrawable(null);
            }
        });

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        //---------------------------------------------
    }

    public void setZoomButtonsEnabled(View v) {
        if (!checkReady()) return;
        // -------- 地圖縮放 ------------------------------------------
        map.getUiSettings().setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    public void setCompassEnabled(View v) {
        //        左上角顯示指北針，要兩指旋轉才會出現
        if (!checkReady()) return;
        map.getUiSettings().setCompassEnabled(((CheckBox) v).isChecked());

    }

    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //----顯示我的位置ICO------
            map.setMyLocationEnabled(((CheckBox) v).isChecked());
        } else {
            Toast.makeText(getApplicationContext(), "GPS定位權限未允許", Toast.LENGTH_LONG).show();
        }
    }

    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) return;
        //---可用捲動手勢操控 , 用手指平移或捲動來拖曳地圖
        map.getUiSettings().setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) return;
        //---縮放手勢按兩下按一下或兩指拉大拉小 ---
        map.getUiSettings().setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) return;
//---傾斜手勢改變地圖的傾斜角度兩指上下拖曳來增加 / 減少傾斜角度 ---
        map.getUiSettings().setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) return;
        //旋轉手勢兩指旋轉地圖 ---
        map.getUiSettings().setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //-------提示使用者開啟GPS定位------------------------------------
    private void u_checkgps() {
        // 取得系統服務的LocationManager物件
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 顯示對話方塊啟用GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("定位管理")
                    .setMessage("GPS目前狀態是尚未啟用.\n"
                            + "請問你是否現在就設定啟用GPS?")
                    .setPositiveButton("啟用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 使用Intent物件啟動設定程式來更改GPS設定
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("不啟用", null).create().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // ============ GPS =================
    //** onMyLocationButtonClick  位置變更狀態監視/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "返回GPS目前位置", Toast.LENGTH_LONG).show();
        return true;
    }

    //*********************************************/
    /* 檢查GPS 是否開啟 */
    private boolean initLocationProvider() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }
        return false;
    }

    //-------------------
    private void nowaddress() {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(provider);
                updateWithNewLocation(location);
//            finish();
                return;
            }
// 取得上次已知的位置

// 監聽 GPS Listener----------------------------------
// long minTime = 5000;// ms
// float minDist = 5.0f;// meter
//---網路和GPS來取得定位，因為GPS精準度比網路來的更好，所以先使用網路定位、
// 後續再用GPS定位，如果兩者皆無開啟，則跳無法定位的錯誤訊息
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location location = null;
            if (!(isGPSEnabled || isNetworkEnabled))
                tmsg.setText("GPS 未開啟");
            else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            minTime, minDist, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    tmsg.setText("使用網路GPS");
                }
//------------------------
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            minTime, minDist, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    tmsg.setText("使用精確GPS");
                }
            }


        }catch (Exception e){
            finish();
        }

    }


    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            double lng = location.getLongitude();// 經度
            double lat = location.getLatitude();// 緯度
            float speed = location.getSpeed();// 速度
            long time = location.getTime();// 時間
            String timeString = getTimeString(time);
            where = "經度: " + lng + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: " + timeString + "\nProvider: "
                    + provider;

            //============================

            if (tracesel == 1) {
                hashMapMarker = new HashMap<String, Object>();
                hashMapMarker.put("lat", Double.toString(lat));
                hashMapMarker.put("lng", Double.toString(lng));
                hashMapMarker.put("vtitle", Integer.toString(location_no));
                hashMapMarker.put("timeString", timeString);
                arrayList.add(hashMapMarker);
                location_no++;
                // 標記"我的位置"
                showMarkerMe(lat, lng);
                cameraFocusOnMe(lat, lng);
                //----------------------------------------
                trackMe(lat, lng);//軌跡圖
                //----------------------------------------

            } else {
                // 標記"我的位置"
                showMarkerMe(lat, lng);
            }

            //========================

//            cameraFocusOnMe(lat, lng);
        } else {
            where = "*位置訊號消失*";
        }
        // 位置改變顯示
        txtOutput.setText(where);
    }

    //追蹤目前我的位置畫軌跡圖
    private void trackMe(double lat, double lng) {
//        for (int i = 0; i < arrayList.size(); i++) {
//            String vtitle = arrayList.get(i).get("vtitle").toString();
//            String timeString = arrayList.get(i).get("timeString").toString();
//            dLat = Double.valueOf(arrayList.get(i).get("lat").toString());
//            dLon = Double.valueOf(arrayList.get(i).get("lng").toString());
//
//            image_des = BitmapDescriptorFactory.fromResource(R.drawable.c0b);// 使用照片
//            MarkerOptions markerOpt = new MarkerOptions();
//            markerOpt.position(new LatLng(dLat, dLon));
//            markerOpt.title(vtitle + "-" + timeString);
//            markerOpt.snippet(getString(R.string.lat) + dLat + getString(R.string.lon) + dLon);
//            markerOpt.infoWindowAnchor(0.5f, 0.9f);
//            markerOpt.draggable(true);
//            markerOpt.icon(image_des);
//            markerMe = map.addMarker(markerOpt);
//
//        }
        for (int i = 0; i < arrayList.size(); i++) {
            String vtitle = arrayList.get(i).get("vtitle").toString();
            String timeString = arrayList.get(i).get("timeString").toString();
            dLat = Double.valueOf(arrayList.get(i).get("lat").toString());
            dLon = Double.valueOf(arrayList.get(i).get("lng").toString());

            image_des = BitmapDescriptorFactory.fromResource(R.drawable.c0b);// 使用照片
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(dLat, dLon));
            String imgName = "c00";
            resID1 = getResources().getIdentifier(imgName, "drawable", getPackageName());
            markerOpt.title(vtitle + "-" + timeString + "#" + resID1);
            markerOpt.snippet(getString(R.string.lat) + dLat + getString(R.string.lon) + dLon);
            markerOpt.infoWindowAnchor(Anchor_x, Anchor_y);
            markerOpt.draggable(true);
            markerOpt.icon(image_des);
            markerMe = map.addMarker(markerOpt);
            //--------------------使用自定義式窗-------------------------------------------------------
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//外圓內方
            //-----------------------------------------
        }
////------------------------------------------------------------------
        if (mytrace == null) {
            mytrace = new ArrayList<LatLng>();
        }
        mytrace.add(new LatLng(lat, lng));

        //畫線
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : mytrace) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE); // 軌跡顏色
        Polyline line = map.addPolyline(polylineOpt);
        line.setWidth(10); // 線寬軌跡寬度
//---

        line.setPoints(mytrace);//這句話才把線加進去

    }

//    ----虛線-----
//    private void trackMe(double lat, double lng) {
//        if (mytrace == null) {
//            mytrace = new ArrayList<LatLng>();
//        }
//        mytrace.add(new LatLng(lat, lng));
//        PolylineOptions polylineOpt = new PolylineOptions()
//                .geodesic(true)
//                .color(Color.CYAN)
//                .width(10)
//                .pattern(PATTERN_POLYGON_ALPHA);
//
////        polylineOpt.addAll(Polyline.getPoints(mytrace));
////        polylinePaths.add(mGoogleMap.addPolyline(polylineOpt));
//
////        for (LatLng latlng : mytrace) {
////            polylineOpt.add(latlng);
////        }
//        // -----***軌跡顏色***-----
//        polylineOpt.color(Color.rgb(188 ,143,143));
//        Polyline line = map.addPolyline(polylineOpt);
//        line.setWidth(10); // 軌跡寬度
//        line.equals(10);
//        line.setPoints(mytrace);
//
//    }

    /***********************************************
     * timeInMilliseconds
     ***********************************************/
    private String getTimeString(long timeInMilliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }

    // cameraFocusOnMe
    private void cameraFocusOnMe(double lat, double lng) {
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(map.getCameraPosition().zoom)
                .build();
        /* 移動地圖鏡頭 */
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
        tmsg.setText("目前Zoom:" + map.getCameraPosition().zoom);
    }

    //*** 顯示目前位置*/
    private void showMarkerMe(double lat, double lng) {
        if (markerMe != null) markerMe.remove();


////------------------
//        int resID = getResources().getIdentifier("q00", "drawable", getPackageName());
////-------------------------
//        dLat = lat; // 南北緯
//        dLon = lng; // 東西經
//        String vtitle = "GPS位置:";
//        String vsnippet = "座標:" + String.valueOf(dLat) + "," + String.valueOf(dLon);
//        VGPS = new LatLng(lat, lng);// 更新成欲顯示的地圖座標
//        MarkerOptions markerOpt = new MarkerOptions();
//        markerOpt.position(new LatLng(lat, lng));
//        markerOpt.title(vtitle);
//        markerOpt.snippet(vsnippet);
//        if (icosel==0){
//            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        }else{
//            image_des = BitmapDescriptorFactory.fromResource(resID);// 使用照片
//            markerOpt.icon(image_des);
//        }
////        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//        markerMe = map.addMarker(markerOpt);
//----------------------------
        VGPS = new LatLng(lat, lng);
        locations[0][1] = lat + "," + lng;
    }

    //----生命週期-----
    @Override
    protected void onStart() {
        super.onStart();
//        checkRequiredPermission(this);     //  檢查SDK版本, 確認是否獲得權限.
        if (initLocationProvider()) {
            nowaddress();
        } else {
            txtOutput.setText("GPS未開啟,請先開啟定位！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    /*** 位置變更狀態監視*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            tmsg.setText("目前Zoom:" + map.getCameraPosition().zoom);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
            tmsg.setText("GPS close");
        }

        @Override
        public void onProviderEnabled(String provider) {
            tmsg.setText("GPS Enabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    tmsg.setText("Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    tmsg.setText("Temporarily Unavailable");
                    break;
                case LocationProvider.AVAILABLE:
                    tmsg.setText("Available");
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        m_traceon = menu.findItem(R.id.menu_trace_on);
        m_traceoff = menu.findItem(R.id.menu_trace_off);
        m_traceon.setVisible(false);
        m_traceoff.setVisible(false);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.menu_icon:
                map.clear();  //歸零
                if (icosel < 1) {
                    icosel = 1; //用照片顯示
                    showloc();
                } else
                    icosel = 0; //用水滴顯示
                showloc();
                break;
            case R.id.menu_trace_on:
                tracesel = 1;//開啟軌跡圖
                m_traceon.setVisible(false);
                m_traceoff.setVisible(true);
                break;
            case R.id.menu_trace_off:
                tracesel = 0;//開啟軌跡圖
                m_traceon.setVisible(true);
                m_traceoff.setVisible(false);
                break;
            case R.id.m_trace_clear:
                //清楚軌跡圖
                arrayList.clear();
                mytrace.clear();
                markerMe.remove();
                map.clear();
//                showloc();
                break;
            case R.id.menu_3D:
                //----
//                LatLng VGPS_3D1 = new LatLng(34.687404, 135.525763);//大阪城
//                LatLng VGPS_3D1 = new LatLng(25.0339640, 121.5644720);//台北101
                LatLng VGPS_3D1 = new LatLng(24.1578471, 120.6659828);//國立科學博物館

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS_3D1, 18));
                CameraPosition Build3D = new CameraPosition.Builder()
                        .target(VGPS_3D1)//目標 大阪城,台北101
                        .zoom(20.0f)   //縮放 1：世界 5：地塊/大陸 10：城市 15：街道 20：建築物
                        .bearing(170)  //0:北  45:西北  90:西  135:西南  180:南 225:東南 270:東 315:東北
                        .tilt(45)       //傾斜度（檢視角度）0-90 0:正上方(0~15 最佳)
                        .build();      // Creates a CameraPosition from the builder
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL); //
                map.animateCamera(CameraUpdateFactory.newCameraPosition(Build3D));
                map.setBuildingsEnabled(true);
                //----
                break;
            case R.id.menu_3Da:
                //----
                LatLng VGPS_3D2 = new LatLng(34.687404, 135.525763);//大阪城
//                LatLng VGPS_3D1 = new LatLng(25.0339640, 121.5644720);//台北101
//                LatLng VGPS_3D1 = new LatLng(24.1579361, 120.6659828);//國立科學博物館

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS_3D2, 19));
                CameraPosition Build2 = new CameraPosition.Builder()
                        .target(VGPS_3D2)//目標 大阪城,台北101
                        .zoom(18.0f)   //縮放 1：世界 5：地塊/大陸 10：城市 15：街道 20：建築物
                        .bearing(165)  //0:北  45:西北  90:西  135:西南  180:南 225:東南 270:東 315:東北
                        .tilt(45)       //傾斜度（檢視角度）0-90 0:正上方(0~15 最佳)
                        .build();      // Creates a CameraPosition from the builder
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); //
                map.animateCamera(CameraUpdateFactory.newCameraPosition(Build2));
                map.setBuildingsEnabled(true);
                //----
                break;
            case R.id.action_settings:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //==========自訂一window副程式
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, null);
            infoWindow.setAlpha(1.0f);


            for(int i=0;i<mdata.size();i++)
            {
                LatLng test = new LatLng(Double.parseDouble(mdata.get(i).Latitude)
                        ,Double.parseDouble(mdata.get(i).Longitude));

                LatLng test01 = marker.getPosition();
                if(test.equals(test01))
                {
                    position=i;
                    break;
                }
            }

            TextView mDesc =infoWindow.findViewById(R.id.m2206_descr);
            TextView title = ((TextView) infoWindow.findViewById(R.id.title));
            TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
            TextView info_opentime=(TextView)infoWindow.findViewById(R.id.info_opentime);//開放時間
            TextView info_tel=(TextView)infoWindow.findViewById(R.id.info_tel);//電話
            TextView info_ticketinfo=(TextView)infoWindow.findViewById(R.id.info_ticketinfo);//票價訊息
            info_tel.setText("電話:"+mdata.get(position).Tel);
            if(opda_index==1){
                info_opentime.setText("開放時間:"+mdata.get(position).Opentime);
                info_ticketinfo.setText("票價訊息:"+mdata.get(position).Ticketinfo);
            }
            else
            {
                info_opentime.setVisibility(View.GONE);
                info_ticketinfo.setVisibility(View.GONE);
            }

            snippet.setText(marker.getSnippet());
            title.setText(mdata.get(position).Name);
//

            //        若圖片檔名是中文無法下載,可用此段檢查圖片網址且將中文解碼
        ans_Url = mdata.get(position).posterThumbnailUrl;
        if (ans_Url.getBytes().length == ans_Url.length() ||
                ans_Url.getBytes().length > 100) {
            ans_Url = ans_Url;//不包含中文，不做處理
        } else {
//    ans_Url = utf8Togb2312(post.posterThumbnailUrl);
            ans_Url = utf8Togb2312(ans_Url).replace("http://", "https://");
        }
            img =(ImageView)infoWindow.findViewById(R.id.content_ico);



            img.setImageResource(R.drawable.error_img);
            try {

                Glide.with(mContext)
                        .asBitmap()
                        .load(ans_Url)
                        .error(R.drawable.error_img)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                img1908.setImageBitmap(resource);
                                img1908.buildDrawingCache();
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) { }
                        });

//                img1908.setVisibility(View.VISIBLE);
                Thread.sleep(1500);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            RequestOptions options = new RequestOptions() .placeholder(R.drawable.aa09);


            Glide.with(mContext)
                    .asBitmap()
                    .load(ans_Url)
                    .error(R.drawable.error_img)
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            img.setImageBitmap(resource);
                            img.buildDrawingCache();
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            img.setBackground(placeholder);

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            img.setBackground(errorDrawable);

                        }
                    });



            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Toast.makeText(getApplicationContext(), "getInfoContents", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    ////    //    -----------把中文字符轉換為帶百分號的瀏覽器編碼-----------
    public static String utf8Togb2312(String inputstr) {
        String r_data = "";
        try {
            for (int i = 0; i < inputstr.length(); i++) {
                char ch_word = inputstr.charAt(i);
//            下面這段代碼的意義是:只對中文進行轉碼
                if (ch_word + "".getBytes().length > 1 && ch_word != ':' && ch_word != '/') {
                    r_data = r_data + java.net.URLEncoder.encode(ch_word + "", "utf-8");
                } else {
                    r_data = r_data + ch_word;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
//            System.out.println(r_data);
        }
        return r_data;
    }


//--------------------end class---------------------------------
}

