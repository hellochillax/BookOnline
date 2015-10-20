package org.build.buybook.avtivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.build.buybook.R;
import org.build.buybook.config.Constant;
import org.build.buybook.utils.CacheUtils;
import org.build.buybook.utils.Statesutil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MAC on 15/10/20.
 */
public class LoginActivity extends BaseActivity {

    String nameStr, pwdStr, validateStr;
    String cookieStr = ";";
    CacheUtils utils;
    @Bind(R.id.user_name)
    EditText userName;
    @Bind(R.id.user_pwd)
    EditText userPwd;
    @Bind(R.id.user_validate)
    EditText userValidate;
    @Bind(R.id.user_validate_image)
    ImageView userValidateImage;
    @OnClick(R.id.user_validate_image)
    public void to_change(){
        new LoginPagerTask().execute();
    }
    @OnClick(R.id.user_login)
    public void to_login() {
        utils=new CacheUtils(this, CacheUtils.CacheType.FOR_ACCOUNT);
        nameStr = userName.getText().toString();
        pwdStr = userPwd.getText().toString();
        validateStr = userValidate.getText().toString();
        if (TextUtils.isEmpty(nameStr) | TextUtils.isEmpty(pwdStr)) {
            showToast("账号密码不能为空");
        } else if (TextUtils.isEmpty(validateStr)) {
            showToast("验证码不能为空");
        } else {
            new LoginTask().execute();
        }

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showToast("您输入的验证码不正确");
                    break;
                case 2:
                    showToast("账号或密码错误");
                    break;
                case 3:
                    showToast("网络错误");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        new LoginPagerTask().execute();
    }

    private class LoginTask extends AsyncTask {
        /**
         * j_username:1511640120
         * j_password:1511640120
         * j_captcha:55734
         *
         * @param params
         * @return
         */
        @Override
        protected Object doInBackground(Object[] params) {
            String ck = new HttpCookie("JSESSIONID", cookieStr.substring(1 + cookieStr.lastIndexOf("=")).toString()).toString().replaceAll("\"", "");
//            getNetData(Constant.URL_LOGIN_CHECK + "?j_username=" + nameStr + "&j_password=" + pwdStr + "&j_captcha=" + validateStr, new HttpCookie("JSESSIONID", ck).toString());
            try {
                StringBuffer sb = new StringBuffer();
                HttpURLConnection conn = (HttpURLConnection) new URL(Constant.URL_LOGIN_CHECK).openConnection();
                conn.setRequestProperty("Cookie", ck);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "gbk"));
                String post = "j_username=" + URLEncoder.encode(nameStr, "gbk") + "&j_password=" + URLEncoder.encode(pwdStr, "gbk") + "&j_captcha=" + URLEncoder.encode(validateStr, "gbk");
//                String post="j_username="+ "1511640120"+"&j_password="+"1511640120"+"&j_captcha="+URLEncoder.encode(validateStr,"gbk");
                writer.write(post);
                writer.flush();
                writer.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println(sb.toString());
                if (sb.toString().contains("综合教务管理系统")) {
                    LogUtils.e("登陆成功");
                    if(utils==null){
                        utils=new CacheUtils(LoginActivity.this, CacheUtils.CacheType.FOR_ACCOUNT);
                    }
                    utils.setCache(CacheUtils.USER_NAME,nameStr);
                    utils.setCache(CacheUtils.USER_PWD,pwdStr);
                    return getNetData(Constant.URL_GET_COURSE_LIST, ck);
                }else if (sb.toString().contains("您输入的验证码不正确")){

                    handler.obtainMessage(1).sendToTarget();
                    return null;
                }else if(sb.toString().contains("不存在")){

                    handler.obtainMessage(2).sendToTarget();
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace(System.out);
                handler.obtainMessage(3).sendToTarget();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o==null)return;
            if (o.toString().contains("本学期课程安排")) {
                utils.setCache(CacheUtils.USER_TYPE,CacheUtils.USER_TYPE_1);
                Document doc = Jsoup.parse(o.toString());
                Elements eles = doc.getElementsByClass("infolist");
                int i = 0;
                CacheUtils bookList=new CacheUtils(LoginActivity.this, CacheUtils.CacheType.FOR_COURSE_LIST);
                for (Element item : eles) {
                    if (i++ % 2 == 0) {
                        bookList.setCache(i/2+"",item.text());
                    }
                }
                showToast("学生登陆成功");
            } else {
                showToast("老师登陆成功");
                utils.setCache(CacheUtils.USER_TYPE, CacheUtils.USER_TYPE_2);
            }
            Statesutil.setLogin(LoginActivity.this, true);
            openActivity(new Intent(LoginActivity.this, MainActivity.class));
            super.onPostExecute(o);
        }
    }

    private class LoginPagerTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String data = getNetData(Constant.URL_LOGIN_PAGE, null);
            Pattern pattern = Pattern.compile("jsessionid=[^\"]*");
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                cookieStr = ";" + matcher.group();
                break;
            }
            LogUtils.d(Constant.URL_LOGIN_VALIDATE + cookieStr);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            userValidateImage.setImageResource(R.mipmap.ic_launcher);
            ImageLoader.getInstance().displayImage(Constant.URL_LOGIN_VALIDATE + cookieStr, userValidateImage);
        }
    }

    private String getNetData(String url, String cookie) {

        try {
            StringBuffer sb = new StringBuffer();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            if (!TextUtils.isEmpty(cookie)) {
                conn.setRequestProperty("Cookie", cookie);
            }
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            handler.obtainMessage(3).sendToTarget();
        }

        return null;
    }

}