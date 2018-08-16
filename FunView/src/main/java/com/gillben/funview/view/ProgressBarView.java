package com.gillben.funview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gillben.funview.R;


public final class ProgressBarView extends View {

    private Paint defaultPaint;
    private Paint progressPaint;
    private Paint textPaint;

    //字体颜色 ----- 默认黑色
    private int textColor;
    //字体尺寸  ----- 默认12
    private float textSize;
    //默认背景色 -----  默认灰色
    private int defaultBackgroundColor;
    //进度条背景色 -----  默认红色
    private int progressBackgroundColor;
    //进度条两端的圆角大小 -----  默认 10
    private float endpointCorners;
    //进度条高度
    private int mHeight;
    //记录当前所在的进度
    private int curProgressLength;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.ProgressBarView);
        textColor = typedArray.getColor(R.styleable.ProgressBarView_text_color, Color.BLACK);
        textSize = typedArray.getDimension(R.styleable.ProgressBarView_text_size, 12);
        defaultBackgroundColor = typedArray.getColor(R.styleable.ProgressBarView_default_background_color, Color.GRAY);
        progressBackgroundColor = typedArray.getColor(R.styleable.ProgressBarView_progress_background_color, Color.RED);
        endpointCorners = typedArray.getDimension(R.styleable.ProgressBarView_endpoint_corner_size, 10);
        typedArray.recycle();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        defaultPaint = new Paint();
        defaultPaint.setAntiAlias(true);
        defaultPaint.setColor(defaultBackgroundColor);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressBackgroundColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    //测量高度
    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        int concreteSize;
        if (mode == MeasureSpec.EXACTLY) {
            concreteSize = size;
        } else {
            int textHeight = (int) (textPaint.descent() - textPaint.ascent());       //获取文字高度
            concreteSize = getPaddingTop() + getPaddingBottom() + Math.max(mHeight, textHeight);
            if (mode == MeasureSpec.AT_MOST) {
                concreteSize = Math.min(concreteSize, size);
            }
        }
        return concreteSize;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getMeasuredHeight();
        int curOverlayProgress = getWidth() * curProgressLength / 100;

        if (Build.VERSION.SDK_INT < 21) {
            //绘制默认背景
            RectF defaultRect = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth(), mHeight);
            canvas.drawRoundRect(defaultRect, endpointCorners, endpointCorners, defaultPaint);
            //绘制进度背景
            RectF rectF = new RectF(getPaddingLeft(), getPaddingTop(), curOverlayProgress, mHeight);
            canvas.drawRoundRect(rectF, endpointCorners, endpointCorners, progressPaint);
        } else {
            //绘制默认背景
            canvas.drawRoundRect(getPaddingLeft(), getPaddingTop(), getMeasuredWidth(), mHeight, endpointCorners, endpointCorners, defaultPaint);
            //绘制进度背景
            canvas.drawRoundRect(getPaddingLeft(), getPaddingTop(), curOverlayProgress, mHeight, endpointCorners, endpointCorners, progressPaint);

        }

        //绘制文本进度显示
        String curProgressText = curProgressLength + "%";
        float textWidth = textPaint.measureText(curProgressText);
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        float offset = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        offset = mHeight / 2 + offset;
        canvas.drawText(curProgressText, getMeasuredWidth() / 2, offset, textPaint);
    }

    public void updateProgress(int temp) {
        curProgressLength = temp;
        invalidate();
    }


    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public void setDefaultBackgroundColor(int defaultBackgroundColor) {
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    public int getProgressBackgroundColor() {
        return progressBackgroundColor;
    }

    public void setProgressBackgroundColor(int progressBackgroundColor) {
        this.progressBackgroundColor = progressBackgroundColor;
    }

    public float getEndpointCorners() {
        return endpointCorners;
    }

    public void setEndpointCorners(float endpointCorners) {
        this.endpointCorners = endpointCorners;
    }
}
