package com.tassadar.nadacquest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NadacImageView extends ImageView {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.5f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    public NadacImageView(Context context) {
        super(context);
    }

    public NadacImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NadacImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScaleBoth(float scale)
    {
        this.scale = scale;
        setAlpha(scale);
        this.invalidate(); // If you want to see the scale every time you set
        // scale you need to have this line here,
        // invalidate() function will call onDraw(Canvas)
        // to redraw the view for you
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // The main mechanism to display scale animation, you can customize it
        // as your needs
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.scale(scale, scale, w/2, h/2);
        super.onDraw(canvas);
    }

    private float scale = BIG_SCALE;
}
