package com.choose.location.getlocation.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.choose.location.getlocation.R;
import com.choose.location.getlocation.adapter.AMapAdapter;
import com.choose.location.getlocation.adapter.AMapBean;
import com.choose.location.getlocation.databinding.ActivityChooseAddressBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuzhendong on 2018/4/20.
 */

public class ChooseAddressActivity extends Activity implements LocationSource,AMapLocationListener,Inputtips.InputtipsListener,PoiSearch.OnPoiSearchListener{
    //初始化地图控制器对象
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private List<AMapBean> pois;
    private List<AMapBean> windowDatas;
    private AMapLocation aMapLocation;
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
    private Inputtips inputTips;
    private InputtipsQuery inputquery;
    private AMapAdapter adapter;
    private AMapAdapter windowAdapter;
    private boolean initLocation;
    private ActivityChooseAddressBinding binding;
    private String location;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_choose_address,null,false);
        setContentView(binding.getRoot());
        initView(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"请打开定位权限",Toast.LENGTH_SHORT).show();
        }
    }


    protected void initView(Bundle savedInstanceState) {
        pois = new ArrayList<>();
        windowDatas = new ArrayList<>();
        binding.map.onCreate(savedInstanceState);
        poiSearch = new PoiSearch(this,null);
        poiSearch.setOnPoiSearchListener(this);
        initMap();
        findViewById(R.id.view_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.ll_search).setVisibility(View.VISIBLE);
                binding.listView.setVisibility(View.GONE);
                binding.rlTitle.setVisibility(View.GONE);
            }
        });
        binding.tvOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("location",location);
                setResult(0,intent);
                finish();
            }
        });
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(AMapBean bean : pois){
                    bean.setChoose(false);
                }
                pois.get(position).setChoose(true);
                adapter.notifyDataSetChanged();
            }
        });
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchInput(s.toString());
            }
        });
        adapter = new AMapAdapter(this, pois,0);
        binding.listView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        binding.map.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        this.aMapLocation = aMapLocation;
        if (mListener != null&& aMapLocation != null) {
            if (aMapLocation != null
                    &&aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                if(!initLocation){
                    search(aMapLocation.getPoiName());
                    initLocation = !initLocation;
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
            }
        }
    }

    public void initMap(){
        if (aMap == null) {
            aMap = binding.map.getMap();
        }
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        aMap.setLocationSource(this);
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                int left = binding.map.getLeft();
                int top = binding.map.getTop();
                int right = binding.map.getRight();
                int bottom = binding.map.getBottom();
                // 获得屏幕点击的位置
                int x = (int) (binding.map.getX() + (right - left) / 2);
                int y = (int) (binding.map.getY() + (bottom - top) / 2);
                Projection projection = aMap.getProjection();
                LatLng latLng = projection.fromScreenLocation(new Point(x, y));
                Log.v("verf","onCameraChangeFinish" + latLng.latitude + " " + latLng.longitude);
                LatLonPoint lp = new LatLonPoint(latLng.latitude, latLng.longitude);
                query = new PoiSearch.Query("", "", aMapLocation.getCity());
                query.setPageSize(20);// 设置每页最多返回多少条poiitem
                query.setPageNum(1);// 设置查第一页
                poiSearch = new PoiSearch(ChooseAddressActivity.this, query);
                poiSearch.setOnPoiSearchListener(ChooseAddressActivity.this);
                poiSearch.setBound(new PoiSearch.SearchBound(lp, 1000, true)); // 设置搜索区域为以lp点为圆心，其周围2000米范围
                poiSearch.searchPOIAsyn();// 异步搜索
            }
        });
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.map.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        binding.map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
    }

    @Override
    public void onGetInputtips(List<Tip> list, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (list != null && inputTips.getQuery() != null) {// 搜索poi的结果
                if (inputTips.getQuery().equals(inputquery)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    windowDatas.clear();
                    for (int i = 0; i < list.size(); i++) {
                        Tip tip = list.get(i);
                        AMapBean bean = new AMapBean(tip.getName(),"");
                        windowDatas.add(bean);
                    }
                    showPopWindow();
                }
            }
        } else {
            Toast.makeText(this, "搜索失败：" + rcode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                // 取得搜索到的poiitems有多少页
                pois.clear();
                for (int i = 0; i < poiResult.getPois().size(); i++) {
                    PoiItem poiItem = poiResult.getPois().get(i);
                    AMapBean bean = new AMapBean(poiItem.getTitle(),poiItem.getProvinceName() + "," + poiItem.getCityName() + ","
                            + poiItem.getAdName() + ","//区
                            + poiItem.getSnippet());
                    pois.add(bean);
                }
                binding.listView.setVisibility(View.VISIBLE);
                binding.listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "搜索失败：" + rcode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    public void search(String text){
        query = new PoiSearch.Query(text, "", aMapLocation.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    public void searchInput(String text){
        inputquery = new InputtipsQuery(text, aMapLocation == null ? "": aMapLocation.getCity());
        inputquery.setCityLimit(true);//限制在当前城市
        inputTips = new Inputtips(ChooseAddressActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    public void showPopWindow() {
        windowAdapter = new AMapAdapter(this,windowDatas,AMapAdapter.TYPE_INPUT);
        binding.searchListView.setAdapter(windowAdapter);
        binding.searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search(windowDatas.get(position).getAddress());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(binding.editSearch.getApplicationWindowToken(), 0);
                }
                binding.llSearch.setVisibility(View.GONE);
                binding.listView.setVisibility(View.VISIBLE);
                binding.rlTitle.setVisibility(View.VISIBLE);
            }
        });
    }
}
