package yandex_map_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.ScreenRect;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapWindow;
import com.yandex.mapkit.map.PointOfView;
import com.yandex.mapkit.map.SizeChangedListener;
import com.yandex.mapkit.map.VisibleRegion;
import com.yandex.mapkit.map.internal.MapWindowBinding;
import com.yandex.runtime.view.PlatformGLTextureView;
import com.yandex.runtime.view.PlatformGLView;
import com.yandex.runtime.view.PlatformGLViewFactory;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MapView2 extends RelativeLayout {
    private PlatformGLView platformGLView;
    private MapWindowBinding mapWindow;

    public MapView2(Context context) {
        this(context, (AttributeSet)null, 0);
    }

    public MapView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode()) {
            MapKitFactory.initialize(context);
            this.platformGLView = PlatformGLVIewFactory2.getPlatformGLView(context, PlatformGLViewFactory.convertAttributeSet(context, attrs));
            this.init(context);
        }

    }

    public void addOnTouchListener(OnTouchListener listener){
        ((PlatformGLSurfaceView2)platformGLView).addOnTouchListener(listener);
    }

    private void init(Context context) {
        this.mapWindow = (MapWindowBinding)MapKitFactory.getInstance().createMapWindow(this.platformGLView);
        this.addView(this.platformGLView.getView(), new LayoutParams(-1, -1));
    }

    public int width() {
        return this.mapWindow.width();
    }

    public int height() {
        return this.mapWindow.height();
    }

    public float getScaleFactor() {
        return this.mapWindow.getScaleFactor();
    }

    public void setScaleFactor(float scaleFactor) {
        this.mapWindow.setScaleFactor(scaleFactor);
    }

    public void startPerformanceMetricsCapture() {
        this.mapWindow.startPerformanceMetricsCapture();
    }

    public String stopPerformanceMetricsCapture() {
        return this.mapWindow.stopPerformanceMetricsCapture();
    }

    public void setMaxFps(float fps) {
        this.mapWindow.setMaxFps(fps);
    }

    public Map getMap() {
        return this.mapWindow.getMap();
    }

    public ScreenPoint worldToScreen(Point worldPoint) {
        return this.mapWindow.worldToScreen(worldPoint);
    }

    public Point screenToWorld(ScreenPoint screenPoint) {
        return this.mapWindow.screenToWorld(screenPoint);
    }

    public ScreenRect getFocusRect() {
        return this.mapWindow.getFocusRect();
    }

    public void setFocusRect(ScreenRect rect) {
        this.mapWindow.setFocusRect(rect);
    }

    public ScreenPoint getZoomFocusPoint() {
        return this.mapWindow.getZoomFocusPoint();
    }

    public void setZoomFocusPoint(ScreenPoint zoomFocusPoint) {
        this.mapWindow.setZoomFocusPoint(zoomFocusPoint);
    }

    public PointOfView getPointOfView() {
        return this.mapWindow.getPointOfView();
    }

    public void setPointOfView(PointOfView pointOfView) {
        this.mapWindow.setPointOfView(pointOfView);
    }

    public double getFieldOfViewY() {
        return this.mapWindow.getFieldOfViewY();
    }

    public void setFieldOfViewY(double fovY) {
        this.mapWindow.setFieldOfViewY(fovY);
    }

    public VisibleRegion getFocusRegion() {
        return this.mapWindow.getFocusRegion();
    }

    public void addSizeChangedListener(SizeChangedListener listener) {
        this.mapWindow.addSizeChangedListener(listener);
    }

    public void removeSizeChangedListener(SizeChangedListener listener) {
        this.mapWindow.removeSizeChangedListener(listener);
    }

    public void setNoninteractive(boolean is) {
        this.platformGLView.setNoninteractive(is);
    }

    public boolean isValid() {
        return this.mapWindow.isValid();
    }

    public void onStop() {
        this.platformGLView.pause();
        this.platformGLView.stop();
    }

    public void onStart() {
        this.platformGLView.start();
        this.platformGLView.resume();
    }


    public Bitmap getScreenshot() {
        if (this.platformGLView instanceof PlatformGLTextureView) {
            PlatformGLTextureView textureView = (PlatformGLTextureView)this.platformGLView;
            return textureView.getBitmap();
        } else {
            return null;
        }
    }

    public void onMemoryWarning() {
        this.platformGLView.onMemoryWarning();
    }

    public MapWindow getMapWindow() {
        return this.mapWindow;
    }
}