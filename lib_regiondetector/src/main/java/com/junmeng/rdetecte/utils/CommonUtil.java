package com.junmeng.rdetecte.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by HWJ on 2017/2/19.
 */

public class CommonUtil {

    /**
     * 根据路径生成Region
     *
     * @param path
     * @return
     */
    public static Region genRegion(Path path) {
        if(path==null){
            return null;
        }
        Region region = new Region();
        RectF r = new RectF();
        //得到Path的矩形边界
        path.computeBounds(r, true);
        // 设置区域路径和剪辑描述的区域
        region.setPath(path, new Region((int) (r.left), (int) (r.top),
                (int) (r.right),
                (int) (r.bottom)));
        return region;
    }

    /**
     * 一个坐标点，以某个点为缩放中心，缩放指定倍数，求这个坐标点在缩放后的新坐标值。
     *
     * @param targetPointX 坐标点的X
     * @param targetPointY 坐标点的Y
     * @param scaleCenterX 缩放中心的X
     * @param scaleCenterY 缩放中心的Y
     * @param scale        缩放倍数
     * @return 坐标点的新坐标
     */
    public static PointF scaleByPoint(float targetPointX, float targetPointY, float scaleCenterX, float scaleCenterY, float scale) {
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前圆所在的位置，
        // 然后再以某个点为中心进行缩放
        matrix.preTranslate(targetPointX, targetPointY);
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        return new PointF(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);
    }


    /**
     * 将vector资源转为Bitmap
     *
     * @param context
     * @param vectorDrawableId
     * @return
     */
    public static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }
}
