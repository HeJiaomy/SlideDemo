package com.example.slidedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 侧滑面板控件, 抽屉面板.
 *
 * @author poplar
 *         <p>
 *         测量             摆放     绘制
 *         measure   ->  layout  ->  draw
 *         |           |          |
 *         onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件
 *         <p>
 *         View流程
 *         onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)
 *         <p>
 *         ViewGroup流程
 *         onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */
public class SlideMenu extends ViewGroup {
    private float downX;
    private float moveX;
    private static final int MAIN_STATE = 0;
    private static final int MENU_STATE = 1;
    private static int current_state = MAIN_STATE;
    private Scroller scroller;
    private float downY;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    /**
     * 测量并设置所有子View的宽高
     *
     * @param widthMeasureSpec  当前控件的宽度测量规则
     * @param heightMeasureSpec 当前控件的高度测量规则
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //指定左面板的宽高
        View leftView = getChildAt(0);
        leftView.measure(leftView.getLayoutParams().width, heightMeasureSpec);

        //指定主面板的宽高
        View mainView = getChildAt(1);
        mainView.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * changed: 当前控件的尺寸大小, 位置 是否发生了变化
     * left:当前控件 左边距
     * top:当前控件 顶边距
     * right:当前控件 右边界
     * bottom:当前控件 下边界
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //摆放内容，左面板
        View leftView = getChildAt(0);
        leftView.layout(-leftView.getMeasuredWidth(), 0, 0, b);
        //主面板
        getChildAt(1).layout(l, t, r, b);
    }

    /**
     * 处理触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                Log.e("downX", String.valueOf(downX));
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                //计算偏移量
                int scrollX = (int) (downX - moveX);

                //计算将要滚到的位置，判断是否会超出去
                //getScrollX()获取当前滚动到的位置
                int newScrollPosition = getScrollX() + scrollX;
                if (newScrollPosition < -getChildAt(0).getMeasuredWidth()) {
                    //如果小于-240，限定左边界
                    scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
                } else if (newScrollPosition > 0) {
                    //限定右边界
                    scrollTo(0, 0);
                } else {
                    //x传入正数则向左移动，传入负数则向右移动；y传入正数则向上移动，传入负数则向下移动
                    scrollBy(scrollX, 0);
                }

                downX = moveX;

                Log.e("moveX", String.valueOf(moveX));
                break;
            case MotionEvent.ACTION_UP:
                //计算菜单面板的1/2位置
                int leftCenter = (int) (-getChildAt(0).getMeasuredWidth() / 2.0f);
                if (getScrollX() < leftCenter) {
                    //如果当前位置小于菜单面板的1/2，显示菜单面板
                    current_state = MENU_STATE;
                    updateCurrentContent();
                } else if (getScrollX() >= leftCenter) {
                    //如果当前位置大于等于菜单面板的1/2，显示主面板
                    current_state = MAIN_STATE;
                    updateCurrentContent();
                }
                break;
        }
        return true;//消费事件
    }

    /**
     * 根据当前状态执行开启、关闭的动画
     */
    private void updateCurrentContent() {
        //平滑移动
        int startX = getScrollX();
        int dx = 0;
        if (current_state == MAIN_STATE) {
//            scrollTo(0, 0);

            /**
             * startX 开始的x值
             * startY 开始的y值
             * dx   移动的x值
             * dy   移动的y值
             * duration 数据模拟执行的时长
             */
            dx = 0 - startX;
        } else if (current_state == MENU_STATE) {
//            scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
            //            dx= 结束位置(-240)-开始位置
            dx = -getChildAt(0).getMeasuredWidth() - startX;
        }
        int duration = Math.abs(dx * 2);
        scroller.startScroll(startX, 0, dx, 0, duration);

        invalidate(); //重绘界面 ->drawChild() ->computeScroll()
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            //true 动画还未结束
            //获取当前要滚动到的位置
            int currX = scroller.getCurrX();
            scrollTo(currX, 0);
            invalidate();
        }
    }

    private void open() {
        current_state = MENU_STATE;
        updateCurrentContent();
    }

    private void close() {
        current_state = MAIN_STATE;
        updateCurrentContent();
    }

    public void switchState() {
        if (current_state == MAIN_STATE) {
            open();
        } else {
            close();
        }
    }

    /**
     * 拦截判断
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float xOffset = Math.abs(ev.getX() - downX);
                float yOffset = Math.abs(ev.getY() - downY);
                if (xOffset > yOffset && xOffset > 5) {  //水平方向超出一定距离才拦截
                    return true;    //拦截此次触摸事件
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
