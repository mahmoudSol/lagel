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


import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CategoryData implements Closeable {
	private static final String TAG = CategoryData.class.getSimpleName();
	private static final String DATABASE_NAME = "catego_data.db";
	private static final int DATABASE_VERSION = 1;
	/**
	 * DATABASE CONSTANTS
	 */
	public final static String CAT_TABLE = "cat_table";

	//MENSAJES COLUMN TABLE
	public final static String COLUMN_IDPK = "_id";
	public final static String COLUMN_ACTIVE_IMAGE = "activeimage";
	public final static String COLUMN_CATEGORY_NODE_ID = "categoryNodeId";
	public final static String COLUMN_DEACTIVE_IMAGE = "deactiveimage";
	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_ISSELECTED = "isSelected";

	/**
	 * FIELDS
	 */
	private Context context;
	private SQLiteDatabase db;

	/**
	 * THIS CLASS IS A SINGLETON
	 */
	private static CategoryData instance = null;

	public static CategoryData instance(Context context) {
		if (instance == null) {
			instance = new CategoryData(context.getApplicationContext());
		}

		return instance;
	}

	private CategoryData(Context context) {
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
						CAT_TABLE + "(" +
						COLUMN_IDPK + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						COLUMN_ACTIVE_IMAGE + " TEXT, " +
						COLUMN_CATEGORY_NODE_ID + " TEXT, " +
						COLUMN_DEACTIVE_IMAGE + " TEXT, " +
						COLUMN_NAME + " TEXT, " +
						COLUMN_ISSELECTED + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CAT_TABLE);
			onCreate(db);
		}
	}

	public int clearDataCate()
	{
		getDb().beginTransaction();
		int result =getDb().delete(CAT_TABLE, null, null);
		getDb().setTransactionSuccessful();
		getDb().endTransaction();

		return result;
	}

	public ArrayList<ProductCategoryResDatas> getNodeId(String id) {
		ArrayList<ProductCategoryResDatas> result = new ArrayList<ProductCategoryResDatas>();
		Cursor c = getDb().rawQuery("SELECT * FROM cat_table where categoryNodeId='"+id+"'", null);
		if (c.getCount() > 0 && c.moveToFirst()) {
			do {
				ProductCategoryResDatas q = cursor2Data(c);
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



	public ArrayList<ProductCategoryResDatas> getAllCategory() {
		ArrayList<ProductCategoryResDatas> result = new ArrayList<ProductCategoryResDatas>();
		Cursor c = getDb().rawQuery("SELECT * FROM cat_table ", null);
		if (c.getCount() > 0 && c.moveToFirst()) {
			do {
				ProductCategoryResDatas q = cursor2Data(c);
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


	private ProductCategoryResDatas cursor2Data(Cursor c) {
		ProductCategoryResDatas result = new ProductCategoryResDatas();
		final int columnsCount = c.getColumnCount();

		try{
			for (int i = 0; i < columnsCount; i++) {
				final String columnName = c.getColumnName(i);
				if (COLUMN_ACTIVE_IMAGE.equals(columnName)) {
					result.setActiveimage(c.getString(i));
				} else if (COLUMN_CATEGORY_NODE_ID.equals(columnName)) {
					result.setCategoryNodeId(c.getString(i));
				} else if (COLUMN_DEACTIVE_IMAGE.equals(columnName)) {
					result.setDeactiveimage(c.getString(i));
				} else if (COLUMN_ISSELECTED.equals(columnName)) {
					result.setSelected(c.getString(i));
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


	public void setInsertCate(ArrayList<ProductCategoryResDatas> value) {
		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(getDb(), CAT_TABLE);

		final int COL_ACTIVE_IMAGE = ih.getColumnIndex(COLUMN_ACTIVE_IMAGE);
		final int COL_CATEGORY_NODE_ID = ih.getColumnIndex(COLUMN_CATEGORY_NODE_ID);
		final int COL_DEACTIVE_IMAGE = ih.getColumnIndex(COLUMN_DEACTIVE_IMAGE);
		final int COL_NAME = ih.getColumnIndex(COLUMN_NAME);
		final int COL_ISSELECTED = ih.getColumnIndex(COLUMN_ISSELECTED);

		Date now = new Date();
		try {
				getDb().setLockingEnabled(false);
				getDb().beginTransaction();
			if (value!=null) {
				for (int i = 0; i < value.size(); i++) {
					ih.prepareForInsert();
					ih.bind(COL_ACTIVE_IMAGE, value.get(i).getActiveimage());
					ih.bind(COL_CATEGORY_NODE_ID, value.get(i).getCategoryNodeId());
					ih.bind(COL_DEACTIVE_IMAGE, value.get(i).getDeactiveimage());
					ih.bind(COL_NAME, value.get(i).getName());
					ih.bind(COL_ISSELECTED, value.get(i).getSelected());


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


	public void setInsertCate(ProductCategoryResDatas value) {
		try{

				getDb().beginTransaction();
				final ContentValues values = new ContentValues();
				Date now = new Date();
				values.put(COLUMN_ACTIVE_IMAGE, value.getActiveimage());
				values.put(COLUMN_CATEGORY_NODE_ID, value.getCategoryNodeId());
				values.put(COLUMN_DEACTIVE_IMAGE, value.getDeactiveimage());
				values.put(COLUMN_NAME, value.getName());
				values.put(COLUMN_ISSELECTED, value.getSelected());


				long result=getDb().insert(CAT_TABLE, null, values);
				getDb().setTransactionSuccessful();
				getDb().endTransaction();


		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

}
