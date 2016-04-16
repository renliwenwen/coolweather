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
 * ���ݿ⴦����
 * 
 * @author Administrator
 * 
 */
public class CoolWeatherDB {
	// ���ݿ���
	public static final String DB_NAME = "cool_weather.db";
	// ���ݿ�汾
	public static final int VERSION = 1;
	private static CoolWeatherDB coolweatherDB;
	private SQLiteDatabase db;

	/**
	 * �����췽��˽�л�
	 */
	private CoolWeatherDB(Context context) {
		// �������ݿ���
		CoolWeatherOpenHelper dbhelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbhelper.getWritableDatabase();
	}

	/**
	 * ��ȡCoolWeatherDB��ʵ��,���þ�̬������ȡʵ����������ֱ�Ӵ���ʵ��
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolweatherDB == null) {
			coolweatherDB = new CoolWeatherDB(context);
		}
		return coolweatherDB;
	}

	/**
	 * ��Province��ʵ����ŵ����ݿ�
	 */
	public void saveProvince(Province province) {
		// ���ж�province�Ƿ�Ϊ��
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			// ��ӵ����ݿ�
			db.insert("Province", null, values);
		}
	}

	/**
	 * �����ݿ��ȡȫ�����е�ʡ�ݵ���Ϣ
	 */
	public List<Province> loadProvinces() {
		// ����List�������������ʡ��
		List<Province> list = new ArrayList<Province>();
		// ��ѯ
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// ����һ��ʡ��ʵ�������Ϣ
				Province province = new Province();
				province.setId(cursor.getColumnIndex("id"));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				// ��ӵ�����
				list.add(province);

			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;

	}

	/**
	 * ��City��ʵ����ŵ����ݿ�
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			// ��ӵ����ݿ�
			db.insert("City", null, values);
		}
	}

	/**
	 * �鿴ĳһʡ�µ����г���,�������ʡ��ID
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
	 * ���ص�ʵ����ŵ����ݿ�
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_Id", county.getCityId());
			// ��ӵ����ݿ�
			db.insert("County", null, values);
		}
	}

	/**
	 * �����ݿ��ж�ȡ��ĳһ������������
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		// ��ѯ
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
