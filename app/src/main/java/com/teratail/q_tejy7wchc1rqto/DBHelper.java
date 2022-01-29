package com.teratail.q_tejy7wchc1rqto;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.provider.BaseColumns;

import java.util.*;

public class DBHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "todo.db";
  static final String TAB_TABLE = "tab";
  static final String TAB_TITLE = "title";
  static final String TAB_ORDER = "'order'";

  static final String USER_TABLE = "user";
  static final String TAB_ID = "tabid";
  static final String TITLE = "title";
  static final String DATE = "date";
  static final String TIME = "time";
  static final String TODO1 = "todo1";
  static final String TODO2 = "todo2";
  static final String TODO3 = "todo3";
  static final String TODO4 = "todo4";
  static final String TODO5 = "todo5";
  static private String[] TODOCOLUMNS = new String[]{TODO1,TODO2,TODO3,TODO4,TODO5};

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, 2);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    StringBuilder sb = new StringBuilder("CREATE TABLE ").append(USER_TABLE).append(" ");
    sb.append("(").append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
    sb.append(",").append(TAB_ID).append(" INTEGER NOT NULL");
    sb.append(",").append(TITLE).append(" TEXT NOT NULL");
    sb.append(",").append(DATE).append(" TEXT NOT NULL");
    sb.append(",").append(TIME).append(" TEXT NOT NULL");
    sb.append(",").append(TODO1).append(" TEXT");
    sb.append(",").append(TODO2).append(" TEXT");
    sb.append(",").append(TODO3).append(" TEXT");
    sb.append(",").append(TODO4).append(" TEXT");
    sb.append(",").append(TODO5).append(" TEXT");
    sb.append(")");
    db.execSQL(sb.toString());

    sb = new StringBuilder("CREATE TABLE ").append(TAB_TABLE).append(" ");
    sb.append("(").append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");
    sb.append(",").append(TAB_TITLE).append(" TEXT NOT NULL");
    sb.append(",").append(TAB_ORDER).append(" INTEGER");
    sb.append(")");
    db.execSQL(sb.toString());
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
    db.execSQL("DROP TABLE IF EXISTS " + TAB_TABLE);
    onCreate(db);
  }

  public void deleteAll() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(USER_TABLE, null, null);
    db.delete(TAB_TABLE, null, null);
  }

  public long insert(TodoTab tab) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(TAB_TITLE, tab.title);
    contentValues.put(TAB_ORDER, tab.order);
    long _id = db.insert(TAB_TABLE, null, contentValues);
    tab._id = _id;
    return _id;
  }

  public List<TodoTab> getAllTabs() {
    List<TodoTab> list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    String[] columns = new String[]{BaseColumns._ID,TAB_TITLE,TAB_ORDER};
    String orderBy = TAB_ORDER;
    try(Cursor cursor = db.query(TAB_TABLE, columns, null, null, null, null, orderBy);) {
      int idIndex = cursor.getColumnIndex(BaseColumns._ID);
      int titleIndex = cursor.getColumnIndex(TAB_TITLE);
      int orderIndex = cursor.getColumnIndex(TAB_ORDER);
      while(cursor.moveToNext()) {
        long _id = cursor.getLong(idIndex);
        String title = cursor.getString(titleIndex);
        int order = cursor.getInt(orderIndex);
        list.add(new TodoTab(_id, title, order));
      }
    }
    return list;
  }

  //データの追加処理
  public long insert(User user) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(TAB_ID, user.tabId);
    contentValues.put(TITLE, user.title);
    contentValues.put(DATE, user.date);
    contentValues.put(TIME, user.time);
    for(User.Todo todo : user.todoList) {
      contentValues.put(TODOCOLUMNS[todo.num-1], todo.text);
    }
    long _id = db.insert(USER_TABLE, null, contentValues);
    user._id = _id;
    return _id;
  }

  public List<User> getUsers(long tabId) {
    List<User> list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    String[] columns = new String[]{BaseColumns._ID,TITLE,DATE,TIME,TODO1,TODO2,TODO3,TODO4,TODO5};
    String selection = TAB_ID + "=?";
    String[] selectionArgs = new String[]{ ""+tabId };
    try(Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);) {
      int idIndex = cursor.getColumnIndex(BaseColumns._ID);
      int titleIndex = cursor.getColumnIndex(TITLE);
      int dateIndex = cursor.getColumnIndex(DATE);
      int timeIndex = cursor.getColumnIndex(TIME);
      int todo1Index = cursor.getColumnIndex(TODO1);
      int todo2Index = cursor.getColumnIndex(TODO2);
      int todo3Index = cursor.getColumnIndex(TODO3);
      int todo4Index = cursor.getColumnIndex(TODO4);
      int todo5Index = cursor.getColumnIndex(TODO5);
      while(cursor.moveToNext()) {
        long _id = cursor.getLong(idIndex);
        String title = cursor.getString(titleIndex);
        String date = cursor.getString(dateIndex);
        String time = cursor.getString(timeIndex);
        String todo1 = cursor.isNull(todo1Index) ? null : cursor.getString(todo1Index);
        String todo2 = cursor.isNull(todo2Index) ? null : cursor.getString(todo2Index);
        String todo3 = cursor.isNull(todo3Index) ? null : cursor.getString(todo3Index);
        String todo4 = cursor.isNull(todo4Index) ? null : cursor.getString(todo4Index);
        String todo5 = cursor.isNull(todo5Index) ? null : cursor.getString(todo5Index);
        list.add(new User(_id, tabId, title, date, time, todo1, todo2, todo3, todo4, todo5));
      }
    }
    return list;
  }
}

