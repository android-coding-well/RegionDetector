package com.junmeng.rdetecte.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 矢量图映射类，与xml结构对应
 * Created by HuWeiJian on 2017/12/28.
 */

public class VectorPathInfo {

    private double viewportHeight;
    private double viewportWidth;
    private List<PathInfo> paths=new ArrayList<>();

    public double getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(double viewportHeight) {
        this.viewportHeight = viewportHeight;
    }

    public double getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(double viewportWidth) {
        this.viewportWidth = viewportWidth;
    }

    public List<PathInfo> getPaths() {
        return paths;
    }

    public void setPaths(List<PathInfo> paths) {
        this.paths = paths;
    }

    public static class PathInfo{
        private String name;
        private String pathData;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPathData() {
            return pathData;
        }

        public void setPathData(String pathData) {
            this.pathData = pathData;
        }
    }

}
