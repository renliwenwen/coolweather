package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.HttpUtil.HttpCallbackListener;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	// 常量记录选择的类别
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	// 定义进度条
	private ProgressDialog progressDialog;
	// 标题栏
	private TextView title;
	private ListView listView;
	// 适配器
	private ArrayAdapter<String> adapter;
	// 数据库处理类
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	// 省的集合
	private List<Province> provinceList;
	// 市的集合
	private List<City> cityList;
	// 县的集合
	private List<County> countyList;
	// 当前选中的级别
	private int currentLevel;

	// 被选中的省份
	private Province selectedProvince;
	// 被选中的市
	private City selectedCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sp.getBoolean("city_selected", false)) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载布局
		setContentView(R.layout.choose_area);
		// 获取listview控件
		listView = (ListView) findViewById(R.id.list_view);
		title = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		// 初始化数据库处理类
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		// 给listview设置条目点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// 判断选择的级别
				if (currentLevel == LEVEL_PROVINCE) {// 选中的是省
					selectedProvince = provinceList.get(position);
					// 查询该省下的所有城市
					querryCities();
				} else if (currentLevel == LEVEL_CITY) {// 选中的市
					selectedCity = cityList.get(position);// 当前选中的城市
					// 查询该城市下的所有县
					querryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position)
							.getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		// 查询所有的省份
		querryProvinces();
	}

	/**
	 * 查询全国所有的省份，优先数据库查询，再次向服务器查询
	 */

	private void querryProvinces() {
		// TODO Auto-generated method stub
		// 首先从数据库中查询所有的省份
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			// 遍历集合
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			// 刷新界面
			adapter.notifyDataSetChanged();
			listView.setSelection(0);// 选中第一个
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			// 从服务器上查找
			querryFormServer(null, "province");
		}
	}

	/**
	 * 查询某省份下的所有的城市，优先数据库查询，再次向服务器查询
	 */

	private void querryCities() {
		// TODO Auto-generated method stub
		// 先从数据库查询
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			querryFormServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询某城市下的所有的县，优先数据库查询，再次向服务器查询
	 */

	private void querryCounties() {
		// TODO Auto-generated method stub
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			querryFormServer(selectedCity.getCityCode(), "county");
		}
	}

	/**
	 * 从服务器上查找数据,根据代号和类型
	 */
	private void querryFormServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;

		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// 显示进度条
		showProgressDialog();
		// 调用请求网络的工具类
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);// 解析服务器返回来的省的数据
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {// 成功true
					// 回到主线程
					runOnUiThread(new Runnable() {
						public void run() {
							// 关闭进度条
							closeProgressDialog();
							if ("province".equals(type)) {
								// 查询所有省份
								querryProvinces();
							} else if ("city".equals(type)) {
								// 查询所有市
								querryCities();
							} else if ("county".equals(type)) {
								// 查询所有县
								querryCounties();
							}
						}

					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0)
								.show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 捕获back键判断选择的级别
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			querryCities();
		} else if (currentLevel == LEVEL_CITY) {
			querryProvinces();
		} else {
			finish();
		}
	}
}
