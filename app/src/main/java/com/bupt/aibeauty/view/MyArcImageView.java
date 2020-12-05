package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class MyArcImageView extends androidx.appcompat.widget.AppCompatImageView {
    /*
     *弧形高度
     */
    private int mArcHeight=80;

    public MyArcImageView(Context context) {
        this(context, null);
    }

    public MyArcImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyArcImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, getHeight());
        path.quadTo(getWidth() / 2, getHeight() - 2 * mArcHeight, getWidth(), getHeight());
        path.lineTo(getWidth(), 0);
        path.close();
        canvas.clipPath(path);

        super.onDraw(canvas);
    }
}
