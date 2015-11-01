package org.build.buybook.cusview;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.build.buybook.R;
import org.build.buybook.adapter.BaseAdapter;
import org.build.buybook.adapter.ViewHolder;
import org.build.buybook.avtivity.MainActivity;
import org.build.buybook.utils.CacheUtils;
import org.build.buybook.utils.Statesutil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/18.
 */
public class NavigationView extends LinearLayout {

    @Bind(R.id.menu_list)
    ListView mMenuLv;
    private Context mContext;
    AlertDialog dialog;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.user_class)
    TextView userClass;
    @Bind(R.id.user_class2)
    TextView userClass2;
    @Bind(R.id.user_class3)
    TextView userClass3;
    @Bind(R.id.user_logo)
    ImageView userLogo;
    @OnClick(R.id.exit)
    public void exit() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mContext).setTitle("提醒").setMessage("确定要退出应用?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) mContext).finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create();
        }
        dialog.show();
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        addView(LayoutInflater.from(mContext).inflate(R.layout.left_menu, null));
        ButterKnife.bind(this);
        userName.setText(Statesutil.getUserNameChinese(context));
        userClass.setText(Statesutil.getUserDepart(context));
        userClass2.setText(Statesutil.getUserGrade(context));
        userClass3.setText(Statesutil.getUserMajor(context));
        userLogo.setImageResource(Statesutil.getUserGender(context).contains("男")?R.mipmap.boy_1:R.mipmap.girl_1);
//        if (new CacheUtils(context, CacheUtils.CacheType.FOR_ACCOUNT).getCache(CacheUtils.USER_TYPE).equals(CacheUtils.USER_TYPE_2)) {
//            menuTexts = new String[]{"已选图书", "注销账号", "数据导出"};
//        } else {
//            menuTexts = new String[]{"已选图书", "注销账号"};
//        }
        menuTexts = new String[]{"已选图书", "注销账号"};
        mMenuLv.setAdapter(new MenuAdapter());
    }

    private int[] menuImages = new int[]{R.mipmap.menu_list_selected_book, R.mipmap.menu_list_logout, R.mipmap.menu_list_selected_book};
    private String[] menuTexts;

    public ListView getmMenuLv() {
        return mMenuLv;
    }

    public class MenuAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return menuTexts.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.left_menu_item, position, parent);
            holder.setText(R.id.text, menuTexts[position]).setImageResource(R.id.image, menuImages[position]);
            return holder.getConvertView();
        }
    }

}
