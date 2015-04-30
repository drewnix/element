package com.xsogen.element;

import android.content.ContentValues;
import android.database.SQLException;
import android.util.Log;


public class LocationDatabase {

  private static final String TAG = "LocationDatabase";

  private static final String SELECTION_POINT      = "SELECT id, longitude, latitude " +
                                                     "FROM location ";

  private DatabaseHelper dbHelper;

  protected LocationDatabase(DatabaseHelper dbHelper) {
    this.dbHelper = dbHelper;
    if(!isInitialized()) {
      initialize();
      Log.d(TAG, "Initialized the location tables.");
    }
  }

  private void initialize() {
    Log.d(TAG, "in initialize()");

    dbHelper.exec("SELECT InitSpatialMetaData();");
    dbHelper.exec("DROP TABLE IF EXISTS location;");

    dbHelper.exec("CREATE TABLE location (" +
                  "id INTEGER NOT NULL PRIMARY KEY, " +
                  "latitude REAL NOT NULL, " +
                  "longitude REAL NOT NULL " +
                  ");");
  }

  private boolean isInitialized() {
    try {

      SpatialCursor locationsQuery = dbHelper.prepare("SELECT 1 FROM location");
      locationsQuery.close();
      return true;

    } catch (SQLException e) {
      dbHelper.displayException(e);
    }

    return false;
  }

  public LocationRecord addLocation(LocationRecord point) {
    ContentValues values = new ContentValues();
    values.put("latitude", point.getY());
    values.put("longitude", point.getX());
    dbHelper.insert("location", values);

    LocationRecord retPoint = null;
    SpatialCursor locationRecords = dbHelper.prepare(SELECTION_POINT + "ORDER BY id DESC LIMIT 1");
    if(locationRecords.moveToNext())
      retPoint = new LocationRecord(locationRecords.getInt(0), point.getX(), point.getY());
    locationRecords.close();

    return retPoint;
  }

  public LocationRecord getLocation(int location_id) {
    LocationRecord outLocation = null;
    SpatialCursor locationRecords = dbHelper.prepare(SELECTION_POINT + "WHERE id = '" + location_id + "'");

    if(locationRecords.moveToNext())
      outLocation = new LocationRecord(locationRecords.getInt(0),
                                 locationRecords.getDouble(2),
                                 locationRecords.getDouble(3));

    locationRecords.close();
    return outLocation;
  }
}
