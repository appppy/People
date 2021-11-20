package jp.osaka.appppy.people.ui.files;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.service.SimplePerson;


/**
 * ヘルパ
 */
public class DetailListHelper {

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
                result.add(toItem(item));
            }
        }
        return result;
    }

    /**
     * 項目に変更
     *
     * @return アセット
     */
    private static SimplePerson toItem(SimplePerson item) {
        item.title = item.displayName;
        item.subtitle = DateFormat.getDateTimeInstance().format(item.creationDate);
        return item;
    }
}
