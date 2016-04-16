package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * 解析服务器返回回来的数据的工具类
 * 
 * @author Administrator
 * 
 */
public class Utility {
	/**
	 * 解析返回来的省份的数据
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		// 首先判断是否有
		if (!TextUtils.isEmpty(response)) {
			// 首先以，号分割存放到数组中
			String[] allProvinces = response.split(",");
			// 然后遍历数组，再次以|号来分割数据
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] one = p.split("\\|");
					// 创建省的实例
					Province province = new Province();
					province.setProvinceName(one[1]);// 索引是1的是省的名字
					province.setProvinceCode(one[0]);// 索引为0的是省的代号
					// 存放到数据库
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器传回来的 市的信息
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
					// 存放到数据库
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析处理服务器传来的县级数据
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
					//添加到数据库
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
