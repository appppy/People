package jp.osaka.appppy.people.service.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * 履歴アクセス
 */
class HistoryAccessor {

    /**
     * @serial 最大数
     */
    private static final int MAX_HISTORY = 100;

    /**
     * 履歴の取得
     *
     * @return 履歴
     */
    static List<History> getData(Context context) {
        List<History> result = new ArrayList<>();

        try (Cursor cursor = context.getContentResolver().query(HistoryColumns.CONTENT_URI, null, null, null, null)) {
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    History history = new History();

                    int index = 1;
                    history.date = cursor.getLong(index++);
                    history.title = cursor.getString(index++);
                    history.message = cursor.getString(index);

                    result.add(history);
                }
            }
        } catch (IllegalArgumentException | UnsupportedOperationException | ClassCastException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        // 終了時にはCursorをcloseする
        return result;
    }

    /**
     * 履歴の挿入
     */
    static public void insert(Context context, History history) {
        int recordCnt;
        try {
            ContentValues insertValue = new ContentValues();

            insertValue.put(HistoryColumns.DATE, history.date);
            insertValue.put(HistoryColumns.TITLE, history.title);
            insertValue.put(HistoryColumns.MESSAGE, history.message);

            context.getContentResolver().insert(HistoryColumns.CONTENT_URI, insertValue);

            Cursor cursor = context.getContentResolver().query(HistoryColumns.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                return;
            }
            try {
                recordCnt = cursor.getCount();
                if (recordCnt == MAX_HISTORY) {
                    cursor.close();
                } else if (recordCnt > MAX_HISTORY) {
                    int count = recordCnt - MAX_HISTORY;

                    for (int i = 0; i < count; i++) {
                        cursor.moveToFirst();
                        int delIndex = cursor.getInt(0);

                        context.getContentResolver().delete(HistoryColumns.CONTENT_URI, HistoryColumns.ID + " = ?", new String[]{String.valueOf(delIndex)});
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 履歴の削除
     */
    static public void delete(Context context) {
        Cursor cursor = context.getContentResolver().query(HistoryColumns.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            cursor.moveToFirst();
            do {
                int delIndex = cursor.getInt(0);
                context.getContentResolver().delete(HistoryColumns.CONTENT_URI, HistoryColumns.ID + " = ?", new String[]{String.valueOf(delIndex)});
            } while(cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

}
