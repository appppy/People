package jp.osaka.appppy.people.ui.content.archive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.constants.ActivityTransition;
import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.databinding.ActivityArchiveBinding;
import jp.osaka.appppy.people.service.PeopleClient;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.service.history.HistoryProxy;
import jp.osaka.appppy.people.ui.Action;
import jp.osaka.appppy.people.ui.content.BaseAdmobActivity;
import jp.osaka.appppy.people.ui.content.ListFragment;
import jp.osaka.appppy.people.ui.content.ModuleFragment;
import jp.osaka.appppy.people.utils.PeopleHelper;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ACTION.INSERT;
import static jp.osaka.appppy.people.constants.ACTION.MODIFY;
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
import static jp.osaka.appppy.people.constants.DISPLAY.EMPTY;
import static jp.osaka.appppy.people.constants.DISPLAY.GRID;
import static jp.osaka.appppy.people.constants.DISPLAY.LINEAR;
import static jp.osaka.appppy.people.constants.PeopleConstants.DEFAULT_LAYOUT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_ARCHIVE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_CONTENT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_LAYOUT_ID;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.LAYOUT_GRID;
import static jp.osaka.appppy.people.constants.SELECTION.MULTI_SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED_ALL;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTING;
import static jp.osaka.appppy.people.constants.SELECTION.UNSELECTED;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.getStartActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Line;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Module;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startEditActivity;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;
import static jp.osaka.appppy.people.utils.SelectHelper.getSelectedCollection;

/**
 * メインアクティビティ
 */
