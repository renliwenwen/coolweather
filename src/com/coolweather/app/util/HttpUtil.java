package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * �ӷ������˻�ȡ���ݵĹ�����
 * 
 * @author Administrator
 * 
 */
public class HttpUtil {
	/**
	 * ����http����ķ���
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
					// ����������
					conn = (HttpURLConnection) url.openConnection();
					// ��������ʽ
					conn.setRequestMethod("GET");
					// �������ӳ�ʱʱ��
					conn.setConnectTimeout(8000);
					// ������Ӧʱ��
					conn.setReadTimeout(8000);
					// ��ȡ��������
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine())!=null){
						response.append(line);
					}
					if(listener != null){
						//�ص�Ŷonfinish����
						listener.onFinish(response.toString());
					}

				} catch (Exception e) {
					// TODO: handle exception
					if(listener != null){
						//�ص�Ŷonfinish����
						listener.onError(e);
					}
				}finally{
					if(conn != null){
						conn.disconnect();//�ͷ���Դ
					}
				}
			}
		}).start();
	}

	// �ӿ�
	public interface HttpCallbackListener {
		void onFinish(String response);

		void onError(Exception e);
	}
}
