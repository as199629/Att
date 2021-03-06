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
            {"????????????", "24.172127,120.610313"},
            {"????????????", "24.172127,120.610313"},
            {"???????????????????????????", "24.179051,120.600610"},
            {"?????????????????????", "24.144671,120.683981"},
            {"?????????", "24.1674900,120.6398902"},
            {"???????????????", "24.136829,120.685011"},
            {"?????????????????????", "24.1579361,120.6659828"}};

    private static String[] mapType = {
            "?????????",
            "?????????",
            "?????????",
            "?????????",
            "????????????",
            "????????????"};

    private Spinner mSpnLocation, mSpnMapType;
    double dLat, dLon;
    double latitude;
    double longitude;
    private BitmapDescriptor image_des;// ????????????
    private int icosel = 0; //????????????
    //----GPS------------
    private TextView txtOutput;
    private TextView tmsg;
    private Marker markerMe;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private LocationManager locationManager;
    private Location currentLocation;
    private String provider = null; // ????????????
    long minTime = 5000;// ms
    float minDist = 1.0f;// meter
    private float Anchor_x=0.7f;
    private float Anchor_y=0.6f;


    private final String TAG = "oldpa=>";
    //-----------------??????????????????????????????---------------

    private List<String> permissionsList = new ArrayList<String>();
    //???????????????????????????
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Menu menu;
    private MenuItem m_traceon;
    private MenuItem m_traceoff;
    private int tracesel = 0;
    private HashMap<String, Object> hashMapMarker = new HashMap<String, Object>();
    private int location_no = 0;
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    private ArrayList<LatLng> mytrace;// ??????????????????
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

            //------------??????MapFragment-----------------------------------
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //-------------------------------------------------------

            u_checkgps();  //??????GPS????????????
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
        opda_index=bundle.getInt("OPDA_INDEX");//?????????????????????????????????
        Home home=new Home();//new?????? ???????????????
        mdata=home.getmData(opda_index);//?????? home?????????  ?????????mdata



//        icosel = 0;  //?????????????????????
        // ---------------
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item);

        for (int i = 0; i < locations.length; i++)
            adapter.add(locations[i][0]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLocation.setAdapter(adapter);
        // ????????????????????????
        mSpnLocation.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//               Toast.makeText(getApplicationContext(),"s",Toast.LENGTH_LONG).show();

                map.clear();
//                mytrace = null; ////?????????????????????????????????????????????
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
        //-----------??????ARGB ?????????----
//        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x00FFFFFF)); //?????????
        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x80FFFFFF)); //50%??????
        mSpnLocation.setPopupBackgroundDrawable(new ColorDrawable(0x80FFFFFF)); //50%??????