public class MainActivity extends BaseAdmobActivity implements
        PeopleClient.Callbacks,
        ICollectionView.Callbacks<SimplePerson>,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        PeopleState.Callbacks {

    /**
     * @serial 状態
     */
    public PeopleState mState = new PeopleState();

    /**
     * @serial タグ
     */
    private final String TAG = "MainActivity";

    /**
     * @serial バインディング
     */
    private ActivityArchiveBinding mBinding;

    /**
     * @serial プリファレンス
     */
    private SharedPreferences mPref;

    /**
     * @serial 自身
     */
    private MainActivity mSelf;

    /**
     * @serial データセット
     */
    private final ArrayList<SimplePerson> mDataSet = new ArrayList<>();

    /**
     * @serial バックアップ
     */
    private final ArrayList<SimplePerson> mBackup = new ArrayList<>();

    /**
     * @serial 選択データセット
     */
    private final ArrayList<SimplePerson> mSelected = new ArrayList<>();

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
     * @serial 戻る
     */
    private final ArrayList<Action> mUndos = new ArrayList<>();

    /**
     * @serial 進む
     */
    private final ArrayList<Action> mRedos = new ArrayList<>();

    /**
     * @serial 表示
     */
    private ICollectionView<SimplePerson> mCollectionView;

    /**
     * @serial トグル
     */
    private ActionBarDrawerToggle mToggle;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 自身の取得
        mSelf = this;

        // プリファレンスの設定
        mPref = getDefaultSharedPreferences(this);

        // テーマの設定
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_ARCHIVE, BLUE_GREY.name()));
        setTheme(ThemeHelper.getTheme(color));

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_archive);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.archive);
        }

        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.archive);
        mState.registerCallacks(this);

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
    protected void onDestroy() {
        // 解除
        mState.unregisterCallacks();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }

        // 結果確認
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case REQUEST_DETAIL_ITEM:
            case REQUEST_EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    if(null != bundle) {
                        SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                        mRedos.clear();
                        mRedos.add(new Action(MODIFY, 0, item));
                    }
                }
                break;
            }
            default: {
                break;
            }
        }

        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 選択状態の取得
            switch (mState.getSelection()) {
                case SELECTED_ALL:
                case MULTI_SELECTED:
                case SELECTING:
                case SELECTED: {
                    // 選択解除
                    mHandler.post(new DiselectRunner());
                    break;
                }
                default: {
                    super.onBackPressed();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 選択状態の確認
        switch (mState.getSelection()) {
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    getMenuInflater().inflate(R.menu.archive_selected_all_one, menu);
                } else {
                    getMenuInflater().inflate(R.menu.archive_selected_all, menu);
                }
                break;
            }
            case SELECTED: {
                // 選択状態のメニュー設定
                getMenuInflater().inflate(R.menu.archive_selected, menu);
                break;
            }
            default: {
                // 未選択状態の空のメニュー設定
                // 未選択状態のメニュー設定
                if (mState.getDisplay() == EMPTY) {
                    getMenuInflater().inflate(R.menu.archive_empty, menu);
                } else {
                    getMenuInflater().inflate(R.menu.archive, menu);
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        // 識別子を取得
        int id = item.getItemId();
        // 識別子ごとの処理
        switch (id) {
            case R.id.menu_edit: {
                // タイムスタンプを保存
                SimplePerson person = mSelected.get(0);
                mSelected.clear();
                person.timestamp = System.currentTimeMillis();
                for (SimplePerson dest : mDataSet) {
                    if (dest.equal(person)) {
                        dest.copyPerson(person);
                    }
                }
                //mHandler.post(new DiselectRunner());
                mClient.setPeople(mDataSet);
                startEditActivity(this, this, person);
                return true;
            }
            case R.id.menu_info: {
                // タイムスタンプを保存
                SimplePerson person = mSelected.get(0);
                mSelected.clear();
                person.timestamp = System.currentTimeMillis();
                for (SimplePerson dest : mDataSet) {
                    if (dest.equal(person)) {
                        dest.copyPerson(person);
                    }
                }
                mClient.setPeople(mDataSet);
                //mHandler.post(new DiselectRunner());
                int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
                if (layout == LAYOUT_GRID) {
                    startDetailActivity_from_Module(this, this, person);
                } else {
                    startDetailActivity_from_Line(this, this, person);
                }
                return true;
            }
            case R.id.menu_selected_all: {
                // 全選択
                mHandler.post(new SelectedAllRunner());
                return true;
            }
            case R.id.menu_unarchive: {
                // 選択したアーカイブの解除
                mHandler.post(new SelectedUnArchiveRunner());
                return true;
            }
            case R.id.menu_white: {
                mHandler.post(new SelectedChangeColorRunner(WHITE));
                return true;
            }
            case R.id.menu_red: {
                mHandler.post(new SelectedChangeColorRunner(RED));
                return true;
            }
            case R.id.menu_pink: {
                mHandler.post(new SelectedChangeColorRunner(PINK));
                return true;
            }
            case R.id.menu_purple: {
                mHandler.post(new SelectedChangeColorRunner(PURPLE));
                return true;
            }
            case R.id.menu_deep_purple: {
                mHandler.post(new SelectedChangeColorRunner(DEEP_PURPLE));
                return true;
            }
            case R.id.menu_indigo: {
                mHandler.post(new SelectedChangeColorRunner(INDIGO));
                return true;
            }
            case R.id.menu_blue: {
                mHandler.post(new SelectedChangeColorRunner(BLUE));
                return true;
            }
            case R.id.menu_green: {
                mHandler.post(new SelectedChangeColorRunner(GREEN));
                return true;
            }
            case R.id.menu_light_green: {
                mHandler.post(new SelectedChangeColorRunner(LIGHT_GREEN));
                return true;
            }
            case R.id.menu_lime: {
                mHandler.post(new SelectedChangeColorRunner(LIME));
                return true;
            }
            case R.id.menu_yellow: {
                mHandler.post(new SelectedChangeColorRunner(YELLOW));
                return true;
            }
            case R.id.menu_amber: {
                mHandler.post(new SelectedChangeColorRunner(AMBER));
                return true;
            }
            case R.id.menu_orange: {
                mHandler.post(new SelectedChangeColorRunner(ORANGE));
                return true;
            }
            case R.id.menu_deep_orange: {
                mHandler.post(new SelectedChangeColorRunner(DEEP_ORANGE));
                return true;
            }
            case R.id.menu_brown: {
                mHandler.post(new SelectedChangeColorRunner(BROWN));
                return true;
            }
            case R.id.menu_blue_grey: {
                mHandler.post(new SelectedChangeColorRunner(BLUE_GREY));
                return true;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(Object object, final List<SimplePerson> people) {
        if (object instanceof PeopleClient) {
            mHandler.post(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    try {
                        boolean result;
                        // 空の場合
                        result = mDataSet.isEmpty() || mDataSet.size() != people.size() || !mDataSet.equals(people);
                        if (result) {
                            // レジューム状態の確認
                            if(!mState.isResumed()) {
                                return;
                            }
                            // データ設定
                            mDataSet.clear();
                            mDataSet.addAll(people);
                            // データ更新
                            updateView(toList(mDataSet));
                        }

                        // 次の動作を指定
                        for (Action doAction : mRedos) {
                            if (doAction.action.equals(MODIFY)) {
                                if (!doAction.object.isArchive) {
                                    UnArchiveRunner runner = new UnArchiveRunner(doAction.object);
                                    mHandler.post(runner);
                                } else {
                                    SimplePerson dest = getPerson(doAction.object.uuid, mDataSet);
                                    if (!dest.equals(doAction.object)) {
                                        if (!PeopleHelper.isModified(dest, doAction.object)) {
                                            ChangeRunner runner = new ChangeRunner(doAction.object);
                                            mHandler.post(runner);
                                        }
                                    }
                                }
                            }
                        }
                        mRedos.clear();

                        // 選択状態を解除
                        mHandler.post(new DiselectRunner());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        mHandler.post(new DiselectRunner());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectChanged(PeopleState state) {
        mHandler.post(() -> {
            try {
                // 選択状態の取得
                switch (mState.getSelection()) {
                    case UNSELECTED: {
                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(false);
                        }

                        // 選択の解除
                        if (mCollectionView != null) {
                            mCollectionView.diselect();
                        }

                        // ナビゲーションドローワーの設定
                        enableNavigationDrawer();

                        // ツールバーの変更
                        updateTitle(toList(mDataSet));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));

                        // メニューを設定
                        invalidateOptionsMenu();

                        break;
                    }
                    // 何もしない
                    case SELECTED_ALL: {

                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);

                        // 全選択
                        if (mCollectionView != null) {
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());
                        }

                        // ツールバーの設定
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));

                        // メニュー更新
                        invalidateOptionsMenu();

                        break;
                    }
                    case MULTI_SELECTED:
                    case SELECTED: {

                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ツールバーの設定
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));

                        // メニュー更新
                        invalidateOptionsMenu();
                        break;
                    }
                    default: {
                        break;
                    }
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
    public void onDisplayChanged(PeopleState state) {
        // 処理なし
    }

    /**
     * ナビゲーションドローワ―の有効化
     */
    private void enableNavigationDrawer() {
        // ナビゲーションドローワ―の生成
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ナビゲーションドローワ―の設定
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * ナビゲーションドローワ―の無効化
     */
    private void disableNavigationDrawer() {
        // ナビゲーションドローワ―の設定解除
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mBinding.drawerLayout.removeDrawerListener(mToggle);
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * 表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimplePerson> collection) {
        // プログレスバーの終了
        updateProgressBar();
        // 一覧表示の更新
        int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
        updateCollectionView(layout, collection);
        // 空表示の更新
        updateEmptyView(collection);
        // タイトルの更新
        updateTitle(collection);
        // メニューの更新
        updateMenu(collection);
    }

    /**
     * プログレスバーの更新
     *
     */
    private void updateProgressBar() {
        // プログレスバーの更新
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * 空表示の更新
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
     * 一覧表示の更新
     *
     * @param id         識別子
     * @param collection 一覧
     */
    private void updateCollectionView(int id, ArrayList<SimplePerson> collection) {
        if (id == LAYOUT_GRID) {// 一覧表示の取得
            mCollectionView = ModuleFragment.newInstance(collection);
        } else {// 一覧表示の取得
            mCollectionView = ListFragment.newInstance(collection);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (androidx.fragment.app.Fragment) mCollectionView)
                .commit();
    }

    /**
     * タイトルの更新
     *
     * @param collection 一覧
     */
    private void updateTitle(ArrayList<SimplePerson> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.archive));
        } else {
            sb.append(this.getString(R.string.archive)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * メニューの更新
     *
     * @param collection 一覧
     */
    private void updateMenu(List<SimplePerson> collection) {
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            mState.changeDisplay(EMPTY);
        } else {
            int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
            if (layout == LAYOUT_GRID) {
                mState.changeDisplay(LINEAR);
            } else {
                mState.changeDisplay(GRID);
            }
        }
        invalidateOptionsMenu();
    }

    /**
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
     */
    @Override
    public void onSelectedMore(ICollectionView<SimplePerson> collectionView, final View view, final SimplePerson item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(final View view, final SimplePerson item) {
        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.archive_selected_all_one, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {

            // 項目を選択する
            mSelected.clear();
            mSelected.add(item);

            // 項目のメニューを選択する
            onOptionsItemSelected(menuItem);

            return false;
        });

        // Finally show the PopupMenu
        popup.show();
    }
    // END_INCLUDE(show_popup)


    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // タイムスタンプを保存
        item.timestamp = System.currentTimeMillis();
        for (SimplePerson dest : mDataSet) {
            if (dest.equal(item)) {
                dest.copyPerson(item);
            }
        }
        mClient.setPeople(mDataSet);

        // 詳細画面の表示
        int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
        if (layout == LAYOUT_GRID) {
            startDetailActivity_from_Module(this, this, view, item);
        } else {
            startDetailActivity_from_Line(this, this, view, item);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item, Collection<? extends SimplePerson> collection) {
        // 選択数の変更
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // 選択状態の確認
        if (mSelected.size() == collection.size()) {
            // 全選択
            if (!mState.changeSelection(SELECTED_ALL)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // マルチ選択
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // 選択
            if (!mState.changeSelection(SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else {
            // 非選択
            mState.changeSelection(UNSELECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimplePerson> collectionView, SimplePerson item) {
        // バックアップ
        mBackup.clear();
        copyPeople(mBackup, mDataSet);

        // データの変更
        item.isArchive = false;
        for (SimplePerson dest : mDataSet) {
            // 一致
            if (dest.equal(item)) {
                dest.copyPerson(item);
            }
        }

        // プログレスバーの更新
        updateProgressBar();
        // 一覧表示の更新
        if (mCollectionView != null) {
            mUndos.clear();
            // 一覧表示の更新
            int position = mCollectionView.remove(item.toContact());
            // 戻す処理に追加
            mUndos.add(new Action(INSERT, position, getPerson(item.uuid, mBackup)));
            Collections.reverse(mUndos);
        }

        // 設定
        mClient.setPeople(mDataSet);

        // スナックバーの生成
        String message = getString(R.string.unarchived_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // キャスト
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);

        // 選択中
        mState.changeSelection(SELECTING);

        // アクションバーの設定
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }
        // ツールバーの変更
        updateTitle(arrayList);
        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_600));

        // ステータスバーの変更
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_800));
            //getWindow().setBackgroundDrawable(DevelopHelper.getBackgroundDrawble(mSelf, mState));
        }
        // 背景色の変更
        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_800));

        // ナビゲーションドローワーの設定
        enableNavigationDrawer();

        // メニューを設定
        invalidateOptionsMenu();

        // データ変更
        for (SimplePerson src1 : mDataSet) {
            boolean isChecked = true;
            for (SimplePerson src2 : arrayList) {
                if (src1.equal(src2)) {
                    isChecked = false;
                }
            }
            if (isChecked) {
                arrayList.add(src1);
            }
        }
        mDataSet.clear();
        copyPeople(mDataSet, arrayList);

        // 設定
        mClient.setPeople(mDataSet);
    }

    /**
     * 更新通知
     *
     * @param view       表示
     * @param collection 一覧
     */
    @Override
    public void onUpdated(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // キャスト
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);
        // タイトルの更新
        updateTitle(arrayList);
        // メニューの更新
        updateMenu(arrayList);
        // 空表示の更新
        updateEmptyView(arrayList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimplePerson> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimplePerson> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (mState.getId() != R.id.archive) {
            Intent intent = getIntent();
            intent.setClass(getApplicationContext(), getStartActivity(mState.getId()));
            startActivity(intent);
            overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerStateChanged(int newState) {
        // 処理なし
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // コンテンツ識別子の設定
        mState.setId(item.getItemId());
        // コンテンツ識別子の保存
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        // ドローワ―を閉める
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

                        // プログレスバーの更新
                        updateProgressBar();
                        // 一覧表示が空でない場合
                        if (mCollectionView != null) {
                            // 一覧表示の更新
                            try {
                                for (Action undo : mUndos) {
                                    switch (undo.action) {
                                        case INSERT: {
                                            mCollectionView.insert(undo.arg, undo.object.toContact());
                                            break;
                                        }
                                        case CHANGE: {
                                            mCollectionView.change(undo.object.toContact());
                                            break;
                                        }
                                        case REMOVE: {
                                            mCollectionView.remove(undo.object.toContact());
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 一覧表示の更新
                                int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
                                updateCollectionView(layout, mBackup);
                            }
                            mUndos.clear();
                        }

                        ArrayList<SimplePerson> list = toList(mBackup);
                        // 空表示の更新
                        updateEmptyView(list);
                        // タイトル更新
                        updateTitle(list);
                        // メニューの更新
                        updateMenu(list);

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
     * アーカイブ一覧の取得
     *
     * @param collection 一覧
     * @return アーカイブ一覧
     */
    public static ArrayList<SimplePerson> toList(Collection<SimplePerson> collection) {
        ArrayList<SimplePerson> result = new ArrayList<>();
        for (SimplePerson item : collection) {
            if (item.isArchive && !item.isTrash) {
                result.add(item.toContact());
            }
        }
        return result;
    }

    /**
     * 全選択
     */
    private class SelectedAllRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 全選択
                mState.changeSelection(SELECTED_ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 選択解除
     */
    private class DiselectRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 選択解除
                mState.changeSelection(UNSELECTED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * アーカイブ解除
     */
    private class UnArchiveRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimplePerson mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        UnArchiveRunner(SimplePerson item) {
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

                // データの変更
                for (SimplePerson item : mDataSet) {
                    // 一致
                    if (item.equal(mItem)) {
                        // データの変更
                        item.copyPerson(mItem);
                    }
                }

                // 一覧表示の更新
                if (mCollectionView != null) {
                    mUndos.clear();
                    // 一覧表示の更新
                    int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                    // 戻す処理に追加
                    mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    Collections.reverse(mUndos);
                }

                // 設定
                mClient.setPeople(mDataSet);

                // スナックバーの生成
                String message = getString(R.string.unarchived_item);
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 選択したアーカイブ解除
     */
    private class SelectedUnArchiveRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);

                // バックアップ
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // データの設定
                for (SimplePerson src : mSelected) {
                    // 選択状態の解除
                    src.isSelected = false;
                    // アーカイブ解除の設定
                    src.isArchive = false;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // 一致
                        if (dest.equal(src)) {
                            // データの変更
                            dest.copyPerson(src);
                        }
                    }
                }

                // プログレスバーの更新
                updateProgressBar();
                // 一覧表示の更新
                if (mCollectionView != null) {
                    mUndos.clear();
                    for (SimplePerson src : mSelected) {
                        // 一覧表示の更新
                        int position = mCollectionView.remove(src.toContact());
                        // 戻す処理に追加
                        mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                    }
                    Collections.reverse(mUndos);
                }

                // 設定
                mClient.setPeople(mDataSet);

                // 選択一覧の削除
                mSelected.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.unarchived_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.unarchived_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 選択したアーカイブの色変更
     */
    private class SelectedChangeColorRunner implements Runnable {

        COLOR mCOLOR;

        /**
         * コンストラクタ
         *
         * @param color 色
         */
        SelectedChangeColorRunner(COLOR color) {
            mCOLOR = color;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);

                // データの設定
                for (SimplePerson src : mSelected) {
                    // 選択状態の解除
                    src.isSelected = false;
                    // 色の設定
                    src.color = mCOLOR;
                    // 画像の設定
                    src.imagePath = INVALID_STRING_VALUE;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // 一致
                        if (dest.equal(src)) {
                            // データの変更
                            dest.copyPerson(src);
                        }
                    }
                }

                // プログレスバーの更新
                updateProgressBar();
                // 一覧表示の更新
                if (mCollectionView != null) {
                    for (SimplePerson src : mSelected) {
                        // 一覧表示の更新
                        mCollectionView.change(src.toContact());
                    }
                }

                // 設定
                mClient.setPeople(mDataSet);

                // 選択一覧の削除
                mSelected.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                // 一覧表示が空でなく、項目が空でない場合
                if ((mCollectionView != null) && (mItem != null)) {

                    // 設定
                    for (SimplePerson dest : mDataSet) {
                        // 一致
                        if (dest.equal(mItem)) {
                            dest.copyPerson(mItem);
                        }
                    }

                    // プログレスバーの更新
                    updateProgressBar();

                    // 一覧表示の更新
                    mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());

                    // 設定
                    mClient.setPeople(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
