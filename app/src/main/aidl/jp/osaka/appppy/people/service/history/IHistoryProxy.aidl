// IHistoryService.aidl
package jp.osaka.appppy.people.service.history;

import jp.osaka.appppy.people.service.history.History;

interface IHistoryProxy {
    /**
     * 設定
     */
    void setHistory(in History history);
}
