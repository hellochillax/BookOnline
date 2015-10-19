package org.build.buybook.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.build.buybook.R;
import org.build.buybook.adapter.BaseAdapter;
import org.build.buybook.adapter.ViewHolder;
import org.build.buybook.config.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/10/18.
 */
public class NavigationView extends LinearLayout{

    @Bind(R.id.menu_list)
    ListView mMenuLv;
    private Context mContext;

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        addView(LayoutInflater.from(mContext).inflate(R.layout.left_menu, null));
        ButterKnife.bind(this);
        mMenuLv.setAdapter(new MenuAdapter());
    }

    private int[] menuImages = new int[]{R.mipmap.menu_list_selected_book, R.mipmap.menu_list_logout};
    private String[] menuTexts = new String[]{"已选图书", "注销账号"};

    public ListView getmMenuLv(){
        return mMenuLv;
    }

    public class MenuAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Constant.MENU_LIST_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = ViewHolder.get(mContext, convertView, R.layout.left_menu_item, position, parent);
            holder.setText(R.id.text, menuTexts[position]).setImageResource(R.id.image, menuImages[position]);
            return holder.getConvertView();
        }
    }
}
