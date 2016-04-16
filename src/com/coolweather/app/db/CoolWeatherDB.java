package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * 数据库处理类
 * 
 * @author Administrator
 * 
 */
public class CoolWeatherDB {
	// 数据库名
	public static final String DB_NAME = "cool_weather.db";
	// 数据库版本
	public static final int VERSION = 1;
	private static CoolWeatherDB coolweatherDB;
	private SQLiteDatabase db;

	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		// 创建数据库类
		CoolWeatherOpenHelper dbhelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbhelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例,调用静态方法获取实例，不允许直接创建实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolweatherDB == null) {
			coolweatherDB = new CoolWeatherDB(context);
		}
		return coolweatherDB;
	}

	/**
	 * 将Province的实例存放到数据库
	 */
	public void saveProvince(Province province) {
		// 先判断province是否为空
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			// 添加到数据库
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库读取全国所有的省份的信息
	 */
	public List<Province> loadProvinces() {
		// 创建List集合来存放所有省份
		List<Province> list = new ArrayList<Province>();
		// 查询
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// 创建一个省的实例存放信息
				Province province = new Province();
				province.setId(cursor.getColumnIndex("id"));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				// 添加到集合
				list.add(province);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;

	}

	/**
	 * 将City的实例存放到数据库
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			// 添加到数据库
			db.insert("City", null, values);
		}
	}

	/**
	 * 查看某一省下的所有城市,根据外键省的ID
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_Id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getColumnIndex("id"));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * 将县的实例存放到数据库
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_Id", county.getCityId());
			// 添加到数据库
			db.insert("County", null, values);
		}
	}

	/**
	 * 从数据库中读取到某一城市下所有县
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		// 查询
		Cursor cursor = db.query("County", null, "city_id=?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getColumnIndex("id"));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setCityId(cityId);
			} while (cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
}
