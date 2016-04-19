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
	// ������¼ѡ������
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	// ���������
	private ProgressDialog progressDialog;
	// ������
	private TextView title;
	private ListView listView;
	// ������
	private ArrayAdapter<String> adapter;
	// ���ݿ⴦����
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	// ʡ�ļ���
	private List<Province> provinceList;
	// �еļ���
	private List<City> cityList;
	// �صļ���
	private List<County> countyList;
	// ��ǰѡ�еļ���
	private int currentLevel;

	// ��ѡ�е�ʡ��
	private Province selectedProvince;
	// ��ѡ�е���
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
		// ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ���ز���
		setContentView(R.layout.choose_area);
		// ��ȡlistview�ؼ�
		listView = (ListView) findViewById(R.id.list_view);
		title = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		// ��ʼ�����ݿ⴦����
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		// ��listview������Ŀ����¼�
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// �ж�ѡ��ļ���
				if (currentLevel == LEVEL_PROVINCE) {// ѡ�е���ʡ
					selectedProvince = provinceList.get(position);
					// ��ѯ��ʡ�µ����г���
					querryCities();
				} else if (currentLevel == LEVEL_CITY) {// ѡ�е���
					selectedCity = cityList.get(position);// ��ǰѡ�еĳ���
					// ��ѯ�ó����µ�������
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
		// ��ѯ���е�ʡ��
		querryProvinces();
	}

	/**
	 * ��ѯȫ�����е�ʡ�ݣ��������ݿ��ѯ���ٴ����������ѯ
	 */

	private void querryProvinces() {
		// TODO Auto-generated method stub
		// ���ȴ����ݿ��в�ѯ���е�ʡ��
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			// ��������
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			// ˢ�½���
			adapter.notifyDataSetChanged();
			listView.setSelection(0);// ѡ�е�һ��
			title.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			// �ӷ������ϲ���
			querryFormServer(null, "province");
		}
	}

	/**
	 * ��ѯĳʡ���µ����еĳ��У��������ݿ��ѯ���ٴ����������ѯ
	 */

	private void querryCities() {
		// TODO Auto-generated method stub
		// �ȴ����ݿ��ѯ
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
	 * ��ѯĳ�����µ����е��أ��������ݿ��ѯ���ٴ����������ѯ
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
	 * �ӷ������ϲ�������,���ݴ��ź�����
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
		// ��ʾ������
		showProgressDialog();
		// ������������Ĺ�����
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);// ������������������ʡ������
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {// �ɹ�true
					// �ص����߳�
					runOnUiThread(new Runnable() {
						public void run() {
							// �رս�����
							closeProgressDialog();
							if ("province".equals(type)) {
								// ��ѯ����ʡ��
								querryProvinces();
							} else if ("city".equals(type)) {
								// ��ѯ������
								querryCities();
							} else if ("county".equals(type)) {
								// ��ѯ������
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
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 0)
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
			progressDialog.setMessage("���ڼ�����....");
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
	 * ����back���ж�ѡ��ļ���
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
