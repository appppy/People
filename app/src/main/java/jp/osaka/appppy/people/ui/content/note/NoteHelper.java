package jp.osaka.appppy.people.ui.content.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_DETAIL_ITEM;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;

/**
 * ヘルパ
 */
public class NoteHelper {

    /**
     * メモの一覧取得
     *
     * @param collection 一覧
     * @return メモ一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash && !item.note.equals(INVALID_STRING_VALUE)) {
                result.add(item.toNote());
            }
        }
        return result;
    }

    /**
     * 検索したメモ一覧取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索したメモ一覧
     */
    public static ArrayList<SimplePerson> toListOf(ArrayList<SimplePerson> collection, String src) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimplePerson person : collection) {
            if (person != null && (person.displayName.contains(src))) {
                result.add(person);
            } else if (person != null && (person.note.contains(src))) {
                result.add(person);
            }
        }
        return result;
    }

    /**
     * 詳細アクティビティの開始
     *
     * @param v    表示
     * @param item 項目
     */
    static void startDetailActivity(Activity activity, Context context, View v, SimplePerson item) {
        if (item.color == COLOR.WHITE) {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = SimpleDetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = SimpleDetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());
                Intent intent = CustomDetailActivity.createIntent(context, item);
                ActivityCompat.startActivityForResult(activity, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
            } else {
                Intent intent = CustomDetailActivity.createIntent(context, item);
                activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
            }
        }
    }

    /**
     * 詳細アクティビティの開始
     *
     * @param item 項目
     */
    static void startDetailActivity(Activity activity, Context context, SimplePerson item) {
        Intent intent;
        if (item.color == COLOR.WHITE) {
            intent = SimpleDetailActivity.createIntent(context, item);
        } else {
            intent = CustomDetailActivity.createIntent(context, item);
        }
        activity.startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
    }
}
