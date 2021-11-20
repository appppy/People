package jp.osaka.appppy.people.android;

/**
 * クライアントのインタフェース
 */
public interface IClient {

    /**
     * 接続
     */
    void connect();

    /**
     * 非接続
     */
    void disconnect();
}
