package com.example.kridsadath.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KridsadaTh on 28/2/2558.
 */
public class DrawLine  {
    private int STICK_ALPHA = 200;
    private int LAYOUT_ALPHA = 200;
    private boolean isPoint,textRange;
    private Context mContext;
    private ViewGroup mLayout;
    private ViewGroup.LayoutParams params;
    private List<Float> x,y;
    private List<Float> realX,realY;
    private String position;

    private DrawCanvas draw;
    private Paint paint;

    public DrawLine (Context context, ViewGroup layout) {
        mContext = context;
        isPoint=true;
        textRange=false;
        draw = new DrawCanvas(mContext);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        mLayout = layout;
        params = mLayout.getLayoutParams();
        x=new ArrayList<Float>();
        y=new ArrayList<Float>();
        realX=new ArrayList<Float>();
        realY=new ArrayList<Float>();
    }
    public void add_point(float x ,float y,float realX,float realY){
        this.x.add(x);
        this.y.add(y);
        this.realX.add(realX);
        this.realY.add(realY);
    }
    public void setTextRange(boolean a){
        this.textRange=a;
    }
    public void remove_last_point(){
        if (x.size()>0) {
            this.x.remove(x.size() - 1);
            this.y.remove(y.size() - 1);
            this.realX.remove(realX.size()-1);
            this.realY.remove(realY.size()-1);
        }
    }
    public float getX(int position){
        return this.x.get(position);
    }
    public float getY(int position){
        return this.y.get(position);
    }
    public int getSize(){
        return x.size();
    }
    public float getFirstX(){
        return x.get(0);
    }
    public float getFirstY(){
        return y.get(0);
    }
    public float getFirstRealX(){
        return realX.get(0);
    }
    public float getFirstRealY(){
        return realY.get(0);
    }
    public void drawLine(boolean isPoint){
        this.isPoint=isPoint;
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
            for (int i=0;i<x.size();i++) {
                if (i==0&&isPoint) {
                    paint.setStrokeWidth(20);
                    canvas.drawPoint(realX.get(i), realY.get(i), paint);
                }
                if (i<x.size()-1) {
                    paint.setStrokeWidth(5);
                    canvas.drawLine(realX.get(i), realY.get(i), realX.get(i + 1), realY.get(i + 1), paint);
                }
                if (i==0&&textRange) {
                    DecimalFormat df=new DecimalFormat("0.00");
                    double dX=(realX.get(i)-50)/25-(realX.get(i+1)-50)/25;
                    double dY=(realY.get(i)-50)/25-(realY.get(i+1)-50)/25;
                    double range=Math.sqrt((dX * dX) + (dY * dY));
                    paint.setTextSize(20);
                    Log.d(""+range+" "+dX+" "+dY+" "+x.get(i)+" "+x.get(i+1)+" "+y.get(i)+" "+y.get(i+1),"checkLog");
                    canvas.drawText("" + df.format(range) + " m", realX.get(i), realY.get(i), paint);
                }
            }

        }
    }
}
