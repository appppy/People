
package jp.osaka.appppy.people.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import jp.osaka.appppy.people.android.IClient;
import jp.osaka.appppy.people.constants.INTERFACE;

import static jp.osaka.appppy.people.Config.LOG_I;

/**
 * ピープルクライアント
 */
public class PeopleClient implements IClient {
    /**
     * @serial 目印
     */
    private static final String TAG = "PeopleClient";

    /**
     * @serial コンテキスト
     */
    private final Context mContext;

    /**
     * @serial コールバック
     */
    private static Callbacks mCallbacks;

    /**
     * @serial インタフェース
     */
    private IPeopleService mBinder;

    /**
     * @serial 自身
     */
    private final PeopleClient mSelf;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public PeopleClient(Context context, Callbacks callbacks) {
        mSelf = this;
        mContext = context;
        mCallbacks = callbacks;
    }

    /** コネクション */
    private final ServiceConnection mConnection = new ServiceConnection() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = IPeopleService.Stub.asInterface(service);
            try {
                mBinder.registerCallback(mCallback);
                mBinder.getPeople();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            mContext.unbindService(mConnection);
        }
    };

    /**
     * @serial コールバック
     */
    private final IPeopleServiceCallback mCallback = new IPeopleServiceCallback.Stub() {
        /**
         * 更新
         *
         * @param people ピープル
         */
        @Override
        public void update(List<SimplePerson> people) {
            if (mCallbacks != null) {
                mCallbacks.onUpdated(mSelf, people);
            }
        }
    };

    /**
     * 接続
     */
    public void connect() {
        if (LOG_I) {
            Log.i(TAG, "connect#enter");
        }
        try {
            Intent intent = new Intent(INTERFACE.IPeopleService);
            intent.setPackage("jp.osaka.appppy.people");
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }  catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (LOG_I) {
            Log.i(TAG, "connect#leave");
        }
    }

    /**
     * 解除
     */
    public void disconnect() {
        if (LOG_I) {
            Log.i(TAG, "disconnect#enter");
        }
        if (mBinder != null) {
            try {
                mBinder.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mContext != null) {
            try {
                mContext.unbindService(mConnection);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "disconnect#leave");
        }
    }

    /**
     * ピープルの設定
     *
     * @param people ピープル
     */
    public void setPeople(List<SimplePerson> people) {
        if (LOG_I) {
            Log.i(TAG, "setPeople#enter");
        }
        if (mBinder != null) {
            try {
                mBinder.setPeople(people);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "setPeople#leave");
        }
    }

    /**
     * コールバックインタフェース
     */
    public interface Callbacks {
        /**
         * コマンド更新通知
         *
         * @param object オブジェクト
         * @param people ピープル
         */
        void onUpdated(Object object, List<SimplePerson> people);
    }
}
