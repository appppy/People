package jp.osaka.appppy.people.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.constants.COLOR;


/**
 * テーマヘルパ
 */

public class ThemeHelper {

    /**
     * マージンの設定
     *
     * @param view   画面
     * @param margin マージン
     */
    public static void setMargins(ImageView view, int margin) {
        try {
            // ImageViewからマージンを取得
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            // 移動させたい距離に変更
            lp.setMargins(margin, margin, margin, margin);
            // ImageViewへ反映
            view.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getImageColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result = ContextCompat.getColor(context, R.color.grey_300);
        try {
            switch (color) {
                case RED: {
                    result = ContextCompat.getColor(context, R.color.red_600);
                    break;
                }
                case PINK: {
                    result = ContextCompat.getColor(context, R.color.pink_600);
                    break;
                }
                case PURPLE: {
                    result = ContextCompat.getColor(context, R.color.purple_600);
                    break;
                }
                case DEEP_PURPLE: {
                    result = ContextCompat.getColor(context, R.color.deep_purple_600);
                    break;
                }
                case INDIGO: {
                    result = ContextCompat.getColor(context, R.color.indigo_600);
                    break;
                }
                case BLUE: {
                    result = ContextCompat.getColor(context, R.color.blue_600);
                    break;
                }
                case LIGHT_BLUE: {
                    result = ContextCompat.getColor(context, R.color.light_blue_600);
                    break;
                }
                case CYAN: {
                    result = ContextCompat.getColor(context, R.color.cyan_600);
                    break;
                }
                case TEAL: {
                    result = ContextCompat.getColor(context, R.color.teal_600);
                    break;
                }
                case GREEN: {
                    result = ContextCompat.getColor(context, R.color.green_600);
                    break;
                }
                case LIGHT_GREEN: {
                    result = ContextCompat.getColor(context, R.color.light_green_600);
                    break;
                }
                case LIME: {
                    result = ContextCompat.getColor(context, R.color.lime_600);
                    break;
                }
                case YELLOW: {
                    result = ContextCompat.getColor(context, R.color.yellow_600);
                    break;
                }
                case AMBER: {
                    result = ContextCompat.getColor(context, R.color.amber_600);
                    break;
                }
                case ORANGE: {
                    result = ContextCompat.getColor(context, R.color.orange_600);
                    break;
                }
                case DEEP_ORANGE: {
                    result = ContextCompat.getColor(context, R.color.deep_orange_600);
                    break;
                }
                case BROWN: {
                    result = ContextCompat.getColor(context, R.color.brown_600);
                    break;
                }
                case BLUE_GREY:
                case GREY: {
                    result = ContextCompat.getColor(context, R.color.blue_grey_600);
                    break;
                }
                default: {
                    result = ContextCompat.getColor(context, R.color.grey_300);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getImagePrimaryColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result = ContextCompat.getColor(context, R.color.grey_200);
        try {
            switch (color) {
                default:
                case WHITE: {
                    result = ContextCompat.getColor(context, R.color.grey_200);
                    break;
                }
                case RED: {
                    result = ContextCompat.getColor(context, R.color.red_200);
                    break;
                }
                case PINK: {
                    result = ContextCompat.getColor(context, R.color.pink_200);
                    break;
                }
                case PURPLE: {
                    result = ContextCompat.getColor(context, R.color.purple_200);
                    break;
                }
                case DEEP_PURPLE: {
                    result = ContextCompat.getColor(context, R.color.deep_purple_200);
                    break;
                }
                case INDIGO: {
                    result = ContextCompat.getColor(context, R.color.indigo_200);
                    break;
                }
                case BLUE: {
                    result = ContextCompat.getColor(context, R.color.blue_200);
                    break;
                }
                case LIGHT_BLUE: {
                    result = ContextCompat.getColor(context, R.color.light_blue_200);
                    break;
                }
                case CYAN: {
                    result = ContextCompat.getColor(context, R.color.cyan_200);
                    break;
                }
                case TEAL: {
                    result = ContextCompat.getColor(context, R.color.teal_200);
                    break;
                }
                case GREEN: {
                    result = ContextCompat.getColor(context, R.color.green_200);
                    break;
                }
                case LIGHT_GREEN: {
                    result = ContextCompat.getColor(context, R.color.light_green_200);
                    break;
                }
                case LIME: {
                    result = ContextCompat.getColor(context, R.color.lime_200);
                    break;
                }
                case YELLOW: {
                    result = ContextCompat.getColor(context, R.color.yellow_200);
                    break;
                }
                case AMBER: {
                    result = ContextCompat.getColor(context, R.color.amber_200);
                    break;
                }
                case ORANGE: {
                    result = ContextCompat.getColor(context, R.color.orange_200);
                    break;
                }
                case DEEP_ORANGE: {
                    result = ContextCompat.getColor(context, R.color.deep_orange_200);
                    break;
                }
                case BROWN: {
                    result = ContextCompat.getColor(context, R.color.brown_200);
                    break;
                }
                case BLUE_GREY: {
                    result = ContextCompat.getColor(context, R.color.blue_grey_200);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getImagePrimaryDarkColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result = ContextCompat.getColor(context, R.color.grey_400);
        try {
            switch (color) {
                default:
                case WHITE: {
                    result = ContextCompat.getColor(context, R.color.grey_400);
                    break;
                }
                case RED: {
                    result = ContextCompat.getColor(context, R.color.red_400);
                    break;
                }
                case PINK: {
                    result = ContextCompat.getColor(context, R.color.pink_400);
                    break;
                }
                case PURPLE: {
                    result = ContextCompat.getColor(context, R.color.purple_400);
                    break;
                }
                case DEEP_PURPLE: {
                    result = ContextCompat.getColor(context, R.color.deep_purple_400);
                    break;
                }
                case INDIGO: {
                    result = ContextCompat.getColor(context, R.color.indigo_400);
                    break;
                }
                case BLUE: {
                    result = ContextCompat.getColor(context, R.color.blue_400);
                    break;
                }
                case LIGHT_BLUE: {
                    result = ContextCompat.getColor(context, R.color.light_blue_400);
                    break;
                }
                case CYAN: {
                    result = ContextCompat.getColor(context, R.color.cyan_400);
                    break;
                }
                case TEAL: {
                    result = ContextCompat.getColor(context, R.color.teal_400);
                    break;
                }
                case GREEN: {
                    result = ContextCompat.getColor(context, R.color.green_400);
                    break;
                }
                case LIGHT_GREEN: {
                    result = ContextCompat.getColor(context, R.color.light_green_400);
                    break;
                }
                case LIME: {
                    result = ContextCompat.getColor(context, R.color.lime_400);
                    break;
                }
                case YELLOW: {
                    result = ContextCompat.getColor(context, R.color.yellow_400);
                    break;
                }
                case AMBER: {
                    result = ContextCompat.getColor(context, R.color.amber_400);
                    break;
                }
                case ORANGE: {
                    result = ContextCompat.getColor(context, R.color.orange_400);
                    break;
                }
                case DEEP_ORANGE: {
                    result = ContextCompat.getColor(context, R.color.deep_orange_400);
                    break;
                }
                case BROWN: {
                    result = ContextCompat.getColor(context, R.color.brown_400);
                    break;
                }
                case BLUE_GREY: {
                    result = ContextCompat.getColor(context, R.color.blue_grey_400);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getAccentColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result =ContextCompat.getColor(context, R.color.grey_600);
        try {
            switch (color) {
                default:
                case WHITE: {
                    //result = ContextCompat.getModuleColor(context, R.color.grey_600);
                    result = ContextCompat.getColor(context, R.color.grey_600);
                    break;
                }
                case RED: {
                    //result = ContextCompat.getModuleColor(context, R.color.red_600);
                    result = ContextCompat.getColor(context, R.color.teal_600);
                    break;
                }
                case PINK: {
                    //result = ContextCompat.getModuleColor(context, R.color.pink_600);
                    result = ContextCompat.getColor(context, R.color.lime_600);
                    break;
                }
                case PURPLE: {
                    //result = ContextCompat.getModuleColor(context, R.color.purple_600);
                    result = ContextCompat.getColor(context, R.color.light_green_600);
                    break;
                }
                case DEEP_PURPLE: {
                    //result = ContextCompat.getModuleColor(context, R.color.deep_purple_600);
                    result = ContextCompat.getColor(context, R.color.amber_600);
                    break;
                }
                case INDIGO: {
                    //result = ContextCompat.getModuleColor(context, R.color.indigo_600);
                    result = ContextCompat.getColor(context, R.color.yellow_600);
                    break;
                }
                case BLUE: {
                    //result = ContextCompat.getModuleColor(context, R.color.blue_600);
                    result = ContextCompat.getColor(context, R.color.orange_600);
                    break;
                }
                case LIGHT_BLUE: {
                    //result = ContextCompat.getModuleColor(context, R.color.light_blue_600);
                    result = ContextCompat.getColor(context, R.color.blue_grey_600);
                    break;
                }
                case CYAN: {
                    //result = ContextCompat.getModuleColor(context, R.color.cyan_600);
                    result = ContextCompat.getColor(context, R.color.deep_orange_600);
                    break;
                }
                case TEAL: {
                    //result = ContextCompat.getModuleColor(context, R.color.teal_600);
                    result = ContextCompat.getColor(context, R.color.red_600);
                    break;
                }
                case GREEN: {
                    //result = ContextCompat.getModuleColor(context, R.color.green_600);
                    result = ContextCompat.getColor(context, R.color.brown_600);
                    break;
                }
                case LIGHT_GREEN: {
                    //result = ContextCompat.getModuleColor(context, R.color.light_green_600);
                    result = ContextCompat.getColor(context, R.color.purple_600);
                    break;
                }
                case LIME: {
                    //result = ContextCompat.getModuleColor(context, R.color.lime_600);
                    result = ContextCompat.getColor(context, R.color.pink_600);
                    break;
                }
                case YELLOW: {
                    //result = ContextCompat.getModuleColor(context, R.color.yellow_600);
                    result = ContextCompat.getColor(context, R.color.indigo_600);
                    break;
                }
                case AMBER: {
                    //result = ContextCompat.getModuleColor(context, R.color.amber_600);
                    result = ContextCompat.getColor(context, R.color.deep_purple_600);
                    break;
                }
                case ORANGE: {
                    //result = ContextCompat.getModuleColor(context, R.color.orange_600);
                    result = ContextCompat.getColor(context, R.color.blue_600);
                    break;
                }
                case DEEP_ORANGE: {
                    //result = ContextCompat.getModuleColor(context, R.color.deep_orange_600);
                    result = ContextCompat.getColor(context, R.color.cyan_600);
                    break;
                }
                case BROWN: {
                    //result = ContextCompat.getModuleColor(context, R.color.brown_600);
                    result = ContextCompat.getColor(context, R.color.green_600);
                    break;
                }
                case BLUE_GREY: {
                    //result = ContextCompat.getModuleColor(context, R.color.blue_grey_600);
                    result = ContextCompat.getColor(context, R.color.light_blue_600);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getModuleColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result = ContextCompat.getColor(context, R.color.grey_200);
        try {
            switch (color) {
                default:
                case WHITE: {
                    result = ContextCompat.getColor(context, R.color.grey_200);
                    break;
                }
                case RED: {
                    result = ContextCompat.getColor(context, R.color.red_200);
                    break;
                }
                case PINK: {
                    result = ContextCompat.getColor(context, R.color.pink_200);
                    break;
                }
                case PURPLE: {
                    result = ContextCompat.getColor(context, R.color.purple_200);
                    break;
                }
                case DEEP_PURPLE: {
                    result = ContextCompat.getColor(context, R.color.deep_purple_200);
                    break;
                }
                case INDIGO: {
                    result = ContextCompat.getColor(context, R.color.indigo_200);
                    break;
                }
                case BLUE: {
                    result = ContextCompat.getColor(context, R.color.blue_200);
                    break;
                }
                case LIGHT_BLUE: {
                    result = ContextCompat.getColor(context, R.color.light_blue_200);
                    break;
                }
                case CYAN: {
                    result = ContextCompat.getColor(context, R.color.cyan_200);
                    break;
                }
                case TEAL: {
                    result = ContextCompat.getColor(context, R.color.teal_200);
                    break;
                }
                case GREEN: {
                    result = ContextCompat.getColor(context, R.color.green_200);
                    break;
                }
                case LIGHT_GREEN: {
                    result = ContextCompat.getColor(context, R.color.light_green_200);
                    break;
                }
                case LIME: {
                    result = ContextCompat.getColor(context, R.color.lime_200);
                    break;
                }
                case YELLOW: {
                    result = ContextCompat.getColor(context, R.color.yellow_200);
                    break;
                }
                case AMBER: {
                    result = ContextCompat.getColor(context, R.color.amber_200);
                    break;
                }
                case ORANGE: {
                    result = ContextCompat.getColor(context, R.color.orange_200);
                    break;
                }
                case DEEP_ORANGE: {
                    result = ContextCompat.getColor(context, R.color.deep_orange_200);
                    break;
                }
                case BROWN: {
                    result = ContextCompat.getColor(context, R.color.brown_200);
                    break;
                }
                case BLUE_GREY: {
                    result = ContextCompat.getColor(context, R.color.blue_grey_200);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getSelectedModuleColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        return getModuleColor(context, color);
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getLineColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        return ContextCompat.getColor(context, R.color.grey_100);
    }

    /**
     * 色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 色
     */
    public static int getSelectedLineColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        return ContextCompat.getColor(context, R.color.grey_300);
    }

    /**
     * 画像取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 画像
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getModuleDrawable(Context context, COLOR color) {
        if (context == null || color == null) {
            return null;
        }
        if (context.getResources() == null) {
            return null;
        }
        Drawable result = context.getResources().getDrawable(R.drawable.ripple_background_white, context.getTheme());
        try {
            switch (color) {
                default:
                case WHITE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_white, context.getTheme());
                    break;
                }
                case RED: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_red, context.getTheme());
                    break;
                }
                case PINK: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_pink, context.getTheme());
                    break;
                }
                case PURPLE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_purple, context.getTheme());
                    break;
                }
                case DEEP_PURPLE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_deep_purple, context.getTheme());
                    break;
                }
                case INDIGO: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_indigo, context.getTheme());
                    break;
                }
                case BLUE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_blue, context.getTheme());
                    break;
                }
                case LIGHT_BLUE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_light_blue, context.getTheme());
                    break;
                }
                case CYAN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_cyan, context.getTheme());
                    break;
                }
                case TEAL: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_teal, context.getTheme());
                    break;
                }
                case GREEN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_green, context.getTheme());
                    break;
                }
                case LIGHT_GREEN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_light_green, context.getTheme());
                    break;
                }
                case LIME: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_lime, context.getTheme());
                    break;
                }
                case YELLOW: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_yellow, context.getTheme());
                    break;
                }
                case AMBER: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_amber, context.getTheme());
                    break;
                }
                case ORANGE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_orange, context.getTheme());
                    break;
                }
                case DEEP_ORANGE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_deep_orange, context.getTheme());
                    break;
                }
                case BROWN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_brown, context.getTheme());
                    break;
                }
                case BLUE_GREY: {
                    result = context.getResources().getDrawable(R.drawable.ripple_background_blue_grey, context.getTheme());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 画像取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 画像
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getLineDrawable(Context context, COLOR color) {
        if (context == null || color == null) {
            return null;
        }
        if (context.getResources() == null) {
            return null;
        }
        return context.getResources().getDrawable(R.drawable.ripple_background_white, context.getTheme());
    }

    /**
     * 画像取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 画像
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getSelectedModuleDrawable(Context context, COLOR color) {
        if (context == null || color == null) {
            return null;
        }
        if (context.getResources() == null) {
            return null;
        }
        Drawable result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_white, context.getTheme());
        try {
            switch (color) {
                default:
                case WHITE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_white, context.getTheme());
                    break;
                }
                case RED: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_red, context.getTheme());
                    break;
                }
                case PINK: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_pink, context.getTheme());
                    break;
                }
                case PURPLE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_purple, context.getTheme());
                    break;
                }
                case DEEP_PURPLE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_deep_purple, context.getTheme());
                    break;
                }
                case INDIGO: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_indigo, context.getTheme());
                    break;
                }
                case BLUE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_blue, context.getTheme());
                    break;
                }
                case LIGHT_BLUE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_light_blue, context.getTheme());
                    break;
                }
                case CYAN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_cyan, context.getTheme());
                    break;
                }
                case TEAL: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_teal, context.getTheme());
                    break;
                }
                case GREEN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_green, context.getTheme());
                    break;
                }
                case LIGHT_GREEN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_light_green, context.getTheme());
                    break;
                }
                case LIME: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_lime, context.getTheme());
                    break;
                }
                case YELLOW: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_yellow, context.getTheme());
                    break;
                }
                case AMBER: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_amber, context.getTheme());
                    break;
                }
                case ORANGE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_orange, context.getTheme());
                    break;
                }
                case DEEP_ORANGE: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_deep_orange, context.getTheme());
                    break;
                }
                case BROWN: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_brown, context.getTheme());
                    break;
                }
                case BLUE_GREY: {
                    result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_blue_grey, context.getTheme());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 画像取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 画像
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getSelectedLineDrawable(Context context, COLOR color) {
        if (context == null || color == null) {
            return null;
        }
        if (context.getResources() == null) {
            return null;
        }
        return context.getResources().getDrawable(R.drawable.ripple_selected_line_background, context.getTheme());
    }

    /**
     * 背景の設定
     *
     * @param context コンテキスト
     * @param view    表示
     * @param color   色
     */
    public static void setModuleBackground(Context context, View view, COLOR color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getModuleDrawable(context, color));
            } else {
                view.setBackgroundColor(getModuleColor(context, color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * テーマ取得
     *
     * @return テーマ
     */
    public static int getImageCreateTheme() {
        int result;
        result = R.style.AppTheme_Detail_Grey;
        return result;
    }

    /**
     * テーマ取得
     *
     * @return テーマ
     */
    public static int getImageDetailTheme() {
        int result;
        result = R.style.AppTheme_Detail_Grey;
        return result;
    }

    /**
     * テーマ取得
     *
     * @param color 色
     * @return テーマ
     */
    public static int getImageEditTheme(COLOR color) {
        int result = R.style.AppTheme_Detail_Grey;
        try {
            switch (color) {
                case RED: {
                    result = R.style.AppTheme_Detail_Red;
                    break;
                }
                case PINK: {
                    result = R.style.AppTheme_Detail_Pink;
                    break;
                }
                case PURPLE: {
                    result = R.style.AppTheme_Detail_Purple;
                    break;
                }
                case DEEP_PURPLE: {
                    result = R.style.AppTheme_Detail_DeepPurple;
                    break;
                }
                case INDIGO: {
                    result = R.style.AppTheme_Detail_Indigo;
                    break;
                }
                case BLUE: {
                    result = R.style.AppTheme_Detail_Blue;
                    break;
                }
                case LIGHT_BLUE: {
                    result = R.style.AppTheme_Detail_LightBlue;
                    break;
                }
                case CYAN: {
                    result = R.style.AppTheme_Detail_Cyan;
                    break;
                }
                case TEAL: {
                    result = R.style.AppTheme_Detail_Teal;
                    break;
                }
                case GREEN: {
                    result = R.style.AppTheme_Detail_Green;
                    break;
                }
                case LIGHT_GREEN: {
                    result = R.style.AppTheme_Detail_LightGreen;
                    break;
                }
                case LIME: {
                    result = R.style.AppTheme_Detail_Lime;
                    break;
                }
                case YELLOW: {
                    result = R.style.AppTheme_Detail_Yellow;
                    break;
                }
                case AMBER: {
                    result = R.style.AppTheme_Detail_Amber;
                    break;
                }
                case ORANGE: {
                    result = R.style.AppTheme_Detail_Orange;
                    break;
                }
                case DEEP_ORANGE: {
                    result = R.style.AppTheme_Detail_DeepOrange;
                    break;
                }
                case BROWN: {
                    result = R.style.AppTheme_Detail_Brown;
                    break;
                }
                case BLUE_GREY: {
                    result = R.style.AppTheme_Detail_BlueGrey;
                    break;
                }
                default:
                case GREY: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * テーマ取得
     *
     * @param color 色
     * @return テーマ
     */
    public static int getDetailTheme(COLOR color) {
        int result = R.style.AppTheme_Light_Detail_White;
        try {
            switch (color) {
                default:
                case WHITE: {
                    break;
                }
                case RED: {
                    result = R.style.AppTheme_Light_Detail_Red;
                    break;
                }
                case PINK: {
                    result = R.style.AppTheme_Light_Detail_Pink;
                    break;
                }
                case PURPLE: {
                    result = R.style.AppTheme_Light_Detail_Purple;
                    break;
                }
                case DEEP_PURPLE: {
                    result = R.style.AppTheme_Light_Detail_DeepPurple;
                    break;
                }
                case INDIGO: {
                    result = R.style.AppTheme_Light_Detail_Indigo;
                    break;
                }
                case BLUE: {
                    result = R.style.AppTheme_Light_Detail_Blue;
                    break;
                }
                case LIGHT_BLUE: {
                    result = R.style.AppTheme_Light_Detail_LightBlue;
                    break;
                }
                case CYAN: {
                    result = R.style.AppTheme_Light_Detail_Cyan;
                    break;
                }
                case TEAL: {
                    result = R.style.AppTheme_Light_Detail_Teal;
                    break;
                }
                case GREEN: {
                    result = R.style.AppTheme_Light_Detail_Green;
                    break;
                }
                case LIGHT_GREEN: {
                    result = R.style.AppTheme_Light_Detail_LightGreen;
                    break;
                }
                case LIME: {
                    result = R.style.AppTheme_Light_Detail_Lime;
                    break;
                }
                case YELLOW: {
                    result = R.style.AppTheme_Light_Detail_Yellow;
                    break;
                }
                case AMBER: {
                    result = R.style.AppTheme_Light_Detail_Amber;
                    break;
                }
                case ORANGE: {
                    result = R.style.AppTheme_Light_Detail_Orange;
                    break;
                }
                case DEEP_ORANGE: {
                    result = R.style.AppTheme_Light_Detail_DeepOrange;
                    break;
                }
                case BROWN: {
                    result = R.style.AppTheme_Light_Detail_Brown;
                    break;
                }
                case BLUE_GREY: {
                    result = R.style.AppTheme_Light_Detail_BlueGrey;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * テーマ取得
     *
     * @param color 色
     * @return テーマ
     */
    public static int getDetailTheme_DarkAppBar(COLOR color) {
        int result = R.style.AppTheme_Detail_Grey;
        try {
            switch (color) {
                case RED: {
                    result = R.style.AppTheme_Detail_Red;
                    break;
                }
                case PINK: {
                    result = R.style.AppTheme_Detail_Pink;
                    break;
                }
                case PURPLE: {
                    result = R.style.AppTheme_Detail_Purple;
                    break;
                }
                case DEEP_PURPLE: {
                    result = R.style.AppTheme_Detail_DeepPurple;
                    break;
                }
                case INDIGO: {
                    result = R.style.AppTheme_Detail_Indigo;
                    break;
                }
                case BLUE: {
                    result = R.style.AppTheme_Detail_Blue;
                    break;
                }
                case LIGHT_BLUE: {
                    result = R.style.AppTheme_Detail_LightBlue;
                    break;
                }
                case CYAN: {
                    result = R.style.AppTheme_Detail_Cyan;
                    break;
                }
                case TEAL: {
                    result = R.style.AppTheme_Detail_Teal;
                    break;
                }
                case GREEN: {
                    result = R.style.AppTheme_Detail_Green;
                    break;
                }
                case LIGHT_GREEN: {
                    result = R.style.AppTheme_Detail_LightGreen;
                    break;
                }
                case LIME: {
                    result = R.style.AppTheme_Detail_Lime;
                    break;
                }
                case YELLOW: {
                    result = R.style.AppTheme_Detail_Yellow;
                    break;
                }
                case AMBER: {
                    result = R.style.AppTheme_Detail_Amber;
                    break;
                }
                case ORANGE: {
                    result = R.style.AppTheme_Detail_Orange;
                    break;
                }
                case DEEP_ORANGE: {
                    result = R.style.AppTheme_Detail_DeepOrange;
                    break;
                }
                case BROWN: {
                    result = R.style.AppTheme_Detail_Brown;
                    break;
                }
                case BLUE_GREY: {
                    result = R.style.AppTheme_Detail_BlueGrey;
                    break;
                }
                default:
                case GREY: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * テーマ取得
     *
     * @param color 色
     * @return テーマ
     */
    public static int getTheme(COLOR color) {
        int result = R.style.AppTheme_Grey;
        try {
            switch (color) {
                case RED: {
                    result = R.style.AppTheme_Red;
                    break;
                }
                case PINK: {
                    result = R.style.AppTheme_Pink;
                    break;
                }
                case PURPLE: {
                    result = R.style.AppTheme_Purple;
                    break;
                }
                case DEEP_PURPLE: {
                    result = R.style.AppTheme_DeepPurple;
                    break;
                }
                case INDIGO: {
                    result = R.style.AppTheme_Indigo;
                    break;
                }
                case BLUE: {
                    result = R.style.AppTheme_Blue;
                    break;
                }
                case LIGHT_BLUE: {
                    result = R.style.AppTheme_LightBlue;
                    break;
                }
                case CYAN: {
                    result = R.style.AppTheme_Cyan;
                    break;
                }
                case TEAL: {
                    result = R.style.AppTheme_Teal;
                    break;
                }
                case GREEN: {
                    result = R.style.AppTheme_Green;
                    break;
                }
                case LIGHT_GREEN: {
                    result = R.style.AppTheme_LightGreen;
                    break;
                }
                case LIME: {
                    result = R.style.AppTheme_Lime;
                    break;
                }
                case YELLOW: {
                    result = R.style.AppTheme_Yellow;
                    break;
                }
                case AMBER: {
                    result = R.style.AppTheme_Amber;
                    break;
                }
                case ORANGE: {
                    result = R.style.AppTheme_Orange;
                    break;
                }
                case DEEP_ORANGE: {
                    result = R.style.AppTheme_DeepOrange;
                    break;
                }
                case BROWN: {
                    result = R.style.AppTheme_Brown;
                    break;
                }
                case BLUE_GREY: {
                    result = R.style.AppTheme_BlueGrey;
                    break;
                }
                default:
                case GREY: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
