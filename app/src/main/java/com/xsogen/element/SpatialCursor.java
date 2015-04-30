package com.xsogen.element;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;

import jsqlite.Stmt;

import java.util.LinkedList;
import java.util.List;

public class SpatialCursor implements Cursor {

  private static final String TAG = "SpatialCursor";

  private static final int FIRST_ROW_INDEX    = 0;
  private static final int FIRST_ROW_POSITION = -1;

  private Stmt statement;
  private List<DataSetObserver> dataSetObservers;

  private boolean is_open = false;
  private int row_count = 0;
  private int row_position = FIRST_ROW_POSITION;

  private Uri notify_uri;

  protected SpatialCursor(DatabaseHelper dbHelper, String sql) {
    dataSetObservers = new LinkedList<DataSetObserver>();

    try {

      statement = dbHelper.getDatabase().prepare(sql);
      is_open = true;
      countRows();

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  private void reset() throws jsqlite.Exception {
    statement.reset();
    row_position = FIRST_ROW_POSITION;
  }

  private void countRows() throws jsqlite.Exception {
    row_count = 0;
    while (statement.step())
      row_count++;

    reset();
  }

  @Override
    public int getCount() {
    return row_count;
  }

  @Override
  public int getPosition() {
    return row_position;
  }

  @Override
  public boolean move(int i) {
    return moveToPosition(row_position + i);
  }

  @Override
  public boolean moveToPosition(int i) {
    if (i < FIRST_ROW_POSITION)
      throw new CursorIndexOutOfBoundsException("New index would be out of bounds!");

    try {

      if (row_position > i)  {
        reset();
        while (row_position < i && moveToNext()) { }
      }
      else if (row_position < i)
        while (row_position < i && moveToNext()) { }

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }

    return (row_position == i);
  }

  @Override
  public boolean moveToFirst() {
    return moveToPosition(FIRST_ROW_INDEX);
  }

  @Override
  public boolean moveToLast() {
    return moveToPosition(row_count - 1);
  }

  @Override
  public boolean moveToNext() {
    try {

      if (statement.step()) {
        row_position++;
        return true;
      }

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }

    return false;
  }

  @Override
  public boolean moveToPrevious() {
    return moveToPosition(row_position - 1);
  }

  @Override
  public boolean isFirst() {
    return (row_position == FIRST_ROW_INDEX);
  }

  @Override
  public boolean isLast() {
    return (row_position == (row_count - 1));
  }

  @Override
  public boolean isBeforeFirst() {
    return (row_position < FIRST_ROW_INDEX);
  }

  @Override
  public boolean isAfterLast() {
    return (row_position > (row_count - 1));
  }

  @Override
  public int getColumnIndex(String s) {
    try {
      for (int i = 0; i < statement.column_count(); i++) {
        if (statement.column_name(i).equals(s))
          return i;
      }

      return -1;
    } catch (jsqlite.Exception e) {
      return -1;
    }
  }

  @Override
  public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
    try {
      for (int i = 0; i < statement.column_count(); i++) {
        if (statement.column_name(i).equals(s))
          return i;
      }

      throw new IllegalArgumentException("Column " + s + " does not exist in cursor.");
    } catch (jsqlite.Exception e) {
      throw new IllegalArgumentException(e.toString());
    }
  }

  @Override
  public String getColumnName(int i) {
    try {
      return statement.column_name(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public String[] getColumnNames() {
    String[] columnNames;

    try {

      columnNames = new String[statement.column_count()];
      for (int i = 0; i < columnNames.length; i++)
        columnNames[i] = statement.column_name(i);

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }

    return columnNames;
  }

  @Override
  public int getColumnCount() {
    try {
      return statement.column_count();
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public byte[] getBlob(int i) {
    try {
      return statement.column_bytes(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public String getString(int i) {
    try {
      return statement.column_string(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
    try {

      String columnString = statement.column_string(i);

      if (columnString.length() >= charArrayBuffer.data.length)
        charArrayBuffer.data = columnString.toCharArray();
      else {
        for (int c = 0; c < columnString.length(); c++)
          charArrayBuffer.data[c] = columnString.charAt(c);
      }

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public short getShort(int i) {
    try {
      return (short) statement.column_int(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public int getInt(int i) {
    try {
      return statement.column_int(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public long getLong(int i) {
    try {
      return statement.column_long(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public float getFloat(int i) {
    try {
      return (float) statement.column_double(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public double getDouble(int i) {
    try {
      return statement.column_double(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public int getType(int i) {
    try {
      return statement.column_type(i);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public boolean isNull(int i) {
    try {
      return (statement.column(i) == null);
    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public void deactivate() {
    // Not implemented
  }


  @Override
  public boolean requery() {
    return true; // Not implemented, cursor data set is static.
  }

  @Override
  public void close() {
    try {

      statement.close();
      is_open = false;
      row_position = FIRST_ROW_POSITION;

      for (DataSetObserver observer : dataSetObservers)
        observer.onInvalidated();

    } catch (jsqlite.Exception e) {
      throw new SQLException(e.toString());
    }
  }

  @Override
  public boolean isClosed() {
    return (is_open == false);
  }

  @Override
  public void registerContentObserver(ContentObserver contentObserver) {
    // Not implemented, cursor data set is static.
  }

  @Override
  public void unregisterContentObserver(ContentObserver contentObserver) {
    // Not implemented, cursor data set is static.
  }

  @Override
  public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    dataSetObservers.add(dataSetObserver);
  }

  @Override
  public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    dataSetObservers.add(dataSetObserver);
  }

  @Override
  public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
    // Not implemented, nothing to do when content URI changes.
    notify_uri = uri;
  }

  @Override
  public Uri getNotificationUri() {
    // Not implemented, nothing to do when content URI changes.
    return notify_uri;
  }

  @Override
  public boolean getWantsAllOnMoveCalls() {
    return true;
  }

  @Override
  public Bundle getExtras() {
    return Bundle.EMPTY;
  }

  @Override
  public Bundle respond(Bundle bundle) {
    return Bundle.EMPTY;
  }

}
