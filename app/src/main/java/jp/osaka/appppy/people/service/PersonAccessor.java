package jp.osaka.appppy.people.service;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

import jp.osaka.appppy.people.utils.controller.BaseCommand;
import jp.osaka.appppy.people.utils.controller.IWorker;
import jp.osaka.appppy.people.utils.controller.command.Backup;
import jp.osaka.appppy.people.utils.controller.command.Restore;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_PEOPLE;

/**
 * パーソンアクセス
 */
class PersonAccessor implements IWorker {

    /**
     * @serial タグ
     */
    private static final String TAG = "PersonAccessor";

    /**
     * @serial メッセージの定義
     */
    private enum MESSAGE {
        RESTORE,
        BACKUP;
        /**
         * 値に合致する enum 定数を返す。
         *
         * @param index インデックス
         * @return メッセージ
         */
        public static MESSAGE get(int index) {
            // 値から enum 定数を特定して返す処理
            for (MESSAGE msg : MESSAGE.values()) {
                if (msg.ordinal() == index) {
                    return msg;
                }
            }
            return null; // 特定できない場合
        }
    }

    /**
     * @serial モデル
     */
    private final PersonDatabase mDatabase;

    /**
     * @serial コールバック
     */
    private final Callbacks mCallbacks;

    /**
     * @serial インスタンス
     */
    private final PersonAccessor mSelf;


    private final HandlerThread mThread;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler;

    /**
     * コンストラクタ
     *
     */
    PersonAccessor(Context context, Callbacks callbacks) {

        mCallbacks = callbacks;

        // モデルの生成
        mDatabase = new PersonDatabase(context);

        mSelf = this;

        mThread = new HandlerThread("People");
        mThread.start();

        mHandler =  new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // 処理開始を通知
                if (mCallbacks != null) {
                    mCallbacks.onStarted(mSelf, (BaseCommand) msg.obj);
                }

                MESSAGE type = MESSAGE.get(msg.what);
                switch(Objects.requireNonNull(type)) {
                    case RESTORE: {
                        Restore command = (Restore) msg.obj;
                        mDatabase.restore();
                        People status = new People(mDatabase.getPeople());
                        // 処理結果を通知
                        if (mCallbacks != null) {
                            mCallbacks.onUpdated(mSelf, command, status);
                        }
                        if (mCallbacks != null) {
                            mCallbacks.onSuccessed(mSelf, (BaseCommand) msg.obj);
                        }
                        break;
                    }
                    case BACKUP: {
                        Backup command = (Backup) msg.obj;
                        ArrayList<SimplePerson> collection = command.args.getParcelableArrayList(EXTRA_PEOPLE);
                        mDatabase.backup(collection);
                        if (mCallbacks != null) {
                            mCallbacks.onSuccessed(mSelf, (BaseCommand) msg.obj);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BaseCommand command) {
        if (LOG_I) {
            Log.i(TAG, "start#enter");
        }

        if (command instanceof Restore) {
            if (mHandler != null) {
                mHandler.removeMessages(MESSAGE.RESTORE.ordinal());
                Message msg = mHandler.obtainMessage(MESSAGE.RESTORE.ordinal(), command);
                mHandler.sendMessage(msg);
            }
        }
        if (command instanceof Backup) {
            if (mHandler != null) {
                mHandler.removeMessages(MESSAGE.BACKUP.ordinal());
                Message msg = mHandler.obtainMessage(MESSAGE.BACKUP.ordinal(), command);
                mHandler.sendMessage(msg);
            }
        }

        if (LOG_I) {
            Log.i(TAG, "start#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (LOG_I) {
            Log.i(TAG, "stop#enter");
        }

        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE.RESTORE.ordinal());
            mHandler.removeMessages(MESSAGE.BACKUP.ordinal());
        }

        if (mThread != null && mThread.isAlive()) {
            mThread.quit();
        }

        if (LOG_I) {
            Log.i(TAG, "stop#leave");
        }
    }
}
