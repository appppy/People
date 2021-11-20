/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.osaka.appppy.people.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import jp.osaka.appppy.people.constants.COLOR;

import static jp.osaka.appppy.people.constants.COLOR.WHITE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_CREATION_DATE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_DISPLAY_NAME;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_EMAIL;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_IMAGE_PATH;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_IS_ARCHIVE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_IS_TRASH;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_KEY;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_MODIFIED_DATE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_NOTE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_TEL;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_TIMESTAMP;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_UUID;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_LONG_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;

/**
 * データ格納
 */
class SimplePersonStore {

    /**
     * @serial プリファレンス
     */
    private final SharedPreferences mPrefs;

    /**
     * @serial プリファンレス名
     */
    static final String SHARED_PREFERENCE_NAME = "SimplePersonStore";

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    SimplePersonStore(Context context) {
        // プリファレンスの取得
        mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
    }

    /**
     * 人の取得
     *
     * @param id 識別子
     * @return ジオフェンス
     */
    public SimplePerson get(String id) {

        // UUID
        String uuid = mPrefs.getString(
                getFieldKey(id, EXTRA_UUID),
                INVALID_STRING_VALUE);

        // 作成日
        long creationDate = mPrefs.getLong(
                getFieldKey(id,EXTRA_CREATION_DATE),
                INVALID_LONG_VALUE);

        // 変更日
        long modifiedDate = mPrefs.getLong(
                getFieldKey(id, EXTRA_MODIFIED_DATE),
                INVALID_LONG_VALUE);

        // 表示名
        String displayName = mPrefs.getString(
                getFieldKey(id, EXTRA_DISPLAY_NAME),
                INVALID_STRING_VALUE);

        // 電話番号
        String tel = mPrefs.getString(
                getFieldKey(id, EXTRA_TEL),
                INVALID_STRING_VALUE);

        // E-MAIL
        String email = mPrefs.getString(
                getFieldKey(id, EXTRA_EMAIL),
                INVALID_STRING_VALUE);

        // 画像のパス
        String imagePath = mPrefs.getString(
                getFieldKey(id, EXTRA_IMAGE_PATH),
                INVALID_STRING_VALUE);

        // ノート
        String note = mPrefs.getString(
                getFieldKey(id, EXTRA_NOTE),
                INVALID_STRING_VALUE);

        // アーカイブの有無
        boolean isArchive = mPrefs.getBoolean(
                getFieldKey(id, EXTRA_IS_ARCHIVE),
                false);

        // ゴミ箱の有無
        boolean isTrash = mPrefs.getBoolean(
                getFieldKey(id, EXTRA_IS_TRASH),
                false);

        // 色
        COLOR color = COLOR.valueOf(mPrefs.getString(
                getFieldKey(id, EXTRA_COLOR),
                WHITE.name()));

        // タイムスタンプ
        long timestamp = mPrefs.getLong(
                getFieldKey(id, EXTRA_TIMESTAMP),
                INVALID_LONG_VALUE);

        // コンテンツの確認
        assert uuid != null;
        assert displayName != null;
        if (
            !uuid.equals(INVALID_STRING_VALUE) &&
            creationDate != INVALID_LONG_VALUE &&
            modifiedDate != INVALID_LONG_VALUE &&
            !displayName.equals(INVALID_STRING_VALUE)
                ) {

            // ジオフェンスの生成

            return new SimplePerson(
                    id,
                    uuid,
                    creationDate,
                    modifiedDate,
                    displayName,
                    tel,
                    email,
                    imagePath,
                    note,
                    isArchive,
                    isTrash,
                    color,
                    timestamp
            );
        } else {

            return null;
        }
    }

    /**
     * 人の保存
     *
     * @param id 識別子
     * @param simplePerson 人
     */
    public void set(String id, SimplePerson simplePerson) {
        /*
         * Get a SharedPreferences editor instance. Among other
         * things, SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        Editor editor = mPrefs.edit();

        // Write the Geofence values to SharedPreferences
        editor.putString(
                getFieldKey(id, EXTRA_UUID),
                simplePerson.uuid);

        editor.putLong(
                getFieldKey(id, EXTRA_CREATION_DATE),
                simplePerson.creationDate);

        editor.putLong(
                getFieldKey(id, EXTRA_MODIFIED_DATE),
                simplePerson.modifiedDate);

        editor.putString(
                getFieldKey(id, EXTRA_DISPLAY_NAME),
                simplePerson.displayName);

        editor.putString(
                getFieldKey(id, EXTRA_TEL),
                simplePerson.call);

        editor.putString(
                getFieldKey(id, EXTRA_EMAIL),
                simplePerson.send);

        editor.putString(
                getFieldKey(id, EXTRA_IMAGE_PATH),
                simplePerson.imagePath);

        editor.putString(
                getFieldKey(id, EXTRA_NOTE),
                simplePerson.note);

        editor.putBoolean(
                getFieldKey(id, EXTRA_IS_ARCHIVE),
                simplePerson.isArchive);

        editor.putBoolean(
                getFieldKey(id, EXTRA_IS_TRASH),
                simplePerson.isTrash);

        editor.putString(
                getFieldKey(id, EXTRA_COLOR),
                simplePerson.color.name());

        editor.putLong(
                getFieldKey(id, EXTRA_TIMESTAMP),
                simplePerson.timestamp);

        // Commit the changes
        editor.apply();
    }

    /**
     * 人の削除
     *
     * @param id 識別子
     */
    public void clear(String id) {
        // Remove a flattened geofence object from storage by removing all of its keys
        Editor editor = mPrefs.edit();
        editor.remove(getFieldKey(id, EXTRA_UUID));
        editor.remove(getFieldKey(id, EXTRA_CREATION_DATE));
        editor.remove(getFieldKey(id, EXTRA_MODIFIED_DATE));
        editor.remove(getFieldKey(id, EXTRA_DISPLAY_NAME));
        editor.remove(getFieldKey(id, EXTRA_TEL));
        editor.remove(getFieldKey(id, EXTRA_EMAIL));
        editor.remove(getFieldKey(id, EXTRA_IMAGE_PATH));
        editor.remove(getFieldKey(id, EXTRA_NOTE));
        editor.remove(getFieldKey(id, EXTRA_IS_ARCHIVE));
        editor.remove(getFieldKey(id, EXTRA_IS_TRASH));
        editor.remove(getFieldKey(id, EXTRA_COLOR));
        editor.remove(getFieldKey(id, EXTRA_TIMESTAMP));
        editor.apply();
    }

    /**
     * フィールドのキー取得
     *
     * @param id 識別子
     * @param fieldName フィールド名
     * @return フィールドのキー
     */
    private String getFieldKey(String id, String fieldName) {
        return EXTRA_KEY +
                id +
                "_" +
                fieldName;
    }
}
