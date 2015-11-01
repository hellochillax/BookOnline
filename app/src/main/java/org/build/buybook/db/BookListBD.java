package org.build.buybook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MAC on 15/10/24.
 */
public class BookListBD extends SQLiteOpenHelper{

    private static final String db_name="BookList";
    private static final String create_table="create table BookListTable(book_id integer primary key,book_name,author text,pub_house text,course_name text,version text,book_price varchar(10))";

    public BookListBD(Context context){
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
