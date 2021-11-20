package jp.osaka.appppy.people.ui.content.call;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.ISearchCollectionCallbacks;
import jp.osaka.appppy.people.android.view.ISearchCollectionView;
import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.databinding.ActivityCallSearchBinding;
import jp.osaka.appppy.people.service.PeopleClient;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.service.history.HistoryProxy;
import jp.osaka.appppy.people.ui.Action;
import jp.osaka.appppy.people.ui.content.BaseAdmobActivity;
import jp.osaka.appppy.people.ui.content.SearchListFragment;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.R.drawable.search_frame;
import static jp.osaka.appppy.people.constants.ACTION.CHANGE;
import static jp.osaka.appppy.people.constants.ACTION.INSERT;
import static jp.osaka.appppy.people.constants.COLOR.AMBER;
import static jp.osaka.appppy.people.constants.COLOR.BLUE;
import static jp.osaka.appppy.people.constants.COLOR.BLUE_GREY;
import static jp.osaka.appppy.people.constants.COLOR.BROWN;
import static jp.osaka.appppy.people.constants.COLOR.DEEP_ORANGE;
import static jp.osaka.appppy.people.constants.COLOR.DEEP_PURPLE;
import static jp.osaka.appppy.people.constants.COLOR.GREEN;
import static jp.osaka.appppy.people.constants.COLOR.INDIGO;
import static jp.osaka.appppy.people.constants.COLOR.LIGHT_GREEN;
import static jp.osaka.appppy.people.constants.COLOR.LIME;
import static jp.osaka.appppy.people.constants.COLOR.ORANGE;
import static jp.osaka.appppy.people.constants.COLOR.PINK;
import static jp.osaka.appppy.people.constants.COLOR.PURPLE;
import static jp.osaka.appppy.people.constants.COLOR.RED;
import static jp.osaka.appppy.people.constants.COLOR.WHITE;
import static jp.osaka.appppy.people.constants.COLOR.YELLOW;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_CALL;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.ui.content.call.CallHelper.toList;
import static jp.osaka.appppy.people.ui.content.call.CallHelper.toListOf;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Line;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startEditActivity;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;

/**
 * サーチアクティビティ
 */
