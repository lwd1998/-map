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
     * ��λSDK������
     */
    private LocationClient locationClient;
    /**
     * ��λ����
     */
    public MyLocationListenner myListener = new MyLocationListenner();
 /**
     * �ٶȵ�ͼ�ؼ�
     */
    private MapView mapView;
    //private Context context;
	private double mLatitude;
	private double mLongtitude;
    /**
     * �ٶȵ�ͼ����
     */
    private BaiduMap baiduMap;
 
    boolean isFirstLoc = true; // �Ƿ��״ζ�λ
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        /**
         * ��ͼ��ʼ��
         */
        //��ȡ�ٶȵ�ͼ�ؼ�
        mapView = (MapView) findViewById(R.id.id_bmapView);
        //��ȡ�ٶȵ�ͼ����
        baiduMap = mapView.getMap();
        // ������λͼ��
        baiduMap.setMyLocationEnabled(true);
        /**
         * ��λ��ʼ��
         */
        //������λSDK������
        locationClient = new LocationClient(this);
        //ע�����
        locationClient.registerLocationListener(myListener);
        //��λ������Ϣ
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ��gps
        option.setCoorType("bd09ll"); // ������������
        option.setScanSpan(1000);//��λ����ʱ����
        locationClient.setLocOption(option);
        //������λ
        locationClient.start();
    }
    /**
     * ��λSDK��������
     */
    public class MyLocationListenner implements BDLocationListener {
 
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //���ٶȵ�ͼ�����뾭γ��
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                //����һ����γ�ȵı���������ȡ���ľ�γ�ȵ�ֵ�����������
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                //��ȡ����
                mLatitude = location.getLatitude();
                //��ȡγ��ֵ
                mLongtitude = location.getLongitude();
                MapStatus.Builder builder = new MapStatus.Builder();
                //�������ŵĳߴ�
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
     * ��������Ƶ�ͼ����ʽ
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
    			item.setTitle("ʵʱ��ͨ(off)");
    		}else {
    			baiduMap.setTrafficEnabled(true);
    			item.setTitle("ʵʱ��ͨ(on)");
    		}
    		break;
    	case R.id.id_map_location:
    		//���û�ȡ�ҵ�λ�õķ���
    		centerToMyLocation();
    		break;
    	default:
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    //��mLatitudeֵ��mLongtitudeֵ����
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
        // �˳�ʱ���ٶ�λ
        locationClient.stop();
        // �رն�λͼ��
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}