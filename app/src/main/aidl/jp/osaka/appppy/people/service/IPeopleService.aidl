// IPeopleService.aidl
package jp.osaka.appppy.people.service;

import jp.osaka.appppy.people.service.IPeopleServiceCallback;
import jp.osaka.appppy.people.service.SimplePerson;

interface IPeopleService {
    /**
     * コールバック登録
     */
    void registerCallback(IPeopleServiceCallback callback);

    /**
     * コールバック解除
     */
    void unregisterCallback(IPeopleServiceCallback callback);

    /**
     * 設定
     */
    void setPeople(in List<SimplePerson> people);

    /**
     * 取得
     */
    void getPeople();
}
