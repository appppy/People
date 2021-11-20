package jp.osaka.appppy.people.ui.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ヘルパ
 */
class FilesHelper {
    /**
     * 検索したファイルの取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索したファイル一覧
     */
    public static ArrayList<SimpleFile> toListOf(ArrayList<SimpleFile> collection, String src) {
        ArrayList<SimpleFile> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimpleFile item : collection) {
            if (item != null && (item.name.contains(src))) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 更新日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 更新日でソートした一覧
     */
    static Collection<SimpleFile> toSortByDateModifiedCollection(Collection<SimpleFile> collection) {
        Collections.sort((List<SimpleFile>) collection, (lhs, rhs) -> (int) (lhs.date - rhs.date));
        return collection;
    }

}
