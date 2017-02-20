package com.junmeng.rdetecte.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 封装了绘图线程和背景等的设置
 * Created by HWJ on 2016/12/10.
 */

public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {
    private static final String TAG = "BaseSurfaceView";
    protected DrawThread drawThread;

    public Paint paint = new Paint();
    public int paintColor = 0xffff0000;//画笔的颜色
    public int paintStrokeWidth = 2;

    public int screenWidth;//surfaveview宽高
    public int screenHeight;
    public float screenCenterX;//surfaceView的中心点x坐标
    public float screenCenterY;//surfaceView的中心点y坐标

    protected int drawPauseTime = 30;//一次绘制后休息50ms

    public Bitmap bgBitmap = null;//背景图片
    public int bgColor = Color.TRANSPARENT;//背景颜色
    public int lineColor = 0xff00ff00;//线的颜色
    public int pointColor = 0xff0000ff;//点的颜色
    public int pointSize = 5;//点的大小


    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseSurfaceView();
    }

    public BaseSurfaceView(Context context) {
        this(context, null, 0);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    private void initBaseSurfaceView() {
        //设置画笔
        paint.setColor(paintColor);
        paint.setStrokeWidth(paintStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(20);


        //必须设置此处，否则获取不到touch事件
        setFocusable(true);
        setFocusableInTouchMode(true);
        getHolder().addCallback(this);
        //drawThread = new DrawThread(getHolder());
    }

    public void setLineColor(@ColorInt int color) {
        this.lineColor = color;
    }


    public void setPointColor(@ColorInt int color) {
        this.pointColor = color;
    }

    /**
     * 设置笔刷的粗细
     *
     * @param px
     */
    public void setPaintStrokeWidth(int px) {
        paintStrokeWidth = px;
        paint.setStrokeWidth(paintStrokeWidth);
    }

    /**
     * 设置点的大小
     *
     * @param radius 半径
     */
    public void setPointSize(int radius) {
        this.pointSize = radius;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    public void setBackgroundColor(@ColorInt int color) {
        this.bgColor = color;
    }

    /**
     * 设置背景
     *
     * @param bg
     */
    public void setBackgroundBitmap(Bitmap bg) {
        bgBitmap = bg;
    }


    /**
     * 设置一次绘制后休息时间，默认50ms
     *
     * @param time ms
     */
    public void setDrawPauseTime(int time) {
        drawPauseTime = time;
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        /*Canvas canvas = surfaceHolder.lockCanvas();
        if(canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }*/
        drawThread = new DrawThread(surfaceHolder);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        screenWidth = i1;
        screenHeight = i2;
        screenCenterX = screenWidth / 2.0f;
        screenCenterY = screenHeight / 2.0f;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        drawThread.stopDraw();
    }

    /**
     * 在此处执行绘制过程
     *
     * @param c
     */
    public abstract void doDraw(Canvas c);

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        if (bgColor == 0) {
            canvas.drawColor(bgColor, PorterDuff.Mode.CLEAR);
        } else {
            canvas.drawColor(bgColor);
        }

        if (bgBitmap != null) {
            try {
                canvas.drawBitmap(Bitmap.createScaledBitmap(bgBitmap, getMeasuredWidth(), getMeasuredHeight(), true), 0, 0, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DrawThread extends Thread {
        SurfaceHolder surfaceHolder;

        boolean isRunning = true;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {

            Canvas canvas = null;
            while (isRunning) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        drawBackground(canvas);
                        doDraw(canvas);
                    }
                    if (drawPauseTime > 0) {
                        Thread.sleep(drawPauseTime);//通过它来控制帧数执行一次绘制后休息50ms)
                    }else{
                        Thread.sleep(5);//休眠一小段时间避免while循环引发CPU被占满
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);

                    }
                }
            }

        }

        /**
         * 停止绘制
         */
        public void stopDraw() {
            isRunning = false;
        }
    }

    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    public int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}