public class SearchActivity extends BaseAdmobActivity implements
        PeopleClient.Callbacks,
        ISearchCollectionCallbacks<SimplePerson> {

    /**
     * @serial 状態
     */
    private final PeopleState mState = new PeopleState();

    /**
     * @serial データセット
     */
    private final ArrayList<SimplePerson> mDataSet = new ArrayList<>();

    /**
     * @serial バックアップ
     */
    private final ArrayList<SimplePerson> mBackup = new ArrayList<>();

    /**
     * @serial クライアント
     */
    private final PeopleClient mClient = new PeopleClient(this, this);

    /**
     * @serial クライアント
     */
    private final HistoryProxy mProxy = new HistoryProxy(this);

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial バインディング
     */
    private ActivityCallSearchBinding mBinding;

    /**
     * @serial 自身
     */
    private Activity mSelf;

    /**
     * @serial 戻る
     */
    private final ArrayList<Action> mUndos = new ArrayList<>();

    /**
     * @serial 一覧表示
     */
    private ISearchCollectionView<SimplePerson> mCollectionView;

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @return インテント
     */
    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(Object object, final List<SimplePerson> people) {
        mHandler.post(() -> {

            try {
                boolean result;
                result = mDataSet.isEmpty() || mDataSet.size() != people.size() || !mDataSet.equals(people);
                if (result) {
                    // レジューム状態の確認
                    if (!mState.isResumed()) {
                        return;
                    }

                    // データ設定
                    mDataSet.clear();
                    mDataSet.addAll(people);
                    // データ更新
                    updateView(toListOf(toList(mDataSet), Objects.requireNonNull(mBinding.editSearch.getText()).toString()));

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mBinding.editSearch, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "SearchActivity";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        mSelf = this;

        // プリファレンスの設定
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // テーマの設定
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_CALL, COLOR.LIGHT_BLUE.name()));
        setTheme(ThemeHelper.getTheme(color));

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_call_search);

        setSupportActionBar(mBinding.toolbar);
        initActionBar();

        //mBinding.editSearch.setInputType(InputType.TYPE_CLASS_PHONE);
        mBinding.editSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 処理なし
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // レジューム状態の確認
                if(!mState.isResumed()) {
                    return;
                }
                // 一覧表示の更新
                updateView(toListOf(toList(mDataSet), charSequence.toString()));
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable editable) {
                // 処理なし
            }
        });
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

        // レジューム状態の設定
        mState.setResumed(true);

        // サービスの接続
        mClient.connect();
        mProxy.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // レジューム状態の設定
        mState.setResumed(false);

        // サービスの非接続
        mProxy.disconnect();
        mClient.disconnect();
        super.onPause();
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

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 識別子ごとの処理
        if (id == android.R.id.home) {
            finishActivity();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 画面表示終了
     */
    private void finishActivity() {
        Intent intent = getIntent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
    }

    /**
     * 画面表示更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimplePerson> collection) {
        // 空表示の更新
        updateEmptyView(collection);
        // 一覧表示の更新
        updateCollectionView(collection);
    }

    /**
     * 空表示更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimplePerson> collection) {
        // 空表示の更新
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            // 空の場合
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            // 空でない場合
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 一覧画面更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimplePerson> collection) {
        ISearchCollectionView<SimplePerson> collectionView = SearchListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) collectionView)
                .commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ISearchCollectionView<SimplePerson> collectionView, View view, SimplePerson item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // タイムスタンプを保存
        item.timestamp = System.currentTimeMillis();
        for (SimplePerson dest : mDataSet) {
            if (dest.equal(item)) {
                // データの変更
                dest.copyPerson(item);
            }
        }
        // 設定
        mClient.setPeople(mDataSet);

        // 電話番号アプリの呼び出し
        try {
            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + item.call));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * アクションバー初期化
     */
    private void initActionBar() {
        // ToolBarの場合はもっとスマートなやり方があるかもしれません。
        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(ContextCompat.getDrawable(this, search_frame));
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        finishActivity();
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
    public void onSelectedMore(ISearchCollectionView<SimplePerson> collectionView, final View view, final SimplePerson item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    // BEGIN_INCLUDE(show_popup)
    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(final View view, final SimplePerson item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.main_search_more, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_info: {
                    // タイムスタンプを保存
                    item.timestamp = System.currentTimeMillis();
                    for (SimplePerson dest : mDataSet) {
                        if (dest.equal(item)) {
                            dest.copyPerson(item);
                        }
                    }
                    mClient.setPeople(mDataSet);
                    startDetailActivity_from_Line(mSelf, mSelf, item);
                    return true;
                }
                case R.id.menu_edit: {
                    // タイムスタンプを保存
                    item.timestamp = System.currentTimeMillis();
                    for (SimplePerson dest : mDataSet) {
                        if (dest.equal(item)) {
                            dest.copyPerson(item);
                        }
                    }
                    mClient.setPeople(mDataSet);
                    startEditActivity(mSelf, mSelf, item);
                    return true;
                }
                case R.id.menu_archive: {
                    // 選択したアーカイブの解除
                    item.isArchive = true;
                    mHandler.post(new ModifyRunner(item));
                    return true;
                }
                case R.id.menu_trash: {
                    // 選択したアーカイブの解除
                    item.isTrash = true;
                    mHandler.post(new ModifyRunner(item));
                    return true;
                }
                case R.id.menu_share: {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TITLE, item.displayName);
                        intent.putExtra(Intent.EXTRA_SUBJECT, item.displayName);
                        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, item.call);
                        intent.setType("text/plain");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                case R.id.menu_white: {
                    item.color = WHITE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_red: {
                    item.color = RED;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_pink: {
                    item.color = PINK;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_purple: {
                    item.color = PURPLE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_deep_purple: {
                    item.color = DEEP_PURPLE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_indigo: {
                    item.color = INDIGO;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_blue: {
                    item.color = BLUE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_green: {
                    item.color = GREEN;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_light_green: {
                    item.color = LIGHT_GREEN;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_lime: {
                    item.color = LIME;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_yellow: {
                    item.color = YELLOW;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_amber: {
                    item.color = AMBER;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_orange: {
                    item.color = ORANGE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_deep_orange: {
                    item.color = DEEP_ORANGE;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_brown: {
                    item.color = BROWN;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                case R.id.menu_blue_grey: {
                    item.color = BLUE_GREY;
                    item.imagePath = INVALID_STRING_VALUE;
                    mHandler.post(new ChangeRunner(item));
                    return true;
                }
                default: {
                    break;
                }
            }
            return false;
        });

        // Finally show the PopupMenu
        popup.show();
    }
    // END_INCLUDE(show_popup)

    /**
     * 更新通知
     *
     * @param view       一覧表示
     * @param collection 一覧
     */
    @Override
    public void onUpdated(ISearchCollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // 処理なし
    }

    /**
     * 一覧画面表示設定
     *
     * @param mCollectionView 一覧画面表示
     */
    public void setmCollectionView(ISearchCollectionView<SimplePerson> mCollectionView) {
        this.mCollectionView = mCollectionView;
    }

    /**
     * 編集
     */
    private class ModifyRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimplePerson mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ModifyRunner(SimplePerson item) {
            mItem = item;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // バックアップ
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // データの設定
                for (SimplePerson dest : mDataSet) {
                    // 一致
                    if (dest.equal(mItem)) {
                        // データの変更
                        dest.copyPerson(mItem);
                    }
                }
                // 一覧表示の更新
                mUndos.clear();
                if (mCollectionView != null) {
                    String message;
                    int position;
                    if (mItem != null) {
                        if (mItem.isArchive) {
                            // 履歴に保存
                            message = getString(R.string.moved_to_archive_item);
                            //   notifyHistory(message, mItem);
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toNote());
                            // 戻す処理に追加
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else if (mItem.isTrash) {
                            // 履歴に保存
                            message = getString(R.string.moved_to_trash_item);
                            //  notifyHistory(message, mItem);
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toNote());
                            // 戻す処理に追加
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else {
                            // 履歴に保存
                            message = getString(R.string.modified_item);
                            //   notifyHistory(message, mItem);
                            position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toNote());
                            mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                        }
                        Collections.reverse(mUndos);
                        // 空表示の更新
                        updateEmptyView(CallHelper.toList(mDataSet));
                        // 設定
                        mClient.setPeople(mDataSet);
                        // スナックバーの生成
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * スナックバーの生成
     *
     * @param layout  レイアウト
     * @param message メッセージ
     */
    private void makeUndoSnackbar(CoordinatorLayout layout, String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(View v) {

                        // レジューム状態の確認
                        if(!mState.isResumed()) {
                            return;
                        }

                        // 一覧表示が空でない場合
                        if (mCollectionView != null) {
                            // 一覧表示の更新
                            try {
                                for (Action undo : mUndos) {
                                    switch (undo.action) {
                                        case INSERT: {
                                            // 一覧表示の更新
                                            mCollectionView.insert(undo.arg, undo.object.toCall());
                                            break;
                                        }
                                        case CHANGE: {
                                            // 一覧表示の更新
                                            mCollectionView.change(undo.object.toCall());
                                            break;
                                        }
                                        case REMOVE: {
                                            // 一覧表示の更新
                                            mCollectionView.remove(undo.object.toCall());
                                            break;
                                        }

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 一覧表示の更新
                                updateCollectionView(mBackup);
                            }
                            mUndos.clear();
                        }

                        ArrayList<SimplePerson> list = CallHelper.toList(mBackup);
                        // 空表示の更新
                        updateEmptyView(list);

                        // データ設定
                        mDataSet.clear();
                        copyPeople(mDataSet, mBackup);

                        // 設定
                        mClient.setPeople(mDataSet);
                    }
                })
                .show();
    }

    /**
     * 変更
     */
    private class ChangeRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimplePerson mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ChangeRunner(SimplePerson item) {
            mItem = item;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // 一覧表示が空でなく
                // 項目が空でない場合
                if ((mCollectionView != null) && (mItem != null)) {

                    // 設定
                    for (SimplePerson dest : mDataSet) {
                        // 一致
                        if (dest.equal(mItem)) {
                            dest.copyPerson(mItem);
                        }
                    }

                    // 一覧表示の更新
                    mCollectionView.change(getPerson(mItem.uuid, mDataSet).toNote());

                    // 設定
                    mClient.setPeople(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}