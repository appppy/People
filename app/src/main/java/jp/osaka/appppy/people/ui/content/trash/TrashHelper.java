package jp.osaka.appppy.people.ui.content.trash;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.service.SimplePerson;

/**
 * ヘルパ
 */
class TrashHelper {
    /**
     * ゴミ箱一覧の取得
     *
     * @param collection 一覧
     * @return ゴミ箱一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (item.isTrash) {
                result.add(item.toContact());
            }
        }
        return result;
    }

    /**
     * ゴミ箱を空にする
     *
     * @param collection 一覧
     * @return ゴミ箱一覧
     */
    static ArrayList<SimplePerson> toEmptyList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (!item.isTrash) {
                result.add(item.toContact());
            }
        }
        return result;
    }

}
