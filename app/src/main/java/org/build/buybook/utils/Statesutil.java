package org.build.buybook.utils;

import android.content.Context;

/**
 * Created by MAC on 15/10/20.
 */
public class Statesutil {
    public static boolean isLogined(Context context){
        return context.getSharedPreferences("Login",0).getBoolean("login",false);
    }
    public static void setLogin(Context context,boolean login){
        context.getSharedPreferences("Login",0).edit().putBoolean("login",login).commit();
    }
}
