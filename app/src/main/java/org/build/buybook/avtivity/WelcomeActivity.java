package org.build.buybook.avtivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.build.buybook.R;
import org.build.buybook.model.Book;
import org.build.buybook.utils.CacheUtils;
import org.build.buybook.utils.Statesutil;

/**
 * Created by MAC on 15/10/20.
 */
public class WelcomeActivity extends BaseActivity{
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(Statesutil.isLogined(WelcomeActivity.this)){
                openActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }else {
                openActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
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
                    Thread.sleep(2000);
                    String item;
                    CacheUtils utils=new CacheUtils(WelcomeActivity.this, CacheUtils.CacheType.FOR_COURSE_LIST);
                    for (int i=0;;i++){
                        item=utils.getCache(""+i);
                        if(item!=null){
                            App.courseList.add(new Book(item,null,null,null));
                        }else break;
                    }
                    handler.obtainMessage().sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