//        # ARGB????????????????????????alpha????????????(red)?????????(green)?????????(blue)
//        100% ??? FF       95% ??? F2        90% ??? E6        85% ??? D9
//        80% ??? CC        75% ??? BF        70% ??? B3        65% ??? A6
//        60% ??? 99        55% ??? 8C        50% ??? 80        45% ??? 73
//        40% ??? 66        35% ??? 59        30% ??? 4D        25% ??? 40
//        20% ??? 33        15% ??? 26        10% ??? 1A         5% ??? 0D         0% ??? 00
        //---------------
        mSpnMapType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); // ???????????????
                        break;
                    case 1:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // ???????????????
                        break;
                    case 2:
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // ?????????
                        break;
                    case 3:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // ???????????????????????????
                        break;
                    case 4:
                        map.setTrafficEnabled(true); //????????????
                        break;
                    case 5:
                        map.setTrafficEnabled(false); //????????????
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
        showloc(); //??????????????????
        int iSelect = mSpnLocation.getSelectedItemPosition();
        String[] sLocation = locations[iSelect][1].split(",");
        double dLat = Double.parseDouble(sLocation[0]);    // ?????????
        double dLon = Double.parseDouble(sLocation[1]);    // ?????????
        String vtitle = locations[iSelect][0];
        //--- ????????????????????????????????? ---//
        image_des = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN); //??????????????????
        VGPS = new LatLng(dLat, dLon);
        map.addMarker(new MarkerOptions()
                .position(VGPS)
                .title(vtitle)
                .snippet("??????:" + dLat + "," + dLon)
                .icon(image_des));// ??????????????????
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        onCameraChange(map.getCameraPosition());//???????????????????????????????????????
    }

    private void onCameraChange(CameraPosition cameraPosition) {
        //  ????????????????????? zoom ??????
        tmsg.setText("?????????zoom" + map.getCameraPosition().zoom);

    }

    private void showloc() {
        if (map != null) map.clear();  //218
        bitmap=new Bitmap[mdata.size()-1];
        Handler handler=new Handler();
        // ???????????????????????????
//        for (int i = 1; i < ArrList_lat.size(); i++) {
//            dLat = Double.parseDouble(ArrList_lat.get(i)); // ?????????
//            dLon = Double.parseDouble(ArrList_lon.get(i)); // ?????????
//            String vtitle = locations[i][0];



//            vtitle=vtitle+"#"+resID1; //????????????????????????
            if(icosel==0)
            {

                for (int i = 1; i < mdata.size(); i++) {
                   dLat = Double.parseDouble(mdata.get(i).Latitude); // ?????????
                   dLon = Double.parseDouble(mdata.get(i).Longitude); // ?????????
                   VGPS = new LatLng(dLat, dLon);// ?????????????????????????????????
                   map.addMarker(new MarkerOptions()
                                   .position(VGPS)
                                   .alpha(0.9f)
                                   .title("." + "vtitle")
                                   .snippet("??????:" + String.valueOf(dLat) + "\n??????:" + String.valueOf(dLon))
//                            .infoWindowAnchor(0.5f, 0.9f)
                                   .infoWindowAnchor(Anchor_x, Anchor_y)
                                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) // ??????????????????
                   );
               }

            }
            else if(icosel==1)
            {

                //?????????
                options = new RequestOptions()
                        .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .priority(Priority.NORMAL);

                    i=0;
                    handler.post(updata_map);



            //--------------------?????????????????????-------------------------------------------------------



        }
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//????????????


    }
    private Runnable updata_map=new Runnable() {
        @Override
        public void run() {
            double dLat = Double.parseDouble(mdata.get(i).Latitude);    // ?????????
            double dLon = Double.parseDouble(mdata.get(i).Longitude);    // ?????????



            String ans_Url = mdata.get(i).posterThumbnailUrl;
            if (ans_Url.getBytes().length == ans_Url.length() ||
                    ans_Url.getBytes().length > 100) {
                ans_Url = ans_Url;//??????????????????????????????
            } else {
//    ans_Url = utf8Togb2312(post.posterThumbnailUrl);
                ans_Url = utf8Togb2312(ans_Url).replace("http://", "https://");
            }

            options = new RequestOptions()
                    .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                    .placeholder(R.mipmap.ic_launcher)
//                    .error(R.drawable.error_img)
                    .priority(Priority.NORMAL);

//            VGPS = new LatLng(dLat, dLon);// ?????????????????????????????????
//            map.addMarker(new MarkerOptions()
//                            .position(VGPS)
//                            .alpha(0.9f)
//                            .title("." + "vtitle")
//                            .snippet("??????:" + String.valueOf(dLat) + "\n??????:" + String.valueOf(dLon))
//                            .infoWindowAnchor(Anchor_x, Anchor_y)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))); // ??????????????????



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
                                //error????????????????????????
                            }
                            VGPS = new LatLng(dLat, dLon);// ?????????????????????????????????
                            map.addMarker(new MarkerOptions()
                                            .position(VGPS)
                                            .alpha(0.9f)
                                            .title("." + "vtitle")
                                            .snippet("??????:" + String.valueOf(dLat) + "\n??????:" + String.valueOf(dLon))
                                            .infoWindowAnchor(Anchor_x, Anchor_y)
                                            .icon(BitmapDescriptorFactory.fromBitmap(resource)) // ??????????????????
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
                    //?????????????????????
                    break;
            }
        }
    };

    //--------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        mUiSettings = map.getUiSettings();//
//        ?????? Google Map ????????????
        map.getUiSettings().setScrollGesturesEnabled(true);
//        ??????????????????????????? Google Map??????
        map.getUiSettings().setMapToolbarEnabled(true);
//        ??????????????????????????????????????????????????????
        map.getUiSettings().setCompassEnabled(true);
//        ????????????????????????????????????????????????
        map.getUiSettings().setZoomControlsEnabled(true);
        // --------------------------------
        map.addMarker(new MarkerOptions().position(VGPS).title("????????????"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        //----------??????????????????-----------------------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //----??????????????????ICO-------
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "GPS????????????????????? ????????????????????? \n??????????????????????????? ???????????????", Toast.LENGTH_LONG).show();


        }


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //?????? window???
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
        // -------- ???????????? ------------------------------------------
        map.getUiSettings().setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    public void setCompassEnabled(View v) {
        //        ??????????????????????????????????????????????????????
        if (!checkReady()) return;
        map.getUiSettings().setCompassEnabled(((CheckBox) v).isChecked());

    }

    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //----??????????????????ICO------
            map.setMyLocationEnabled(((CheckBox) v).isChecked());
        } else {
            Toast.makeText(getApplicationContext(), "GPS?????????????????????", Toast.LENGTH_LONG).show();
        }
    }

    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) return;
        //---???????????????????????? , ???????????????????????????????????????
        map.getUiSettings().setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) return;
        //---??????????????????????????????????????????????????? ---
        map.getUiSettings().setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) return;
