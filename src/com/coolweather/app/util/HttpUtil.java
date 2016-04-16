package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 从服务器端获取数据的工具类
 * 
 * @author Administrator
 * 
 */
public class HttpUtil {
	/**
	 * 发送http请求的方法
	 */
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection conn = null;
				try {
					URL url = new URL(address);
					// 打开网络连接
					conn = (HttpURLConnection) url.openConnection();
					// 设置请求方式
					conn.setRequestMethod("GET");
					// 设置连接超时时间
					conn.setConnectTimeout(8000);
					// 设置响应时间
					conn.setReadTimeout(8000);
					// 获取到输入流
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine())!=null){
						response.append(line);
					}
					if(listener != null){
						//回调哦onfinish方法
						listener.onFinish(response.toString());
					}

				} catch (Exception e) {
					// TODO: handle exception
					if(listener != null){
						//回调哦onfinish方法
						listener.onError(e);
					}
				}finally{
					if(conn != null){
						conn.disconnect();//释放资源
					}
				}
			}
		}).start();
	}

	// 接口
	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);
	}
}
