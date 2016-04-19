package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * �������������ػ��������ݵĹ�����
 * 
 * @author Administrator
 * 
 */
public class Utility {
	/**
	 * ������������ʡ�ݵ�����
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		// �����ж��Ƿ���
		if (!TextUtils.isEmpty(response)) {
			// �����ԣ��ŷָ��ŵ�������
			String[] allProvinces = response.split(",");
			// Ȼ��������飬�ٴ���|�����ָ�����
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] one = p.split("\\|");
					// ����ʡ��ʵ��
					Province province = new Province();
					province.setProvinceName(one[1]);// ������1����ʡ������
					province.setProvinceCode(one[0]);// ����Ϊ0����ʡ�Ĵ���
					// ��ŵ����ݿ�
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * ������������������ �е���Ϣ
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {

		if (!TextUtils.isEmpty(response)) {
			String[] allCity = response.split(",");
			if (allCity != null && allCity.length > 0) {
				for (String c : allCity) {
					String[] oneCity = c.split("\\|");
					City city = new City();
					city.setCityCode(oneCity[0]);
					city.setCityName(oneCity[1]);
					city.setProvinceId(provinceId);
					// ��ŵ����ݿ�
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * ��������������������ؼ�����
	 */
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounty = response.split(",");
			if (allCounty != null && allCounty.length > 0) {
				for (String ocounty : allCounty) {
					String[] oneCounty = ocounty.split("\\|");
					County county = new County();
					county.setCountyCode(oneCounty[0]);
					county.setCountyName(oneCounty[1]);
					county.setCityId(cityId);
					// ��ӵ����ݿ�
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * ����������������JSON����
	 */

	public static void handleWeatherResponse(Context context, String response) {
		try {
			System.out.println("�����JSON����������Ϣ");
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			// ����������Ϣ
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �����������ص�����������浽SharedPreferences�ļ���
	 * 
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 */

	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
		// SharedPreferences sp = context.getSharedPreferences("weather", mode)
		//SharedPreferences.Editor editor = PreferenceManager
		//		.getDefaultSharedPreferences(context).edit();
		 SharedPreferences sp = context.getSharedPreferences("weather", context.MODE_APPEND);
		 Editor editor = sp.edit();
		//������
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("weather_time", publishTime);
		editor.putString("weather_date", sdf.format(new Date()));
		editor.commit();

	}
}
