// IPeopleServiceCallback.aidl
package jp.osaka.appppy.people.service;

import jp.osaka.appppy.people.service.SimplePerson;

interface IPeopleServiceCallback {
    /**
     * 更新
     */
    void update(in List<SimplePerson> people);
}
