package com.gillben.hodgepodgecode.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.gillben.hodgepodgecode.R;
import com.gillben.hodgepodgecode.utils.LogUtils;


public class SlideMenuPage extends ViewGroup {


    private static final String TAG = SlideMenuPage.class.getName();
    private static final boolean DEBUG = false;

    //右边menu的宽度,也是滑动的最大距离
    private int rightMenuWidth;
    //保存主内容View
    private View contentView;

    private int mTouchSlop;
    //menu滑动的限制距离，大于这个值时才弹出
    private int slideMenuLimitDistance;
    //滑动是否开启
    private boolean isSlidingEnable = true;
    //是否可以向左滑动
    private boolean isSlidingLeft = true;
    //没有移动
    private boolean nonMove;
    //用户是否处于滑动状态
    private boolean userSliding;
    //是否已经点击过
    private boolean isTouched;
    //是否拦截
    private boolean isIntercept;
    private boolean tempIntercept;
    //记录点击Id
    private int mPointerId;

    //缓存自己
    @SuppressLint("StaticFieldLeak")
    private static SlideMenuPage cacheSelf;

    //记录点击坐标
    private PointF firstPoint = new PointF();
    private PointF lastPoint = new PointF();

    private VelocityTracker mVelocityTracker;
    private float maxVelocity;

    private ValueAnimator openAnimator;
    private ValueAnimator closeAnimator;
    private boolean isOpen;
    private int animatorTime = 300;

    public SlideMenuPage(Context context) {
        this(context, null);
    }

