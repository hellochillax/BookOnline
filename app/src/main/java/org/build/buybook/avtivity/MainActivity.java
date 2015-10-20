package org.build.buybook.avtivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.yalantis.taurus.PullToRefreshView;

import org.build.buybook.R;
import org.build.buybook.adapter.BaseAdapter;
import org.build.buybook.adapter.ViewHolder;
import org.build.buybook.cusview.NavigationView;

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
    @Bind(R.id.bar_search)
    ImageView mSearch;
    @Bind(R.id.list_view)
    PullToRefreshView mPtrv;
    @Bind(R.id.in_list_view)
    ListView listView;

    @OnClick(R.id.bar_logo)
    public void bar_logo() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @OnClick(R.id.bar_search)
    public void searchBook() {
        openActivity(new Intent(this,SearchBook.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mNvMenu.getmMenuLv().setOnItemClickListener(this);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mLogo.setImageResource(R.mipmap.back);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mLogo.setImageResource(R.mipmap.open_left_menu);
            }
        });
        listView.setAdapter(new BookListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mPtrv.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPtrv.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDrawerLayout.closeDrawers();
        switch (position) {
            case 0:
                openActivity(new Intent(this, SelectedBook.class));
                break;
            case 1:
                showToast("注销登陆 ");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
    public class BookListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return App.courseList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(MainActivity.this,convertView,R.layout.book_list_item,position,parent);
            holder.setText(R.id.book_name,App.courseList.get(position).name);
            return holder.getConvertView();
        }
    }
}
