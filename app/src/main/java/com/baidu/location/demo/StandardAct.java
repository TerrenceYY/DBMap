package com.baidu.location.demo;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

import com.baidu.mapapi.search.poi.*;
//import com.baidu.mapapi.search.poi.PoiResult;
//import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.map.Overlay.*;

/***
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 * 
 * @author baidu
 *
 */
public class StandardAct extends Activity {
	private LocationService locationService;
	private Button locButton;
	private Button saveButton;
	private Button naviButton;
	private TextView showText;
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private LatLng desPoint;
	private LatLng startPoint;

    private Button searchButton;
    private EditText searchText;
    private PoiSearch mPoiSearch;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// -----------demo view config ------------
		setContentView(R.layout.standard);
        locButton = (Button) findViewById(R.id.loc_button);
        saveButton = (Button) findViewById(R.id.save_button);
        naviButton = (Button) findViewById(R.id.navi_button);

        showText = (TextView)findViewById(R.id.show_text55);

        searchButton = (Button) findViewById(R.id.search_button);
        searchText = (EditText) findViewById(R.id.search_text);

        mPoiSearch = PoiSearch.newInstance();

		mMapView = (MapView) findViewById(R.id.bView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

		if(desPoint == null && startPoint == null) {
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(39.1436485, 117.2179097)));
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		locationService = ((LocationApplication) getApplication()).locationService;
		locationService.registerListener(mListener);
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}

		locationService.start();// 定位SDK

        mPoiSearch.setOnGetPoiSearchResultListener(listener);

        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String sText = searchText.getText().toString();


                /**
                 *  PoiCiySearchOption 设置检索属性
                 *  city 检索城市
                 *  keyword 检索内容关键字
                 *  pageNum 分页页码
                 */
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city("天津") //必填
                        .keyword(sText) //必填
                        .pageNum(10));


            }
        });

        locButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != startPoint) {
					desPoint = startPoint;
					onResume();
					mark();
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(desPoint));
				}
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(StandardAct.this, SaveAct.class);
				intent.putExtra("from", 1);

				intent.putExtra("latitude", (long)(desPoint.latitude * 1000000 * 1000000));
				intent.putExtra("longitude", (long)(desPoint.longitude * 1000000 * 1000000));
				StandardAct.this.startActivity(intent);
			}
		});

        naviButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(startPoint == null || desPoint == null) {
					return;
				}

				Intent i1 = new Intent();
				i1.setData(Uri.parse("baidumap://map/direction?" +
						"&origin=" + startPoint.latitude + "," + startPoint.longitude +
						"&destination=" + desPoint.latitude + "," + desPoint.longitude +
						"&coord_type=bd09ll&mode=driving&src=andr.baidu.openAPIdemo"));
				startActivity(i1);
			}
		});


        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				desPoint = point;
				onResume();
				mark();
			}
   	     });

		if (null != startPoint) {
			desPoint = startPoint;
			onResume();
			mark();
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(desPoint));
		}
	}

	@Override
    public void onBackPressed() {
	    if(mBaiduMap.isMyLocationEnabled()){
            mBaiduMap.setMyLocationEnabled(false);
            desPoint = null;
            onResume();
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
				LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

				startPoint = point;

			}
		}

	};

    private OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            showText.setText(poiResult.toString());
			if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
				mBaiduMap.clear();

				//创建PoiOverlay对象
				PoiOverlay poiOverlay = new PoiOverlay(mBaiduMap);

				//设置Poi检索数据
				poiOverlay.setData(poiResult);

				//将poiOverlay添加至地图并缩放至合适级别
				poiOverlay.addToMap();
				poiOverlay.zoomToSpan();
			}

		}
//        @Override
//        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
//
//        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

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
		showText.setText(st + de);

		if(desPoint == null) {
			naviButton.setEnabled(false);
			saveButton.setEnabled(false);
		} else {
			mark();
			if(startPoint != desPoint){
				naviButton.setEnabled(true);
			}
			saveButton.setEnabled(true);
		}
		super.onResume();
	}

	private void mark() {
		mBaiduMap.setMyLocationEnabled(true);
		MyLocationData locData = new MyLocationData.Builder()
				//						.direction(100)
				.latitude(desPoint.latitude)
				.longitude(desPoint.longitude).build();
		mBaiduMap.setMyLocationData(locData);
		BitmapDescriptor mCurrentMarker =
				BitmapDescriptorFactory.fromResource(R.drawable.icon);
		MyLocationConfiguration config =
				new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker);
		mBaiduMap.setMyLocationConfiguration(config);
	}

	@Override
	protected void onDestroy() {
		desPoint = null;
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
        mPoiSearch.destroy();
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
