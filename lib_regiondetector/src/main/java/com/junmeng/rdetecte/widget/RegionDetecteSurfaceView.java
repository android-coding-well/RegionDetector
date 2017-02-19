package com.junmeng.rdetecte.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.junmeng.gdv.detector.MoveGestureDetector;
import com.junmeng.rdetecte.MapPathInfo;
import com.junmeng.rdetecte.R;
import com.junmeng.rdetecte.utils.CommonUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by HWJ on 2017/2/17.
 */

public class RegionDetecteSurfaceView extends BaseSurfaceView {
    private static final String TAG = "RegionDetecteSurface";

    public interface OnRegionDetecteListener {
        /**
         * 所有区域检测
         *
         * @param name
         */
        void onRegionDetecte(String name);
    }

    public interface OnActivateRegionDetecteListener {
        /**
         * 激活区域检测
         *
         * @param name
         */
        void onActivateRegionDetecte(String name);
    }

    public interface OnDoubleClickListener {
        /**
         * 双击事件
         *
         * @param scaleMode
         */
        void onDoubleClick(@ScaleMode int scaleMode);
    }

    /**
     * 放大
     */
    public static final int SCALE_ZOOMIN = 0;
    /**
     * 缩小
     */
    public static final int SCALE_ZOOMOUT = 1;

    @IntDef({SCALE_ZOOMIN, SCALE_ZOOMOUT})
    public @interface ScaleMode {
    }

    /**
     * 中心点图标以图标中心为中心点
     */
    public static final int CENTER_ICON_POSITION_CENTER = 0;
    /**
     * 中心点图标以底部为中心点
     */
    public static final int CENTER_ICON_POSITION_BOTTOM = 1;

    @IntDef({CENTER_ICON_POSITION_CENTER, CENTER_ICON_POSITION_BOTTOM})
    public @interface CenterIconPosition {
    }


    public RegionDetecteSurfaceView(Context context) {
        this(context, null);
    }

    public RegionDetecteSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public RegionDetecteSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    VectorDrawableCompat vectorDrawableCompat;
    // HashMap<String, Region> areaRegions = new HashMap<>();
    Paint paint = new Paint();
    OnRegionDetecteListener onRegionListener;
    OnActivateRegionDetecteListener onActivateRegionListener;
    OnDoubleClickListener onDoubleClickListener;

    float scale = 1f;//实际缩放比例，相对于起始图片的缩放比例

    float originalScale = 1f;//最原始的缩放比例
    private float maxScale = 3.0f;//最大缩放比例
    private float minScale = originalScale;//最小缩放比例，最小默认为最合适的居中的比例，这里就不提供设置了

    //平移的差值，相对于起始位置的差值
    float translateDx = 0;
    float translateDy = 0;

    float originalTranslateDx = 0;//最原始的平移差值
    float originalTranslateDy = 0;//最原始的平移差值

    //屏幕宽高
    int screenWidth = 0;
    int screenHeight = 0;

    //屏幕中心点的坐标
    float screenCenterX = 0;
    float screenCenterY = 0;

    //地图原始宽高，与xml中vector的viewportWidth和viewportHeight一致
    int mapOriginalWidth = 700;
    int mapOriginalHeight = 600;


    //地图起始位置的中心坐标
    float mapOriginalCenterX = mapOriginalWidth / 2f;
    float mapOriginalCenterY = mapOriginalHeight / 2f;

    //中心定位点的图标
    private Bitmap centerIcon;

    MoveGestureDetector moveGestureDetector;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;


    //地图的矩阵，用于缩放
    Matrix mapPathMatrix = new Matrix();

    //存放地图绘制path
    HashMap<String, MapPathInfo> pathInfoMap = new HashMap<>();

    //当前中心定位点指向的路径path的key值
    String currentKey = "";

    //上一个中心定位点指向的路径path的key值
    String lastKey = "";

    private boolean isDebugMode = false;

    int highlightColor = Color.YELLOW;

    int activateAreaColor = Color.BLUE;

    int normalAreaColor = Color.GRAY;


    private boolean isCenterIconVisible = true;

    private int centerIconPosition = CENTER_ICON_POSITION_BOTTOM;


    private boolean isSupportDoubleScale = true;//是否支持双击缩放操作


