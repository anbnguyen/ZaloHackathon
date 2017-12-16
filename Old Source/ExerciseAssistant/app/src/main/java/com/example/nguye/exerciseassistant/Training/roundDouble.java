package com.example.nguye.exerciseassistant.Training;

import android.icu.text.DecimalFormat;

/**
 * Created by nguye on 3/3/2017.
 */

public class roundDouble {
    static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
