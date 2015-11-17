package org.build.buybook.avtivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.yalantis.taurus.PullToRefreshView;

import org.build.buybook.R;
import org.build.buybook.adapter.BaseAdapter;
import org.build.buybook.adapter.ViewHolder;
import org.build.buybook.cusview.NavigationView;
import org.build.buybook.db.BookListDao;
import org.build.buybook.model.Book;
import org.build.buybook.utils.BookGetUtils;
import org.build.buybook.utils.CacheUtils;
import org.build.buybook.utils.NetworkChecker;
import org.build.buybook.utils.Statesutil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.id_nv_menu)
    NavigationView mNvMenu;
    @Bind(R.id.id_drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.bar_logo)
    ImageView mLogo;
    @Bind(R.id.bar_buy)
    TextView mBuy;
    @Bind(R.id.list_view)
    PullToRefreshView mPtrv;
    @Bind(R.id.in_list_view)
    ListView listView;
    @Bind(R.id.title)
    TextView title;
    CacheUtils accountUtils;
    BookListAdapter adapter;
    boolean list_clickable=true;
    @OnClick(R.id.bar_logo)
    public void bar_logo() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    AlertDialog dialog;
    AlertDialog exitDialog;

    public void buy(final Book book, final int position) {
        dialog = new AlertDialog.Builder(this).setTitle(book.ordered == null ? "确定要预定吗?" : "确定要取消预定吗?").setMessage("书名:" + book.book_name)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingDialog("操作中,请稍候...");
                        BookGetUtils.orderOneBook(MainActivity.this, new CacheUtils(MainActivity.this, CacheUtils.CacheType.FOR_ACCOUNT).getCache(CacheUtils.USER_NAME), book.book_id, book.ordered == null ? "1" : "0", position, handler);
                    }

                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissLoadingDialog();
            switch (msg.what) {
                case BookGetUtils.NET_ERROR:
                    showToast("网络错误");
                    break;
                case BookGetUtils.GET_SUCCESS:
                    Pattern pattern = Pattern.compile("\\[.*?\\]");
                    Matcher matcher = pattern.matcher(msg.obj.toString());
                    LogUtils.d("获取图书列表:" + msg.obj.toString());
                    if (matcher.find()) {
                        App.courseList.clear();
                        Gson gson = new Gson();
                        Book[] books = gson.fromJson(matcher.group(), Book[].class);
                        for (Book book : books) {
                            App.courseList.add(book);
                            LogUtils.d("图书列表数据:" + book.toString());
                        }
                        showToast("获取数据成功");
                        mPtrv.setRefreshing(false);
                    }
                    break;
                case BookGetUtils.ORDER_BOOK_OK:
                    App.courseList.get(msg.arg1).ordered = App.courseList.get(msg.arg1).ordered == null ? "1" : null;
                    showToast("购买成功");
                    break;
                case BookGetUtils.UN_ORDER_BOOK_OK:
                    App.courseList.get(msg.arg1).ordered = App.courseList.get(msg.arg1).ordered == null ? "1" : null;
                    showToast("取消成功");
                    break;
                case BookGetUtils.ORDER_BOOK_FAILE:
                    showToast("操作失败,请重试");
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        accountUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_ACCOUNT);
        mNvMenu.getmMenuLv().setOnItemClickListener(this);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mLogo.setImageResource(R.mipmap.back);
                list_clickable=false;
                title.setText("个人中心");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mLogo.setImageResource(R.mipmap.open_left_menu);
                list_clickable=true;
                title.setText("掌上选书");
            }

        });
        adapter = new BookListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(list_clickable){
                    buy(App.courseList.get(position), position);
                }
            }
        });
        mPtrv.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkChecker.IsNetworkAvailable(MainActivity.this)) {
                    refrushBookList();
                } else showToast("网络错误");
            }
        });
        if (App.courseList.size() == 0) {
            if (NetworkChecker.IsNetworkAvailable(MainActivity.this)) {
                refrushBookList();
            } else showToast("网络错误");
        }
    }

    private void refrushBookList() {
        showLoadingDialog("刷新数据中,请稍候...");
        CacheUtils cacheUtils = new CacheUtils(this, CacheUtils.CacheType.FOR_COURSE_LIST);
        List<String> list = new ArrayList<>();
        for (int i = 0; ; i++) {
            if (cacheUtils.getCache(i + "") != null) {
                list.add(cacheUtils.getCache(i + ""));
            } else {
                break;
            }
        }
        BookGetUtils.getMyBookList(this, accountUtils.getCache(CacheUtils.USER_NAME), new Gson().toJson(list), handler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawerLayout.closeDrawers();
        switch (position) {
            case 0:
                openActivityForResult(new Intent(this, SelectedBook.class), 0x250);
                break;
            case 1:
                if (exitDialog == null) {
                    exitDialog = new AlertDialog.Builder(this).setTitle("提醒").setMessage("确定要注销应用?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    logout();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();
                }
                exitDialog.show();
                break;
            case 2:
                showToast("数据导出");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refrushBookList();
    }

    private void logout() {
        Statesutil.clear(this);
        new CacheUtils(this, CacheUtils.CacheType.FOR_ACCOUNT).clear();
        new CacheUtils(this, CacheUtils.CacheType.FOR_COURSE_LIST).clear();
        new BookListDao(this).clear();
        showToast("注销成功");
        openActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public class BookListAdapter extends BaseAdapter {

        String []colors=new String[]{"#a6ede7","#bbf0d2","#f75f6b","#eccdb0"};
        @Override
        public int getCount() {
            return App.courseList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder = ViewHolder.get(MainActivity.this, convertView, R.layout.book_list_item, position, parent);
            Book item = App.courseList.get(position);
            holder.setText(R.id.book_name, item.book_name)
                    .setText(R.id.book_course, "相关课程:" + item.course_name)
                    .setText(R.id.book_price, "¥"+item.book_price)
                    .setText(R.id.book_publish, "出版社:"+item.pub_house)
                    .setText(R.id.book_order, item.ordered == null ? "未预定" : "已预定")
                    .setColor(R.id.left_color, Color.parseColor(colors[(int)(Math.random()*4)]))
                    .setBackgroundResource(R.id.book_order, item.ordered == null ? R.mipmap.not_order : R.mipmap.is_order);
            holder.getConvertView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            holder.getView(R.id.content).setBackgroundResource(R.drawable.list_item_selected_bg);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            return true;
                        case MotionEvent.ACTION_CANCEL:
                            holder.getView(R.id.content).setBackgroundResource(R.drawable.list_item_normal_bg);
                            return true;
                        case MotionEvent.ACTION_UP:
                            holder.getView(R.id.content).setBackgroundResource(R.drawable.list_item_normal_bg);
                            if(list_clickable){
                                buy(App.courseList.get(listView.getPositionForView(v)), listView.getPositionForView(v));
                            }
                            return true;
                    }
                    return false;
                }
            });
            return holder.getConvertView();
        }
    }

}
