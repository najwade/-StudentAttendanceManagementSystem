package com.example.gesionabsence;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utils {
    public static int px(float dp, Context context){
        return ((int)(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
    }
    public static int dp(float px, Context context){
        return (int)(px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