    private int animateTime = 300;//动画时间，ms


    private boolean isOpenCenterLocation = true;//是否启用中心定位点

    private String selectedActivateKey = "";//选中的激活区域（会高亮显示），只有在isOpenCenterLocation为false才生效

    private void init(Context context) {

        moveGestureDetector = new MoveGestureDetector(context, new MyMoveGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleGestureListener());
        gestureDetector = new GestureDetector(context, new MyGestureListener());

        setAreaMap(R.drawable.ic_map_china, 700, 600);

        centerIcon = CommonUtil.getBitmap(context, R.drawable.ic_location_24dp);

        initPaint();

    }

    /**
     * 初始化区域路径Map
     */
    private void initAreaPathMap(@NonNull VectorDrawableCompat vectorDrawableCompat) {
        if (vectorDrawableCompat == null) {
            return;
        }
        pathInfoMap.clear();
        HashMap<String, Path> pathsMap = (HashMap<String, Path>) getAllPath(vectorDrawableCompat);
        for (String key : pathsMap.keySet()) {
            MapPathInfo mapPathInfo = new MapPathInfo(pathsMap.get(key));
            pathInfoMap.put(key, mapPathInfo);
        }
    }

    private void initPaint() {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
    }


    //******************************************开放接口*****************************************

    /**
     * 设置选中的激活区域（在关闭中心定位点的情况下才生效，表现为高亮显示)
     *
     * @param areaName
     */
    public void setSelectedAreaOnlyCloseCenterLocation(String areaName) {
        if (TextUtils.isEmpty(areaName)) {
            return;
        }
        selectedActivateKey = areaName;
    }

    /**
     * 设置选中的激活区域（在关闭中心定位点的情况下才生效，表现为高亮显示)
     *
     * @param areaNameRes
     */
    public void setSelectedAreaOnlyCloseCenterLocation(@StringRes int areaNameRes) {
        String areaName = getResources().getString(areaNameRes);
        if (TextUtils.isEmpty(areaName)) {
            return;
        }
        selectedActivateKey = areaName;
    }

    /**
     * 是否启用中心定位点，关闭则如普通地图一样只能缩放位移
     *
     * @param isOpen
     */
    public void isOpenCenterLocation(boolean isOpen) {
        isOpenCenterLocation = isOpen;
        isCenterIconVisible = isOpen;
    }


    /**
     * 设置缩放动画时间,默认300毫秒
     *
     * @param ms 毫秒
     */
    public void setAnimateTime(int ms) {
        this.animateTime = ms;
    }

    /**
     * 是否支持双击缩放操作,默认支持
     *
     * @param isSupport
     */
    public void isSupportDoubleClickScale(boolean isSupport) {
        isSupportDoubleScale = isSupport;
    }

    /**
     * 设置最大缩放比例（默认为最小的3倍），如果小于默认的最小比例则设置无效
     *
     * @param scale
     */
    public void setMaxScale(float scale) {
        if (scale > originalScale) {
            this.maxScale = scale;
        }
    }

    /**
     * 获得最大的缩放比例
     *
     * @return
     */
    public float getMaxScale() {
        return maxScale;
    }


    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public float getCurrentScale() {
        return scale;
    }

    /**
     * 获得最小的缩放比例
     *
     * @return
     */
    public float getMinScale() {
        return originalScale;
    }

    /**
     * 缩放地图
     *
     * @param scale
     */
    public void scaleMap(float scale) {
        mapPathMatrix.postScale(scale, scale, mapOriginalCenterX, mapOriginalCenterY);
        this.scale *= scale;
        Log.i(TAG, "onScale: " + scale);

    }

    /**
     * 平移地图
     *
     * @param translateDx
     * @param translateDy
     */
    public void translateMap(float translateDx, float translateDy) {
        this.translateDx = translateDx;
        this.translateDy = translateDy;
    }

    /**
     * 设置中心图标的定位位置
     *
     * @param position 图标的定位中心位置
     */
    public void setCenterIconPosition(@CenterIconPosition int position) {
        this.centerIconPosition = position;
        // invalidate();
    }

    /**
     * 设置中心图标
     *
     * @param bitmap
     */
    public void setCenterIcon(Bitmap bitmap) {
        if (bitmap != null) {
            this.centerIcon = bitmap;
            // invalidate();
        }
    }

