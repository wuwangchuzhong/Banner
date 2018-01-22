package com.yidian.buyer.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yidian.buyer.R;
import com.yidian.buyer.activity.synchronousauction.chatroom.base.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miao on 2017/5/17.
 * 自定义首页轮播图
 */

public class YiDianBanner extends FrameLayout {

    private static final String TAG = "ImageSlideshow";
    private Context context;
    private View contentView;
    private ViewPager vpImageTitle;
    private LinearLayout llDot;
    private int count;
    private List<View> viewList;
    private boolean isAutoPlay;
    private Handler handler;
    private int currentItem;
    private SparseBooleanArray isLarge;
    private List<ImageTitleBean> imageTitleBeanList;
    private int widtch = 40;
    private int height = 4;
    private int dotSpace = 12;
    private int delay = 3000;

    public YiDianBanner(Context context) {
        this(context, null);
    }

    public YiDianBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YiDianBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // 初始化View
        initView();
        // 初始化数据
        initData();
    }

    private void initData() {
        imageTitleBeanList = new ArrayList<>();
    }
    /**
     * 初始化View
     */
    private void initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.is_main_banner_layout, this, true);
        vpImageTitle = (ViewPager) findViewById(R.id.vp_image_title);
        llDot = (LinearLayout) findViewById(R.id.ll_dot);
    }

    // 设置方块的高
    public void setWidtch(int widtch) {
        this.widtch = widtch;
    }
    public void setHeight(int height){
        this.height=height;
    }


    // 设置小圆点的间距
    public void setDotSpace(int dotSpace) {
        this.dotSpace = dotSpace;
    }

    // 设置图片轮播间隔时间
    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void reSet() {
        try {
            imageTitleBeanList.clear();
            viewList.clear();
            delay=0;
            handler.removeCallbacks(task);
        } catch (Exception e) {
        }
    }

    // 添加图片 设置重叠的两张图片
    public void addImageUrl(String imageUrl) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageUrl(imageUrl);
        imageTitleBeanList.add(imageTitleBean);
    }

    // 添加图片和标题
    public void addImageTitle(String imageUrl, String title) {
        ImageTitleBean imageTitleBean = new ImageTitleBean();
        imageTitleBean.setImageUrl(imageUrl);
        imageTitleBean.setTitle(title);
        imageTitleBeanList.add(imageTitleBean);
    }

    // 添加图片和标题的JavaBean
    public void addImageTitleBean(ImageTitleBean imageTitleBean) {
        imageTitleBeanList.add(imageTitleBean);
    }

    // 设置图片和标题的JavaBean数据列表
    public void setImageTitleBeanList(List<ImageTitleBean> imageTitleBeanList) {
        this.imageTitleBeanList = imageTitleBeanList;
    }

    // 设置完后最终提交
    public void commit() {
        if (imageTitleBeanList != null) {
            count = imageTitleBeanList.size();
            // 设置ViewPager
            setViewPager(imageTitleBeanList);
            // 设置指示器
            setIndicator();
            // 开始播放
            starPlay();
        } else {
            Log.e(TAG, "数据为空");
        }
    }

    /**
     * 设置指示器
     */
    private void setIndicator() {
        isLarge = new SparseBooleanArray();
        // 记得创建前先清空数据，否则会受遗留数据的影响。
        llDot.removeAllViews();
        for (int i = 0; i < count; i++) {
            View view = new View(context);
            view.setBackgroundResource(R.drawable.check_unselected);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widtch, height);
            layoutParams.leftMargin = dotSpace / 2;
            layoutParams.rightMargin = dotSpace / 2;
            layoutParams.topMargin = dotSpace / 2;
            layoutParams.bottomMargin = dotSpace / 2;
            llDot.addView(view, layoutParams);
            isLarge.put(i, false);
        }
        llDot.getChildAt(0).setBackgroundResource(R.drawable.check_selected);
        isLarge.put(0, true);
    }

    /**
     * 开始自动播放图片
     */
    private void starPlay() {
            // 如果少于2张就不用自动播放了
            if (count < 2) {
                isAutoPlay = false;
            } else {
                isAutoPlay = true;
                handler = new Handler();
                handler.postDelayed(task, delay);
            }
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                // 位置循环
                currentItem = currentItem % (count + 1) + 1;
                // 正常每隔3秒播放一张图片
                vpImageTitle.setCurrentItem(currentItem);
                handler.postDelayed(task, 3000);
            } else {
                // 如果处于拖拽状态停止自动播放，会每隔5秒检查一次是否可以正常自动播放。
                handler.postDelayed(task, 5000);
            }
        }
    };

    // 创建监听器接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // 声明监听器
    private ImageSlideshow.OnItemClickListener onItemClickListener;

    // 提供设置监听器的公共方法
    public void setOnItemClickListener(ImageSlideshow.OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    class ImageTitlePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = viewList.get(position);
            // 设置Item的点击监听器
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 注意：位置是position-1
                    onItemClickListener.onItemClick(v, position - 1);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }

    /**
     * 设置ViewPager
     *
     * @param imageTitleBeanList
     */
    private void setViewPager(List<ImageTitleBean> imageTitleBeanList) {
        // 设置View列表
        setViewList(imageTitleBeanList);
        vpImageTitle.setAdapter(new ImageTitlePagerAdapter());
        // 从第1张图片开始（位置刚好也是1，注意：0位置现在是最后一张图片）
        currentItem = 1;
        vpImageTitle.setCurrentItem(1);
        vpImageTitle.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 遍历一遍子View，设置相应的背景。
                for (int i = 0; i < llDot.getChildCount(); i++) {
                    if (i == position - 1) {// 被选中
                        llDot.getChildAt(i).setBackgroundResource(R.drawable.check_selected);
                        if (!isLarge.get(i)) {
                            isLarge.put(i, true);
                        }
                    } else {// 未被选中 此时灰色
                        llDot.getChildAt(i).setBackgroundResource(R.drawable.check_unselected);
                        if (isLarge.get(i)) {
                            isLarge.put(i, false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    // 闲置中
                    case ViewPager.SCROLL_STATE_IDLE:
                        // “偷梁换柱”
                        if (vpImageTitle.getCurrentItem() == 0) {
                            vpImageTitle.setCurrentItem(count, false);
                        } else if (vpImageTitle.getCurrentItem() == count + 1) {
                            vpImageTitle.setCurrentItem(1, false);
                        }
                        currentItem = vpImageTitle.getCurrentItem();
                        isAutoPlay = true;
                        break;
                    // 拖动中
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isAutoPlay = false;
                        break;
                    // 设置中
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isAutoPlay = true;
                        break;
                }
            }
        });
    }

    /**
     * 根据出入的数据设置View列表
     *
     * @param imageTitleBeanList
     */
    private void setViewList(List<ImageTitleBean> imageTitleBeanList) {
        viewList = new ArrayList<>();
        for (int i = 0; i < count + 2; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.is_image_title_layout, null);
            ImageView ivImage = (ImageView) view.findViewById(R.id.iv_image);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            if (i == 0) {// 将最前面一页设置成本来最后的那页
                Glide.with(context).//将集合设置这里
                        load(imageTitleBeanList.get(count - 1).getImageUrl()).placeholder(R.drawable.syncauction).into(ivImage);
                tvTitle.setText(imageTitleBeanList.get(count - 1).getTitle());
            } else if (i == count + 1) {// 将最后面一页设置成本来最前的那页
                Glide.with(context).
                        load(imageTitleBeanList.get(0).getImageUrl()).placeholder(R.drawable.syncauction).into(ivImage);
                tvTitle.setText(imageTitleBeanList.get(0).getTitle());
            } else {
                Glide.with(context).
                        load(imageTitleBeanList.get(i - 1).getImageUrl()).placeholder(R.drawable.syncauction).into(ivImage);
                tvTitle.setText(imageTitleBeanList.get(i - 1).getTitle());
            }
            // 将设置好的View添加到View列表中
            viewList.add(view);
        }
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        handler.removeCallbacksAndMessages(null);
        context = null;
    }
}
