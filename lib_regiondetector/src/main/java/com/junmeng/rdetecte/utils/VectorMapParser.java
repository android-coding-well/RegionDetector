package com.junmeng.rdetecte.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.junmeng.rdetecte.bean.VectorPathInfo;

import org.xmlpull.v1.XmlPullParser;

/**
 * vector矢量图解析器
 * Created by HuWeiJian on 2017/12/27.
 */

public class VectorMapParser {
    private static final String TAG = "VectorMapParser";
    public static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    public VectorMapParser() {

    }


    public VectorPathInfo parse(@NonNull Resources res, @DrawableRes int resId) {
        VectorPathInfo vectorPathInfo = new VectorPathInfo();
        try {
            @SuppressLint("ResourceType") final XmlPullParser parser = res.getXml(resId);

            int event = parser.getEventType();// 触发第一個事件;
            String tag;
            while (event != XmlPullParser.END_DOCUMENT) {
                tag = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.w(TAG, "parse:START_DOCUMENT " + parser.getName());
                        break;
                    case XmlPullParser.START_TAG:
                        //parser.getAttributeName(0)=name
                        //parser.getAttributeNamespace(0)= http://schemas.android.com/apk/res/android
                        Log.w(TAG, "parse:START_TAG " + parser.getName());
                        if ("vector".equals(tag)) {
                            vectorPathInfo.setViewportWidth(Double.parseDouble(parser.getAttributeValue(NAMESPACE, "viewportWidth")));
                            vectorPathInfo.setViewportHeight(Double.parseDouble(parser.getAttributeValue(NAMESPACE, "viewportHeight")));
                        } else if ("path".equals(tag)) {
                            VectorPathInfo.PathInfo pathInfo = new VectorPathInfo.PathInfo();
                            pathInfo.setName(res.getString(Integer.parseInt(parser.getAttributeValue(NAMESPACE, "name").replace("@", ""))));
                            pathInfo.setPathData(parser.getAttributeValue(NAMESPACE, "pathData"));
                            vectorPathInfo.getPaths().add(pathInfo);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.w(TAG, "parse:END_TAG " + parser.getName());
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w(TAG, "parse: " + vectorPathInfo.getPaths().size());
        return vectorPathInfo;
    }


}
