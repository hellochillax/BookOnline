package org.build.buybook.avtivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.build.buybook.R;
import org.build.buybook.config.Constant;
import org.build.buybook.db.BookListDao;
import org.build.buybook.model.Book;
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
    @Bind(R.id.scrollView)
    ScrollView scrollView;
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
            showLoadingDialog("登录中,请稍候...");
            new LoginTask().execute();
        }

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dismissLoadingDialog();
            switch (msg.what){
                case 1:
                    showToast("您输入的验证码不正确");
                    new LoginPagerTask().execute();
                    break;
                case 2:
                    showToast("账号或密码错误");
                    new LoginPagerTask().execute();
                    break;
                case 3:
                    showToast("网络错误");
                    new LoginPagerTask().execute();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.left_list_text_color);//通知栏所需颜色
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userValidate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            userValidate.setOnFocusChangeListener(null);
                            userValidate.requestFocus();
                        }
                    });
                }
            }
        });

        if(utils==null){
            utils=new CacheUtils(LoginActivity.this, CacheUtils.CacheType.FOR_ACCOUNT);
        }
        userName.setText(utils.getCache(CacheUtils.USER_NAME));
        userPwd.setText(utils.getCache(CacheUtils.USER_PWD));
        new LoginPagerTask().execute();
    }

    private class LoginTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String ck = new HttpCookie("JSESSIONID", cookieStr.substring(1 + cookieStr.lastIndexOf("=")).toString()).toString().replaceAll("\"", "");
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
                writer.write(post);
                writer.flush();
                writer.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gbk"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.toString().contains("综合教务管理系统")) {
                    LogUtils.e("登陆成功");
                    if(utils==null){
                        utils=new CacheUtils(LoginActivity.this, CacheUtils.CacheType.FOR_ACCOUNT);
                    }
                    utils.setCache(CacheUtils.USER_NAME,nameStr);
                    utils.setCache(CacheUtils.USER_PWD, pwdStr);
                    try{
                        String userInfo=getNetDataUTF(Constant.URL_GET_USER_MSG, ck);
                        Document doc=Jsoup.parse(userInfo);
                        String userName=doc.getElementsByAttributeValue("name", "realname").first().attr("value");
                        String userDepart=doc.getElementsByAttributeValue("name", "departmentid").first().attr("value");
                        String userGrade=doc.getElementsByAttributeValue("name", "gradeid").first().attr("value");
                        String userMajor=doc.getElementsByAttributeValue("name", "majorid").first().attr("value");
                        String userGender=doc.getElementsByAttributeValue("name","gender").first().attr("value");
                        //处理一下~
                        Pattern pattern=Pattern.compile(userMajor+"([^<]*)");
                        Matcher matcher=pattern.matcher(userInfo);
                        matcher.find();userMajor=matcher.group(1);
                        pattern=Pattern.compile(userDepart + "([^<]*)");
                        matcher=pattern.matcher(userInfo);
                        matcher.find();userDepart=matcher.group(1);
                        pattern=Pattern.compile(userGrade + "([^<]*)");
                        matcher=pattern.matcher(userInfo);
                        matcher.find();userGrade=matcher.group(1);
                        Statesutil.setUserNameChinese(LoginActivity.this, userName);
                        Statesutil.setUserMajor(LoginActivity.this, userMajor.replaceFirst("\">", ""));
                        Statesutil.setUserDepart(LoginActivity.this, userDepart.replaceFirst("\">", ""));
                        Statesutil.setUserGrade(LoginActivity.this, userGrade.replaceFirst("\">", ""));
                        Statesutil.setUserGender(LoginActivity.this,userGender);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        handler.obtainMessage(3).sendToTarget();
                    }
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
//                BookListDao dao=new BookListDao(LoginActivity.this);
                for (Element item : eles) {
                    if (i++ % 2 == 0) {
                        bookList.setCache(i/2+"",item.text());
//                        dao.insert(new Book(null, item.text(), null, null, null, null, null));
//                        App.courseList.add(new Book(null,item.text(),null,null,null,null,null));
                    }
                }
                showToast("学生登陆成功");
            } else {
                showToast("老师登陆成功");
                utils.setCache(CacheUtils.USER_TYPE, CacheUtils.USER_TYPE_2);
            }
            Statesutil.setLogin(LoginActivity.this, true);
            openActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
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
    private String getNetDataUTF(String url, String cookie) {

        try {
            StringBuffer sb = new StringBuffer();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            if (!TextUtils.isEmpty(cookie)) {
                conn.setRequestProperty("Cookie", cookie);
            }
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
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
//    /**
//     * LoginDialog
//     */
//    public Dialog create(Context context,String msg){
//        View view= LayoutInflater.from(context).inflate(R.layout.login_dialog_layout,null);
//        ((TextView)view.findViewById(R.id.msgTv)).setText(TextUtils.isEmpty(msg)?"":msg);
//        Dialog dialog=new Dialog(context,R.style.cus_dialog_style);
//        dialog.setContentView(view);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        return dialog;
//    }
}