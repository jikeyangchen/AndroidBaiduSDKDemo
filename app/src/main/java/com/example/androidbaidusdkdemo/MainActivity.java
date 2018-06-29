package com.example.androidbaidusdkdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
<span style="color:#ff6666;"> </span><span style="color:#333333;">   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION">
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION">
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE">
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE/">
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE">
<uses-permission android:name="android.permission.READ_PHONE_STATE">
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE">
<uses-permission android:name="android.permission.INTERNET">
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS">
<uses-permission android:name="android.permission.READ_LOGS"></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></uses-permission></span>

package com.majianjie.baidumap;

        import android.app.Activity;
        import android.graphics.Bitmap;
        import android.graphics.drawable.Drawable;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.MeasureSpec;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.baidu.location.BDLocation;
        import com.baidu.location.BDLocationListener;
        import com.baidu.location.LocationClient;
        import com.baidu.location.LocationClientOption;
        import com.baidu.mapapi.BMapManager;
        import com.baidu.mapapi.MKGeneralListener;
        import com.baidu.mapapi.map.LocationData;
        import com.baidu.mapapi.map.MKEvent;
        import com.baidu.mapapi.map.MKMapViewListener;
        import com.baidu.mapapi.map.MapController;
        import com.baidu.mapapi.map.MapPoi;
        import com.baidu.mapapi.map.MapView;
        import com.baidu.mapapi.map.MyLocationOverlay;
        import com.baidu.mapapi.map.PopupClickListener;
        import com.baidu.mapapi.map.PopupOverlay;
        import com.baidu.platform.comapi.basestruct.GeoPoint;
        import com.example.baidumap.R;

public class MainActivity extends Activity {

    //声明控件
    private Button request;

    private Toast mToast=null;
    private BMapManager mBMapManager=null;
    private MapView mMapView = null;            //MapView 是地图主控件
    private MapController mMapController = null;//用MapController完成地图控制

    private LocationClient mLocClient;
    public LocationData mLocData = null;

    private LocationOverlay myLocationOverlay = null;//定位图层
    private boolean isRequest = false;//是否手动触发请求定位
    private boolean isFirstLoc = true;//是否首次定位

