package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallbackListener;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ������Activity
 * 
 * @author Administrator
 * 
 */
public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	// ������
	private TextView cityNameText;
	// ����ʱ��
	private TextView publishText;
	// ����������
	private TextView weatherDespText;
	// ��ʾ����1
	private TextView temp1Text;
	// ��ʾ����2
	private TextView temp2Text;
	// ��ʾ��ǰ����
	private TextView currentDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// ��ʼ���ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		// �ж��Ƿ����ؼ����ţ������ѯ�ؼ�������������ʾ��������
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("ͬ���С�������");
			weatherInfoLayout.setVisibility(View.INVISIBLE);// ���ɼ�������ռ��ԭ���Ŀռ�
			cityNameText.setVisibility(View.INVISIBLE);
			querryWeatherCode(countyCode);
		} else {
			// ��ʾ��������
			showWeather();
		}
	}

	/**
	 * ��ѯ��Ӧ���ؼ����ŵ�����
	 * 
	 * @param countyCode
	 */
	private void querryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode
				+ ".xml";// ���ص������� �ؼ�����|��������
		// �ӷ������ϲ�ѯ����
		querryFromServer(address, "countyCode");

	}

	/**
	 * ��ѯ�������Ŷ�Ӧ���������
	 */
	public void querryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode
				+ ".html";
		querryFromServer(address, "weatherCode");
	}

	/**
	 * ��������ѯ����
	 * 
	 * @param address
	 * @param string
	 */
	private void querryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		// ���ݴ����type����������ѯ�������Ż�������
		// ��������
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				// �ж�type������
				if ("countyCode".equals(type)) {
					// ��ѯ��������
					if (!TextUtils.isEmpty(response)) {
						// ����������������
						String[] code = response.split("\\|");
						if (code != null && code.length == 2) {
							String weatherCode = code[1];
							// �����������Ų�ѯ����
							querryWeatherInfo(weatherCode);
						}
					} 
				}else if ("weatherCode".equals(type)) {
					// �����������������������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					// �л������߳�
					runOnUiThread(new Runnable() {
						public void run() {
							// ��ʾ������Ϣ
							showWeather();
						}
					});

				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��������ʾ�������ļ���
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		// ��sharedPreferences��ȡ������
		SharedPreferences sp = this.getSharedPreferences("weather", MODE_APPEND);
		//������ȡ������ʾ��ҳ����
		cityNameText.setText(sp.getString("city_name", ""));
		temp1Text.setText(sp.getString("temp1", ""));
		temp2Text.setText(sp.getString("temp2", ""));
		weatherDespText.setText(sp.getString("weather_desp", ""));
		publishText.setText("����" +sp.getString("weather_time","")+"����");
		currentDateText.setText(sp.getString("weather_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
