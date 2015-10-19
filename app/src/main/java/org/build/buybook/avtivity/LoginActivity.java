package org.build.buybook.avtivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.build.buybook.R;
import org.build.buybook.config.Constant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MAC on 15/10/20.
 */
public class LoginActivity extends BaseActivity {

    String nameStr,pwdStr;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_pwd)
    TextView userPwd;
    @Bind(R.id.user_validate)
    ImageView userValidate;
    @OnClick(R.id.user_login)
    public void to_login(){
        nameStr=userName.getText().toString();
        pwdStr=userPwd.getText().toString();
        if(TextUtils.isEmpty(nameStr)|TextUtils.isEmpty(pwdStr)){
            showToast("账号密码不能为空");
            return;
        }
        new LoginPagerTask().execute();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    private class LoginPagerTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            StringBuffer sb=new StringBuffer();
            try {
                HttpURLConnection conn= (HttpURLConnection) new URL(Constant.URL_LOGIN_PAGE).openConnection();
//                String cookie = new HttpCookie("JSESSIONID",nameStr).toString();
//                conn.setRequestProperty("Cookie", cookie);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"gbk"));
                String line;
                while ((line=br.readLine())!=null){
                    sb.append(line);
                }
                System.out.println(sb.toString());
                System.out.println("over");
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            return sb;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

}