package com.xsogen.element;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import jsqlite.Database;
import jsqlite.Exception;

import java.io.File;
import java.util.Map;

public class DatabaseHelper {

  private static final String TAG = "DatabaseHelper";

  private static DatabaseHelper instance;
  private Context context;
  private Database database;

  private LocationDatabase locationDatabase;

  public synchronized static DatabaseHelper getInstance(Context context) {
    if(instance == null) {
      Log.d(TAG, "instance == null, opening instance dir=" + context.getFilesDir());
      instance = new DatabaseHelper(new File(context.getFilesDir(), "element.sqlite"), context);
    }
    return instance;
  }

  private DatabaseHelper(File spatialDbFile, Context context) {
    Log.d(TAG, "private DatabaseHelper() start");
    this.context = context;

    try {
      database = new jsqlite.Database();
      database.open(spatialDbFile.getAbsolutePath(), jsqlite.Constants.SQLITE_OPEN_READWRITE | jsqlite.Constants.SQLITE_OPEN_CREATE);
      locationDatabase = new LocationDatabase(this);

    } catch (Exception e) {
      displayException(e);
    }

    Log.d(TAG, "private DatabaseHelper() end");
  }

  public LocationDatabase getLocationDatabase() {
    return locationDatabase;
  }

  protected static String escapeString(String input) {
    return input.replace("'", "\'");
  }
  
  protected static String unescapeString(String input) {
    return input.replace("\'", "'");
  }

  protected String getStringResource(int resource_id) {
    return context.getString(resource_id);
  }

  protected Database getDatabase() {
    return database;
  }

  protected SpatialCursor prepare(String sql) {
    return new SpatialCursor(this, sql);
  }

  protected void exec(String sql) {
    try {
      database.exec("PRAGMA foreign_keys=ON; " + sql, null);
    }
    catch (Exception e) {
      displayException(e);
    }
  }

  private ContentValues prepareValues(ContentValues values) {
    ContentValues out = new ContentValues();

    for(Map.Entry<String, Object> entry : values.valueSet()) {
      if(entry.getValue().toString().startsWith("ST_"))
        out.put(entry.getKey(), entry.getValue().toString());
      else if (entry.getValue().toString().startsWith("datetime"))
        out.put(entry.getKey(), entry.getValue().toString());
      else
        out.put(entry.getKey(), "'" + entry.getValue() + "'");
    }

    return out;
  }

  protected void insert(String table, ContentValues contentValues) {
    if(contentValues.size() == 0 || table == null)
      return;

    int i = 1;
    ContentValues insertValues = prepareValues(contentValues);

    String sql = "INSERT INTO " + table + " (";
    for(Map.Entry<String, Object> entry : insertValues.valueSet()) {
      if(i == insertValues.size())
        sql += entry.getKey() + ")";
      else
        sql += entry.getKey() + ", ";

      i++;
    }

    i = 1;
    sql += " VALUES(";
    for(Map.Entry<String, Object> entry : insertValues.valueSet()) {
      if(i == insertValues.size())
        sql += entry.getValue() + ")";
      else
        sql += entry.getValue() + ", ";

      i++;
    }

    sql += ";";

    Log.w(TAG, sql);
    exec(sql);
  }

  protected void displayException(java.lang.Exception e) {
    Log.e(TAG, "displayException(): " + e.toString());
  }

  public void close() {
    try {
      database.close();
    } catch (Exception e) {
      displayException(e);
    }
  }

}
