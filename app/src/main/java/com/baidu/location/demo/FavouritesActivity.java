package com.baidu.location.demo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.baidulocationdemo.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView.OnItemClickListener;


/***List<String> data = new ArrayList<String>();
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 * 
 * @author baidu
 *
 */
public class FavouritesActivity extends Activity {
	private TextView favResult;
	private Button search_button;
	private EditText search_text;
	private ListView fav_list;

	private DBHelper dbhelper;
//	private SQLiteDatabase sqldb;

	private List<String> data = new ArrayList<String>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dbhelper = new DBHelper(this, "mydb.sqlite", null, 1);

		// -----------demo view config ------------
		setContentView(R.layout.favourites);
		favResult = (TextView) findViewById(R.id.textView12);
		favResult.setMovementMethod(ScrollingMovementMethod.getInstance());
		search_button = (Button) findViewById(R.id.search_button2);

		search_text = (EditText) findViewById(R.id.search_text2);

		fav_list = (ListView) findViewById(R.id.fav_list2);

	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String searchStr = search_text.getText().toString();
				if(searchStr.trim().length() == 0) {
					return;
				} else {
					String searchEXP = "%" + searchStr + "%";

					if(!data.isEmpty()) {
						fav_list.setAdapter(
								new ArrayAdapter<String>(
										FavouritesActivity.this,
										R.layout.array_adapter,
										data));
					}
				}

			}
		});

		fav_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String clicked_id = data.get(arg2);

				//得到数据库对象
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				//创建游标
				Cursor mCursor = db.query("fav_table",
						new String[] { "id", "name", "number",
								"type", "grade", "remark",
								"latitude", "longitude" },
						"id=?", new String[]{clicked_id}, null, null,
						null);
				//游标置顶
				mCursor.moveToFirst();

				String clicked_lati = mCursor.getString(mCursor.getColumnIndex("latitude"));
				String clicked_longi = mCursor.getString(mCursor.getColumnIndex("longitude"));

				db.close();

				long lati3 = Long.parseLong(clicked_lati);
				long longi3 = Long.parseLong(clicked_longi);

				Intent intent = new Intent(FavouritesActivity.this, StandardActivity.class);
				intent.putExtra("from", 2);

				intent.putExtra("clicked_lati", lati3);
				intent.putExtra("clicked_longi", longi3);

				startActivity(intent);
			}
		});
	}


	private void getData(String searchEXP) {

//		List<String> data = new ArrayList<String>();

		//得到数据库对象
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		//创建游标
		Cursor mCursor = db.query("fav_table",
				new String[] { "id", "name", "number",
						"type", "grade", "remark",
						"latitude", "longitude" },
				"id LIKE ?", new String[]{searchEXP}, null, null,
				null);
		//游标置顶
		mCursor.moveToFirst();
		//遍历
		do{
			String id_result = mCursor.getString(mCursor.getColumnIndex("id"));

			data.add(id_result);
//			double d = Long.parseLong(name);
//			showText.setText("" + d/1000000d/1000000d);
		}while(mCursor.moveToNext());

		db.close();
	}

	/**
	 * 显示请求字符串
	 *
	 * @param str
	 */
	public void logMsg(String str) {
		final String s = str;
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


	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}

