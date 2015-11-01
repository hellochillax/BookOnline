package org.build.buybook.avtivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.yalantis.taurus.PullToRefreshView;

import org.build.buybook.R;
import org.build.buybook.adapter.BaseAdapter;
import org.build.buybook.adapter.ViewHolder;
import org.build.buybook.model.Book;
import org.build.buybook.utils.BookGetUtils;
import org.build.buybook.utils.CacheUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/18.
 */
public class SelectedBook extends BaseActivity implements AdapterView.OnItemClickListener,PullToRefreshView.OnRefreshListener{

    @OnClick(R.id.bar_back)
    public void back(){
        onBackPressed();
    }
    @Bind(R.id.list_view)
    PullToRefreshView mPtrv;
    @Bind(R.id.in_list_view)
    ListView listView;
    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_book);
        ButterKnife.bind(this);
        listView.setOnItemClickListener(this);
        mPtrv.setOnRefreshListener(this);
        adapter=new OrderAdapter();
        listView.setAdapter(adapter);
        onRefresh();
    }
    AlertDialog dialog;

    public void buy(final Book book, final int position) {
        dialog = new AlertDialog.Builder(this).setTitle("确定要取消预定吗?").setMessage("书籍价格:" + book.book_price)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookGetUtils.orderOneBook(SelectedBook.this, new CacheUtils(SelectedBook.this, CacheUtils.CacheType.FOR_ACCOUNT).getCache(CacheUtils.USER_NAME), book.book_id, "0", position, handler);
                    }

                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mPtrv.setRefreshing(false);
            switch (msg.what){
                case BookGetUtils.GET_ORDER_LIST_OK:
                    showToast("获取数据成功");
                        App.orderList.clear();
                        Gson gson = new Gson();
                        Book[] books = gson.fromJson(msg.obj.toString(), Book[].class);
                    try {
                        for (Book book : books) {
                            App.orderList.add(book);
                            LogUtils.d("图书列表数据:" + book.toString());
                        }
                        adapter.notifyDataSetChanged();
                        mPtrv.setRefreshing(false);
                    } catch (Exception e) {
                        showToast("暂无已定图书");
                        mPtrv.setRefreshing(false);
                    }
                    break;
                case BookGetUtils.GET_ORDER_LIST_FAIL:
                    showToast("网络错误,请重试");
                    break;
                case BookGetUtils.UN_ORDER_BOOK_OK:
                    App.orderList.remove(msg.arg1);
                    adapter.notifyDataSetChanged();
                    showToast("成功取消预订");
                    break;
                case BookGetUtils.ORDER_BOOK_FAILE:
                    showToast("操作失败,请重试");
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        buy(App.orderList.get(position),position);
    }

    @Override
    public void onRefresh() {
        BookGetUtils.getOrderList(this,new CacheUtils(this, CacheUtils.CacheType.FOR_ACCOUNT).getCache(CacheUtils.USER_NAME),handler);
    }
    public class OrderAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return App.orderList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=ViewHolder.get(SelectedBook.this,convertView,R.layout.book_list_item,position,parent);
            Book item=App.orderList.get(position);
            holder.setText(R.id.book_name, item.book_name)
                    .setText(R.id.book_publish,item.pub_house)
                    .setText(R.id.book_price,item.book_price)
                    .setText(R.id.book_course,item.course_name)
                    .setBackgroundResource(R.id.book_order,R.drawable.un_buy_bg);
            return holder.getConvertView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }
}
