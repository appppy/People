package jp.osaka.appppy.people.android.view;

/**
 * 検索一覧表示のインタフェース
 */
public interface ISearchCollectionView<T> {
    /**
     * 挿入
     *
     * @param item 項目
     */
    void insert(int index, T item);

    /**
     * 削除
     *
     * @param item 項目
     * @return 項目位置
     */
    int remove(T item);

    /**
     * 変更通知
     *
     * @param item 項目
     * @return 項目位置
     */
    int change(T item);
}
