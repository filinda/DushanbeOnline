package yandex_map_2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.yandex.runtime.view.PlatformGLTextureView;
import com.yandex.runtime.view.PlatformGLView;
import com.yandex.runtime.view.PlatformGLViewFactory;

import java.util.HashSet;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.ECLAIR)

public class PlatformGLVIewFactory2 {

    public static PlatformGLView getPlatformGLView(Context context, Set<PlatformGLViewFactory.Attribute> attrs) {
        Set<PlatformGLViewFactory.Attribute> notNullAttrs = attrs != null ? attrs : new HashSet();
        PlatformGLView result = null;
        if (((Set)notNullAttrs).contains(PlatformGLViewFactory.Attribute.MOVABLE)) {
            result = new PlatformGLTextureView(context, ((Set)notNullAttrs).contains(PlatformGLViewFactory.Attribute.GL_DEBUG));
        } else {
            result = new PlatformGLSurfaceView2(context, ((Set)notNullAttrs).contains(PlatformGLViewFactory.Attribute.GL_DEBUG));
        }

        if (((Set)notNullAttrs).contains(PlatformGLViewFactory.Attribute.NONINTERACTIVE)) {
            ((PlatformGLView)result).setNoninteractive(true);
        }

        return (PlatformGLView)result;
    }

    public static final Set<PlatformGLViewFactory.Attribute> convertAttributeSet(Context context, AttributeSet attrs) {
        Set<PlatformGLViewFactory.Attribute> result = new HashSet();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, com.yandex.runtime.R.styleable.PlatformView, 0, 0);

        try {
            if (a.getBoolean(com.yandex.runtime.R.styleable.PlatformView_movable, false)) {
                result.add(PlatformGLViewFactory.Attribute.MOVABLE);
            }

            if (a.getBoolean(com.yandex.runtime.R.styleable.PlatformView_noninteractive, false)) {
                result.add(PlatformGLViewFactory.Attribute.NONINTERACTIVE);
            }

            if (a.getBoolean(com.yandex.runtime.R.styleable.PlatformView_gldebug, false)) {
                result.add(PlatformGLViewFactory.Attribute.GL_DEBUG);
            }
        } finally {
            a.recycle();
        }

        return result;
    }

    public static enum Attribute {
        MOVABLE,
        NONINTERACTIVE,
        GL_DEBUG;

        private Attribute() {
        }
    }
}
