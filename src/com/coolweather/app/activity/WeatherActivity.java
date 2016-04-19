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
 * 天气的Activity
 * 
 * @author Administrator
 * 
 */
public class WeatherActivity extends Activity {
	private LinearLayout weatherInfoLayout;
	// 城市名
	private TextView cityNameText;
	// 发布时间
	private TextView publishText;
	// 天气的描述
	private TextView weatherDespText;
	// 显示气温1
	private TextView temp1Text;
	// 显示天气2
	private TextView temp2Text;
	// 显示当前日期
	private TextView currentDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		// 初始化控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		// 判断是否有县级代号，有则查询县级天气，否则显示本地天气
		if (!TextUtils.isEmpty(countyCode)) {
			publishText.setText("同步中。。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);// 不可见，但是占用原来的空间
			cityNameText.setVisibility(View.INVISIBLE);
			querryWeatherCode(countyCode);
		} else {
			// 显示恩地天气
			showWeather();
		}
	}

	/**
	 * 查询对应的县级代号的天气
	 * 
	 * @param countyCode
	 */
	private void querryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode
				+ ".xml";// 返回的数据是 县级代号|天气代号
		// 从服务器上查询数据
		querryFromServer(address, "countyCode");

	}

	/**
	 * 查询天气代号对应的天气情况
	 */
	public void querryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode
				+ ".html";
		querryFromServer(address, "weatherCode");
	}

	/**
	 * 服务器查询天气
	 * 
	 * @param address
	 * @param string
	 */
	private void querryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		// 根据传入的type的类型来查询天气代号或是天气
		// 访问网络
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				// 判断type的类型
				if ("countyCode".equals(type)) {
					// 查询天气代号
					if (!TextUtils.isEmpty(response)) {
						// 解析出来天气代号
						String[] code = response.split("\\|");
						if (code != null && code.length == 2) {
							String weatherCode = code[1];
							// 根据天气代号查询天气
							querryWeatherInfo(weatherCode);
						}
					} 
				}else if ("weatherCode".equals(type)) {
					// 处理服务器返回来的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					// 切换到主线程
					runOnUiThread(new Runnable() {
						public void run() {
							// 显示天气信息
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
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 把天气显示到布局文件中
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		// 从sharedPreferences中取出数据
		SharedPreferences sp = this.getSharedPreferences("weather", MODE_APPEND);
		//把数据取出，显示到页面上
		cityNameText.setText(sp.getString("city_name", ""));
		temp1Text.setText(sp.getString("temp1", ""));
		temp2Text.setText(sp.getString("temp2", ""));
		weatherDespText.setText(sp.getString("weather_desp", ""));
		publishText.setText("今天" +sp.getString("weather_time","")+"发布");
		currentDateText.setText(sp.getString("weather_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
}
