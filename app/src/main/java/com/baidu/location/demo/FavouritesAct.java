package com.baidu.location.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.baidulocationdemo.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class FavouritesAct extends Activity {
	private TextView favResult;
	private Button search_button;
	private EditText search_text;
	private TextView show_text;
	private ListView fav_list;
	private List<String> data = new ArrayList<String>();

	public static final int SHOW_RESPONSE = 0;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case SHOW_RESPONSE:
					String response = (String) msg.obj;
//                    textView_response.setText(response);
					show_text.setText(response);
					if(!data.isEmpty()) {
						fav_list.setAdapter(
								new ArrayAdapter<String>(
										FavouritesAct.this,
										R.layout.array_adapter,
										data));
					}
					break;
				default:
					break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favourites);
		favResult = (TextView) findViewById(R.id.textView12);
		favResult.setMovementMethod(ScrollingMovementMethod.getInstance());
		search_button = (Button) findViewById(R.id.search_button2);
		search_text = (EditText) findViewById(R.id.search_text2);
        show_text = (TextView) findViewById(R.id.show_text22);
		fav_list = (ListView) findViewById(R.id.fav_list2);
	}


	@Override
	protected void onStart() {
		super.onStart();

		search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				data.clear();
				String searchStr = search_text.getText().toString();
				if(searchStr.trim().length() == 0) {
					return;
				} else {
					getData(searchStr);
				}

			}
		});

		fav_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final int i = arg2;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String clicked_line = data.get(i);
						String url = "http://192.168.43.137:8088/web2/LocServlet.action";
						String urlget = url + "?line=" + clicked_line;

						HttpClient httpCient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet(urlget);

						try {
							HttpResponse httpResponse = httpCient.execute(httpGet);
							if (httpResponse.getStatusLine().getStatusCode() == 200) {
								HttpEntity entity = httpResponse.getEntity();
								String response = EntityUtils.toString(entity,"utf-8");

								try {
									JSONObject obj = new JSONObject(response.toString());
									String remark = obj.getString("remark");
									String lati = obj.getString("lati");
									String longi = obj.getString("longi");

									long lati3 = Long.parseLong(lati);
									long longi3 = Long.parseLong(longi);

									Intent intent = new Intent(FavouritesAct.this, NaviAct.class);
									intent.putExtra("from", 2);
									intent.putExtra("clicked_lati", lati3);
									intent.putExtra("clicked_longi", longi3);
									intent.putExtra("clicked_line", clicked_line);
									startActivity(intent);

									Message message = new Message();
									message.what = SHOW_RESPONSE;
									message.obj = obj.toString();
									handler.sendMessage(message);

								} catch (JSONException e) {
									e.printStackTrace();
								}

							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}


	private void getData(final String searchStr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "http://192.168.43.137:8088/web2/SearchServlet.action";
				String urlget = url + "?search=" + searchStr;

				HttpClient httpCient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(urlget);

				try {
					HttpResponse httpResponse = httpCient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity,"utf-8");

						try {
							JSONArray getJsonArray= new JSONArray(response.toString());

							for(int j = 0; j < getJsonArray.length(); j = j + 1) {
								String id_result = getJsonArray.getString(j);
								data.add(id_result);
							}
							Message message = new Message();
							message.what = SHOW_RESPONSE;
							message.obj = getJsonArray.getString(0);
							handler.sendMessage(message);

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
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

