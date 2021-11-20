package jp.osaka.appppy.people.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.constants.ActivityTransition;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.ui.content.DetailActivity;
import jp.osaka.appppy.people.ui.content.DetailActivity_DarkAppBar;
import jp.osaka.appppy.people.ui.content.ImageCreateActivity;
import jp.osaka.appppy.people.ui.content.ImageDetailActivity;
import jp.osaka.appppy.people.ui.content.ImageEditActivity;
import jp.osaka.appppy.people.ui.content.contact.MainActivity;

import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_CREATE_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_DETAIL_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_EDIT_ITEM;
import static jp.osaka.appppy.people.constants.COLOR.WHITE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;


/**
 * Created by appy_000 on 2017/08/06.
 */
public class ActivityTransitionHelper {

    /**
     * 開始画面の取得
     *
     * @param id 識別子
     * @return 開始アクティビティ
     */
    @SuppressLint("NonConstantResourceId")
    public static Class<?> getStartActivity(int id) {
        Class<?> c = MainActivity.class;

        try {
            switch (id) {
                case R.id.contact: {
                    c = MainActivity.class;
                    break;
                }
                case R.id.call: {
                    c = jp.osaka.appppy.people.ui.content.call.MainActivity.class;
                    break;
                }
                case R.id.send: {
                    c = jp.osaka.appppy.people.ui.content.send.MainActivity.class;
                    break;
                }
                case R.id.note: {
                    c = jp.osaka.appppy.people.ui.content.note.MainActivity.class;
                    break;
                }
                case R.id.archive: {
                    c = jp.osaka.appppy.people.ui.content.archive.MainActivity.class;
                    break;
                }
                case R.id.trash: {
                    c = jp.osaka.appppy.people.ui.content.trash.MainActivity.class;
                    break;
                }
                case R.id.recent: {
                    c = jp.osaka.appppy.people.ui.content.recent.MainActivity.class;
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * 詳細画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startDetailActivity_from_Line(Activity activity, Context context, View v, SimplePerson item) {
        try {
            if (item.color == WHITE) {
                startDetailActivity_from_Module(activity, context, v, item);
            } else {
                if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                                v, 0, 0, v.getWidth(), v.getHeight());
                        Intent intent = DetailActivity_DarkAppBar.createIntent(context, item);
                        ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
                    } else {
                        Intent intent = DetailActivity_DarkAppBar.createIntent(context, item);
                        activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                                v, 0, 0, v.getWidth(), v.getHeight());
                        Intent intent = ImageDetailActivity.createIntent(context, item);
                        ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
                    } else {
                        Intent intent = ImageDetailActivity.createIntent(context, item);
                        activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 詳細画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startDetailActivity_from_Module(Activity activity, Context context, View v, SimplePerson item) {
        try {
            if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                if (Build.VERSION.SDK_INT >= 21) {
                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                            v, 0, 0, v.getWidth(), v.getHeight());
                    Intent intent = DetailActivity.createIntent(context, item);
                    ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
                } else {
                    Intent intent = DetailActivity.createIntent(context, item);
                    activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
                }
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                            v, 0, 0, v.getWidth(), v.getHeight());
                    Intent intent = ImageDetailActivity.createIntent(context, item);
                    ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
                } else {
                    Intent intent = ImageDetailActivity.createIntent(context, item);
                    activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 詳細画面の開始
     *
     * @param item 項目
     */
    public static void startDetailActivity_from_Line(Activity activity, Context context, SimplePerson item) {
        try {
            if (item.color == WHITE) {
                startDetailActivity_from_Module(activity, context, item);
            } else {
                Intent intent;
                if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                    intent = DetailActivity_DarkAppBar.createIntent(context, item);
                } else {
                    intent = ImageDetailActivity.createIntent(context, item);
                }
                activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 詳細画面の開始
     *
     * @param item 項目
     */
    public static void startDetailActivity_from_Module(Activity activity, Context context, SimplePerson item) {
        try {
            Intent intent;
            if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                intent = DetailActivity.createIntent(context, item);
            } else {
                intent = ImageDetailActivity.createIntent(context, item);
            }
            activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 編集画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startEditActivity(Activity activity, Context context, View v, SimplePerson item) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = ImageEditActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, REQUEST_EDIT_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = ImageEditActivity.createIntent(context, item);
                activity.startActivityForResult(intent, REQUEST_EDIT_ITEM.ordinal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 編集画面の開始
     *
     * @param item 項目
     */
    public static void startEditActivity(Activity activity, Context context, SimplePerson item) {
        try {
            Intent intent = ImageEditActivity.createIntent(context, item);
            activity.startActivityForResult(intent, REQUEST_EDIT_ITEM.ordinal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 編集画面の開始
     *
     * @param v    表示
     * @param item 項目
     */
    public static void startCreateActivity(Activity activity, Context context, View v, SimplePerson item) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = ImageCreateActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, REQUEST_CREATE_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = ImageCreateActivity.createIntent(context, item);
                activity.startActivityForResult(intent, REQUEST_CREATE_ITEM.ordinal());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * フォルダ画面の開始
     */
    public static void startFolderActivity(Activity activity) {
        try {
            Intent intent = jp.osaka.appppy.people.ui.files.MainActivity.createIntent(activity);
            activity.startActivityForResult(intent, ActivityTransition.REQUEST_OPEN_FILE.ordinal());
            activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 履歴画面の開始
     */
    public static void startHistoryActivity(Activity activity) {
        try {
            Intent intent = jp.osaka.appppy.people.ui.history.MainActivity.createIntent(activity);
            activity.startActivityForResult(intent, ActivityTransition.REQUEST_OPEN_HISTORY.ordinal());
            activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
