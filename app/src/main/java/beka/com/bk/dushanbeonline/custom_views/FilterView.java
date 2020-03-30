package beka.com.bk.dushanbeonline.custom_views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import java.util.Date;

import beka.com.bk.dushanbeonline.DateTime;

public class FilterView extends FrameLayout {

    int categoryId=-1;
    int subCategoryId=-1;
    int subSubCategoryId=-1;
    Date start=null, end=null;
    int lowPrice=-1;
    int highPrice=-1;

    public FilterView(@NonNull Context context) {
        super(context);
    }

}
