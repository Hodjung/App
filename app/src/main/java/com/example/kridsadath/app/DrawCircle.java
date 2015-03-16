package com.example.kridsadath.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 9/3/2558.
 */
public class DrawCircle {
    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;
    private boolean isPoint;
    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;
    private Float x,y,r;
    private DrawCanvas draw;
    private Paint paint;

    public DrawCircle (Context context, ViewGroup layout) {
        mContext = context;
        isPoint=true;
        draw = new DrawCanvas(mContext);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        mLayout = layout;
        params = mLayout.getLayoutParams();
    }
    public void setXYR(float x ,float y,float r){
        this.x=x;
        this.y=y;
        this.r=r;
    }
    public void drawCircle(){
        draw();
    }
    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }
    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }
    private void draw() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
        mLayout.addView(draw);
    }
    private class DrawCanvas extends View {
        private DrawCanvas(Context mContext) {
            super(mContext);
        }
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(x,y,r,paint);
        }
    }
}
