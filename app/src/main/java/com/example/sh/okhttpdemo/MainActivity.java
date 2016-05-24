package com.example.sh.okhttpdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.net.NetAsynTask;
import com.gaiay.base.net.NetCallbackAdapter;
import com.gaiay.base.request.BaseRequest;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) findViewById(R.id.btn_do_net);
        mBtn.setOnClickListener(this);
        mBtn.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        mBtn.setEnabled(false);
        String url = "http://t.zm.gaiay.cn/api/zm/circle/detail?" +
                "appOs=android&appVersion=5.7.0&type=2&userId=0aea05151b4e594b7-7e03&token=17f570148a67038d5f452f232698d05bccdc9a33&circleId=6cc7601545c30dfe2-8000&deviceType=1";
        // okhttpNetOriginal(url);
       // okhttpNetPackaging(url);
         netAsynTask(url);

    }


    private void okhttpNetOriginal(String url){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new  Request.Builder().url(url).build();
       final Call call  = mOkHttpClient.newCall(request);
       final long startTime = System.currentTimeMillis();
       call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i("TAG","原始okhttp耗时=="+(System.currentTimeMillis() - startTime));
                String body = response.body().string();
                Log.i("TAG","body=="+body);
            }
        });

       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = call.execute().body().string();
                     Log.i("TAG","body1=="+response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }
    int i = 0;
    private void okhttpNetPackaging(final String url){
        final long startTime = System.currentTimeMillis();
        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                i++;
                if(i>=50){
                    mBtn.setEnabled(true);
                    return;
                }
                okhttpNetPackaging(url);
                Log.i("TAG","okhttp耗时=="+(System.currentTimeMillis() - startTime));

                String body = response;
                Log.i("TAG","body=="+body);
            }
        });
    }

    private void netAsynTask(final String url){
        final long startTime = System.currentTimeMillis();
        final MyReq req = new MyReq();
        NetAsynTask.connectByGet(url, null, new NetCallbackAdapter() {
            @Override
            public void onGetSucc() {
                super.onGetSucc();
                i++;
                if(i>=50){
                    mBtn.setEnabled(true);
                    return;
                }
                netAsynTask(url);
                Log.i("TAG","NetAsyncTask耗时"+i+"="+(System.currentTimeMillis() - startTime));

                String body = req.response;
                Log.i("TAG","body=="+body);
            }
            @Override
            public void onComplete() {
                super.onComplete();
            }
        },req);
    }

    protected  class MyReq extends  BaseRequest{
        public String response;
        @Override
        public int parseJson(String paramString) throws JSONException {
            this.response = paramString;
            return CommonCode.SUCCESS;
        }
    }
}
