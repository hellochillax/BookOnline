package org.build.buybook.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MAC on 15/10/20.
 */
public class CacheUtils {
    private SharedPreferences mPreference;
    public CacheUtils(Context context,CacheType type) {
        switch (type){
            case FOR_ACCOUNT:
                mPreference=context.getSharedPreferences("FOR_ACCOUNT",0);
                break;
            case FOR_COURSE_LIST:
                mPreference=context.getSharedPreferences("FOR_COURSE_LIST",0);

        }
    }

    /**
     * FOR_ACCOUNT:设置当前登陆账户
     * key :user_name |  user_pwd
     * value:  ..     |    ...
     *
     *
     * FOR_COURSE_LIST:设置用户的课程列表
     * key :0-...
     * value:课程名
     *
     *
     */
    public enum  CacheType{
        FOR_ACCOUNT,FOR_COURSE_LIST
    }
    public void setCache(String key,String cache){
        mPreference.edit().putString(key,cache).commit();
    }
    public String getCache(String key){
        return mPreference.getString(key,null);
    }
    public static final String USER_NAME="user_name";
    public static final String USER_PWD="user_pwd";
    public static final String USER_TYPE="user_type";//账号类型
    public static final String USER_TYPE_1="user_type_1";//学生
    public static final String USER_TYPE_2="user_type_2";//老师
}
