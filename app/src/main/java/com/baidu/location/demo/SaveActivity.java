package com.baidu.location.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.baidulocationdemo.R;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;


/***
 * 展示定位sdk配置的示例，配置选项后调用的实际上是locationActivity的定位功能，但是覆盖了新的配置项
 * 注意：有些选项存在缓存的原因，所有在选中后再取消依然会在定位结果中显示出来
 * @author baidu
 *
 */
public class SaveActivity extends Activity{
	private EditText lineText;
	private EditText numberText;
	private EditText remarkText, showText;
	private RadioButton towerRB, boxRB, otherRB, kv110, kv220, kvElse;
	private Button addButton, showButton;

	private DBHelper dbhelper;
	private SQLiteDatabase sqldb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dbhelper = new DBHelper(this, "mydb.sqlite", null, 1);

		setContentView(R.layout.save);
		lineText = (EditText)findViewById(R.id.line_text);
		remarkText = (EditText)findViewById(R.id.remark_text);
		showText = (EditText)findViewById(R.id.show_text);
		numberText = (EditText)findViewById(R.id.number_text);
		towerRB = (RadioButton) findViewById(R.id.tower_radio);
		boxRB = (RadioButton) findViewById(R.id.box_radio);
		otherRB = (RadioButton) findViewById(R.id.other_radio);
		kv110 = (RadioButton) findViewById(R.id.kv110_radio);
		kv220 = (RadioButton) findViewById(R.id.kv220_radio);
		kvElse = (RadioButton) findViewById(R.id.kvelse_radio);
		addButton = (Button)findViewById(R.id.add_button);
		showButton = (Button)findViewById(R.id.show_button);

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				long lati = getIntent().getLongExtra("latitude", 0);
				long longi = getIntent().getLongExtra("longitude", 0);
				String lineStr = lineText.getText().toString();
				String numberStr = numberText.getText().toString().trim();
                int number = 0;
				try {
				    number = Integer.parseInt(numberStr);
                } catch (NumberFormatException e) {
                }
				String remarkStr = remarkText.getText().toString();
				int grade = 0;
				if(kv110.isChecked()) {
					grade = 110;
				} else if(kv220.isChecked()) {
					grade = 220;
				} else {
					grade = 0;
				}

				String type  = "其它";
				if(towerRB.isChecked()) {
					type = "终端塔";
				} else if(boxRB.isChecked()) {
					type = "接地箱";
				} else {
					type = "其它";
				}


				SQLiteDatabase db =dbhelper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put("id", lineStr + number + "#" + type
						+ "(" + grade + "kV)\n坐标（" + lati +", " + longi + ")");
				cv.put("name", lineStr);
				cv.put("number", number);
				cv.put("type", type);
				cv.put("grade", grade);
				cv.put("remark", remarkStr);
				cv.put("latitude", lati);
				cv.put("longitude", longi);
				long re = db.insert("fav_table", null, cv);
				db.close();

//				int fr = getIntent().getIntExtra("from", 0);
//				int le = getIntent().getIntExtra("le", 0);
				showText.setText("inserted successfully " + re
						+ "\\n坐标（" + lati +", " + longi + ")" );
			}
		});

		lineText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(lineText.getText().toString().trim().length() == 0) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        } );


		showButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//得到数据库对象
				sqldb = dbhelper.getReadableDatabase();
				//创建游标
				Cursor mCursor = sqldb.query("fav_table",
						new String[] { "id", "name", "number",
								"type", "grade", "remark",
								"latitude", "longitude" },
						"number=?", new String[]{"1"}, null, null,
						null);
				//游标置顶
				mCursor.moveToFirst();
				//遍历
				do{
					String name = mCursor.getString(mCursor.getColumnIndex("longitude"));
//					System.out.println(name);
					long d = Long.parseLong(name);
					showText.setText("" + d/1000000d/1000000d);
				}while(mCursor.moveToNext());

				sqldb.close();

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		sqldb.close();
		dbhelper.close();
		super.onStop();
	}
}
