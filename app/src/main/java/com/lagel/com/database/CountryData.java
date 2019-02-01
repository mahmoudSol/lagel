package com.lagel.com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lagel.com.county_code_picker.Country;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CountryData implements Closeable {
	private static final String TAG = CountryData.class.getSimpleName();
	private static final String DATABASE_NAME = "country_data.db";
	private static final int DATABASE_VERSION = 1;
	/**
	 * DATABASE CONSTANTS
	 */
	public final static String COUNTRY_TABLE = "country_table";

	//MENSAJES COLUMN TABLE
	public final static String COLUMN_IDPK = "_id";
	public final static String COLUMN_CODE = "_code";
	public final static String COLUMN_FULL_NAME = "fullname";
	public final static String COLUMN_NAME = "name";


	/**
	 * FIELDS
	 */
	private Context context;
	private SQLiteDatabase db;

	/**
	 * THIS CLASS IS A SINGLETON
	 */
	private static CountryData instance = null;

	public static CountryData instance(Context context) {
		if (instance == null) {
			instance = new CountryData(context.getApplicationContext());
		}

		return instance;
	}

	private CountryData(Context context) {
		this.context = context;
	}

	@Override
	public void close() throws IOException {
		if (db != null) {
			db.close();
			db = null;
		}
	}


	public SQLiteDatabase getDb() {
		if (db == null) {
			DbQuizOpenHelper helper = new DbQuizOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION);

			try {
				db = helper.getWritableDatabase();
			} catch (SQLiteException ex) {
				//SmartLogger.error(ex);
				db = helper.getReadableDatabase();
			}
		}

		return db;
	}

	private static class DbQuizOpenHelper extends SQLiteOpenHelper {
		private Context context;

		public DbQuizOpenHelper(Context context, String name,
                                CursorFactory factory, int version) {
			super(context, name, factory, version);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			    db.execSQL("CREATE TABLE " +
						COUNTRY_TABLE + "(" +
						COLUMN_IDPK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						COLUMN_CODE + " TEXT, " +
						COLUMN_FULL_NAME + " TEXT, " +
						COLUMN_NAME + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + COUNTRY_TABLE);
			onCreate(db);
		}
	}

	public int clearDataCountry()
	{
		getDb().beginTransaction();
		int result =getDb().delete(COUNTRY_TABLE, null, null);
		getDb().setTransactionSuccessful();
		getDb().endTransaction();

		return result;
	}

	public ArrayList<Country> getCountryFilter(String id) {
		ArrayList<Country> result = new ArrayList<Country>();
		//Cursor c = getDb().rawQuery("SELECT * FROM country_table where fullname='"+id+"'", null);
		Cursor c = getDb().rawQuery("SELECT * FROM country_table where fullname LIKE '"+id+"%'", null);
		if (c.getCount() > 0 && c.moveToFirst()) {
			do {
				Country q = cursor2Data(c);
				result.add(q);
			} while (c.moveToNext());
		}

		if (c!=null && !c.isClosed())
		{
			c.deactivate();
			c.close();
		}

		return result;
	}



	public ArrayList<Country> getAllCountry() {
		ArrayList<Country> result = new ArrayList<Country>();
		Cursor c = getDb().rawQuery("SELECT * FROM country_table ", null);
		if (c.getCount() > 0 && c.moveToFirst()) {
			do {
				Country q = cursor2Data(c);
				result.add(q);
			} while (c.moveToNext());
		}

		if (c!=null && !c.isClosed())
		{
			c.deactivate();
			c.close();
		}

		return result;
	}


	private Country cursor2Data(Cursor c) {
		Country result = new Country();
		final int columnsCount = c.getColumnCount();

		try{
			for (int i = 0; i < columnsCount; i++) {
				final String columnName = c.getColumnName(i);
				if (COLUMN_CODE.equals(columnName)) {
					result.setCode(c.getString(i));
				} else if (COLUMN_FULL_NAME.equals(columnName)) {
					result.setFullname(c.getString(i));
				} else if (COLUMN_NAME.equals(columnName)) {
					result.setName(c.getString(i));
				}

			}
		}catch(Exception ex)
		{
			Log.e("Error", "SQLLITE: "+ex.getMessage());
		}

		return result;
	}


	public void setInsertCountry(ArrayList<Country> value) {
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getDb(), COUNTRY_TABLE);

		final int COL_CODE = ih.getColumnIndex(COLUMN_CODE);
		final int COL_FULL_NAME = ih.getColumnIndex(COLUMN_FULL_NAME);
		final int COL_NAME = ih.getColumnIndex(COLUMN_NAME);

		Date now = new Date();
		try {
				getDb().setLockingEnabled(false);
				getDb().beginTransaction();
			if (value!=null) {
				for (int i = 0; i < value.size(); i++) {
					ih.prepareForInsert();
					ih.bind(COL_CODE, value.get(i).getCode());
					ih.bind(COL_FULL_NAME, value.get(i).getFullname());
					ih.bind(COL_NAME, value.get(i).getName());


					ih.execute();
				}
				getDb().setTransactionSuccessful();
			}
		}
		finally {
			getDb().endTransaction();
			getDb().setLockingEnabled(true);
	        ih.close();  
		}

	}


	public void setInsertCountry(Country value) {
		try{

				getDb().beginTransaction();
				final ContentValues values = new ContentValues();
				Date now = new Date();
				values.put(COLUMN_CODE, value.getCode());
				values.put(COLUMN_FULL_NAME, value.getFullname());
				values.put(COLUMN_NAME, value.getName());

				long result=getDb().insert(COUNTRY_TABLE, null, values);
				getDb().setTransactionSuccessful();
				getDb().endTransaction();


		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

}
