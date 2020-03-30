package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

public class MapEditText extends FrameLayout {

    TextBox editText;

    public MapEditText(@NonNull Context context, float scale) {
        super(context);
        editText = new TextBox(context, scale);
        editText.setSize(800 * scale, 100 * scale);
        editText.setX(280 * scale);
    }
}
