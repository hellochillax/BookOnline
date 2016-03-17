package org.build.buybook.avtivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.build.buybook.R;
import org.build.buybook.db.BookListDao;
import org.build.buybook.model.Book;
import org.build.buybook.utils.CacheUtils;
import org.build.buybook.utils.Statesutil;

/**
 * Created by MAC on 15/10/20.
 */
public class WelcomeActivity extends Activity {
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(Statesutil.isLogined(WelcomeActivity.this)){
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    BookListDao dao=new BookListDao(WelcomeActivity.this);
                    dao.addAllDataToList(App.courseList);
                    handler.obtainMessage().sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
