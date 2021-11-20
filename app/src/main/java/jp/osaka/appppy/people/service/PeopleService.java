
package jp.osaka.appppy.people.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.utils.controller.BaseCommand;
import jp.osaka.appppy.people.utils.controller.Controller;
import jp.osaka.appppy.people.utils.controller.IWorker;
import jp.osaka.appppy.people.utils.controller.command.Backup;
import jp.osaka.appppy.people.utils.controller.command.Restore;
import jp.osaka.appppy.people.utils.controller.status.BaseStatus;

import static jp.osaka.appppy.people.Config.LOG_I;

/**
 * ピープルサービス
 */
public class PeopleService extends Service implements
        IWorker.Callbacks {

    /**
     * @serial タグ
     */
    private static final String TAG = "PeopleService";

    /**
     * @serial コールバック一覧
     */
    private final RemoteCallbackList<IPeopleServiceCallback> mCallbacks =
            new RemoteCallbackList<>();

    /**
     * @serial コントローラー
     */
    private final Controller mController = new Controller();

    /**
     * @serial インタフェース
     */
    private final IPeopleService.Stub mBinder = new IPeopleService.Stub() {

        /**
         * コールバック登録
         *
         * @param callback コールバック
         */
        @Override
        public void registerCallback(IPeopleServiceCallback callback) {
            mCallbacks.register(callback);
        }

        /**
         * コールバック解除
         *
         * @param callback コールバック
         */
        @Override
        public void unregisterCallback(IPeopleServiceCallback callback) {
            mCallbacks.unregister(callback);
        }

        /**
         * 設定
         *
         * @param people ピープル
         */
        @Override
        public void setPeople(List<SimplePerson> people) {
            // バックアップ
            BaseCommand command = new Backup();
            ArrayList<SimplePerson> arrayList = new ArrayList<>(people);
            command.args.putParcelableArrayList(PeopleConstants.EXTRA_PEOPLE, arrayList);
            mController.start(command);
        }

        /**
         * 取得
         */
        @Override
        public void getPeople() {
            // リストア
            mController.start(new Restore());
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
        mController.register(new PersonAccessor(this, this));


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
        if (IPeopleService.class.getName().equals(intent.getAction())) {
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
            if (worker instanceof PersonAccessor) {
                // コマンド
                if (command instanceof Restore) {
                    // リストア
                    if (status instanceof People) {
                        People s = (People) status;
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
    public void broadcast(Collection<SimplePerson> collection) {
        List<SimplePerson> list = new ArrayList<>(collection);
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
