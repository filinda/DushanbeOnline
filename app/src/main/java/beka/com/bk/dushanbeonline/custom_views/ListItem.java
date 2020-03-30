package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

public class ListItem extends FrameLayout {
    String header;
    public ListItem(@NonNull Context context, String header) {
        super(context);
        this.header = header;
    }
}
