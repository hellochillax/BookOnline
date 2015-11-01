package org.build.buybook.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.build.buybook.model.Book;

import java.util.List;

/**
 * Created by MAC on 15/10/24.
 */
public class BookListDao {

    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;

    private static final String sql_insert="insert into "+"BookListTable"+"(book_name,pub_house,book_price,course_name) values(?,?,?,?)";
//    private static final String sql_drop=null;
//    private static final String sql_change=null;
//    private static final String sql_quary=null;



    public BookListDao(Context context){
        helper=new BookListBD(context);
    }

    public void insert(Book book){
        db=helper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL(sql_insert,new String[]{book.book_name,book.pub_house,book.book_price,book.course_name});
            db.close();
        }
    }

    public void addAllDataToList(List<Book> courseList) {
        db=helper.getReadableDatabase();
        if(db.isOpen()){
            Cursor c=db.rawQuery("select * from " + "BookListTable" + "", null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                for (int i = 0; !c.isAfterLast(); i++, c.moveToNext()) {
                    courseList.add(new Book(c.getInt(0) + "", c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
                }
            }
        }
    }

    public void clear() {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from BookListTable");
            db.close();
        }
    }
}
