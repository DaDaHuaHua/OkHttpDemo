package com.gaiay.base.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {

	/**
	 * 检查某表列是否存在
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return
	 */
	public static boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;
		try {
			// 查询一行
			cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
			result = cursor != null && cursor.getColumnIndex(columnName) != -1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return result;
	}

	/**
	 * 检查表中某列是否存在
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return
	 */
	public static boolean checkTableAndColumnExists(SQLiteDatabase db, String tableName, String columnName) {
		boolean result = false;
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?", new String[] { tableName,
					"%" + columnName + "%" });
			result = null != cursor && cursor.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}

	/**
	 * 检查表是否存在
	 * 
	 * @param db
	 * @param tableName
	 *            表名
	 * @return
	 */
	public static boolean checkTableExists(SQLiteDatabase db, String tableName) {
		boolean result = false;
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("select * from sqlite_master where name = ?", new String[] { tableName });
			result = null != cursor && cursor.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 数据库查询，特殊字符转译
	 * 
	 * @param keyWord
	 * @return
	 */
	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}
}