//---?????????????????????????????????????????????????????????????????? / ?????????????????? ---
        map.getUiSettings().setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) return;
        //?????????????????????????????? ---
        map.getUiSettings().setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //-------?????????????????????GPS??????------------------------------------
    private void u_checkgps() {
        // ?????????????????????LocationManager??????
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // ?????????????????????GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // ????????????????????????GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("????????????")
                    .setMessage("GPS???????????????????????????.\n"
                            + "????????????????????????????????????GPS?")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ??????Intent?????????????????????????????????GPS??????
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("?????????", null).create().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // ============ GPS =================
    //** onMyLocationButtonClick  ????????????????????????/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "??????GPS????????????", Toast.LENGTH_LONG).show();
        return true;
    }

    //*********************************************/
    /* ??????GPS ???????????? */
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
// ???????????????????????????

// ?????? GPS Listener----------------------------------
// long minTime = 5000;// ms
// float minDist = 5.0f;// meter
//---?????????GPS????????????????????????GPS???????????????????????????????????????????????????????????????
// ????????????GPS?????????????????????????????????????????????????????????????????????
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location location = null;
            if (!(isGPSEnabled || isNetworkEnabled))
                tmsg.setText("GPS ?????????");
            else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            minTime, minDist, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    tmsg.setText("????????????GPS");
                }
//------------------------
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            minTime, minDist, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    tmsg.setText("????????????GPS");
                }
            }


        }catch (Exception e){
            finish();
        }

    }


    private void updateWithNewLocation(Location location) {
        String where = "";
        if (location != null) {
            double lng = location.getLongitude();// ??????
            double lat = location.getLatitude();// ??????
            float speed = location.getSpeed();// ??????
            long time = location.getTime();// ??????
            String timeString = getTimeString(time);
            where = "??????: " + lng + "\n??????: " + lat + "\n??????: " + speed + "\n??????: " + timeString + "\nProvider: "
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
                // ??????"????????????"
                showMarkerMe(lat, lng);
                cameraFocusOnMe(lat, lng);
                //----------------------------------------
                trackMe(lat, lng);//?????????
                //----------------------------------------

            } else {
                // ??????"????????????"
                showMarkerMe(lat, lng);
            }

            //========================

//            cameraFocusOnMe(lat, lng);
        } else {
            where = "*??????????????????*";
        }
        // ??????????????????
        txtOutput.setText(where);
    }

    //????????????????????????????????????
    private void trackMe(double lat, double lng) {
//        for (int i = 0; i < arrayList.size(); i++) {
//            String vtitle = arrayList.get(i).get("vtitle").toString();
//            String timeString = arrayList.get(i).get("timeString").toString();
//            dLat = Double.valueOf(arrayList.get(i).get("lat").toString());
//            dLon = Double.valueOf(arrayList.get(i).get("lng").toString());
//
//            image_des = BitmapDescriptorFactory.fromResource(R.drawable.c0b);// ????????????
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

            image_des = BitmapDescriptorFactory.fromResource(R.drawable.c0b);// ????????????
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
            //--------------------?????????????????????-------------------------------------------------------
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//????????????
            //-----------------------------------------
        }
////------------------------------------------------------------------
        if (mytrace == null) {
            mytrace = new ArrayList<LatLng>();
        }
        mytrace.add(new LatLng(lat, lng));

        //??????
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : mytrace) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE); // ????????????
        Polyline line = map.addPolyline(polylineOpt);
        line.setWidth(10); // ??????????????????
//---

        line.setPoints(mytrace);//???????????????????????????

    }

