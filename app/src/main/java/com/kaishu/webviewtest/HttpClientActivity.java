package com.kaishu.webviewtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;


public class HttpClientActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int SHOW_RESPONSE = 0;

    private Button mHttpClient;
    private TextView mHttpClientResponse;
    private EditText mHttpClientURL;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    // 在这里进行UI操作，将结果显示到界面上
                    mHttpClientResponse.setText(response);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);

        mHttpClientResponse = (TextView) findViewById(R.id.http_client_response);
        mHttpClientURL = (EditText) findViewById(R.id.http_client_http_url);
        mHttpClient = (Button) findViewById(R.id.http_client_send_request);
        mHttpClient.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_http_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.http_client_send_request) {
            sendRequestWithHttpClient();
        }
    }

    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpClient mHttpClient = new DefaultHttpClient();
                    HttpGet mHttpGet = new HttpGet("http://10.0.2.2/get_data.json");
                    HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
                    if(mHttpResponse.getStatusLine().getStatusCode() == 200){
                        //请求和响应都成功
                        HttpEntity entity = mHttpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");

                        //使用pull解析方法解析xml
                        //parseXMLWithPull(response);

                        //使用SAX解析方法解析xml
                        //parseXMLWithSAX(response);

                        //使用JSON解析JSON数据
                        //parseJSONWithJSONObject(response);

                        //使用GSON解析JSON数据
                        paserJSONWithGSON(response);


                        //Message message = new Message();
                        //message.what = SHOW_RESPONSE;
                        //将服务器返回的结果存放到Message中
                        //message.obj = response.toString();
                        //handler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void paserJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        List<App> appList = gson.fromJson(jsonData, new TypeToken<List<App>>() {}.getType());
        for(App app : appList){
            Log.d("HttpClientActivityGSON", "id is " + app.getId());
            Log.d("HttpClientActivityGSON", "name is " + app.getName());
            Log.d("HttpClientActivityGSON", "version is " + app.getVersion());
        }
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.d("HttpClientActivityJSON", "id is " + id);
                Log.d("HttpClientActivityJSON", "name is " + name);
                Log.d("HttpClientActivityJSON", "version is " + version);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String xmlData) {
        try{
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ContentHandler handler = new ContentHandler();
            //将ContentHandler的实例设置到XMLReader中
            xmlReader.setContentHandler(handler);
            //开始解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseXMLWithPull(String xmlData) {
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while(eventType != xmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    //开始解析某个结点
                    case XmlPullParser.START_TAG:{
                        if("id".equals(nodeName)){
                            id = xmlPullParser.nextText();
                        }else if("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        }else if("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    //完成解析结点
                    case XmlPullParser.END_TAG:{
                        if("app".equals(nodeName)){
                            Log.d("HttpClientActivity", "id is " + id);
                            Log.d("HttpClientActivity", "name is " + name);
                            Log.d("HttpClientActivity", "version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
