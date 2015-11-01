package org.build.buybook.utils;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by MAC on 15/10/24.
 */
public class CusIntentService extends IntentService {

    public static final String NET_TASK="intent_service_task";
    public static final int ERROR_TASK=-1;
    public static final int TASK_GET_BOOK_LIST=1;
    public static final int TASK_ORDER_BOOK=2;
    public static final int TASK_GET_ORDERED_LIST=3;

    public CusIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
