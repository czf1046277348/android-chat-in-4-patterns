package nju.androidchat.client.hw1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Log
@AllArgsConstructor
public class Mvp0PicPersenter implements Mvp0Contract.PicPresenter {

    @Override
    public void start() {

    }

    @Override
    public void showPic(String url, UUID messageID, Handler handler,int type) {
        new Thread(() -> {
            URL myFileURL;
            Bitmap bitmap=null;
            try{
                String targetUrl = url;
                if(!url.contains("https") && url.contains("http")) {
                    targetUrl = url.replace("http", "https");
                }
                myFileURL = new URL(targetUrl);
                //获得连接
                HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                InputStream is = conn.getInputStream();
                //解析得到图片
                bitmap = BitmapFactory.decodeStream(is);
                //关闭数据流
                is.close();
                Message message = handler.obtainMessage(type);
                message.obj = bitmap;
                Bundle bundle=new Bundle();
                bundle.putString("messageID",messageID.toString());
                message.setData(bundle);
                handler.sendMessage(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}
