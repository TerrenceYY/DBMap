package com.baidu.location.demo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.baidulocationdemo.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

import android.net.Uri;

/***
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 * 
 * @author baidu
 *
 */
public class NaviAct extends Activity {
	private LocationService locationService;
	private Button naviButton;
	private TextView showText;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;

	private LatLng desPoint;
	private LatLng startPoint;
	private String li;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// -----------demo view config ------------
		setContentView(R.layout.navi_map);
        naviButton = (Button) findViewById(R.id.button66);

        showText = (TextView)findViewById(R.id.show_text66);

		mMapView = (MapView) findViewById(R.id.bView66);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationService = ((LocationApplication) getApplication()).locationService;
		locationService.registerListener(mListener);

		locationService.setLocationOption(locationService.getOption());

		li = getIntent().getStringExtra("clicked_line");
		long la = getIntent().getLongExtra("clicked_lati", 10);
		long lo = getIntent().getLongExtra("clicked_longi", 10);
		LatLng sPoint = new LatLng(la/1000000.0/1000000.0, lo/1000000.0/1000000.0);
		desPoint = sPoint;

		locationService.start();// 定位SDK

        naviButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onResume();
				if(startPoint == null || desPoint == null) {
					return;
				}

				Intent i1 = new Intent();
				i1.setData(Uri.parse("baidumap://map/direction?" +
						"&origin=" + startPoint.latitude + "," + startPoint.longitude +
						"&destination=name:" + li + "|latlng:" + desPoint.latitude + "," + desPoint.longitude +
						"&coord_type=bd09ll&mode=driving&src=andr.baidu.openAPIdemo"));
				startActivity(i1);
			}
		});

	}

	@Override
    public void onBackPressed() {
		super.onBackPressed();
    }

	/*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

				startPoint = point;
			}
		}

	};


	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		String st = "nullstr";
		String de = "nullstr";
		if(startPoint != null) {
			st = startPoint.toString();
		}
		if(desPoint != null) {
			de = desPoint.toString();
		}
		showText.setText("start:" + st + "\n" + li + ":" + de);

		if(desPoint == null) {
			naviButton.setEnabled(false);
		} else {
			mBaiduMap.setMyLocationEnabled(true);
			MyLocationData locData = new MyLocationData.Builder()
					//						.direction(100)
					.latitude(desPoint.latitude)
					.longitude(desPoint.longitude).build();
			mBaiduMap.setMyLocationData(locData);
			BitmapDescriptor mCurrentMarker =
					BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
			MyLocationConfiguration config =
					new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfiguration(config);

			naviButton.setEnabled(true);

			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(desPoint));
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		desPoint = null;
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}
}
