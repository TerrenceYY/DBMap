package com.baidu.location.demo;

//import java.util.LinkedList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
//import android.widget.RadioGroup;
//import android.widget.TextView;

import com.baidu.baidulocationdemo.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

/***
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 * 
 * @author baidu
 *
 */
public class StandardActivity extends Activity {
	private LocationService locationService;
	private Button locButton;
	private Button saveButton;
	private Button favButton;
	private Button naviButton;
    private Button searchButton;

	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
//	private LocationService locService;
//	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果

    private EditText searchText;

	private static BDLocation staticLocation;
	private LatLng desPoint;
	private LatLng startPoint;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// -----------demo view config ------------
		setContentView(R.layout.standard);
//		LocationResult = (TextView) findViewById(R.id.textView1);
//		LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        locButton = (Button) findViewById(R.id.loc_button);
        saveButton = (Button) findViewById(R.id.save_button);
        favButton = (Button) findViewById(R.id.fav_button);
        naviButton = (Button) findViewById(R.id.navi_button);
        searchButton = (Button) findViewById(R.id.search_button);

        searchText = (EditText)findViewById(R.id.search_text);

		mMapView = (MapView) findViewById(R.id.bView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
//		locService = ((LocationApplication) getApplication()).locationService;
//		LocationClientOption mOption = locService.getDefaultLocationClientOption();
//		mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//		mOption.setCoorType("bd09ll");
//		locService.setLocationOption(mOption);
//		locService.registerListener(listener);
//		locService.start();

	}

	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
//		final String s = str;
//		try {
//			if (LocationResult != null){
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						LocationResult.post(new Runnable() {
//							@Override
//							public void run() {
//								LocationResult.setText(s);
//							}
//						});
//
//					}
//				}).start();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// -----------location config ------------
		locationService = ((LocationApplication) getApplication()).locationService; 
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}

		locationService.start();// 定位SDK


        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StandardActivity.this, SaveActivity.class);
                intent.putExtra("from", 1);

                intent.putExtra("latitude", (long)(desPoint.latitude * 1000000 * 1000000));
				intent.putExtra("longitude", (long)(desPoint.longitude * 1000000 * 1000000));

//				intent.putExtra("le", (int)(desPoint.longitude));

				StandardActivity.this.startActivity(intent);
            }
        });
        locButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != staticLocation && staticLocation.getLocType() != BDLocation.TypeServerError) {
					LatLng point = new LatLng(staticLocation.getLatitude(), staticLocation.getLongitude());
					// 构建Marker图标
					BitmapDescriptor bitmap = null;
					bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
					OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
					// 在地图上添加Marker，并显示
					mBaiduMap.addOverlay(option);
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

					saveButton.setEnabled(true);
					naviButton.setEnabled(true);

				}

//				LatLng point = new LatLng(120.0, 45.0);
//				// 构建Marker图标
//				BitmapDescriptor bitmap = null;
//                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
//				OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//				// 在地图上添加Marker，并显示
//				mBaiduMap.addOverlay(option);
//				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
//				if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
//					locationService.start();// 定位SDK
//											// start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
//					startLocation.setText(getString(R.string.stoplocation));
//				} else {
//					locationService.stop();
//					startLocation.setText(getString(R.string.startlocation));
//				}
			}
		});

        naviButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				double mLat1 = 39.915291;
//                double mLon1 = 116.403857;
//                double mLat2 = 40.056858;
//                double mLon2 = 116.308194;
//                LatLng pt1 = new LatLng(mLat1, mLon1);
//                LatLng pt2 = new LatLng(mLat2, mLon2);
//
//                NaviParaOption para = new NaviParaOption()
//                        .startPoint(pt1).endPoint(pt2)
//                        .startName("天安门").endName("百度大厦");
				if(startPoint == null || desPoint == null) {
					return;
				}

				LatLng pt1 = new LatLng(startPoint.latitude, startPoint.longitude);
				LatLng pt2 = new LatLng(desPoint.latitude, desPoint.longitude);

				NaviParaOption para = new NaviParaOption()
						.startPoint(pt1).endPoint(pt2)
						.startName("起始点").endName("目的地");



                try {

// 调起百度地图步行导航
                    BaiduMapNavigation.openBaiduMapNavi(para, StandardActivity.this);
                } catch (BaiduMapAppNotSupportNaviException e) {
                    e.printStackTrace();
//                    showDialog();
//                    text_112.setText(e.toString());
                }
			}
		});

		favButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StandardActivity.this, FavouritesActivity.class);

				StandardActivity.this.startActivity(intent);
			}
		});

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus mapStatus) {

			}

			@Override
			public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

			}

			@Override
			public void onMapStatusChange(MapStatus mapStatus) {

			}

			@Override
			public void onMapStatusChangeFinish(MapStatus mapStatus) {
				if(mBaiduMap.isMyLocationEnabled()) {
					naviButton.setEnabled(true);
					saveButton.setEnabled(true);
				} else {
					naviButton.setEnabled(false);
					saveButton.setEnabled(false);
				}

			}
		});


        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				desPoint = point;

					// 开启定位图层
					mBaiduMap.setMyLocationEnabled(true);

// 构造定位数据
					MyLocationData locData = new MyLocationData.Builder()
							.accuracy(90)
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(100).latitude(point.latitude)
							.longitude(point.longitude).build();

// 设置定位数据
					mBaiduMap.setMyLocationData(locData);

// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
					BitmapDescriptor mCurrentMarker =
							BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark);
					MyLocationConfiguration config =
							new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
					mBaiduMap.setMyLocationConfiguration(config);


// 当不需要定位图层时关闭定位图层
//					mBaiduMap.setMyLocationEnabled(false);
			}
        });
	}

	@Override
    public void onBackPressed() {
	    if(mBaiduMap.isMyLocationEnabled()){
            mBaiduMap.setMyLocationEnabled(false);
        } else {
	        super.onBackPressed();
        }

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
				staticLocation = location;

				LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

				startPoint = point;
				// 构建Marker图标
				BitmapDescriptor bitmap = null;
				bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
				OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
				// 在地图上添加Marker，并显示
				mBaiduMap.addOverlay(option);
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));

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
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
//		mLocClient.stop();
		// 关闭定位图层
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
