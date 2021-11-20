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
public enum ActivityTransition {
    REQUEST_PERMISSIONS,
    REQUEST_CREATE_ITEM,
    REQUEST_EDIT_ITEM,
    REQUEST_DETAIL_ITEM,
    REQUEST_CHOOSER,
    REQUEST_CONTACTS_PERMISSION,
    REQUEST_SYNC_FILE,
    REQUEST_OPEN_FILE,
    REQUEST_OPEN_HISTORY;
    /**
     * 値に合致する enum 定数を返す。
     *
     * @param index インデックス
     * @return メッセージ
     */
    public static ActivityTransition get(int index) {
        // 値から enum 定数を特定して返す処理
        for (ActivityTransition request : ActivityTransition.values()) {
            if (request.ordinal() == index) {
                return request;
            }
        }
        return null; // 特定できない場合
    }
}
