package jp.osaka.appppy.people.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.PeopleConstants.MAX;

/**
 * 人データベース
 */
class PersonDatabase {

    /**
     * @serial タグ
     */
    private static final String TAG = "PersonDatabase";

    /**
     * @serial プリファレンス
     */
    private final SimplePersonStore mPrefs;

    /**
     * @serial パーソン
     */
    private final Collection<SimplePerson> mCollection = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    PersonDatabase(Context context) {
        mPrefs = new SimplePersonStore(context);
    }

    /**
     * リストア
     */
    public void restore() {
        if (LOG_I) {
            Log.i(TAG, "restore#enter");
        }

        // クリア
        mCollection.clear();

        // リストア
        for (int i = 0; i < MAX; i++) {
            SimplePerson person = mPrefs.get(String.valueOf(i));
            if (null != person) {
                mCollection.add(person);
            }
        }

        if (LOG_I) {
            Log.i(TAG, "restore#leave");
        }
    }

    /**
     * バックアップ
     */
    void backup(Collection<SimplePerson> collection) {
        if (LOG_I) {
            Log.i(TAG, "backup#enter");
        }
        // バックアップ
        for (int i = 0; i < MAX; i++) {
            mPrefs.clear(String.valueOf(i));
        }
        int position = 0;
        if (collection != null) {
            for (SimplePerson person : collection) {
                mPrefs.set(String.valueOf(position), person);
                position++;
            }
        }

        mCollection.clear();
        mCollection.addAll(collection);

        if (LOG_I) {
            Log.i(TAG, "backup#leave");
        }
    }

    /**
     * 人々を得る
     *
     * @return 人々
     */
    public Collection<SimplePerson> getPeople() {
        return mCollection;
    }
}
