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
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.UUID;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.constants.COLOR;

import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_LONG_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;


/**
 * 項目
 */
public class SimplePerson implements Parcelable {

    /**
     * @serial 生成
     */
    public static final Creator<SimplePerson> CREATOR =
            new Creator<SimplePerson>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public SimplePerson createFromParcel(Parcel source) {
                    return new SimplePerson(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public SimplePerson[] newArray(int size) {
                    return new SimplePerson[size];
                }
            };

    /**
     * @serial 識別子
     */
    public String id;

    /**
     * @serial ユニーク識別子
     */
    public String uuid;

    /**
     * @serial 作成日
     */
    public long creationDate;

    /**
     * @serial 変更日
     */
    public long modifiedDate;

    /**
     * @serial 表示名
     */
    public String displayName;

    /**
     * @serial 電話番号
     */
    public String call;

    /**
     * @serial E-mail
     */
    public String send;

    /**
     * @serial 画像のパス
     */
    public String imagePath = INVALID_STRING_VALUE;

    /**
     * @serial ノート
     */
    public String note;

    /**
     * @serial アーカイブの有無
     */
    public boolean isArchive;

    /**
     * @serial ゴミ箱の有無
     */
    public boolean isTrash;

    /**
     * @serial 色
     */
    public COLOR color;

    /**
     * @serial タイムスタンプ
     */
    public long timestamp;

    /**
     * @serial 変更の有無
     */
    public boolean isChanged = false;

    /**
     * @serial 選択
     */
    public boolean isSelected = false;

    /**
     * @serial タイトル
     */
    public String title = INVALID_STRING_VALUE;

    /**
     * @serial サブタイトル
     */
    public String subtitle = INVALID_STRING_VALUE;

    /**
     * @serial シンプル設定の有無
     */
    public boolean isSimpled = false;

    /**
     * 人の生成
     *
     * @param id           識別子
     * @param uuid         ユニーク識別子
     * @param creationDate 作成日
     * @param modifiedDate 変更日
     * @param displayName  表示名
     * @param tel          電話番号
     * @param email        e-mail
     * @param imagePath    画像パス
     * @param note         ノート
     * @param isArchive    アーカイブの有無
     * @param isTrash      ゴミ箱の有無
     * @param color        色
     * @param timestamp    タイムスタンプ
     */
    public SimplePerson(
            String id,
            String uuid,
            long creationDate,
            long modifiedDate,
            String displayName,
            String tel,
            String email,
            String imagePath,
            String note,
            boolean isArchive,
            boolean isTrash,
            COLOR color,
            long timestamp
    ) {
        this.id = id;
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.modifiedDate = modifiedDate;
        this.displayName = displayName;
        this.call = tel;
        this.send = email;
        this.imagePath = imagePath;
        this.note = note;
        this.isArchive = isArchive;
        this.isTrash = isTrash;
        this.color = color;
        this.timestamp = timestamp;
    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    public SimplePerson(Parcel parcel) {
        id = parcel.readString();
        uuid = parcel.readString();
        creationDate = parcel.readLong();
        modifiedDate = parcel.readLong();
        displayName = parcel.readString();
        call = parcel.readString();
        send = parcel.readString();
        imagePath = parcel.readString();
        note = parcel.readString();
        isArchive = parcel.readByte() != 0;
        isTrash = parcel.readByte() != 0;
        color = COLOR.valueOf(parcel.readString());
        timestamp = parcel.readLong();
    }

    /**
     * インスタンス生成
     *
     * @return インスタンス
     */
    public static SimplePerson createInstance() {
        return new SimplePerson(
                String.valueOf(UUID.randomUUID()),
                String.valueOf(UUID.randomUUID()),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                false,
                false,
                COLOR.WHITE,
                INVALID_LONG_VALUE
        );
    }

    /***
     * 項目のコピー
     *
     * @param item 項目
     */
    public void copyPerson(SimplePerson item) {
        id = item.id;
        uuid = item.uuid;
        creationDate = item.creationDate;
        modifiedDate = item.modifiedDate;
        displayName = item.displayName;
        send = item.send;
        call = item.call;
        note = item.note;
        imagePath = item.imagePath;
        isArchive = item.isArchive;
        isTrash = item.isTrash;
        color = item.color;
        timestamp = item.timestamp;
    }

    /***
     * 項目のコピー
     *
     * @param item 項目
     */
    public void setParams(SimplePerson item) {
        creationDate = item.creationDate;
        modifiedDate = item.modifiedDate;
        displayName = item.displayName;
        send = item.send;
        call = item.call;
        note = item.note;
        imagePath = item.imagePath;
        isArchive = item.isArchive;
        isTrash = item.isTrash;
        color = item.color;
        timestamp = item.timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uuid);
        dest.writeLong(creationDate);
        dest.writeLong(modifiedDate);
        dest.writeString(displayName);
        dest.writeString(call);
        dest.writeString(send);
        dest.writeString(imagePath);
        dest.writeString(note);
        dest.writeByte((byte) (isArchive ? 1 : 0));
        dest.writeByte((byte) (isTrash ? 1 : 0));
        dest.writeString(color.name());
        dest.writeLong(timestamp);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equal(Object o) {
        boolean result = false;
        if (o instanceof SimplePerson) {
            SimplePerson item = (SimplePerson) o;
            if (item.uuid.equals(uuid)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 文字に変換する
     *
     * @param context コンテキスト
     * @return 文字
     */
    public String toString(Context context) {
        StringBuilder sb = new StringBuilder();
        //sb.append("id").append("{").append(id).append("},");
        //sb.append("uuid").append("{").append(uuid).append("},");
        //sb.append("creationDate").append("{").append(creationDate).append("},");
        //sb.append("modifiedDate").append("{").append(modifiedDate).append("},");
        sb.append(context.getString(R.string.name)).append(":").append(displayName);
        if ((call != null) && call.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.call)).append(":").append(call);
        }
        if (send != null && send.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.send)).append(":").append(send);
        }
        //sb.append("imagePath").append("{").append(imagePath).append("},");
        if (note != null && note.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.note)).append(":").append(note);
        }
        //sb.append("isArchive").append("{").append(isArchive).append("},");
        //sb.append("isTrash").append("{").append(isTrash).append("},");
        //sb.append("color").append("{").append(color.name()).append("},");
        //sb.append("recent").append("{").append(recent).append("},");
        String s = sb.toString();
        sb.delete(0, sb.length());
        return s;
    }

    /**
     * CSVに変換する
     *
     * @return CSV
     */
    public String toCSV() {
        String result;
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(id).append("\"").append(","); //id
        sb.append("\"").append(uuid).append("\"").append(","); //uuid
        sb.append("\"").append(creationDate).append("\"").append(","); //createDate
        sb.append("\"").append(modifiedDate).append("\"").append(","); //modifiedDate
        sb.append("\"").append(displayName).append("\"").append(","); //displayName
        sb.append("\"").append(call).append("\"").append(","); //tel
        sb.append("\"").append(send).append("\"").append(","); //email
        sb.append("\"").append(imagePath).append("\"").append(","); //imagePath
        sb.append("\"").append(note).append("\"").append(","); //note
        sb.append("\"").append(isArchive).append("\"").append(","); //isArchive
        sb.append("\"").append(isTrash).append("\"").append(","); //isTrash
        sb.append("\"").append(color.name()).append("\"").append(","); //color
        sb.append("\"").append(timestamp).append("\"").append("\n"); //recent
        result = sb.toString();
        sb.delete(0, sb.length());
        return result;
    }

    /**
     * 連絡先一覧用に変換
     *
     * @return 人
     */
    public SimplePerson toContact() {
        title = displayName;
        subtitle = DateFormat.getDateTimeInstance().format(creationDate);
        isSimpled = true;
        return this;
    }

    /**
     * ノート一覧用に変換
     *
     * @return 人
     */
    public SimplePerson toNote() {
        title = displayName;
        subtitle = note;
        isSimpled = false;
        return this;
    }

    /**
     * メール一覧用に変換
     *
     * @return 人
     */
    public SimplePerson toSend() {
        title = displayName;
        subtitle = send;
        isSimpled = false;
        return this;
    }

    /**
     * 電話一覧用に変換
     *
     * @return 人
     */
    public SimplePerson toCall() {
        title = displayName;
        subtitle = call;
        isSimpled = false;
        return this;
    }

    /**
     * 最近一覧用に変換
     *
     * @return 人
     */
    public SimplePerson toRecent() {
        title = displayName;
        subtitle = DateFormat.getDateTimeInstance().format(timestamp);
        isSimpled = false;
        return this;
    }

    /**
     * JSONオブジェクトに変換する
     *
     * @return jsonオブジェクト
     */
    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("uuid",uuid);
            object.put("creationDate",creationDate);
            object.put("modifiedDate",modifiedDate);
            object.put("displayName",displayName);
            object.put("call", call);
            object.put("send", send);
            object.put("imagePath", imagePath);
            object.put("note", note);
            object.put("isArchive", isArchive);
            object.put("isTrash", isTrash);
            object.put("color", color.name());
            object.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * コンストラクタ
     *
     * @param object jsonオブジェクト
     */
    public SimplePerson(JSONObject object) {
        try {
            id = object.getString("id");
            uuid = object.getString("uuid");
            creationDate = object.getLong("creationDate");
            modifiedDate = object.getLong("modifiedDate");
            displayName = object.getString("displayName");
            call = object.getString("call");
            send = object.getString("send");
            imagePath = object.getString("imagePath");
            note = object.getString("note");
            isArchive = object.getBoolean("isArchive");
            isTrash = object.getBoolean("isTrash");
            color = COLOR.valueOf(object.getString("color"));
            timestamp = object.getLong("timestamp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