    /**
     * 设置中心图标是否可见
     *
     * @param isVisible
     */
    public void setCenterIconVisibility(boolean isVisible) {
        isCenterIconVisible = isVisible;
        // invalidate();
    }

    /**
     * 将地图合适居中
     */
    public void fitCenter() {
        scale = originalScale;
        translateDx = (screenWidth - mapOriginalWidth) / 2f;
        translateDy = (screenHeight - mapOriginalHeight) / 2f;
        mapPathMatrix.setScale(originalScale, originalScale, mapOriginalCenterX, mapOriginalCenterY);
        // invalidate();
    }

    /**
     * 设置区域颜色
     *
     * @param highlightColor 高亮颜色,-1表示不设置
     * @param activatedColor 激活颜色,-1表示不设置
     * @param normalColor    正常颜色,-1表示不设置
     */
    public void setAreaColor(@StringRes int areaNameRes, @ColorInt int highlightColor, @ColorInt int activatedColor, @ColorInt int normalColor) {
        String key = getResources().getString(areaNameRes);
        MapPathInfo info = pathInfoMap.get(key);
        if (info != null) {
            info.highlightColor = highlightColor;
            info.activatedColor = activatedColor;
            info.normalColor = normalColor;
        }
        //invalidate();
    }

    /**
     * 设置区域颜色
     *
     * @param highlightColor 高亮颜色,-1表示不设置
     * @param activatedColor 激活颜色,-1表示不设置
     * @param normalColor    正常颜色,-1表示不设置
     */
    public void setAreaColor(@NonNull String areaName, @ColorInt int highlightColor, @ColorInt int activatedColor, @ColorInt int normalColor) {
        MapPathInfo info = pathInfoMap.get(areaName);
        if (info != null) {
            info.highlightColor = highlightColor;
            info.activatedColor = activatedColor;
            info.normalColor = normalColor;
        }
        // invalidate();
    }

    /**
     * 设置默认的高亮颜色，优先级最低
     *
     * @param color
     */
    public void setDefaultHighlightColor(@ColorInt int color) {
        this.highlightColor = color;
        //invalidate();
    }

    /**
     * 设置默认的激活颜色，优先级最低
     *
     * @param color
     */
    public void setDefaultActivateColor(@ColorInt int color) {
        this.activateAreaColor = color;
        //invalidate();
    }

    /**
     * 设置默认的正常颜色，优先级最低
     *
     * @param color
     */
    public void setDefaultNormalColor(@ColorInt int color) {
        this.normalAreaColor = color;
        // invalidate();
    }

    /**
     * 设置区域激活状态
     *
     * @param areaNameRes
     * @param isActivated
     */
    public void setAreaActivateStatus(@StringRes int areaNameRes, boolean isActivated) {
        String key = getResources().getString(areaNameRes);
        if (pathInfoMap.get(key) != null) {

            pathInfoMap.get(key).isActivated = isActivated;
        }
        // invalidate();
    }

    /**
     * 设置区域激活状态
     *
     * @param areaNameRes
     */
    public void setAreaActivateStatus(@NonNull @StringRes int[] areaNameRes, boolean isActivated) {
        for (int res : areaNameRes) {
            String key = getResources().getString(res);
            if (pathInfoMap.get(key) != null) {
                pathInfoMap.get(key).isActivated = isActivated;
            }
        }
        // invalidate();

    }


    /**
     * 设置区域激活状态
     *
     * @param areaNames
     */
    public void setAreaActivateStatus(@NonNull String[] areaNames, boolean isActivated) {
        for (String key : areaNames) {
            if (pathInfoMap.get(key) != null) {
                pathInfoMap.get(key).isActivated = isActivated;
            }
        }
        // invalidate();
    }

    /**
     * 设置区域激活状态
     *
     * @param areaName
     */
    public void setAreaActivateStatus(String areaName, boolean isActivated) {
        if (pathInfoMap.get(areaName) != null) {
            pathInfoMap.get(areaName).isActivated = isActivated;
        }
        // invalidate();
    }

