package com.baidu.location.demo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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

import java.util.ArrayList;

public class LoginAct extends Activity {

     public static final int SHOW_RESPONSE = 0;
     private Button loginButton;
     private EditText password, username;
     private String uname,pword;
     private TextView result;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    public static boolean log = false;

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

     //新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
     private Handler handler = new Handler() {

         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
//                    textView_response.setText(response);
                    result.setText(response);
                    if(response.equals("login successfully")) {
                        log = true;
                        Intent intent = new Intent(LoginAct.this, MainActivity.class);
                        intent.putExtra("from", 0);
                        startActivity(intent);
                    } else {
                        log = false;
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

         setContentView(R.layout.login);
         loginButton = (Button)findViewById(R.id.login_button);
//         zcButton = (Button)findViewById(R.id.zc_button);
         password = (EditText)findViewById(R.id.password);
         username = (EditText)findViewById(R.id.username);

         result = (TextView) findViewById(R.id.result_text);

         loginButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 loginRequest();
             }
         });

//         zcButton.setOnClickListener(new OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 zcRequest();
//             }
//         });

         password.addTextChangedListener(new TextWatcher() {
             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 if(password.getText().toString().trim().length() == 0 || username.getText().toString().trim().length() == 0) {
                     loginButton.setEnabled(false);
//                     zcButton.setEnabled(false);
                 } else {
                     loginButton.setEnabled(true);
//                     zcButton.setEnabled(true);
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
                     loginButton.setEnabled(false);
//                     zcButton.setEnabled(false);
                 } else {
                     loginButton.setEnabled(true);
//                     zcButton.setEnabled(true);
                 }
             }

             @Override
             public void afterTextChanged(Editable editable) {

             }

             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }
         } );

         getPersimmions();
     }

     //方法：发送网络请求，获取百度首页的数据。在里面开启线程
     private void loginRequest() {
         new Thread(new Runnable() {

             @Override
             public void run() {
                 //用HttpClient发送请求，分为五步
                 //第一步：创建HttpClient对象
                 uname = username.getText().toString().trim();
                 pword = password.getText().toString().trim();
                 String url = "http://192.168.43.137:8088/web2/LoginServlet.action";
                 String urlget = url + "?username=" + uname + "&password=" + pword;
                 HttpClient httpCient = new DefaultHttpClient();
                 //第二步：创建代表请求的对象,参数是访问的服务器地址
                 HttpGet httpGet = new HttpGet(urlget);

                 try {
                     //第三步：执行请求，获取服务器发还的相应对象
                     HttpResponse httpResponse = httpCient.execute(httpGet);
                     //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                     if (httpResponse.getStatusLine().getStatusCode() == 200) {
                         //第五步：从相应对象当中取出数据，放到entity当中
                         HttpEntity entity = httpResponse.getEntity();
                         String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串

                         //在子线程中将Message对象发出去
                         Message message = new Message();
                         message.what = SHOW_RESPONSE;
                         message.obj = response.toString();
                         handler.sendMessage(message);
                     }

                 } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }
             }
         }).start();//这个start()方法不要忘记了
     }

//    private void zcRequest() {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                uname = username.getText().toString().trim();
//                pword = password.getText().toString().trim();
//                String url = "http://192.168.43.137:8088/web2/ZcServlet.action";
//                String urlget = url + "?username=" + uname + "&password=" + pword;
//                HttpClient httpCient = new DefaultHttpClient();
//                HttpGet httpGet = new HttpGet(urlget);
//
//                try {
//                    HttpResponse httpResponse = httpCient.execute(httpGet);
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        HttpEntity entity = httpResponse.getEntity();
//                        String response = EntityUtils.toString(entity,"utf-8");
//                        Message message = new Message();
//                        message.what = SHOW_RESPONSE;
//                        message.obj = response.toString();
//                        handler.sendMessage(message);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

 }