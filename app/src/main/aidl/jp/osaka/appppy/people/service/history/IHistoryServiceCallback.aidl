// IHistoryServiceCallback.aidl
package jp.osaka.appppy.people.service.history;

import jp.osaka.appppy.people.service.history.History;

interface IHistoryServiceCallback {
    /**
     * 更新
     */
    void update(in List<History> history);
}
