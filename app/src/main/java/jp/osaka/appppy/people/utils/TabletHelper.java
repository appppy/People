package jp.osaka.appppy.people.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * テーブルヘルパ
 */

public class TabletHelper {

    /**
     * ハニーコンボの有無
     *
     * @return ハニーコンボ
     */
    private static boolean isHoneycomb() {
        return true;
    }

    /**
     * ハニーコンボタブレットの有無
     *
     * @return ハニーコンボタブレット
     */
    public static boolean isHoneycombTablet(Context context) {
        if(context == null) {
            return false;
        }
        if(context.getResources() == null) {
            return false;
        }
        if(context.getResources().getConfiguration() == null) {
            return false;
        }
        return isHoneycomb() && (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

}
