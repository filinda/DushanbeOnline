package yandex_map_2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.yandex.runtime.NativeObject;
import com.yandex.runtime.graphics.GLDebugBinding;
import com.yandex.runtime.view.PlatformGLView;
import com.yandex.runtime.view.internal.EGLConfigChooserImpl;
import com.yandex.runtime.view.internal.GLContextFactory;
import com.yandex.runtime.view.internal.PlatformGLRenderer;
import com.yandex.runtime.view.internal.PlatformViewBinding;
import com.yandex.runtime.view.internal.RenderDelegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.ECLAIR)
public class PlatformGLSurfaceView2 extends GLSurfaceView implements RenderDelegate, PlatformGLView, PlatformGLRenderer.GLContextListener {

    private static final String LOG_TAG = "PlatformGLSurfaceView";
    private boolean glDebugEnabled_;
    private PlatformViewBinding platformViewBinding_;
    private int width_;
    private int height_;
    private ArrayList<OnTouchListener> touchListeners = new ArrayList<>();


    public void addOnTouchListener(OnTouchListener l){
        touchListeners.add(l);
    }

    public PlatformGLSurfaceView2(Context context) {
        this(context, (AttributeSet)null, 0, false);
    }

    public PlatformGLSurfaceView2(Context context, boolean glDebugEnabled) {
        this(context, (AttributeSet)null, 0, glDebugEnabled);
    }

    public PlatformGLSurfaceView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0, false);
    }

    public PlatformGLSurfaceView2(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, false);
    }

    public PlatformGLSurfaceView2(Context context, AttributeSet attrs, int defStyle, boolean glDebugEnabled) {
        super(context, attrs);
        this.glDebugEnabled_ = glDebugEnabled;
        DisplayMetrics metrics = new DisplayMetrics();
        Context applicationContext = this.getContext().getApplicationContext();
        WindowManager manager = (WindowManager)applicationContext.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        this.width_ = metrics.widthPixels;
        this.height_ = metrics.heightPixels;
        EGLConfigChooserImpl configChooser = new EGLConfigChooserImpl();
        this.setEGLConfigChooser(configChooser);
        this.setEGLContextFactory(new GLContextFactory(this.glDebugEnabled_, configChooser));
        this.platformViewBinding_ = new PlatformViewBinding(this, this.width_, this.height_);
        this.setRenderer(new PlatformGLRenderer(this.platformViewBinding_, this));
        this.setRenderMode(0);
    }

    public NativeObject getNativePlatformView() {
        return this.platformViewBinding_.getNative();
    }

    public void destroyNativePlatformView() {
        this.platformViewBinding_.destroyNative();
    }

    public View getView() {
        return this;
    }

    public void pause() {
        this.platformViewBinding_.onPause();
    }

    public void resume() {
        this.platformViewBinding_.onResume();
    }

    public void stop() {
        this.platformViewBinding_.onStop();
        this.onPause();
    }

    public void start() {
        this.onResume();
        this.platformViewBinding_.onStart(this.width_, this.height_);
    }

    public void onMemoryWarning() {
        this.platformViewBinding_.onMemoryWarning();
    }

    public void setNoninteractive(boolean is) {
        this.platformViewBinding_.setNoninteractive(is);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        for(OnTouchListener listener : touchListeners){
            listener.onTouch(this,event);
        }
        return this.platformViewBinding_.onTouchEvent(event) ? true : super.dispatchTouchEvent(event);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width_ = w;
        this.height_ = h;
        super.onSizeChanged(w, h, oldw, oldh);
        this.platformViewBinding_.onSizeChanged(this.width_, this.height_);
    }

    public void onContextCreated() {
        this.handlePreserveEGLContextOnPause();
        if (this.glDebugEnabled_) {
            GLDebugBinding.enable();
        }

    }

    private void handlePreserveEGLContextOnPause() {
        if (Build.VERSION.SDK_INT >= 11) {
            String vendor = GLES20.glGetString(7936);
            if (vendor == null || !vendor.toUpperCase().contains("NVIDIA")) {
                try {
                    Method m = GLSurfaceView.class.getMethod("setPreserveEGLContextOnPause", Boolean.TYPE);
                    m.invoke(this, true);
                } catch (SecurityException var4) {
                    Log.e("PlatformGLSurfaceView", "error of calling setPreserveEGLContextOnPause", var4);
                } catch (NoSuchMethodException var5) {
                    Log.e("PlatformGLSurfaceView", "error of calling setPreserveEGLContextOnPause", var5);
                } catch (IllegalArgumentException var6) {
                    Log.e("PlatformGLSurfaceView", "error of calling setPreserveEGLContextOnPause", var6);
                } catch (IllegalAccessException var7) {
                    Log.e("PlatformGLSurfaceView", "error of calling setPreserveEGLContextOnPause", var7);
                } catch (InvocationTargetException var8) {
                    Log.e("PlatformGLSurfaceView", "error of calling setPreserveEGLContextOnPause", var8);
                }

            }
        }
    }

}
