# RegionDetector
一个支持灵活设置的不规则区域检测控件
---
![image](https://github.com/huweijian5/RegionDetector/blob/master/screenshots/QQ%E6%88%AA%E5%9B%BE20170220173408.png)

![image](https://github.com/huweijian5/RegionDetector/blob/master/screenshots/device-2017-02-20-174131.mp4_1487584213.gif)

![image](https://github.com/huweijian5/RegionDetector/blob/master/screenshots/device-2017-02-20-174131.mp4_1487584269.gif)

![image](https://github.com/huweijian5/RegionDetector/blob/master/screenshots/device-2017-02-20-174131.mp4_1487584566.gif)
---
##介绍说明

* 此控件支持不规则区域的识别监测，采用矢量图，有效降低内存，常用来做地图区域识别
* 支持手动点击模式和中心点定位模式
* 支持缩放位移操作
* 支持双击缩放操作
* 提供渐变动画，拒绝生硬
* 支持自定义矢量图，但需满足一定要求
* 开放众多接口满足个性化定制
* ...

---
##使用说明
* 布局xml中添加：
```
<com.junmeng.rdetecte.widget.RegionDetectSurfaceView
            android:id="@+id/rdv_detect"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```
* 设置激活区域：
```java
int[] areaRes = new int[]{
            R.string.china_anhui, R.string.china_beijing, R.string.china_guangdong
            , R.string.china_chongqing, R.string.china_xinjiang, R.string.china_fujian
            , R.string.china_gansu, R.string.china_zhejiang, R.string.china_yunnan
            , R.string.china_xizang, R.string.china_tianjin
            , R.string.china_shandong, R.string.china_heilongjiang, R.string.china_hainan
    };
binding.rdvDetect.setAreaActivateStatus(areaRes, true);
```
* 全部区域请查看：
```
<resources>
    <string name="china_beijing">北京</string>
    <string name="china_tianjin">天津</string>
    <string name="china_shanghai">上海</string>
    <string name="china_chongqing">重庆</string>
    <string name="china_hebei">河北</string>
    <string name="china_shanxi">山西</string>
    <string name="china_liaoning">辽宁</string>
    <string name="china_jilin">吉林</string>
    <string name="china_heilongjiang">黑龙江</string>
    <string name="china_jiangsu">江苏</string>
    <string name="china_zhejiang">浙江</string>
    <string name="china_anhui">安徽</string>
    <string name="china_fujian">福建</string>
    <string name="china_jiangxi">江西</string>
    <string name="china_shandong">山东</string>
    <string name="china_henan">河南</string>
    <string name="china_hubei">湖北</string>
    <string name="china_hunan">湖南</string>
    <string name="china_guangdong">广东</string>
    <string name="china_hainan">海南</string>
    <string name="china_sichuan">四川</string>
    <string name="china_guizhou">贵州</string>
    <string name="china_yunnan">云南</string>
    <string name="china_shaanxi">陕西</string>
    <string name="china_gansu">甘肃</string>
    <string name="china_qinghai">青海</string>
    <string name="china_taiwan">台湾</string>
    <string name="china_neimenggu">内蒙古</string>
    <string name="china_guangxi">广西</string>
    <string name="china_xizang">西藏</string>
    <string name="china_ningxia">宁夏</string>
    <string name="china_xinjiang">新疆</string>
    <string name="china_xianggang">香港</string>
    </resources>
```
* 设置监听器:
```
binding.rdvDetect.setOnActivateRegionDetectListener(new RegionDetectSurfaceView.OnActivateRegionDetectListener() {
            @Override
            public void onActivateRegionDetect(String name) {
                binding.tvActivate.setText("高亮区域：" + name);
                binding.rdvDetect.setSelectedAreaOnlyCloseCenterLocation(name);
            }
        });
        binding.rdvDetect.setOnRegionDetectListener(new RegionDetectSurfaceView.OnRegionDetectListener() {
            @Override
            public void onRegionDetect(String name) {
                binding.tvDetect.setText("当前区域：" + name);
            }
        });

        binding.rdvDetect.setOnDoubleClickListener(new RegionDetectSurfaceView.OnDoubleClickListener() {
            @Override
            public void onDoubleClick(@RegionDetectSurfaceView.ScaleMode int scaleMode) {
                binding.tvZoom.setText("双击操作:" + (scaleMode == SCALE_ZOOMIN ? "放大" : "缩小"));
            }
        });      
```
* 至此最简单的引用方式已经结束了。
### 常用api说明：
#### public void setRegionDetectMode(@RegionDetectMode int detectMode);

* 可在此设置支持的模式为手动点击模式和中心定位模式
* 手动点击模式符合一般使用习惯，但区域较小时很难点击
* 中心定位模式则可以较细致的定位，推荐使用，默认也是此模式

---
#### public void setCenterIcon(Bitmap bitmap);

* 自定义中心定位图标

---
#### public void setCenterIconLocationType(@CenterIconLocationType int locationType);

* 设置中心定位图标的定位位置，支持图标中心和图标底部两种

---
#### public void fitCenter();

* 将地图适合屏幕缩放并居中

---
#### public void setAreaColor(@NonNull String areaName, @ColorInt int highlightColor, @ColorInt int activatedColor, @ColorInt int normalColor);

* 设置区域高亮颜色，激活颜色和普通颜色
* 此设置优先级高于默认的颜色，如上图的广东会变成黄色

---
#### public void setAreaMap(@DrawableRes int map, int originalWidth, int originalHeight);

* 自定义区域地图
* 格式需要如下：

```
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportHeight="600.0"
    android:viewportWidth="700.0">
    <!--黑龙江-->
    <path
        android:name="@string/china_heilongjiang"
        android:fillAlpha="0.1"
        android:fillColor="#51b133"
        android:pathData="M646.2,152 ...... Z"
        android:strokeColor="#ffffff"
        android:strokeWidth="0" />
    <!--吉林-->
    <path
        android:name="@string/china_jilin"
        android:fillAlpha="0.1"
        android:fillColor="#51b133"       
	android:pathData=" ......"
        android:strokeColor="#ffffff"
        android:strokeWidth="0" />
    <!--辽宁-->
    <path
        android:name="@string/china_liaoning"
        android:fillAlpha="0.1"
        android:fillColor="#ff9116"
        android:pathData="M575.1,247.2 ......"
        android:strokeColor="#ffffff"
        android:strokeWidth="0" />
    ......
</vector>
```
* 其中<path>元素的name和pathData必须设置
---
#### 更多请详看在线文档或源码及参照demo
 ---
## JavaDoc文档

* [在线JavaDoc](https://jitpack.io/com/github/huweijian5/RegionDetector/1.0.0/javadoc/index.html)
* 网址：`https://jitpack.io/com/github/huweijian5/RegionDetector/[VersionCode]/javadoc/index.html`
* 其中[VersionCode](https://github.com/huweijian5/RegionDetector/releases)请替换为最新版本号
* 注意文档使用UTF-8编码，如遇乱码，请在浏览器选择UTF-8编码即可

---
## 引用

* 如果需要引用此库,做法如下：
* Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```	
* and then,add the dependecy:
```
dependencies {
	        compile 'com.github.huweijian5:RegionDetector:latest_version'
}
```
* 其中latest_version请到[releases](https://github.com/huweijian5/RegionDetector/releases)中查看

##注意
* 为了避免引入第三方库导致工程依赖多个版本的问题，如android support库
* 故建议在个人的工程目录下的build.gradle下加入以下变量，具体请看此[build.gradle](https://github.com/huweijian5/RegionDetector/blob/master/build.gradle)
```
ext{
    minSdkVersion = 16
    targetSdkVersion = 25
    compileSdkVersion = 25
    buildToolsVersion = '25.0.1'

    // App dependencies
    supportLibraryVersion = '25.0.1'
    junitVersion = '4.12'
    espressoVersion = '2.2.2'
}
```	
* 请注意，对于此库已有的变量，命名请保持一致


