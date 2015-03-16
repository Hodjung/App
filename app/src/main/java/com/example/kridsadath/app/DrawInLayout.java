package com.example.kridsadath.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by KridsadaTh on 24/2/2558.
 */
public class DrawInLayout {

    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;

    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;
    private int stick_width, stick_height;
    private String position;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;
    private String macId;
    private boolean touch_state = false;
    private float oldX,oldY;

    public DrawInLayout (Context context, ViewGroup layout, int stick_res_id) {
        mContext = context;

        stick = BitmapFactory.decodeResource(mContext.getResources(), stick_res_id);

        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(mContext);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
    }
    public void setMacId(String macId){
        this.macId=macId;
    }
    public String getMacId(){
        return this.macId;
    }
    public void setPosition(int i,int j){
        position=String.valueOf(i+j);
    }
    public String getPosition(){
        return position;
    }
    public void setStickAndReDraw(int stick_res_id){
        stick = BitmapFactory.decodeResource(mContext.getResources(), stick_res_id);
        stick = Bitmap.createScaledBitmap(stick,stick_width,stick_height,false);
        draw.position(oldX,oldY);
        draw();
    }
    public void drawPoint(float x,float y){
        oldX=x;
        oldY=y;
        draw.position(x,y);
        draw();
    }
    public void setStickAlpha(int alpha) {
        STICK_ALPHA = alpha;
        paint.setAlpha(alpha);
    }
    public void setLayoutAlpha(int alpha) {
        LAYOUT_ALPHA = alpha;
        mLayout.getBackground().setAlpha(alpha);
    }
    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
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

    public void delete() {
        try {
            mLayout.removeView(draw);
        } catch (Exception e) { }
    }

    private class DrawCanvas extends View {
        float x, y;
        private DrawCanvas(Context mContext) {
            super(mContext);
        }
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }
        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }
}