    public SlideMenuPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenuPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        maxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.SlideMenuPage);
        isSlidingEnable = typedArray.getBoolean(R.styleable.SlideMenuPage_SlideEnable, true);
        isSlidingLeft = typedArray.getBoolean(R.styleable.SlideMenuPage_SlideLeft, true);
        tempIntercept = typedArray.getBoolean(R.styleable.SlideMenuPage_SlideIntercept, true);
        typedArray.recycle();
    }


    public boolean isSlidingEnable() {
        return isSlidingEnable;
    }

    public SlideMenuPage setSlidingEnable(boolean enable) {
        isSlidingEnable = enable;
        return this;
    }

    public boolean isIntercept() {
        return tempIntercept;
    }

    public SlideMenuPage setIntercept(boolean intercept) {
        tempIntercept = intercept;
        return this;
    }

    public boolean isSlidingLeft() {
        return isSlidingLeft;
    }

    public SlideMenuPage setSlidingLeft(boolean slidingLeft) {
        isSlidingLeft = slidingLeft;
        return this;
    }

    public boolean isOpen() {
        return isOpen;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setClickable(true);
        int contentViewWidth = 0;
        int selfHeight = 0;

        rightMenuWidth = 0;
        final boolean childViewIfNeedMatchParent = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        boolean childViewIncludeMatchParentAttr = false;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.setClickable(true);

            if (childView.getVisibility() != GONE) {
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                final MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                //以子View确定父容器的高度
                selfHeight = Math.max(selfHeight, childView.getMeasuredHeight());

                if (childViewIfNeedMatchParent && layoutParams.height == LayoutParams.MATCH_PARENT) {
                    childViewIncludeMatchParentAttr = true;
                }

                //第一个View表示主内容，第二个开始才是menu的内容
                if (i > 0) {
                    rightMenuWidth += childView.getMeasuredWidth();
                } else {
                    contentView = childView;
                    contentViewWidth = childView.getMeasuredWidth();
                }
            }
        }
        //save
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentViewWidth,
                getPaddingTop() + getPaddingBottom() + selfHeight);

        slideMenuLimitDistance = rightMenuWidth * 2 / 7;
        if (childViewIncludeMatchParentAttr) {
            forceUniformHeight(childCount, widthMeasureSpec);
        }

    }

    //模拟LinearLayout
    private void forceUniformHeight(int count, int widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accommodate the heightMeasureSpec from the parent
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                if (lp.height == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured width
                    // FIXME: this may not be right for something like wrapping text?
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    // Remeasure with new dimensions
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = getPaddingLeft();
        int right = getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                //对主内容的View进行layout
                if (i == 0) {
                    childView.layout(left, getPaddingTop(),
                            left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                    left += childView.getMeasuredWidth();
                } else {
                    //对menu开始layout
                    if (isSlidingLeft) {
                        childView.layout(left, getPaddingTop(),
                                left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                        left += childView.getMeasuredWidth();
                    } else {
                        childView.layout(right - childView.getMeasuredWidth(), getPaddingTop(),
                                right, getPaddingTop() + childView.getMeasuredHeight());
                        right -= childView.getMeasuredWidth();
                    }
                }
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isSlidingEnable) {
            initVelocityTracker(ev);
            final VelocityTracker velocityTracker = mVelocityTracker;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    userSliding = false;
                    nonMove = true;
                    isIntercept = false;
                    //防止多个手指触碰
                    if (isTouched) {
                        LogUtils.logWarn(TAG, "dispatchTouchEvent: isTouched = true");
                        return false;
                    } else {
                        isTouched = true;
                    }
                    firstPoint.set(ev.getRawX(), ev.getRawY());
                    lastPoint.set(ev.getRawX(), ev.getRawY());

                    //如果当前点击和cacheSelf不同，则立刻还原
                    if (cacheSelf != null) {
                        LogUtils.logWarn(TAG, "dispatchTouchEvent: " + "cacheSelf: " + cacheSelf.hashCode() +
                                " - " + "this: " + SlideMenuPage.this.hashCode());

                        if (cacheSelf != SlideMenuPage.this) {
                            cacheSelf.smoothClose();
                            isIntercept = tempIntercept;
                        }
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //只记录第一个手指点击Id
                    mPointerId = ev.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float offset = lastPoint.x - ev.getRawX();
                    if (Math.abs(offset) > 10 || Math.abs(getScrollX()) > 10) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //menu展开时，点击内容区域关闭menu
                    if (Math.abs(offset) > mTouchSlop) {
                        nonMove = false;
                    }
                    scrollBy((int) offset, 0);   //滑动
                    //超出范围，修正
                    if (isSlidingLeft) {
                        if (getScrollX() < 0) {
                            scrollTo(0, 0);
                        }
                        if (getScrollX() > rightMenuWidth) {
                            scrollTo(rightMenuWidth, 0);
                        }
                    } else {
                        if (getScrollX() < -rightMenuWidth) {
                            scrollTo(-rightMenuWidth, 0);
                        }
                        if (getScrollX() > 0) {
                            scrollTo(0, 0);
                        }
                    }
                    lastPoint.set(ev.getRawX(), ev.getRawY());
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //用户已经滑动
                    if (Math.abs(ev.getRawX() - firstPoint.x) > mTouchSlop) {
                        userSliding = true;
                    }
                    //计算最大速度
                    if (!isIntercept) {
                        velocityTracker.computeCurrentVelocity(1000, maxVelocity);
                    }
                    final float velocityX = velocityTracker.getXVelocity(mPointerId);
                    //滑动速度超过阈值
                    if (Math.abs(velocityX) > 1000) {
                        if (velocityX < -1000) {
                            if (isSlidingLeft && !isOpen()) {     //左滑
                                LogUtils.logWarn(TAG, "velocityX < -1000  ====  smoothOpen()");
                                smoothOpen();
                            } else {
                                LogUtils.logWarn(TAG, "velocityX < -1000  ====  smoothClose()");
                                smoothClose();
                            }
                        } else {
                            if (isSlidingLeft && isOpen()) {
                                LogUtils.logWarn(TAG, "velocityX > -1000  ====  smoothClose()");
                                smoothClose();
                            } else {
                                LogUtils.logWarn(TAG, "velocityX > -1000  ====  smoothOpen()");
                                smoothOpen();
                            }
                        }
                    } else {
                        if (Math.abs(getScrollX()) > slideMenuLimitDistance && !isOpen()) {
                            LogUtils.logWarn(TAG, "速度绝对值 < 1000 &&  最短距离 > slideMenuLimitDistance ====  smoothOpen()");
                            smoothOpen();
                        } else {
                            LogUtils.logWarn(TAG, "速度绝对值 < 1000  &&  最短距离 < slideMenuLimitDistance ====  smoothClose()");
                            smoothClose();
                        }
                    }
                    releaseVelocityTracker();
                    isTouched = false;
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSlidingEnable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(ev.getRawX() - lastPoint.x) > mTouchSlop) {
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
//                    if (isSlidingLeft) {
//                        if (getScrollX() > mTouchSlop) {
//                            if (ev.getX() < getWidth() - getScrollX()) {
//                                if (nonMove) {
//                                    if (DEBUG) {
//                                        Log.e(TAG, "onInterceptTouchEvent: nonMove = true");
//                                    }
//                                    smoothClose();
//                                }
//                                if (DEBUG) {
//                                    Log.e(TAG, "onInterceptTouchEvent: ACTION_UP ---  ev.getX() < getWidth() - getScrollX()");
//                                }
//                                return true;
//                            }
//                        }
//                    }
//                    else {
//                        if (-getScrollX() > mTouchSlop) {
//                            if (ev.getX() > -getScrollX()) {
//                                if (nonMove) {
//                                    smoothClose();
//                                }
//                                return true;
//                            }
//                        }
//                    }
                    //menu打开状态，用户向右滑动关闭menu，拦截
                    if (userSliding) {
                        if (DEBUG) {
                            LogUtils.logWarn(TAG, "onInterceptTouchEvent: userSliding = true");
                        }
                        return true;
                    }
                    break;
            }

//            if (isIntercept) {
//                if (DEBUG) {
//                    Log.e(TAG, "onInterceptTouchEvent: isIntercept = true");
//                }
//                return true;
//            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    //平滑展开menu
    public void smoothOpen() {
        cacheSelf = SlideMenuPage.this;
        if (contentView != null) {
            contentView.setLongClickable(false);
        }
        cancelAnimator();
        openAnimator = ValueAnimator.ofInt(getScrollX(), isSlidingLeft ? rightMenuWidth : -rightMenuWidth);
        openAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        openAnimator.setInterpolator(new LinearInterpolator());
        openAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getChildAt(0).setEnabled(false);
                isOpen = true;
            }
        });
        openAnimator.setDuration(animatorTime).start();
    }

    //平滑关闭menu
    public void smoothClose() {
        cacheSelf = null;
        if (contentView != null) {
            contentView.setLongClickable(true);
        }
        cancelAnimator();
        closeAnimator = ValueAnimator.ofInt(getScrollX(), 0);
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        closeAnimator.setInterpolator(new LinearInterpolator());
        closeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                getChildAt(0).setEnabled(true);
                isOpen = false;
            }
        });
        closeAnimator.setDuration(animatorTime).start();
    }


    private void cancelAnimator() {
        if (openAnimator != null && openAnimator.isRunning()) {
            openAnimator.cancel();
            openAnimator = null;
        }
        if (closeAnimator != null && closeAnimator.isRunning()) {
            closeAnimator.cancel();
            closeAnimator = null;
        }
    }

    public void quickClose() {
        if (this == cacheSelf) {
            cancelAnimator();
            cacheSelf.scrollTo(0, 0);
            cacheSelf = null;
        }
    }


    private void initVelocityTracker(final MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean performLongClick() {
        return Math.abs(getScrollX()) <= mTouchSlop && super.performLongClick();
    }


    @Override
    protected void onDetachedFromWindow() {
        if (this == cacheSelf) {
            cacheSelf.smoothClose();
            cacheSelf = null;
        }
        super.onDetachedFromWindow();
    }

}