    /**
     * 设置是否激活所有区域
     *
     * @param isActivated
     */
    public void setAllAreaActivateStatus(boolean isActivated) {
        for (String key : pathInfoMap.keySet()) {
            pathInfoMap.get(key).isActivated = isActivated;
        }
        //invalidate();
    }

    /**
     * 设置激活地图区域监听器，只有激活区域能触发
     *
     * @param listener
     */
    public void setOnActivateRegionDetecteListener(OnActivateRegionDetecteListener listener) {
        this.onActivateRegionListener = listener;
    }

    /**
     * 设置地图区域监听器
     *
     * @param listener
     */
    public void setOnRegionDetecteListener(OnRegionDetecteListener listener) {
        this.onRegionListener = listener;
    }

    /**
     * 设置双击事件监听器
     *
     * @param listener
     */
    public void setOnDoubleClickListener(OnDoubleClickListener listener) {
        this.onDoubleClickListener = listener;
    }

    /**
     * 设置区域地图
     *
     * @param map            vector图
     * @param originalWidth  原图宽
     * @param originalHeight 原图高
     */
    public void setAreaMap(@DrawableRes int map, int originalWidth, int originalHeight) {
        this.mapOriginalWidth = originalWidth;
        this.mapOriginalHeight = originalHeight;
        vectorDrawableCompat = VectorDrawableCompat.create(getResources(), map, null);
        vectorDrawableCompat.setBounds(new Rect(0, 0, mapOriginalWidth, mapOriginalHeight));
        initAreaPathMap(vectorDrawableCompat);
        initScaleAndTranslate();
        //invalidate();
    }

    /**
     * 获得所有VectorDrawableCompat中的所有path,包含第一级组里的path(暂不支持第二级及更深层次的)
     *
     * @param vectorDrawableCompat
     * @return
     */
    public Map<String, Path> getAllPath(VectorDrawableCompat vectorDrawableCompat) {
        Map<String, Path> pathsMap = new HashMap<String, Path>();
        Object mVectorState = getVectorStateFieldFromVectorDrawableCompatClassByReflect(vectorDrawableCompat);
        if (mVectorState == null) {
            return pathsMap;
        }

        Object mVPathRenderer = getVPathRendererFieldFromVectorDrawableCompatStateClassByReflect(mVectorState);
        if (mVPathRenderer == null) {
            return pathsMap;
        }
        ArrayMap<String, Object> am = getVGTargetsMapFieldFromVPathRendererClassByReflect(mVPathRenderer);
        if (am == null) {
            return pathsMap;
        }
        for (String key : am.keySet()) {
            //Log.i(TAG, "getAllPath: key= " + key + " and value= " + am.get(key).toString());
            //如果是VFullPath
            if (am.get(key).toString().contains("VectorDrawableCompat$VFullPath")) {
                pathsMap.put(key, toPathMethodFromVFullPathSupperClassByReflect(am.get(key)));
            }
            //如果是VGroup,VGroup可能包含多个VFullPath
            if (am.get(key).toString().contains("VectorDrawableCompat$VGroup")) {

                ArrayList<Object> list = getChildrenFieldFromVGroupClassByReflect(am.get(key));

                for (Object object : list) {
                    //Log.i(TAG, "getAllPath: " + (object.toString()));
                    if (object.toString().contains("VectorDrawableCompat$VFullPath")) {
                        pathsMap.put(getPathNameFieldFromVFullPathSupperClassByReflect(object), toPathMethodFromVFullPathSupperClassByReflect(object));
                    }
                }
            }
        }
        return pathsMap;
    }

    /**
     * 根据名称获得xml中pathData对应的Path对象
     *
     * @param pathName xml中的path元素的name属性
     * @return pathName对应的path
     */
    public Path getPathByPathNameFromXML(@NonNull VectorDrawableCompat vectorDrawableCompat, String pathName) {
        Path path = new Path();
        try {
            Object obj = getTargetByNameMethodFromVectorDrawableCompatClassByReflect(vectorDrawableCompat, pathName);
            //Log.i(TAG, "getPathByPathNameFromXML: " + (obj.toString()));

            if (obj.toString().contains("VectorDrawableCompat$VFullPath")) {
                path = toPathMethodFromVFullPathSupperClassByReflect(obj);
            }

        } catch (Exception e) {
            Log.e(TAG, "getPathByPathNameFromXML exception");
            e.printStackTrace();

        }
        return path;
    }

