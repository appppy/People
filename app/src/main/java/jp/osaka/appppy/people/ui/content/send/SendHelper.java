package jp.osaka.appppy.people.ui.content.send;

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
class SendHelper {
    /**
     * メール一覧の取得
     *
     * @param collection 一覧
     * @return メール一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<String> check = new ArrayList<>();
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isArchive && !item.isTrash && !item.send.equals(INVALID_STRING_VALUE)) {
                if (!check.contains(item.send)) {
                    check.add(item.send);
                    result.add(item.toSend());
                }
            }
        }
        return result;
    }

    /**
     * 検索したメール一覧の取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索したメール一覧
     */
    public static ArrayList<SimplePerson> toListOf(ArrayList<SimplePerson> collection, String src) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimplePerson person : collection) {
            if (person != null && (person.displayName.contains(src))) {
                result.add(person);
            } else if (person != null && (person.send.contains(src))) {
                result.add(person);
            }
        }
        return result;
    }

    /**
     * 検索画面開始
     *
     * @param activity 画面
     */
    static void startSearchActivity(Activity activity) {
        Intent intent = SearchActivity.createIntent(activity);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
        activity.finish();
    }

}
