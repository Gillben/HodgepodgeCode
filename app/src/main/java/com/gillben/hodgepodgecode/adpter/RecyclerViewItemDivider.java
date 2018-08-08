package com.gillben.hodgepodgecode.adpter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gillben.hodgepodgecode.R;

public class RecyclerViewItemDivider extends RecyclerView.ItemDecoration{

    private Context mContext;
    private int dividerHeight;
    private Paint mPaint = new Paint();


    public RecyclerViewItemDivider(Context context,int divider){
        this.mContext = context;
        mPaint.setColor(mContext.getResources().getColor(R.color.colorPrimary));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        dividerHeight = divider;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() + parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams
                    = (RecyclerView.LayoutParams) childView.getLayoutParams();

            final int top = childView.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + dividerHeight;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,dividerHeight);
    }
}
