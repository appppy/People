
package jp.osaka.appppy.people.service.history;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.appppy.people.constants.INTERFACE;
import jp.osaka.appppy.people.utils.controller.Controller;
import jp.osaka.appppy.people.utils.controller.IWorker;
import jp.osaka.appppy.people.utils.controller.command.Backup;
import jp.osaka.appppy.people.utils.controller.BaseCommand;
import jp.osaka.appppy.people.utils.controller.command.Delete;
import jp.osaka.appppy.people.utils.controller.command.Restore;
import jp.osaka.appppy.people.utils.controller.status.BaseStatus;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_HISTORY;

/**
 * 履歴サービス
 */
public class HistoryService extends Service implements
        IWorker.Callbacks {

    /**
     * @serial タグ
     */
    private static final String TAG = "HistoryService";

    /**
     * @serial コールバック一覧
     */
    private final RemoteCallbackList<IHistoryServiceCallback> mCallbacks =
            new RemoteCallbackList<>();

    /**
     * @serial コントローラー
     */
    private final Controller mController = new Controller();

    /**
     * @serial インタフェース
     */
    private final IHistoryService.Stub mBinder = new IHistoryService.Stub() {

        /**
         * コールバック登録
         *
         * @param callback コールバック
         */
        @Override
        public void registerCallback(IHistoryServiceCallback callback) {
            mCallbacks.register(callback);
        }

        /**
         * コールバック解除
         *
         * @param callback コールバック
         */
        @Override
        public void unregisterCallback(IHistoryServiceCallback callback) {
            mCallbacks.unregister(callback);
        }

        /**
         * 取得
         */
        @Override
        public void getHistoryList() {
            // リストア
            mController.start(new Restore());
        }

        /**
         * 削除
         */
        @Override
        public void delete() {
            // リストア
            mController.start(new Delete());
        }
    };

    /**
     * @serial インタフェース
     */
    private final IHistoryProxy.Stub mProxy = new IHistoryProxy.Stub() {

        /**
         * 設定
         *
         * @param history 履歴
         */
        @Override
        public void setHistory(History history) {
            // バックアップ
            BaseCommand command = new Backup();
            command.args.putParcelable(EXTRA_HISTORY, history);
            mController.start(command);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 登録
        mController.register(new HistoryWorker(this, this));


        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        // コールバックの削除
        mCallbacks.kill();

        // 停止
        mController.stop();

        // 解除
        mController.unregisterAll();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        IBinder result = null;
        if (INTERFACE.IHistoryProxy.equals(intent.getAction())) {
            result = mProxy;
        }
        if (INTERFACE.IHistoryService.equals(intent.getAction())) {
            result = mBinder;
        }
        return result;
    }

    /**
     * コマンド開始通知
     *
     * @param worker  ワーカー
     * @param command コマンド
     */
    @Override
    public void onStarted(IWorker worker, BaseCommand command) {
    }

    /**
     * コマンド更新通知
     *
     * @param worker  ワーカー
     * @param command コマンド
     * @param status  状態
     */
    @Override
    public void onUpdated(IWorker worker, BaseCommand command, BaseStatus status) {
        try {
            // データベースコントローラー
            if (worker instanceof HistoryWorker) {
                // コマンド
                if (command instanceof Restore) {
                    // リストア
                    if (status instanceof HistoryList) {
                        HistoryList s = (HistoryList) status;
                        // ブロードキャスト
                        broadcast(s.collection);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 成功通知
     *
     * @param worker  ワーカー
     * @param command コマンド
     */
    @Override
    public void onSuccessed(IWorker worker, BaseCommand command) {
    }

    /**
     * ブロードキャスト
     *
     * @param collection 一覧
     */
    public void broadcast(Collection<History> collection) {
        List<History> list = new ArrayList<>(collection);
        int n = mCallbacks.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                mCallbacks.getBroadcastItem(i).update(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }
}
