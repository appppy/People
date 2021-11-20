package jp.osaka.appppy.people.ui.content.contact;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.service.SimplePerson;

/**
 * ヘルパ
 */
class ContactHelper {

    /**
     * 連絡先一覧の取得
     *
     * @param collection 一覧
     * @return 連絡先一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash) {
                result.add(item.toContact());
            }
        }
        return result;
    }

    /**
     * 連絡先一覧の取得
     *
     * @param collection 一覧
     * @return 連絡先一覧
     */
    public static ArrayList<SimplePerson> toList(ArrayList<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash) {
                result.add(item.toContact());
            }
        }
        return result;
    }

    /**
     * 検索した連絡先一覧の取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索した連絡先一覧
     */
    public static ArrayList<SimplePerson> toListOf(ArrayList<SimplePerson> collection, String src) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimplePerson person : collection) {
            if (person != null && (person.displayName.contains(src))) {
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
