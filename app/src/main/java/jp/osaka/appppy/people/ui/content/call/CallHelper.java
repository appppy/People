package jp.osaka.appppy.people.ui.content.call;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;

/**
 * ヘルパ
 */
class CallHelper {

    /**
     * 電話一覧の取得
     *
     * @param collection 一覧
     * @return 電話一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<String> check = new ArrayList<>();
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash && !item.call.equals(INVALID_STRING_VALUE)) {
                if (!check.contains(item.call)) {
                    check.add(item.call);
                    result.add(item.toCall());
                }
            }
        }
        return result;
    }

    /**
     * 検索した電話一覧の取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索した電話一覧
     */
    public static ArrayList<SimplePerson> toListOf(ArrayList<SimplePerson> collection, String src) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimplePerson person : collection) {
            if (person != null && (person.displayName.contains(src))) {
                result.add(person);
            } else if (person != null && (person.call.contains(src))) {
                result.add(person);
            }
        }
        return result;
    }


    /**
     * 検索アクティビティの開始
     */
    static void startSearchActivity(Activity acticity) {
        Intent intent = SearchActivity.createIntent(acticity);
        acticity.startActivity(intent);
        acticity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        acticity.finish();
    }

}
