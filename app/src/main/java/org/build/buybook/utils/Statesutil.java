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
    public static String getUserNameChinese(Context context){
        return context.getSharedPreferences("Login",0).getString("name_chinese", null);
    }
    public static void setUserNameChinese(Context context,String userName){
        context.getSharedPreferences("Login",0).edit().putString("name_chinese", userName).commit();
    }
    public static String getUserMajor(Context context){
        return context.getSharedPreferences("Login",0).getString("major",null);
    }
    public static void setUserMajor(Context context,String major){
        context.getSharedPreferences("Login",0).edit().putString("major",major).commit();
    }
    public static void clear(Context context){
        context.getSharedPreferences("Login",0).edit().clear().commit();
    }
    public static String getUserDepart(Context context){
        return context.getSharedPreferences("Login",0).getString("depart", null);
    }
    public static void setUserDepart(Context context,String userName){
        context.getSharedPreferences("Login",0).edit().putString("depart", userName).commit();
    }
    public static String getUserGrade(Context context){
        return context.getSharedPreferences("Login",0).getString("grade", null);
    }
    public static void setUserGrade(Context context,String userName){
        context.getSharedPreferences("Login",0).edit().putString("grade", userName).commit();
    }
    public static String getUserGender(Context context){
        return context.getSharedPreferences("Login",0).getString("gender", null);
    }
    public static void setUserGender(Context context,String userName){
        context.getSharedPreferences("Login",0).edit().putString("gender", userName).commit();
    }
}
