package jp.osaka.appppy.people.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;

/**
 * Created by appy_000 on 2017/08/06.
 */

public class BitmapHelper {
    /**
     * Uriから指定されたサイズを下回らない最小のサイズのBitmapを生成します。
     * inSampleSizeが整数でしか倍率を指定できないのでぴったりにはなりません。
     *
     * @param uri   画像のUri
     * @param width 縮小後のサイズ
     * @return Bitmap画像
     */
    public static Bitmap decodeUri(Context context, Uri uri, int width) {
        try {
            // 縮小する倍率を計算する
            int sampleSize = calcSampleSize(context, uri, width);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // 縮小する倍率をセット
            options.inSampleSize = sampleSize;

            InputStream is = context.getContentResolver().openInputStream(uri);
            // Bitmapを生成
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                is.close();
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 縮小する倍率を計算します。
     * 具体的には、指定されたサイズを下回らない最小のサイズに
     * なるような倍率を計算します。
     *
     * @param context コンテキスト
     * @param uri     画像のuri
     * @param size    縮小後のサイズ
     * @return 縮小する倍率
     */
    private static int calcSampleSize(Context context, Uri uri, int size) {
        int sampleSize = 1;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Bitmapは生成せずに画像のサイズを測るだけの設定
            options.inJustDecodeBounds = true;
            // 測定
            BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                is.close();
            }

            // 画像サイズを指定されたサイズで割る
            // int同士の除算なので自動的に小数点以下は切り捨てられる
            if (options.outWidth <= 0) {
                options.outWidth = 1;
            }
            if (size <= 0) {
                size = 1;
            }
            sampleSize = options.outWidth / size;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sampleSize;
    }

    /**
     * Uriから指定されたサイズを下回らない最小のサイズのBitmapを生成します。
     * inSampleSizeが整数でしか倍率を指定できないのでぴったりにはなりません。
     *
     * @param pathName 画像のファイルパス
     * @return Bitmap画像
     */
    public static Bitmap decodeFile(String pathName) {
        Bitmap result = null;

        try {
            // 縮小する倍率を計算する
            int sampleSize = calcSampleSize(pathName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // 縮小する倍率をセット
            options.inSampleSize = sampleSize;

            // Bitmapを生成
            result = BitmapFactory.decodeFile(pathName, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 縮小する倍率を計算します。
     * 具体的には、指定されたサイズを下回らない最小のサイズに
     * なるような倍率を計算します。
     *
     * @param pathName 画像のファイルパス
     * @return 縮小する倍率
     */
    private static int calcSampleSize(String pathName) {
        int sampleSize = 1;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Bitmapは生成せずに画像のサイズを測るだけの設定
            options.inJustDecodeBounds = true;
            // 測定
            BitmapFactory.decodeFile(pathName, options);

            // 画像サイズを指定されたサイズで割る
            // int同士の除算なので自動的に小数点以下は切り捨てられる
            //       sampleSize = options.outWidth / size;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleSize;
    }


    /**
     * UriからPathへの変換処理
     *
     * @param uri URI
     * @return パス
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String toPath(Context context, Uri uri) {
        String path = INVALID_STRING_VALUE;

        try {
            ContentResolver contentResolver = context.getContentResolver();
            String[] columns = {MediaStore.Images.Media._ID};
            Cursor cursor = contentResolver.query(uri, columns, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(0);
                cursor.close();
            }
            if (path != null) {
                return path;
            }

            path = getImageUrlWithAuthority(context, uri);
            if (path == null) {
                path = uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        String path = null;
        try {
            if (uri.getAuthority() != null) {
                try {
                    is = context.getContentResolver().openInputStream(uri);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    Uri tempUri = writeToTempImageAndGetPathUri(context, bmp);
                    ContentResolver contentResolver = context.getContentResolver();
                    String[] columns = {MediaStore.Images.Media._ID, MediaStore.Files.FileColumns._ID};
                    Cursor cursor = contentResolver.query(tempUri, columns, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        path = cursor.getString(0);
                        cursor.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Uri取得
     *
     * @param inContext コンテキスト
     * @param inImage 画像
     * @return Uri
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        Uri result = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            //String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            //result = Uri.parse(path);

            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.TITLE, "Title");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            result = inContext.getContentResolver().insert(collection, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
