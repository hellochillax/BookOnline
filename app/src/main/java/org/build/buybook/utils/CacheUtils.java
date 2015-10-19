package org.build.buybook.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MAC on 15/10/20.
 */
public class CacheUtils {
    private SharedPreferences mPreference;
    public CacheUtils(Context context) {
        mPreference=context.getSharedPreferences("FOR_ACCOUNT",0);
    }

    /**
     * FOR_ACCOUNT:设置当前登陆账户
     * key :user_name |  user_pwd
     * value:  ..     |    ...
     */
    public enum  CacheType{
        FOR_ACCOUNT,
    }
    public void setCache(String key,String cache){
        mPreference.edit().putString(key,cache);
    }
    public void getCache(String key){
        mPreference.getString(key,null);
    }
    public static final String USER_NAME="user_name";
    public static final String USER_PWD="user_pwd";
}
