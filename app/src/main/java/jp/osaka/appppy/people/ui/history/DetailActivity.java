package jp.osaka.appppy.people.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.constants.RESULT;
import jp.osaka.appppy.people.databinding.ActivityHistoryDetailBinding;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.ui.files.DetailListHelper;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_CREATE_ITEM;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_FILE_NAME;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_PEOPLE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_RESULT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;


/**
 * 詳細アクティビティ
 */
public class DetailActivity extends AppCompatActivity implements
        DetailListFragment.Callbacks<SimplePerson> {

    /**
     * @serial タグ
     */
    private final String TAG = "DetailActivity";

    /**
     * @serial データセット
     */
    private ArrayList<SimplePerson> mDataSet;

    /**
     * @serial 状態
     */
    private final PeopleState mState = new PeopleState();

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param item    項目
     * @return インテント
     */
    public static Intent createIntent(Context context, ArrayList<SimplePerson> item, String name) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_PEOPLE, item);
        bundle.putString(EXTRA_FILE_NAME, name);
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
        mDataSet = intent.getParcelableArrayListExtra(EXTRA_PEOPLE);
        String name = intent.getStringExtra(EXTRA_FILE_NAME);

        // レイアウトの設定
        ActivityHistoryDetailBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_detail);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(name);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // 表示の設定
        setView();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        mState.setResumed(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        mState.setResumed(false);

        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // メニュー設定
        getMenuInflater().inflate(R.menu.history_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.menu_open: {
                setResult();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }
        // 結果確認
        if (requestCode == REQUEST_CREATE_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // データの取得
                Bundle bundle = data.getExtras();
                assert bundle != null;
                mDataSet = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                setView();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave()");
        }
    }

    /**
     * 表示の設定
     */
    private void setView() {
        // 一覧表示の更新
        updateCollectionView(DetailListHelper.toList(mDataSet));
    }

    /**
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimplePerson> collection) {
        // 一覧表示の取得
        ICollectionView<SimplePerson> mCollectionView = DetailListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_PEOPLE, mDataSet);
        bundle.putString(EXTRA_RESULT, RESULT.FINISH.name());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
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
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    @Override
    public void onSelectedMore(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item) {
        // 処理なし
    }

    /**
     * 項目の選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    @Override
    public void onSelected(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item) {
        // 処理なし
    }

    /**
     * 項目の選択状態の変更
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     * @param collection     一覧
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item, Collection<? extends SimplePerson> collection) {
        // 処理なし
    }

    /**
     * 項目のスワイプ
     *
     * @param collectionView 一覧表示
     * @param item           項目
     */
    @Override
    public void onSwiped(ICollectionView<SimplePerson> collectionView, SimplePerson item) {
        // 処理なし
    }

    /**
     * スクロール開始
     *
     * @param view 一覧表示
     */
    @Override
    public void onScroll(ICollectionView<SimplePerson> view) {
        // 処理なし
    }

    /**
     * スクロール終了
     *
     * @param view 一覧表示
     */
    @Override
    public void onScrollFinished(ICollectionView<SimplePerson> view) {
        // 処理なし
    }

    /**
     * 移動変化通知
     *
     * @param view   　　 一覧表示
     * @param collection 一覧
     */
    @Override
    public void onMoveChanged(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // 処理なし
    }

    /**
     * 更新通知
     *
     * @param view   　　 一覧表示
     * @param collection 一覧
     */
    @Override
    public void onUpdated(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // 処理なし
    }
}
