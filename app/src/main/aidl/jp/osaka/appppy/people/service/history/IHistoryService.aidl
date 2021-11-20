// IHistoryService.aidl
package jp.osaka.appppy.people.service.history;

import jp.osaka.appppy.people.service.history.IHistoryServiceCallback;
import jp.osaka.appppy.people.service.history.History;

interface IHistoryService {
    /**
     * コールバック登録
     */
    void registerCallback(IHistoryServiceCallback callback);

    /**
     * コールバック解除
     */
    void unregisterCallback(IHistoryServiceCallback callback);

    /**
     * 取得
     */
    void getHistoryList();

    /**
     * 削除
     */
    void delete();
}
