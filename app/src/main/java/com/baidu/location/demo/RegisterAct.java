package com.baidu.location.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.baidulocationdemo.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

public class RegisterAct extends Activity {

     public static final int SHOW_RESPONSE = 0;
    private Button zcButton;
     private EditText password, username;
     private String uname,pword;
     private TextView result;

     //新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
     private Handler handler = new Handler() {

         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    result.setText(response);
                     break;
                 default:
                     break;
             }
         }
     };

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

         setContentView(R.layout.register);
         zcButton = (Button)findViewById(R.id.zc_button);
         password = (EditText)findViewById(R.id.pword52);
         username = (EditText)findViewById(R.id.uname51);
         result = (TextView) findViewById(R.id.result_text51);

         zcButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 zcRequest();
             }
         });

         password.addTextChangedListener(new TextWatcher() {
             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 if(password.getText().toString().trim().length() == 0 || username.getText().toString().trim().length() == 0) {
                     zcButton.setEnabled(false);
                 } else {
                     zcButton.setEnabled(true);
                 }
             }

             @Override
             public void afterTextChanged(Editable editable) {

             }

             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }
         } );

         username.addTextChangedListener(new TextWatcher() {
             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 if(password.getText().toString().trim().length() == 0 || username.getText().toString().trim().length() == 0) {
                     zcButton.setEnabled(false);
                 } else {
                     zcButton.setEnabled(true);
                 }
             }

             @Override
             public void afterTextChanged(Editable editable) {

             }

             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }
         } );
     }

    private void zcRequest() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                uname = username.getText().toString().trim();
                pword = password.getText().toString().trim();
                String url = "http://192.168.43.137:8088/web2/ZcServlet.action";
                String urlget = url + "?username=" + uname + "&password=" + pword;
                HttpClient httpCient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urlget);

                try {
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

 }