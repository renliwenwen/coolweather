package com.coolweather.app.util;

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
		if(!TextUtils.isEmpty(response)){
			String[] allCounty = response.split(",");
			if(allCounty != null && allCounty.length>0){
				for(String ocounty : allCounty){
					String[] oneCounty = ocounty.split("\\|");
					County county = new County();
					county.setCountyCode(oneCounty[0]);
					county.setCountyName(oneCounty[1]);
					county.setCityId(cityId);
					//��ӵ����ݿ�
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
