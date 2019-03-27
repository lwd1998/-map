package com.test.app03;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
 
public class MainActivity extends Activity{
 /**
     * 定位SDK核心类
     */
    private LocationClient locationClient;
    /**
     * 定位监听
     */
    public MyLocationListenner myListener = new MyLocationListenner();
 /**
     * 百度地图控件
     */
    private MapView mapView;
    //private Context context;
	private double mLatitude;
	private double mLongtitude;
    /**
     * 百度地图对象
     */
    private BaiduMap baiduMap;
 
    boolean isFirstLoc = true; // 是否首次定位
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        /**
         * 地图初始化
         */
        //获取百度地图控件
        mapView = (MapView) findViewById(R.id.id_bmapView);
        //获取百度地图对象
        baiduMap = mapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        /**
         * 定位初始化
         */
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
 
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //给百度地图对象传入经纬度
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                //定义一个经纬度的变量，将获取到的经纬度的值赋给这个变量
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                //获取经度
                mLatitude = location.getLatitude();
                //获取纬度值
                mLongtitude = location.getLongitude();
                MapStatus.Builder builder = new MapStatus.Builder();
                //控制缩放的尺寸
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
    	return true;
    }
    /*
     * 下拉框控制地图的样式
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.id_map_common:
    		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    		break;
    	case R.id.id_map_site:
    		baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    		break;
    	case R.id.id_map_traffic:
    		if (baiduMap.isTrafficEnabled()) {
    			baiduMap.setTrafficEnabled(false);
    			item.setTitle("实时交通(off)");
    		}else {
    			baiduMap.setTrafficEnabled(true);
    			item.setTitle("实时交通(on)");
    		}
    		break;
    	case R.id.id_map_location:
    		//调用获取我的位置的方法
    		centerToMyLocation();
    		break;
    	default:
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    //将mLatitude值和mLongtitude值传入
    private void centerToMyLocation() {
    	LatLng latlng = new LatLng(mLatitude,mLongtitude);
    	MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
    	baiduMap.animateMapStatus(msu);	
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
 
    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}