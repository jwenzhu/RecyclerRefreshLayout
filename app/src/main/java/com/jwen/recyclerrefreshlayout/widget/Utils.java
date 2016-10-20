package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * author: Jwen
 * date:2016-10-20.
 */
public class Utils {

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


}
