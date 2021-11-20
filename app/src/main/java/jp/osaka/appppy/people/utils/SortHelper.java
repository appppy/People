package jp.osaka.appppy.people.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.ui.files.SimpleFile;

/**
 * ソートヘルパ
 */

public class SortHelper {

    /**
     * 更新日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 更新日でソートした一覧
     */
    public static Collection<SimplePerson> toSortByDateModifiedCollection(Collection<SimplePerson> collection) {
        Collections.sort((List<SimplePerson>) collection, (lhs, rhs) -> (int) (lhs.modifiedDate - rhs.modifiedDate));
        return collection;
    }

    /**
     * 作成日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 作成日でソートした一覧
     */
    public static Collection<SimplePerson> toSortByDateCreatedCollection(Collection<SimplePerson> collection) {
        try {
            Collections.sort((List<SimplePerson>) collection, (lhs, rhs) -> (int) (lhs.creationDate - rhs.creationDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }

    /**
     * 名前でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 名前でソートした一覧
     */
    public static Collection<SimplePerson> toSortByNameCollection(Collection<SimplePerson> collection) {
        try {
            Collections.sort((List<SimplePerson>) collection, (lhs, rhs) -> lhs.displayName.compareTo(rhs.displayName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }

    /**
     * 名前でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 名前でソートした一覧
     */
    public static Collection<SimpleFile> toSortByNameFileCollection(Collection<SimpleFile> collection) {
        try {
            Collections.sort((List<SimpleFile>) collection, (lhs, rhs) -> lhs.name.compareTo(rhs.name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collection;
    }
}
