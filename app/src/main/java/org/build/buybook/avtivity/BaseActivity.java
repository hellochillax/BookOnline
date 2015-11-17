package org.build.buybook.avtivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.build.buybook.R;

/**
 * Created by Administrator on 2015/10/17.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        if (getClass() == MainActivity.class) {
            if (System.currentTimeMillis() - lastTime > 2000) {
                lastTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_clam, R.anim.slide_out_right);
        }
    }

    public void openActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
    }
    public void openActivityForResult(Intent intent,int reqCode) {
        startActivityForResult(intent, reqCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_clam);
    }
    public Dialog showLoadingDialog(String msg){
        if(loadingDialog==null){
            loadingDialog=createLoadingDialog(msg);
        }
        loadingDialog.show();
        return loadingDialog;
    }
    public void dismissLoadingDialog(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog=null;
        }
    }
    public Dialog createLoadingDialog(String msg){
        View view= LayoutInflater.from(this).inflate(R.layout.login_dialog_layout,null);
        ((TextView)view.findViewById(R.id.msgTv)).setText(msg);
        Dialog dialog=new Dialog(this,R.style.cus_dialog_style);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
    private Dialog loadingDialog;
}
