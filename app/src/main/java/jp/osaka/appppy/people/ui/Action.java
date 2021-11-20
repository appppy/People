package jp.osaka.appppy.people.ui;

import jp.osaka.appppy.people.constants.ACTION;
import jp.osaka.appppy.people.service.SimplePerson;

/**
 * 実行
 */
public class Action {

    /**
     * 識別子
     */
    public ACTION action;

    /**
     * パラメータ
     */
    public int arg;

    /**
     * オブジェクト
     */
    public SimplePerson object;

    /**
     * コンストラクタ
     *
     * @param action 識別子
     * @param position パラメータ
     * @param object オブジェクト
     */
    public Action(ACTION action, int position, SimplePerson object) {
        this.action = action;
        this.arg = position;
        this.object = object;
    }
}
