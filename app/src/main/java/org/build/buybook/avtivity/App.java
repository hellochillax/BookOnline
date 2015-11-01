package org.build.buybook.avtivity;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.build.buybook.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAC on 15/10/20.
 */
public class App extends Application {


    /**
     * 书籍总列表
     */
    public static List<Book> courseList=new ArrayList();

    /**
     * 已订购图书列表
     */
    public static List<Book> orderList=new ArrayList<>();


    private DisplayImageOptions mOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    private void initImageLoader() {
        mOptions = new DisplayImageOptions.Builder().showImageOnLoading(0) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(0)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(0) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                        // .delayBeforeLoading(int delayInMillis)//int
                        // delayInMillis为你设置的下载前的延迟时间
                        // 设置图片加入缓存前，对bitmap进行设置
                        // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();// 构建完成

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                // 自定义缓存路径
                .defaultDisplayImageOptions(mOptions)
                .imageDownloader(
                        new BaseImageDownloader(App.this, 5 * 1000, 30 * 1000))
                        // readTimeout(30)// 超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }
}