    /**
     * 根据组名获得xml中pathData对应的Path对象
     *
     * @param groupName xml中的group元素的name属性
     * @return
     */
    public List<Path> getPathsByGroupNameFromXML(@NonNull VectorDrawableCompat vectorDrawableCompat, String groupName) {
        List<Path> paths = new ArrayList<Path>();
        try {
            Object obj = getTargetByNameMethodFromVectorDrawableCompatClassByReflect(vectorDrawableCompat, groupName);
            //Log.i(TAG, "getPathsByGroupNameFromXML: " + (obj.toString()));

            if (obj.toString().contains("VectorDrawableCompat$VGroup")) {
                ArrayList<Object> list = getChildrenFieldFromVGroupClassByReflect(obj);

                for (Object object : list) {
                    Log.i(TAG, "VGroup: " + (object.toString()));
                    if (object.toString().contains("VectorDrawableCompat$VFullPath")) {
                        paths.add(toPathMethodFromVFullPathSupperClassByReflect(object));
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getPathsByGroupNameFromXML exception");
            e.printStackTrace();

        }
        return paths;

    }


    //*************************************反射接口************************************

    /**
     * 反射获得VFullPath的父类VPath的toPath方法
     *
     * @param vFullPath
     * @return
     */
    private Path toPathMethodFromVFullPathSupperClassByReflect(Object vFullPath) {
        Path path = new Path();
        try {
            //获得父类VPath
            Class vPathClass = Class.forName(vFullPath.getClass().getSuperclass().getName());
            //反射出父类中的私有方法 toPath
            Method toPathMethod = vPathClass.getDeclaredMethod("toPath", Path.class);
            toPathMethod.invoke(vFullPath, path);
        } catch (Exception e) {
            Log.e(TAG, "toPathFromVFullPathByReflect exception");
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 反射获得VGroup.mChildren成员
     *
     * @param vGroup VGroup对象
     * @return
     */
    private ArrayList<Object> getChildrenFieldFromVGroupClassByReflect(Object vGroup) {
        ArrayList<Object> list = new ArrayList<>();
        try {
            Class vGroupClass = Class.forName(vGroup.getClass().getName());
            Field mChildrenField = vGroupClass.getDeclaredField("mChildren");
            mChildrenField.setAccessible(true);
            list = (ArrayList<Object>) mChildrenField.get(vGroup);
        } catch (Exception e) {
            Log.e(TAG, "getChildrenFromVGroupByReflect exception");
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 反射执行VectorDrawableCompat.getTargetByName方法
     *
     * @param vdc  VectorDrawableCompat对象
     * @param name xml中的group元素或path元素的name属性
     * @return
     */
    private Object getTargetByNameMethodFromVectorDrawableCompatClassByReflect(VectorDrawableCompat vdc, String name) {
        if (vdc == null || TextUtils.isEmpty(name)) {
            return null;
        }
        Object obj = null;
        //反射VectorDrawableCompat类的私有方法 getTargetByName
        Method getTargetByNameMethod = null;
        try {
            getTargetByNameMethod = vdc.getClass().getDeclaredMethod("getTargetByName", String.class);
            // 设置访问权限
            getTargetByNameMethod.setAccessible(true);
            // 执行私有方法
            obj = getTargetByNameMethod.invoke(vdc, name);
            //Log.i(TAG, "getTargetByNameFromVectorDrawableCompatByReflect: " + (obj.toString()));
        } catch (Exception e) {
            Log.e(TAG, "getTargetByNameFromVectorDrawableCompatByReflect exception");
            e.printStackTrace();
        }

        return obj;
    }


    /**
     * 反射得到VFullPath的父类VPath的成员mPathName
     *
     * @param vFullPath
     * @return
     */
    private String getPathNameFieldFromVFullPathSupperClassByReflect(Object vFullPath) {
        if (vFullPath == null) {
            return null;
        }
        try {
            Class vPathClass = Class.forName(vFullPath.getClass().getSuperclass().getName());
            Field mPathNameField = vPathClass.getDeclaredField("mPathName");
            mPathNameField.setAccessible(true);
            Object obj = mPathNameField.get(vFullPath);
            if (obj != null) {
                return (String) obj;
            }
        } catch (Exception e) {
            Log.e(TAG, "getPathNameFromVFullPathClassByReflect exception");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 反射得到VPathRenderer的成员mVGTargetsMap
     *
     * @param vPathRender VPathRenderer的实例对象
     * @return
     */
    private ArrayMap<String, Object> getVGTargetsMapFieldFromVPathRendererClassByReflect(Object vPathRender) {
        if (vPathRender == null) {
            return null;
        }
        try {
            Class vPathRendererClass = Class.forName(vPathRender.getClass().getName());
            Field mVGTargetsMapField = vPathRendererClass.getDeclaredField("mVGTargetsMap");
            mVGTargetsMapField.setAccessible(true);
            ArrayMap<String, Object> mVGTargetsMap = (ArrayMap<String, Object>) mVGTargetsMapField.get(vPathRender);
            return mVGTargetsMap;
        } catch (Exception e) {
            Log.e(TAG, "getVGTargetsMapFieldFromVPathRendererClassByReflect exception");
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 反射得到VectorDrawableCompatState的成员mVPathRenderer
     *
     * @param vectorDrawableCompatState VectorDrawableCompatStat的实例对象
     * @return
     */
    private Object getVPathRendererFieldFromVectorDrawableCompatStateClassByReflect(Object vectorDrawableCompatState) {
        if (vectorDrawableCompatState == null) {
            return null;
        }
        try {
            Class vectorDrawableCompatStateClass = Class.forName(vectorDrawableCompatState.getClass().getName());
            Field vPathRendererField = vectorDrawableCompatStateClass.getDeclaredField("mVPathRenderer");
            vPathRendererField.setAccessible(true);
            Object mVPathRenderer = vPathRendererField.get(vectorDrawableCompatState);
            return mVPathRenderer;
        } catch (Exception e) {
            Log.e(TAG, "getVPathRendererFieldFromVectorDrawableCompatStateClassByReflect exception");
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 反射得到VectorDrawableCompat的成员mVectorState
     *
     * @param vectorDrawableCompat VectorDrawableCompat的实例对象
     * @return
     */
    private Object getVectorStateFieldFromVectorDrawableCompatClassByReflect(VectorDrawableCompat vectorDrawableCompat) {
        if (vectorDrawableCompat == null) {
            return null;
        }
        try {
            Class vectorDrawableCompatClass = Class.forName(vectorDrawableCompat.getClass().getName());
            Field mVectorStateField = vectorDrawableCompatClass.getDeclaredField("mVectorState");
            mVectorStateField.setAccessible(true);
            Object mVectorState = mVectorStateField.get(vectorDrawableCompat);
            return mVectorState;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveGestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
        initScaleAndTranslate();
    }


    private void initScaleAndTranslate() {
        screenCenterX = screenWidth / 2f;
        screenCenterY = screenHeight / 2f;
        mapOriginalCenterX = mapOriginalWidth / 2f;
        mapOriginalCenterY = mapOriginalHeight / 2f;

        float scaleX = screenWidth * 1f / mapOriginalWidth;
        float scaleY = screenHeight * 1f / mapOriginalHeight;
        minScale = originalScale = scale = Math.min(scaleX, scaleY);// 获得缩放比例最大的那个缩放比，即scaleX和scaleY中小的那个
        maxScale = originalScale * 3;
        Log.i(TAG, "onMeasure: " + screenWidth + "*" + screenHeight + "," + scale);
        originalTranslateDx = translateDx = (screenWidth - mapOriginalWidth) / 2f;
        originalTranslateDy = translateDy = (screenHeight - mapOriginalHeight) / 2f;
        mapPathMatrix.setScale(originalScale, originalScale, mapOriginalCenterX, mapOriginalCenterY);
    }


    /**
     * 在此处执行绘制过程
     *
     * @param canvas
     */
    @Override
    public void doDraw(Canvas canvas) {
        //long pre=System.currentTimeMillis();
        canvas.save();
        canvas.translate(translateDx, translateDy);

        //绘制地图各省市
        paint.setColor(Color.GRAY);
        for (String key : pathInfoMap.keySet()) {
            MapPathInfo mapPathInfo = pathInfoMap.get(key);
            Path path = new Path();
            path.addPath(mapPathInfo.path, mapPathMatrix);

            if (currentKey.equals(key)) {//被选中的

                if (mapPathInfo.isActivated) {//是否激活了的
                    paint.setColor(isOpenCenterLocation ?
                            (mapPathInfo.highlightColor != -1 ? mapPathInfo.highlightColor : highlightColor)
                            : (mapPathInfo.activatedColor != -1 ? mapPathInfo.activatedColor : activateAreaColor));
                } else {
                    paint.setColor(mapPathInfo.normalColor != -1 ? mapPathInfo.normalColor : normalAreaColor);
                }

            } else {//未被选中的
                if (mapPathInfo.isActivated) {
                    paint.setColor(mapPathInfo.activatedColor != -1 ? mapPathInfo.activatedColor : activateAreaColor);
                } else {
                    paint.setColor(mapPathInfo.normalColor != -1 ? mapPathInfo.normalColor : normalAreaColor);
                }

            }
            //只有在关闭中心定位点并选中了激活区域才会高亮显示
            if (!isOpenCenterLocation && mapPathInfo.isActivated && selectedActivateKey.equals(key)) {
                paint.setColor((mapPathInfo.highlightColor != -1 ? mapPathInfo.highlightColor : highlightColor));
            }
            canvas.drawPath(path, paint);

        }

        canvas.restore();

        //绘制中心定位点
        if (centerIcon != null && isCenterIconVisible) {
            canvas.drawBitmap(centerIcon, (screenCenterX - centerIcon.getWidth() / 2f),
                    centerIconPosition == CENTER_ICON_POSITION_BOTTOM ?
                            (screenCenterY - centerIcon.getHeight()) :
                            (screenCenterY - centerIcon.getHeight() / 2f), null);
        }

        if (isDebugMode) {
            drawDebugView(canvas);
        }
        //Log.i(TAG, "onDraw: "+ (System.currentTimeMillis()-pre));
    }

    /**
     * 区域检测
     */
    private void areaDetect() {
        if (!isOpenCenterLocation) {
            return;
        }

        //区域检测
        currentKey = "";
        boolean hasDetected = false;
        for (String key : pathInfoMap.keySet()) {
            Region region = pathInfoMap.get(key).region;
            if (region.contains((int) getMapCentrCoordinate().x, (int) getMapCentrCoordinate().y)) {
                hasDetected = true;
                Log.i(TAG, "选中了 " + key);
                if (onRegionListener != null && !lastKey.equals(key)) {
                    onRegionListener.onRegionDetecte(key);
                }
                if (onActivateRegionListener != null
                        && pathInfoMap.get(key).isActivated
                        && !lastKey.equals(key)) {
                    onActivateRegionListener.onActivateRegionDetecte(key);
                }
                currentKey = key;
                lastKey = currentKey;
                break;
            }
        }
        lastKey = currentKey;
        if (onRegionListener != null && hasDetected == false) {
            onRegionListener.onRegionDetecte("");
        }
    }

    /**
     * 获得中心定位点在地图起始位置的相对坐标
     * 主要用于区域检测
     *
     * @return
     */
    private PointF getMapCentrCoordinate() {

        PointF mapCenter = getMapCenterCoordinate();
        //中心定位点与实际地图中点的差值
        float dx = screenCenterX - mapCenter.x;
        float dy = screenCenterY - mapCenter.y;

        //根据位移的差值和缩放比例计算中心定位点在地图起始位置的相对坐标
        //主要用于区域检测
        float x = mapOriginalCenterX + dx / scale;
        float y = mapOriginalCenterY + dy / scale;
        return new PointF(x, y);
    }

    /**
     * 获得实际地图（即经过平移和缩放后的地图）的中心坐标
     *
     * @return
     */
    private PointF getMapCenterCoordinate() {

        float mapCenterX = mapOriginalWidth / 2f + translateDx;
        float mapCenterY = mapOriginalHeight / 2f + translateDy;

        return new PointF(mapCenterX, mapCenterY);
    }


    /**
     * 绘制测试画面，调试用
     *
     * @param canvas
     */
    private void drawDebugView(Canvas canvas) {

        //绘制原始地图
        vectorDrawableCompat.draw(canvas);

        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        canvas.drawText("缩放：" + scale, 20, 50, paint);
        canvas.drawText("选中：" + currentKey, 300, 50, paint);


        //实际地图（即经过平移和缩放后的地图）的中心坐标
        float mapCenterX = mapOriginalWidth / 2f + translateDx;
        float mapCenterY = mapOriginalHeight / 2f + translateDy;


        //中心定位点与实际地图中点的差值
        float dx = screenCenterX - mapCenterX;
        float dy = screenCenterY - mapCenterY;

        //根据位移的差值和缩放比例计算中心定位点在地图起始位置的相对坐标
        //主要用于区域检测
        float x = mapOriginalCenterX + dx / scale;
        float y = mapOriginalCenterY + dy / scale;

        //绘制实际地图的中心点
        paint.setColor(Color.BLUE);
        canvas.drawCircle(mapCenterX, mapCenterY, 8, paint);
        canvas.drawText("中心：(" + mapCenterY + "," + mapCenterY + ")", mapCenterX, mapCenterY, paint);

        //起始地图的中心点
        canvas.drawCircle(mapOriginalCenterX, mapOriginalCenterY, 8, paint);
        canvas.drawText("中心：(" + mapOriginalCenterX + "," + mapOriginalCenterY + ")", mapOriginalCenterX, mapOriginalCenterY, paint);

        //绘制中心定位点在原图起始位置的相对坐标
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, 8, paint);
        canvas.drawText("(" + x + "," + y + ")", x, y, paint);

        canvas.drawText("(" + screenCenterX + "," + screenCenterY + ")", screenCenterX, screenCenterY, paint);
    }


    private class MyMoveGestureListener extends MoveGestureDetector.SimpleOnMoveGestureListener {

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            if (Math.abs(d.x) < 2 && Math.abs(d.y) < 2) {//忽略小范围的滑动
                return true;
            }
            translateDx += d.x;
            translateDy += d.y;
            Log.i(TAG, "onMove: " + d.x + "," + d.y);
            areaDetect();
            return true;
        }

        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return super.onMoveBegin(detector);
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
            super.onMoveEnd(detector);
            Log.i(TAG, "onMoveEnd:");

        }
    }

    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float factor = detector.getScaleFactor();
            if (Math.abs(factor - 1) < 0.015) {
                return true;
            }
            scaleMap(factor);
            areaDetect();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            if (scale > maxScale) {
                animateToTargetScale(maxScale);
            }
            if (scale < originalScale) {
                animateToTargetScale(originalScale);
            }


        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap: ");
            if (!isSupportDoubleScale) {
                return super.onDoubleTap(e);
            }


            if (scale >= maxScale) {//缩小为原来的大小
                if (onDoubleClickListener != null) {
                    onDoubleClickListener.onDoubleClick(SCALE_ZOOMOUT);
                }
                animateToFitCenter();
            } else {//缩放到最大值
                if (onDoubleClickListener != null) {
                    onDoubleClickListener.onDoubleClick(SCALE_ZOOMIN);
                }
                animateToTargetScale(maxScale);
            }
            return super.onDoubleTap(e);
        }
    }

    /**
     * 缩放到目标比例targetScale
     *
     * @param targetScale
     */
    private void animateToTargetScale(final float targetScale) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(scale, targetScale);
        valueAnimator.setDuration(animateTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();
                scaleMap(animatorValue / scale);
                if (animatorValue == targetScale) {
                    areaDetect();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 缩放到合适居中的比例
     */
    private void animateToFitCenter() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(scale, originalScale);
        valueAnimator.setDuration(animateTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();

                scaleMap(animatorValue / scale);
            }
        });

        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(translateDx, originalTranslateDx);
        valueAnimator2.setDuration(animateTime);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();


                translateMap(animatorValue, translateDy);

            }
        });

        ValueAnimator valueAnimator3 = ValueAnimator.ofFloat(translateDy, originalTranslateDy);
        valueAnimator3.setDuration(animateTime);
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float) animation.getAnimatedValue();

                translateMap(translateDx, animatorValue);
                if (animatorValue == originalTranslateDy) {
                    areaDetect();
                }
            }
        });
        valueAnimator.start();
        valueAnimator2.start();
        valueAnimator3.start();
    }


}
