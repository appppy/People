package jp.osaka.appppy.people.service;

import java.util.Collection;

import jp.osaka.appppy.people.utils.controller.status.BaseStatus;

/**
 * 人々
 */
class People extends BaseStatus {

    /**
     * @serial 一覧
     */
    public Collection<SimplePerson> collection;


    /**
     * コンストラクタ
     *
     * @param collection 一覧
     */
    People(Collection<SimplePerson> collection) {
        this.collection = collection;
    }

}
