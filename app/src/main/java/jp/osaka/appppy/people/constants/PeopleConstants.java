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

package jp.osaka.appppy.people.constants;

/**
 * 定数
 */
public final class PeopleConstants {
    /**
     * @serial パッケージ名
     */
    private static final String PACKAGE = "jp.osaka.appppy.people";

    /**
     * @serial モデル
     */
    public static final String MODEL = "SH-04F";

    /**
     * @serial 最大数
     */
    public static final int MAX = 100;

    /**
     * @serial フローティングアクションボタン非表示タイムアウト
     */
    public static final int TIMEOUT_FLOATING_ACTION_BUTTON_HIDE = 1000;

    /*
     * 不定値
     */

    /**
     * @serial LONG不定値
     */
    public static final long INVALID_LONG_VALUE = -999L;

    /**
     * @serial STRING不定値
     */
    public static final String INVALID_STRING_VALUE = "";

    /*
     * インテントアクション
     */

    /*
     * カテゴリ
     */

    public static final String EXTRA_KEY =
            PACKAGE + ".EXTRA_KEY";

    /*
     * エキストラ
     */

    /**
     * @serial UUID
     */
    public static final String EXTRA_UUID =
            PACKAGE + ".EXTRA_UUID";

    /**
     * @serial 作成日
      */
    public static final String EXTRA_CREATION_DATE =
            PACKAGE + ".EXTRA_CREATION_DATE";

    /**
     * @serial 変更日
     */
    public static final String EXTRA_MODIFIED_DATE =
            PACKAGE + ".EXTRA_MODIFIED_DATE";

    /**
     * @serial 表示名
     */
    public static final String EXTRA_DISPLAY_NAME =
            PACKAGE + ".EXTRA_DISPLAY_NAME";

    /**
     * @serial 電話番号
     */
    public static final String EXTRA_TEL =
            PACKAGE + ".EXTRA_TEL";

    /**
     * @serial E-mail
     */
    public static final String EXTRA_EMAIL =
            PACKAGE + ".EXTRA_EMAIL";

    /**
     * @serial ノート
     */
    public static final String EXTRA_NOTE =
            PACKAGE + ".EXTRA_NOTE";

    /**
     * @serial アーカイブの有無
     */
    public static final String EXTRA_IS_ARCHIVE =
            PACKAGE + ".EXTRA_IS_ARCHIVE";

    /**
     * @serial ゴミ箱の有無
     */
    public static final String EXTRA_IS_TRASH =
            PACKAGE + ".EXTRA_IS_TRASH";

    /**
     * @serial 画像
     */
    public static final String EXTRA_IMAGE_PATH =
            PACKAGE + ".EXTRA_IMAGE_PATH";

    /**
     * @serial シンプルパーソン
     */
    public static final String EXTRA_SIMPLE_PERSON =
            PACKAGE + ".EXTRA_SIMMPLE_PERSON";

    /**
     * @serial シンプルピープル
     */
    public static final String EXTRA_PEOPLE =
            PACKAGE + ".EXTRA_PEOPLE";

    /**
     * @serial 履歴
     */
    public static final String EXTRA_HISTORY =
            PACKAGE + ".EXTRA_HISTORY";

    /**
     * @serial レイアウト
     */
    public static final String EXTRA_LAYOUT_ID =
            PACKAGE + ".EXTRA_LAYOUT_ID";

    /**
     * @serial ソート
     */
    public static final String EXTRA_SORT_ID =
            PACKAGE + ".EXTRA_SORT_ID";

    /**
     * @serial ファイル名
     */
    public static final String EXTRA_FILE_NAME =
            PACKAGE + ".EXTRA_FILE_NAME";

    /**
     * @serial ファイル
     */
    public static final String EXTRA_FILE =
            PACKAGE + ".EXTRA_FILE";

    /**
     * @serial 色
     */
    public static final String EXTRA_COLOR =
            PACKAGE + ".EXTRA_COLOR";

    /**
     * @serial タイムスタンプ
     */
    public static final String EXTRA_TIMESTAMP =
            PACKAGE + ".EXTRA_TIMESTAMP";

    /**
     * @serial 結果要求
     */
    public static final String EXTRA_RESULT =
            PACKAGE + ".EXTRA_RESULT";

    /**
     * @serial 連絡先の色
     */
    public static final String EXTRA_COLOR_OF_CONTACT =
            PACKAGE + ".EXTRA_COLOR_OF_CONTACT";

    /**
     * @serial 最近使用した項目の色
     */
    public static final String EXTRA_COLOR_OF_RECENT =
            PACKAGE + ".EXTRA_COLOR_OF_RECENT";

    /**
     * @serial 電話の色
     */
    public static final String EXTRA_COLOR_OF_CALL =
            PACKAGE + ".EXTRA_COLOR_OF_CALL";

    /**
     * @serial メールの色
     */
    public static final String EXTRA_COLOR_OF_SEND =
            PACKAGE + ".EXTRA_COLOR_OF_SEND";

    /**
     * @serial ノートの色
     */
    public static final String EXTRA_COLOR_OF_NOTE =
            PACKAGE + ".EXTRA_COLOR_OF_NOTE";

    /**
     * @serial アーカイブの色
     */
    public static final String EXTRA_COLOR_OF_ARCHIVE =
            PACKAGE + ".EXTRA_COLOR_OF_ARCHIVE";

    /**
     * @serial ゴミ箱の色
     */
    public static final String EXTRA_COLOR_OF_TRASH =
            PACKAGE + ".EXTRA_COLOR_OF_TRASH";

    /**
     * @serial ファイルの色
     */
    public static final String EXTRA_COLOR_OF_FILES =
            PACKAGE + ".EXTRA_COLOR_OF_FILES";

    /**
     * レイアウト
     */
    public static final int LAYOUT_LINEAR = 0;
    public static final int LAYOUT_GRID = LAYOUT_LINEAR + 1;
    public static final int DEFAULT_LAYOUT = LAYOUT_LINEAR;

    /**
     * ソート
     */
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_DATE_CREATED = SORT_BY_NAME + 1;
    public static final int SORT_BY_DATE_MODIFIED = SORT_BY_DATE_CREATED + 1;

    /**
     * @serial コンテンツ
     */
    public static final String EXTRA_CONTENT =
            PACKAGE + ".EXTRA_CONTENT";
}
