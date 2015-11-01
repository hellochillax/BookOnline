package org.build.buybook.utils;

import android.content.Context;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.build.buybook.config.Constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MAC on 15/10/24.
 */
public class BookGetUtils {

    public static final int NET_ERROR = -1;
    public static final int GET_SUCCESS = 0;
    public static final int ORDER_BOOK_OK = 1;
    public static final int UN_ORDER_BOOK_OK = 5;
    public static final int ORDER_BOOK_FAILE = 2;
    public static final int GET_ORDER_LIST_OK = 3;
    public static final int GET_ORDER_LIST_FAIL = 4;

    /**
     * 获取该账号对应的所有图书信息
     *
     * @param context     上下文
     * @param accountName 当前登录的用户名
     * @param courseList  该账户当前的课程列表(json)
     * @param handler     你懂得....
     */
    public static void getMyBookList(Context context, final String accountName, final String courseList, final Handler handler) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.URL_GET_BOOKS_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handler.obtainMessage(GET_SUCCESS, response).sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.obtainMessage(NET_ERROR).sendToTarget();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("username", URLEncoder.encode(accountName, "utf-8"));
                    params.put("course", URLEncoder.encode(courseList, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * 订阅一本书
     */
    public static void orderOneBook(Context context, final String accountName, final String book_id, final String order, final int position,final Handler handler) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.URL_GET_ORDER_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handler.obtainMessage(order == "0" ? UN_ORDER_BOOK_OK : ORDER_BOOK_OK,position,position, response).sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.obtainMessage(ORDER_BOOK_FAILE).sendToTarget();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", accountName);
                params.put("book_id", book_id);
                params.put("ordered", order);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    /**
     * 获取已定图书列表
     */
    public static void getOrderList(Context context, final String accountName, final Handler handler) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.URL_GET_ORDERED_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handler.obtainMessage(GET_ORDER_LIST_OK, response).sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.obtainMessage(GET_ORDER_LIST_FAIL).sendToTarget();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("username", URLEncoder.encode(accountName, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }
}