    private PopupOverlay mPopupOverlay  = null;//弹出泡泡图层，浏览节点时使用
    private View viewCache=null;
    public BDLocation location = new BDLocation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //**使用地图sdk前需先初始化BMapManager，这个必须在setContentView()先初始化
        mBMapManager = new BMapManager(this);
        //第一个参数是API key,   第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
        mBMapManager.init(LDtH1sVwr7kygaF0aTqaVwWU, new MKGeneralListener() {
            //授权错误的时候调用的回调函数
            @Override
            public void onGetPermissionState(int iError) {
                if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                    showToast(API KEY错误, 请检查！);
                }
            }
            //一些网络状态的错误处理回调函数
            @Override
            public void onGetNetworkState(int iError) {
                if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                    Toast.makeText(getApplication(), 您的网络出错啦！, Toast.LENGTH_LONG).show();
                }
            }
        });
        //初始化
        init();
        //单击事件
        click();
    }

    //* 显示Toast消息
    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    private void click() {
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                requestLocation();
            }
        });

    }
    @Override
    protected void onResume() {
        //MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onResume();
        mBMapManager.start();//重新启动
        super.onResume();
    }
    @Override
    protected void onPause() {
        //MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    private void init() {
        //使用自定义的title，注意顺序
        setContentView(R.layout.activity_main);   //activity的布局                   //这里是添加自己定义的titlebtn.xml

        //通过id找到他们
        mMapView = (com.baidu.mapapi.map.MapView) findViewById(R.id.bmapView);
        mMapController=mMapView.getController(); //获取地图控制器
        mMapController.enableClick(true);   //设置地图是否响应点击事件
        request=(Button)findViewById(R.id.request);


        viewCache = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
        mPopupOverlay= new PopupOverlay(mMapView, new PopupClickListener() {// * 点击弹出窗口图层回调的方法
            @Override
            public void onClickedPopup(int arg0) {
                //隐藏弹出窗口图层
                mPopupOverlay.hidePop();

            }
        });


        mMapController.enableClick(true);                //*  设置地图是否响应点击事件  .
        mMapController.setZoom(12);                 // * 设置地图缩放级别
        mMapView.setBuiltInZoomControls(true);      // * 显示内置缩放控件
        mMapView.setTraffic(true);

        mLocData = new LocationData();

        mLocClient = new LocationClient(getApplicationContext());  //   * 定位SDK的核心类
        //实例化定位服务，LocationClient类必须在主线程中声明
        mLocClient.registerLocationListener(new BDLocationListenerImpl());//注册定位监听接口
        /**
         * 设置定位参数
         */
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开GPRS
        option.setAddrType(all);//返回的定位结果包含地址信息
        option.setCoorType(bd09ll);//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); //设置发起定位请求的间隔时间为5000ms
        option.disableCache(false);//禁止启用缓存定位
        option.setPoiNumber(5);    //最多返回POI个数
        option.setPoiDistance(1000); //poi查询距离
        option.setPoiExtraInfo(true);  //是否需要POI的电话和地址等详细信息

        mLocClient.setLocOption(option);
        mLocClient.start();  // 调用此方法开始定位

        myLocationOverlay = new LocationOverlay(mMapView);//定位图层初始化

        //将定位数据设置到定位图层里

        myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.set));
        //添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();

        //更新图层数据执行刷新后生效
        mMapView.refresh();

  /*
        //准备要添加的Overlay
        double mLat1 = 39.910159;
        double mLon1 = 119.544697;

        // 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
        GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));

        //准备overlay图像数据，根据实情情况修复
        Drawable mark= getResources().getDrawable(R.drawable.set);
        //用OverlayItem准备Overlay数据
        OverlayItem item1 = new OverlayItem(p1,item1,item1);
        //使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置


        //创建IteminizedOverlay
        CustomItemizedOverlay itemOverlay = new CustomItemizedOverlay(mark, mMapView);
        //将IteminizedOverlay添加到MapView中

        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(itemOverlay);

                //现在所有准备工作已准备好，使用以下方法管理overlay.
                //添加overlay, 当批量添加Overlay时使用addItem(List<overlayitem>)效率更高
        itemOverlay.addItem(item1);
              //删除overlay .
              //itemOverlay.removeItem(itemOverlay.getItem(0));
              //mMapView.refresh();
              //清除overlay
              // itemOverlay.removeAll();
              // mMapView.refresh();
        mMapView.refresh();
   */


        // mMapController.setCenter(p1);

        mMapView.regMapViewListener(mBMapManager, new MKMapViewListener() {
            // * 地图移动完成时会回调此接口 方法
            @Override
            public void onMapMoveFinish() {
                showToast(地图移动完毕！);
            }
            //* 地图加载完毕回调此接口方法
            @Override
            public void onMapLoadFinish() {
                showToast(地图载入完毕！);
            }
            //*  地图完成带动画的操作（如: animationTo()）后，此回调被触发
            @Override
            public void onMapAnimationFinish() {

            }
            //当调用过 mMapView.getCurrentMap()后，此回调会被触发  可在此保存截图至存储设备
            @Override
            public void onGetCurrentMap(Bitmap arg0) {
            }
            //* 点击地图上被标记的点回调此方法
            @Override
            public void onClickMapPoi(MapPoi arg0) {
                if (arg0 != null){
                    showToast(arg0.strText);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CreateMenu(menu);
        return true;
    }

    private void CreateMenu(Menu menu){
        MenuItem mnu1 =menu.add(0,0,0,显示卫星地图);
        {
            mnu1.setAlphabeticShortcut('a');//设置快捷键
            //mnu1.serIcon(R.drawable.icon);//设置图片
        }
        MenuItem mnu2 =menu.add(0,1,1,显示街道地图);
        {
            mnu2.setAlphabeticShortcut('b');//设置快捷键
            //mnu1.serIcon(R.drawable.icon);//设置图片
        }
        MenuItem mnu3 =menu.add(0,2,2,3D地图);
        {
            mnu3.setAlphabeticShortcut('c');//设置快捷键
            //mnu1.serIcon(R.drawable.icon);//设置图片
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 0){
            mMapView.setSatellite(true);               //设置显示为卫星地图：
            mMapView.setTraffic(false);
        }else if(item.getItemId() == 1){
            mMapView.setTraffic(true);                 //显示街道地图
            mMapView.setSatellite(false);
        }else if(item.getItemId() == 2){
            //mMapView.se
        }
        return true;
    }


    public class BDLocationListenerImpl implements BDLocationListener {
        //  * 接收异步返回的定位结果，参数是BDLocation类型参数
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
    /*
            StringBuffer sb = new StringBuffer(256);
              sb.append(time : );
              sb.append(location.getTime());
              sb.append(
error code : );
              sb.append(location.getLocType());
              sb.append(
latitude : );
              sb.append(location.getLatitude());
              sb.append(
lontitude : );
              sb.append(location.getLongitude());
              sb.append(
radius : );
              sb.append(location.getRadius());
              if (location.getLocType() == BDLocation.TypeGpsLocation){
                   sb.append(
speed : );
                   sb.append(location.getSpeed());
                   sb.append(
satellite : );
                   sb.append(location.getSatelliteNumber());
                   } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                   sb.append(
addr : );
                   sb.append(location.getAddrStr());
                }
      */
            MainActivity.this.location = location;

            mLocData.latitude = location.getLatitude();
            mLocData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            mLocData.accuracy = location.getRadius();
            mLocData.direction = location.getDerect();

            //将定位数据设置到定位图层里
            myLocationOverlay.setData(mLocData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();

            if(isFirstLoc || isRequest){
                //将给定的位置点以动画形式移动至地图中心
                mMapController.animateTo(new GeoPoint(
                        (int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6)));
                showPopupOverlay(location);  //载入时候就弹出
                isRequest = false;
            }
            isFirstLoc = false;

        }

        // 接收异步返回的POI查询结果，参数是BDLocation类型参数
        @Override
        public void onReceivePoi(BDLocation poiLocation) {

        }

    }

    private void requestLocation() {
        isRequest = true;
        if(mLocClient != null && mLocClient.isStarted()){
            showToast(正在定位......);
            mLocClient.requestLocation();
        }
    }

    //继承MyLocationOverlay重写dispatchTap方法
    private class LocationOverlay extends MyLocationOverlay{

        public LocationOverlay(MapView arg0) {
            super(arg0);
        }
        //  * 在“我的位置”坐标上处理点击事件。
        @Override
        protected boolean dispatchTap() {
            //点击我的位置显示PopupOverlay
            showPopupOverlay(location);
            return super.dispatchTap();
        }
        @Override
        public void setMarker(Drawable arg0) {
            super.setMarker(arg0);
        }


    }

    @Override
    protected void onDestroy() {
        //MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.destroy();

        //退出应用调用BMapManager的destroy()方法
        if(mBMapManager != null){
            mBMapManager.destroy();
            mBMapManager = null;
        }

        //退出时销毁定位
        if (mLocClient != null){
            mLocClient.stop();
        }

        super.onDestroy();
    }


    //* 显示弹出窗口图层PopupOverlay
    private void showPopupOverlay(BDLocation location){
        TextView popText = ((TextView)viewCache.findViewById(R.id.location_tips));
        popText.setText([我的位置]
                + location.getAddrStr());
        mPopupOverlay.showPopup(getBitmapFromView(popText),
                new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6)),
                15);

    }

    // * 将View转换成Bitmap的方法
    public static Bitmap getBitmapFromView(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


}
</overlayitem>
