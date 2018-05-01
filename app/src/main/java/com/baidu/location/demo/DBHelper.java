package com.baidu.location.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

//    private String createSQL = "create table myposi("
////            + "id integer primary key  autoincrement not null ,"
//            + "number integer,"
//            + "linename varchar(20) not null,"
////            + "latitude real not null ,"
////            + "longitude real not null,"
////            + "remark varchar(50),"
////            + "grade integer not null,"
//            + "type varchar(20) not null)";

    /**
     *
     * @param context
     * @param name 数据库名字
     * @param factory 数据库进行查询的时候会返回一个cursor，这个cursor就是在上面的factory中产生的。
    如果有需求，可以自定义factory，这样返回的cursor就会符合自己的需求！
     * @param version 数据库版本号
     */
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table fav_table(" +
                "id varchar(80) primary key," +
                "name varchar(50)," +
                "number int," +
                "type varchar(10)," +
                "grade int," +
                "latitude long," +
                "longitude long," +
                "remark varchar(50))";
        db.execSQL(sql);
    }

    /**
     * 该方法会在数据库需要升级的时候调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE stu_table ADD COLUMN other TEXT");
    }
}