//    ----??????-----
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
//        // -----***????????????***-----
//        polylineOpt.color(Color.rgb(188 ,143,143));
//        Polyline line = map.addPolyline(polylineOpt);
//        line.setWidth(10); // ????????????
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
        /* ?????????????????? */
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
        tmsg.setText("??????Zoom:" + map.getCameraPosition().zoom);
    }

    //*** ??????????????????*/
    private void showMarkerMe(double lat, double lng) {
        if (markerMe != null) markerMe.remove();


////------------------
//        int resID = getResources().getIdentifier("q00", "drawable", getPackageName());
////-------------------------
//        dLat = lat; // ?????????
//        dLon = lng; // ?????????
//        String vtitle = "GPS??????:";
//        String vsnippet = "??????:" + String.valueOf(dLat) + "," + String.valueOf(dLon);
//        VGPS = new LatLng(lat, lng);// ?????????????????????????????????
//        MarkerOptions markerOpt = new MarkerOptions();
//        markerOpt.position(new LatLng(lat, lng));
//        markerOpt.title(vtitle);
//        markerOpt.snippet(vsnippet);
//        if (icosel==0){
//            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        }else{
//            image_des = BitmapDescriptorFactory.fromResource(resID);// ????????????
//            markerOpt.icon(image_des);
//        }
////        markerOpt.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//        markerMe = map.addMarker(markerOpt);
//----------------------------
        VGPS = new LatLng(lat, lng);
        locations[0][1] = lat + "," + lng;
    }

    //----????????????-----
    @Override
    protected void onStart() {
        super.onStart();
//        checkRequiredPermission(this);     //  ??????SDK??????, ????????????????????????.
        if (initLocationProvider()) {
            nowaddress();
        } else {
            txtOutput.setText("GPS?????????,?????????????????????");
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

    /*** ????????????????????????*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            tmsg.setText("??????Zoom:" + map.getCameraPosition().zoom);
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
                map.clear();  //??????
                if (icosel < 1) {
                    icosel = 1; //???????????????
                    showloc();
                } else
                    icosel = 0; //???????????????
                showloc();
                break;
            case R.id.menu_trace_on:
                tracesel = 1;//???????????????
                m_traceon.setVisible(false);
                m_traceoff.setVisible(true);
                break;
            case R.id.menu_trace_off:
                tracesel = 0;//???????????????
                m_traceon.setVisible(true);
                m_traceoff.setVisible(false);
                break;
            case R.id.m_trace_clear:
                //???????????????
                arrayList.clear();
                mytrace.clear();
                markerMe.remove();
                map.clear();
//                showloc();
                break;
            case R.id.menu_3D:
                //----
//                LatLng VGPS_3D1 = new LatLng(34.687404, 135.525763);//?????????
//                LatLng VGPS_3D1 = new LatLng(25.0339640, 121.5644720);//??????101
                LatLng VGPS_3D1 = new LatLng(24.1578471, 120.6659828);//?????????????????????

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS_3D1, 18));
                CameraPosition Build3D = new CameraPosition.Builder()
                        .target(VGPS_3D1)//?????? ?????????,??????101
                        .zoom(20.0f)   //?????? 1????????? 5?????????/?????? 10????????? 15????????? 20????????????
                        .bearing(170)  //0:???  45:??????  90:???  135:??????  180:??? 225:?????? 270:??? 315:??????
                        .tilt(45)       //???????????????????????????0-90 0:?????????(0~15 ??????)
                        .build();      // Creates a CameraPosition from the builder
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL); //
                map.animateCamera(CameraUpdateFactory.newCameraPosition(Build3D));
                map.setBuildingsEnabled(true);
                //----
                break;
            case R.id.menu_3Da:
                //----
                LatLng VGPS_3D2 = new LatLng(34.687404, 135.525763);//?????????
//                LatLng VGPS_3D1 = new LatLng(25.0339640, 121.5644720);//??????101
//                LatLng VGPS_3D1 = new LatLng(24.1579361, 120.6659828);//?????????????????????

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS_3D2, 19));
                CameraPosition Build2 = new CameraPosition.Builder()
                        .target(VGPS_3D2)//?????? ?????????,??????101
                        .zoom(18.0f)   //?????? 1????????? 5?????????/?????? 10????????? 15????????? 20????????????
                        .bearing(165)  //0:???  45:??????  90:???  135:??????  180:??? 225:?????? 270:??? 315:??????
                        .tilt(45)       //???????????????????????????0-90 0:?????????(0~15 ??????)
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

    //==========?????????window?????????
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
            TextView info_opentime=(TextView)infoWindow.findViewById(R.id.info_opentime);//????????????
            TextView info_tel=(TextView)infoWindow.findViewById(R.id.info_tel);//??????
            TextView info_ticketinfo=(TextView)infoWindow.findViewById(R.id.info_ticketinfo);//????????????
            info_tel.setText("??????:"+mdata.get(position).Tel);
            if(opda_index==1){
                info_opentime.setText("????????????:"+mdata.get(position).Opentime);
                info_ticketinfo.setText("????????????:"+mdata.get(position).Ticketinfo);
            }
            else
            {
                info_opentime.setVisibility(View.GONE);
                info_ticketinfo.setVisibility(View.GONE);
            }

            snippet.setText(marker.getSnippet());
            title.setText(mdata.get(position).Name);
//

            //        ????????????????????????????????????,????????????????????????????????????????????????
        ans_Url = mdata.get(position).posterThumbnailUrl;
        if (ans_Url.getBytes().length == ans_Url.length() ||
                ans_Url.getBytes().length > 100) {
            ans_Url = ans_Url;//??????????????????????????????
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
    ////    //    -----------??????????????????????????????????????????????????????-----------
    public static String utf8Togb2312(String inputstr) {
        String r_data = "";
        try {
            for (int i = 0; i < inputstr.length(); i++) {
                char ch_word = inputstr.charAt(i);
//            ??????????????????????????????:????????????????????????
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

