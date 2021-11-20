package jp.osaka.appppy.people.ui.content;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.constants.RESULT;
import jp.osaka.appppy.people.databinding.ActivityImageCreateBinding;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_CHOOSER;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_PERMISSIONS;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_RESULT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.utils.BitmapHelper.decodeFile;
import static jp.osaka.appppy.people.utils.BitmapHelper.decodeUri;
import static jp.osaka.appppy.people.utils.BitmapHelper.toPath;
import static jp.osaka.appppy.people.utils.ThemeHelper.getAccentColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getImageEditTheme;
import static jp.osaka.appppy.people.utils.ThemeHelper.getImagePrimaryColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getImagePrimaryDarkColor;

/**
 * 作成アクティビティ
 */
public class ImageEditActivity extends AppCompatActivity {

    /**
     * @serial タグ
     */
    private final String TAG = "ImageEditActivity";

    /**
     * @serial バインディング
     */
    private ActivityImageCreateBinding mBinding;

    /**
     * @serial データセット
     */
    private SimplePerson mDataSet;

    /**
     * @serial バックアップ
     */
    private SimplePerson mBackup;

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param item 項目
     * @return インテント
     */
    public static Intent createIntent(Context context, SimplePerson item) {
        Intent intent = new Intent(context, ImageEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SIMPLE_PERSON, item);
        intent.putExtras(bundle);
        return intent;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // インテントの取得
        Intent intent = getIntent();
        mDataSet = intent.getParcelableExtra(EXTRA_SIMPLE_PERSON);
        mBackup = SimplePerson.createInstance();
        mBackup.copyPerson(mDataSet);

        // テーマの設定
        setTheme(getImageEditTheme(mDataSet.color));

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_create);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // フローティングアクションボタンの設定
        setupFloatingActionButton();

        // 表示の設定
        setView();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * 表示の設定
     */
    void setView() {
        if (mDataSet.imagePath.equals(INVALID_STRING_VALUE)) {
            mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER);
            mBinding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_image_black_100dp));
            mBinding.imageView.setColorFilter(getImagePrimaryDarkColor(this, mDataSet.color));
            mBinding.imageView.setBackgroundColor(getImagePrimaryColor(this, mDataSet.color));
        } else {
            try {
                mBinding.imageView.setColorFilter(null);
                // 画像を縮小して取得
                Bitmap bitmap = decodeFile(mDataSet.imagePath);
                // imageViewの初期化
                mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mBinding.imageView.setImageURI(null);
                // ImageViewにセット
                mBinding.imageView.setImageBitmap(bitmap);
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
            }
        }

        mBinding.detailContainer.iconDisplayName.setColorFilter(getAccentColor(this, mDataSet.color));
        mBinding.detailContainer.icon1.setColorFilter(getAccentColor(this, mDataSet.color));
        mBinding.detailContainer.icon2.setColorFilter(getAccentColor(this, mDataSet.color));
        mBinding.detailContainer.icon3.setColorFilter(getAccentColor(this, mDataSet.color));

        mBinding.detailContainer.editName.setText(mDataSet.displayName);
        mBinding.detailContainer.editTel.setText(mDataSet.call);
        mBinding.detailContainer.editEmail.setText(mDataSet.send);
        mBinding.detailContainer.editNote.setText(mDataSet.note);
    }

    /**
     * 権限の確認
     *
     * @param activity   アクティビティ
     * @return 権限の有無
     */

    private boolean hasSelfPermission(Activity activity) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // version 7.2 bug fixed
        if (permissions.length == 0 || grantResults.length == 0) {
            invisibleFab();
        } else if (requestCode == REQUEST_PERMISSIONS.ordinal()
                && Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0])) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                visibleFab();
            } else {
                invisibleFab();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * フローティングアクションボタンの設定
     */
    protected void setupFloatingActionButton() {
        if (!hasSelfPermission(this)) {
            // 使用許可がない場合はこれをリクエストする
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS.ordinal());
            } else {
                invisibleFab();
            }
        } else {
            // 使用許可がある場合はフローティングボタンを設定
            visibleFab();
        }
    }

    /**
     * フローティングアクションボタンの有効化
     */
    @SuppressLint("IntentReset")
    private void visibleFab() {
        mBinding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "chooser"), REQUEST_CHOOSER.ordinal());

        });
        mBinding.fab.show();
    }

    /**
     * フローティングアクションボタンの無効化
     */
    private void invisibleFab() {
        mBinding.fab.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        //結果の設定
        setResult();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {//結果の設定
            setResult();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }
        // 結果確認
        if (requestCode == REQUEST_CHOOSER.ordinal()) {
            if (resultCode == RESULT_OK) {
                mBinding.imageView.setColorFilter(null);
                mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                // 選択された画像のUriを取得
                Uri uri = data.getData();
                // 画像を縮小して取得
                Bitmap bitmap = decodeUri(this, uri, mBinding.imageView.getWidth());
                mBinding.imageView.setImageBitmap(bitmap);
                // 変更を保存
                mDataSet.imagePath = toPath(this, uri);
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave()");
        }
    }

    /**
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
        if (mBinding.detailContainer.editName.getText().toString().equals(INVALID_STRING_VALUE)) {
            setResult(RESULT_CANCELED, intent);
        } else if (mBinding.detailContainer.editName.getText().toString().equals(mDataSet.displayName)
                && mBinding.detailContainer.editTel.getText().toString().equals(mDataSet.call)
                && Objects.requireNonNull(mBinding.detailContainer.editEmail.getText()).toString().equals(mDataSet.send)
                && Objects.requireNonNull(mBinding.detailContainer.editNote.getText()).toString().equals(mDataSet.note)
                && mBackup.imagePath.equals(mDataSet.imagePath)
                ) {
            setResult(RESULT_CANCELED, intent);
        } else {
            mDataSet.displayName = mBinding.detailContainer.editName.getText().toString();
            mDataSet.note = Objects.requireNonNull(mBinding.detailContainer.editNote.getText()).toString();
            mDataSet.send = Objects.requireNonNull(mBinding.detailContainer.editEmail.getText()).toString();
            mDataSet.call = mBinding.detailContainer.editTel.getText().toString();
            mDataSet.modifiedDate = System.currentTimeMillis();
            // 変更ありの場合、編集日を最新にする
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_SIMPLE_PERSON, mDataSet);
            bundle.putString(EXTRA_RESULT, RESULT.FINISH.name());
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
    }
}
