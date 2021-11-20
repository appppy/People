package jp.osaka.appppy.people.ui.content.contact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.IDialog;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.constants.ActivityTransition;
import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.databinding.ActivityContactBinding;
import jp.osaka.appppy.people.service.PeopleClient;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.service.history.History;
import jp.osaka.appppy.people.service.history.HistoryProxy;
import jp.osaka.appppy.people.ui.Action;
import jp.osaka.appppy.people.ui.content.BaseAdmobActivity;
import jp.osaka.appppy.people.ui.content.EditDialog;
import jp.osaka.appppy.people.ui.content.ListFragment;
import jp.osaka.appppy.people.ui.content.ModuleFragment;
import jp.osaka.appppy.people.utils.FileHelper;
import jp.osaka.appppy.people.utils.PeopleHelper;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ACTION.CHANGE;
import static jp.osaka.appppy.people.constants.ACTION.CREATE;
import static jp.osaka.appppy.people.constants.ACTION.INSERT;
import static jp.osaka.appppy.people.constants.ACTION.MODIFY;
import static jp.osaka.appppy.people.constants.ACTION.REMOVE;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_CONTACTS_PERMISSION;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_CREATE_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_DETAIL_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_EDIT_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_OPEN_FILE;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_OPEN_HISTORY;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_SYNC_FILE;
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
import static jp.osaka.appppy.people.constants.PERMISSION.GRANTED;
import static jp.osaka.appppy.people.constants.PeopleConstants.DEFAULT_LAYOUT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_CONTACT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_CONTENT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_FILE_NAME;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_HISTORY;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_LAYOUT_ID;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SORT_ID;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_LONG_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.LAYOUT_GRID;
import static jp.osaka.appppy.people.constants.PeopleConstants.LAYOUT_LINEAR;
import static jp.osaka.appppy.people.constants.PeopleConstants.MAX;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_DATE_CREATED;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_DATE_MODIFIED;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_NAME;
import static jp.osaka.appppy.people.constants.SELECTION.MULTI_SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED_ALL;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTING;
import static jp.osaka.appppy.people.constants.SELECTION.UNSELECTED;
import static jp.osaka.appppy.people.service.SimplePerson.createInstance;
import static jp.osaka.appppy.people.ui.content.contact.ContactHelper.startSearchActivity;
import static jp.osaka.appppy.people.ui.content.contact.ContactHelper.toList;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.getStartActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startCreateActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Line;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Module;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startEditActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startFolderActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startHistoryActivity;
import static jp.osaka.appppy.people.utils.FileHelper.readFile;
import static jp.osaka.appppy.people.utils.FileHelper.writeFile;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;
import static jp.osaka.appppy.people.utils.PeopleHelper.isMax;
import static jp.osaka.appppy.people.utils.SelectHelper.getSelectedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByDateCreatedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByDateModifiedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByNameCollection;

/**
 * メインアクティビティ
 */
public class MainActivity extends BaseAdmobActivity implements
        IDialog.Callbacks<String>,
        PeopleClient.Callbacks,
        ICollectionView.Callbacks<SimplePerson>,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        PeopleState.Callbacks {

    /**
     * @serial タグ
     */
    private final String TAG = "MainActivity";

    /**
     * @serial バインディング
     */
    private ActivityContactBinding mBinding;

    /**
     * @serial プリファレンス
     */
    private SharedPreferences mPref;

    /**
     * @serial 自身
     */
    private Activity mSelf;

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
     * @serial 一覧表示
     */
    private ICollectionView<SimplePerson> mCollectionView;

    /**
     * @serial トグル
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial URI
     */
    private Uri mUri;

    /**
     * @serial ファイル読み込み要求
     */
    private boolean isFile = false;


    /**
     * @serial 履歴読み込み要求
     */
    private boolean isHistory = false;

    /**
     * @serial 履歴
     */
    private History mHistory;


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
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_CONTACT, COLOR.LIGHT_BLUE.name()));
        setTheme(ThemeHelper.getTheme(color));

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.contact);
        }

        // フローティングアクションボタンの設定
        setupFloatingActionButton();

        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.contact);
        mState.registerCallacks(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                CharSequence name = extras.getCharSequence(Intent.EXTRA_TITLE);
                CharSequence sub = extras.getCharSequence(Intent.EXTRA_SUBJECT);
                CharSequence phone = extras.getCharSequence(Intent.EXTRA_PHONE_NUMBER);
                CharSequence email = extras.getCharSequence(Intent.EXTRA_EMAIL);
                CharSequence ext = extras.getCharSequence(Intent.EXTRA_TEXT);
                // 項目の作成
                SimplePerson item = SimplePerson.createInstance();
                if (name != null) {
                    item.displayName = item.toString();
                }
                if (sub != null) {
                    item.displayName = sub.toString();
                }
                if (phone != null) {
                    item.call = phone.toString();
                }
                if (email != null) {
                    item.send = email.toString();
                }
                if (ext != null) {
                    item.note = ext.toString();
                }
                // レジューム後に動作させる
                mRedos.clear();
                mRedos.add(new Action(CREATE, 0, item));
            }
        }
        if(!mRedos.isEmpty()) {
            mPref.edit().putInt(EXTRA_CONTENT, R.id.contact).apply();
        }

        // 権限を持っているか確認する
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestContactsPermission();
//        } else {
//            // 連絡先アクセスの有効化
//            mState.setContacts(PERMISSSION.GRANTED);
//        }


//        mProxy.connect();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        int id = mPref.getInt(EXTRA_CONTENT, R.id.contact);
        if(id != R.id.contact) {
            mState.setId(id);
            if (mState.getId() != R.id.contact) {
                Intent intent = getIntent();
                intent.setClass(getApplicationContext(), getStartActivity(mState.getId()));
                startActivity(intent);
                overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
                finish();
            }
        }
   //     mProxy.connect();
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
    protected void onStop() {
   //     mProxy.disconnect();

        super.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // 解除
        mState.unregisterCallacks();

        //mProxy.disconnect();
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
            case REQUEST_CREATE_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                    // レジューム後に動作させる
                    mRedos.clear();
                    mRedos.add(new Action(CREATE, 0, item));
                }
                break;
            }
            case REQUEST_DETAIL_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    SimplePerson item = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_PERSON);
                    // レジューム後に動作させる
                    mRedos.clear();
                    mRedos.add(new Action(MODIFY, 0, item));
                }
                break;
            }
            case REQUEST_EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                    // レジューム後に動作させる
                    mRedos.clear();
                    mRedos.add(new Action(MODIFY, 0, item));
                }
                break;
            }
            case REQUEST_SYNC_FILE: {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        // レジューム後に動作させる
                        mUri = data.getData();
                    }
                }
                break;
            }
            case REQUEST_OPEN_FILE: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    mPref.edit().putString(EXTRA_FILE_NAME, data.getStringExtra(EXTRA_FILE_NAME)).apply();

                    // レジューム後に動作させる
                    isFile = true;
                }
                break;
            }
            case REQUEST_OPEN_HISTORY: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    mHistory = bundle.getParcelable(EXTRA_HISTORY);
                    // レジューム後に動作させる
                    isHistory = true;
                }
                break;
            }
            default: {
                break;
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave()");
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
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                switch (mState.getDisplay()) {
                    case EMPTY: {
                        if (mState.getContacts() == GRANTED) {
                            getMenuInflater().inflate(R.menu.contact_empty_download, menu);
                        } else {
                            getMenuInflater().inflate(R.menu.contact_empty, menu);
                        }
                        break;
                    }
                    default:
                    case LINEAR: {
                        if (toList(mDataSet).size() == 1) {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.contact_linear_download_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.contact_linear_one, menu);
                            }
                        } else {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.contact_linear_download, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.contact_linear, menu);
                            }
                        }
                        break;
                    }
                    case GRID: {
                        if (toList(mDataSet).size() == 1) {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.contact_grid_download_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.contact_grid_one, menu);
                            }
                        } else {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.contact_grid_download, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.contact_grid, menu);
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case SELECTED: {
                if (isMax(mDataSet)) {
                    getMenuInflater().inflate(R.menu.main_selected_limted, menu);
                } else {
                    getMenuInflater().inflate(R.menu.main_selected_one, menu);
                }
                break;
            }
            case MULTI_SELECTED: {
                getMenuInflater().inflate(R.menu.main_selected, menu);
                break;
            }
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    if (isMax(mDataSet)) {
                        getMenuInflater().inflate(R.menu.main_selected_all_one_limited, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.main_selected_all_one, menu);
                    }
                } else {
                    getMenuInflater().inflate(R.menu.main_selected_all, menu);
                }
                break;
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
                mClient.setPeople(mDataSet);
                //mHandler.post(new DiselectRunner());
                startEditActivity(mSelf, mSelf, person);
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
                // 詳細画面の表示
                int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
                if (layout == LAYOUT_GRID) {
                    startDetailActivity_from_Module(this, this, person);
                } else {
                    startDetailActivity_from_Line(this, this, person);
                }
                return true;
            }
            // 一覧表示
            case R.id.menu_linear: {
                mHandler.post(() -> {
                    if(mState.isResumed()) {
                        mPref.edit().putInt(EXTRA_LAYOUT_ID, LAYOUT_LINEAR).apply();
                        updateView(toList(mDataSet));
                    }
                });
                return true;
            }
            // ギャラリー表示
            case R.id.menu_grid: {
                mHandler.post(() -> {
                    if(mState.isResumed()) {
                        mPref.edit().putInt(EXTRA_LAYOUT_ID, LAYOUT_GRID).apply();
                        updateView(toList(mDataSet));
                    }
                });
                return true;
            }
            case R.id.menu_by_name: {
                mPref.edit().putInt(EXTRA_SORT_ID, SORT_BY_NAME).apply();
                ArrayList<SimplePerson> collection = (ArrayList<SimplePerson>) toSortByNameCollection(mDataSet);
                updateView(toList(collection));
                mClient.setPeople(collection);
                break;
            }
            case R.id.menu_by_date_modified: {
                mPref.edit().putInt(EXTRA_SORT_ID, SORT_BY_DATE_MODIFIED).apply();
                ArrayList<SimplePerson> collection = (ArrayList<SimplePerson>) toSortByDateModifiedCollection(mDataSet);
                Collections.reverse(collection);
                updateView(toList(collection));
                mClient.setPeople(collection);
                break;
            }
            case R.id.menu_by_date_created: {
                mPref.edit().putInt(EXTRA_SORT_ID, SORT_BY_DATE_CREATED).apply();
                ArrayList<SimplePerson> collection = (ArrayList<SimplePerson>) toSortByDateCreatedCollection(mDataSet);
                Collections.reverse(collection);
                updateView(toList(collection));
                mClient.setPeople(collection);
                break;
            }
            case R.id.menu_swap_vert: {
                Collections.reverse(mDataSet);
                updateView(toList(mDataSet));
                return true;
            }
            case R.id.menu_sync_file: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/*");
                try {
                    startActivityForResult(intent, REQUEST_SYNC_FILE.ordinal());
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            }
//            case R.id.menu_sync_contacts: {
//                new ContactAsyncTask(this).execute();
//                return true;
//            }
            case R.id.menu_search: {
                startSearchActivity(this);
                return true;
            }
            // 選択
            case R.id.menu_archive: {
                mHandler.post(new SelectedArchiveRunner());
                return true;
            }
            case R.id.menu_trash: {
                mHandler.post(new SelectedTrashRunner());
                return true;
            }
            case R.id.menu_copy: {
                mHandler.post(new CopyRunner());
                return true;
            }
            case R.id.menu_selected_all: {
                // 全選択
                mHandler.post(new SelectedAllRunner());
                return true;
            }
            case R.id.menu_share: {

                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TITLE, mSelected.get(0).displayName);
                        intent.putExtra(Intent.EXTRA_SUBJECT, mSelected.get(0).displayName);
                        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, mSelected.get(0).call);
                        intent.putExtra(Intent.EXTRA_EMAIL, mSelected.get(0).send);
                        intent.putExtra(Intent.EXTRA_TEXT, mSelected.get(0).note);
                        intent.setType("text/plain");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                return true;
            }
            case R.id.menu_open_folder: {
                startFolderActivity(this);
                return true;
            }
            case R.id.menu_save_file: {
                // ファイル名を取得
                String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);

                // 編集ダイアログの表示
                EditDialog fragment = EditDialog.newInstance(filename);
                fragment.show(getSupportFragmentManager(), "EditDialog");

                return true;
            }
            case R.id.menu_open_history: {
                startHistoryActivity(this);
                return true;
            }
            case R.id.menu_leave_history: {
                // 履歴更新
                mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), PeopleHelper.toJSONString(mDataSet)));
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
                    // 表示の更新
                    updateView(toList(mDataSet));
                    // FAB設定
                    setupFloatingActionButton();
                    mBinding.fab.show();
                }

                // 次の動作を指定
                for (Action doAction : mRedos) {
                    if (!isMax(mDataSet)) {
                        if (doAction.action.equals(CREATE)) {
                            CreateRunner runner = new CreateRunner(doAction.object);
                            mHandler.post(runner);
                        }
                    }
                    if (doAction.action.equals(MODIFY)) {
                        SimplePerson dest = getPerson(doAction.object.uuid, mDataSet);
                        if (!dest.equals(doAction.object)) {
                            if (!PeopleHelper.isModified(dest, doAction.object)) {
                                ChangeRunner runner = new ChangeRunner(doAction.object);
                                mHandler.post(runner);
                            } else {
                                ModifyRunner runner = new ModifyRunner(doAction.object);
                                mHandler.post(runner);
                            }
                        }
                    }
                }
                mRedos.clear();
                if (mUri != null) {
                    SyncRunner runner = new SyncRunner(mUri);
                    mHandler.post(runner);
                    mUri = null;
                }
                if (isFile) {
                    // ファイル名を取得
                    String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);
                    ReadFileRunner runner = new ReadFileRunner(filename);
                    mHandler.post(runner);
                    isFile = false;
                }
                if (isHistory) {
                    ReadHistoryRunner runner = new ReadHistoryRunner(mHistory);
                    mHandler.post(runner);
                    isHistory = false;
                }

                // 選択状態を解除
                mHandler.post(new DiselectRunner());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS_PERMISSION.ordinal()) {// If request is cancelled, the result arrays are main_empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mState.setContacts(GRANTED);

                new ContactAsyncTask(this).execute();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // コンテンツ識別子の保存
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        if (mState.getId() != R.id.contact) {
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

    /**
     * ポップアップメニュー表示
     *
     * @param view 表示
     * @param item 項目
     */
    private void showPopupMenu(final View view, final SimplePerson item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        if (isMax(mDataSet)) {
            popup.getMenuInflater().inflate(R.menu.main_selected_all_one_limited, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.main_selected_all_one, popup.getMenu());
        }

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            // 項目を選択
            mSelected.clear();
            mSelected.add(item);

            // メニュー選択
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

        // データ設定
        item.isArchive = true;
        // 設定
        for (SimplePerson dest : mDataSet) {
            // 一致
            if (dest.equal(item)) {
                dest.copyPerson(item);
            }
        }

        // プログレスバーの更新
        disableProgressBar();
        // 一覧表示の更新
        if (mCollectionView != null) {
            mUndos.clear();
            // 一覧表示の更新
            int position = mCollectionView.remove(item.toContact());
            mUndos.add(new Action(INSERT, position, getPerson(item.uuid, mBackup)));
            Collections.reverse(mUndos);
        }

        // 設定
        mClient.setPeople(mDataSet);

        // スナックバーの生成
        String message = getString(R.string.moved_to_archive_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // キャスト
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);

        mState.changeSelection(SELECTING);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }

        updateTitle(arrayList);
        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_600));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue_800));
            //getWindow().setBackgroundDrawable(DevelopHelper.getBackgroundDrawble(mSelf, mState));
        }
        // 背景色の変更
        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_800));
        enableNavigationDrawer();
        invalidateOptionsMenu();

        //データ設定
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
     * @param view       一覧表示
     * @param collection 一覧
     */
    @Override
    public void onUpdated(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // キャスト
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);
        // 空表示の更新
        updateEmptyView(arrayList);
        // タイトル更新
        updateTitle(arrayList);
        // メニューの更新
        updateMenu(arrayList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimplePerson> view) {
        mBinding.fab.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimplePerson> view) {
        mBinding.fab.show();
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
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));

                        // フローティングアクションボタンの表示
                        setupFloatingActionButton();
                        mBinding.fab.show();

                        // メニューを設定
                        invalidateOptionsMenu();

                        break;
                    }
                    // 何もしない
                    case SELECTED_ALL: {

                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // 全選択
                        if (mCollectionView != null) {
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());
                        }

                        // ツールバーの設定
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));

                        // フローティングアクションボタンの非表示
                        //mBinding.fab.hide();
                        setupFloatingActionButton();

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
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }

                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));

                        // フローティングアクションボタンの非表示
                        //mBinding.fab.hide();
                        setupFloatingActionButton();

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
     * フローティングアクションボタンの設定
     */
    private void setupFloatingActionButton() {
        if (mState.getSelection() == UNSELECTED) {
            mBinding.fab.setImageResource(R.drawable.ic_add_white_36dp);
            mBinding.fab.setOnClickListener(v -> {
                // レジューム状態の確認
                if (!mState.isResumed()) {
                    return;
                }
                if (isMax(mDataSet)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.warning_message)).append("  ").append(mDataSet.size()).append("\n");
                    // 注意喚起
                    new AlertDialog.Builder(mSelf)
                            .setTitle(getString(R.string.warning))
                            .setMessage(sb.toString())
                            .show();
                    sb.delete(0, sb.length());
                } else {
                    // 詳細表示
                    startCreateActivity(mSelf, mSelf, v, createInstance());
                }
            });
            mBinding.fab.hide();
            mBinding.fab.show();
        } else {
            if (mSelected.size() == 1) {
                mBinding.fab.setImageResource(R.drawable.ic_create_white_36dp);
                mBinding.fab.setOnClickListener(v -> {
                    // レジューム状態の確認
                    if (!mState.isResumed()) {
                        return;
                    }

                    // タイムスタンプを保存
                    SimplePerson person = mSelected.get(0);
                    person.timestamp = System.currentTimeMillis();
                    for (SimplePerson dest : mDataSet) {
                        if (dest.equal(person)) {
                            dest.copyPerson(person);
                        }
                    }
                    mClient.setPeople(mDataSet);
                    mHandler.post(new DiselectRunner());
                    startEditActivity(mSelf, mSelf, v, person);
                });
                mBinding.fab.hide();
                mBinding.fab.show();
            } else {
                mBinding.fab.hide();
            }
        }
    }

    /**
     * ナビゲーションの有効
     */
    private void enableNavigationDrawer() {
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }

        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * ナビゲーションの無効
     */
    private void disableNavigationDrawer() {
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mBinding.drawerLayout.removeDrawerListener(mToggle);
        }
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * 連絡先の読み込み
     *
     * @return 連絡先
     */
    private ArrayList<SimplePerson> loadContects() {
        ArrayList<SimplePerson> result = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            String id;
            StringBuilder sb = new StringBuilder();
            do {
                // ID
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                SimplePerson person = new SimplePerson(
                        String.valueOf(UUID.randomUUID()),
                        String.valueOf(UUID.randomUUID()),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        cursor.getString(cursor.getColumnIndex("display_name")),
                        getPhoneNumber(resolver, id),
                        getEmailData(resolver, id),
                        INVALID_STRING_VALUE,
                        INVALID_STRING_VALUE,
                        false,
                        false,
                        WHITE,
                        INVALID_LONG_VALUE
                );
                sb.delete(0, sb.length());
                result.add(person);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }

//    /**
//     * 連絡先の権限の要求
//     */
//    private void requestContactsPermission() {
//        if (LOG_I) {
//            Log.i(TAG, enter());
//        }
//
//        ActivityCompat.requestPermissions(this, new String[]{
//                        android.Manifest.permission.READ_CONTACTS
//                },
//                REQUEST_CONTACTS_PERMISSION);
//
//        if (LOG_I) {
//            Log.i(TAG, leave());
//        }
//    }

    /**
     * ダウロードピッカー
     */
    private void downloadPicker(Uri uri) {
        new FileAsyncTask(this, this, uri).execute();
    }

    /**
     * IDから電話番号を取得
     *
     * @param resolver 解決
     * @param id       識別子
     * @return 電話番号
     */
    private String getPhoneNumber(ContentResolver resolver, String id) {
        String phones = "";

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (phones.equals("")) {
                    phones = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return phones;
    }

    /**
     * IDからメールアドレスを取得
     *
     * @param resolver 解決
     * @param id       識別子
     * @return メールアドレス
     */
    private String getEmailData(ContentResolver resolver, String id) {
        String emails = "";

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (emails.equals("")) {
                    emails = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return emails;
    }

    /**
     * 表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimplePerson> collection) {
        // プログレスの終了
        disableProgressBar();
        // 空表示の更新
        updateEmptyView(collection);
        // メニューの更新
        updateMenu(collection);
        // 一覧表示の更新
        int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
        updateCollectionView(layout, collection);
        // タイトル更新
        updateTitle(collection);
    }

    /**
     * プログレスバーの更新
     */
    private void disableProgressBar() {
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
        ImageView view = mBinding.emptyView;
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            view.setVisibility(View.VISIBLE);
        } else {

            view.setVisibility(View.GONE);
        }
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
     * 一覧表示の更新
     *
     * @param id         識別子
     * @param collection 一覧
     */
    private void updateCollectionView(int id, ArrayList<SimplePerson> collection) {
        // 一覧表示の取得
        mCollectionView = getCollectionView(id, collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
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
            sb.append(this.getString(R.string.contact));
        } else {
            sb.append(this.getString(R.string.contact)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * 一覧表示の取得
     *
     * @param id         識別子
     * @param collection 一覧
     * @return 一覧表示
     */
    private ICollectionView<SimplePerson> getCollectionView(int id, ArrayList<SimplePerson> collection) {
        if (id == LAYOUT_GRID) {
            return ModuleFragment.newInstance(collection);
        }
        return ListFragment.newInstance(collection);
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
                        disableProgressBar();
                        // 一覧表示の空でない場合
                        if (mCollectionView != null) {
                            // 一覧表示の更新
                            try {
                                for (Action undo : mUndos) {
                                    switch (undo.action) {
                                        case INSERT: {
                                            // 一覧表示の更新
                                            mCollectionView.insert(undo.arg, undo.object.toContact());
                                            break;
                                        }
                                        case CHANGE: {
                                            // 一覧表示の更新
                                            mCollectionView.change(undo.object.toContact());
                                            break;
                                        }
                                        case REMOVE: {
                                            // 一覧表示の更新
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

                        // データ取得
                        ArrayList<SimplePerson> list = toList(mBackup);
                        // 空表示の更新
                        updateEmptyView(list);
                        // タイトルの更新
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
     * ポジティブボタン押下.
     *
     * @param dialog ダイアログ
     * @param name   設定値
     */
    @Override
    public void onPositiveButtonClicked(IDialog dialog, String name) {
        if (name.equals(PeopleConstants.INVALID_STRING_VALUE)) {
            Toast.makeText(mSelf, R.string.failed_save_file, Toast.LENGTH_LONG).show();
        } else {
            // ファイルを上書き保存
            mHandler.post(new WriteFileRunner(name));
        }
    }

    /**
     * ネガティブボタン押下.
     *
     * @param dialog ダイアログ
     * @param name   設定値
     */
    @Override
    public void onNegativeButtonClicked(IDialog dialog, String name) {

    }

    /**
     * 生成
     */
    private class CreateRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimplePerson mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        CreateRunner(SimplePerson item) {
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

                    // バックアップ
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

                    // 項目を追加
                    mDataSet.add(0, mItem);

                    // プログレスバーの更新
                    disableProgressBar();

                    // 一覧表示の更新
                    mUndos.clear();
                    mCollectionView.insert(0, mItem.toContact());
                    mUndos.add(new Action(REMOVE, 0, mItem));
                    if (mItem.imagePath.equals(INVALID_STRING_VALUE)) {
                        for (SimplePerson dest : mDataSet) {
                            if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                                if (dest.displayName.equals(mItem.displayName)) {
                                    if (!dest.imagePath.equals(INVALID_STRING_VALUE)) {
                                        mItem.imagePath = dest.imagePath;
                                        int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());
                                        mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                                    }
                                }
                            }
                        }
                    } else {
                        for (SimplePerson dest : mDataSet) {
                            if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                                if (dest.displayName.equals(mItem.displayName)) {
                                    dest.imagePath = mItem.imagePath;
                                    int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());
                                    mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                                }

                            }
                        }

                    }
                    Collections.reverse(mUndos);

                    // 設定
                    mClient.setPeople(mDataSet);

                    // スナックバーの生成
                    makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.created_item));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                // 一覧表示が空でなく、項目が空でない場合
                if ((mCollectionView != null) && (mItem != null)) {

                    // バックアップ
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

                    // 設定
                    for (SimplePerson dest : mDataSet) {
                        // 一致
                        if (dest.equal(mItem)) {
                            dest.copyPerson(mItem);
                        }
                    }

                    // プログレスバーの更新
                    disableProgressBar();

                    // 一覧表示の更新
                    mUndos.clear();
                    if (mItem.isArchive) {
                        // 一覧表示の更新
                        int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    } else if (mItem.isTrash) {
                        // 一覧表示の更新
                        int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    } else {
                        // 一覧表示の更新
                        int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                    }
                    for (SimplePerson dest : mDataSet) {
                        if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                            if (dest.displayName.equals(mItem.displayName)) {
                                if (dest.imagePath.equals(mItem.imagePath)) {
                                    dest.imagePath = mItem.imagePath;
                                    if (!dest.isTrash && !dest.isArchive) {
                                        // 一覧表示の更新
                                        int position = mCollectionView.change(dest.toContact());
                                        mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                    }
                                }
                            }
                        }
                    }
                    Collections.reverse(mUndos);

                    // 設定
                    mClient.setPeople(mDataSet);

                    // スナックバーの生成
                    String message;
                    if (mItem.isArchive) {
                        message = getString(R.string.moved_to_archive_item);
                    } else if (mItem.isTrash) {
                        message = getString(R.string.moved_to_trash_item);
                    } else {
                        message = getString(R.string.modified_item);
                    }
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同期
     */
    private class SyncRunner implements Runnable {

        Uri mUri;

        SyncRunner(Uri uri) {
            mUri = uri;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                Cursor cursor = getContentResolver().query(mUri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    //int docIdIndex = cursor.getColumnIndex("document_id");
                    int index = cursor.getColumnIndex("mime_type");
                    String type = cursor.getString(index);
                    if (type != null) {
                        downloadPicker(mUri);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ダウンロード
     */
    private class ContactAsyncTask {
        //ProgressDialog mProgressDialog;

        boolean isActive = false;

        Activity mActivity;

        ContactAsyncTask(Activity activity) {
            mActivity = activity;
        }

        private void execute() {
            final Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                // do something in background
                ArrayList<SimplePerson> result = new ArrayList<>(loadContects());
                // onPreExecute
                //
                //mProgressDialog = new ProgressDialog(mActivity);

                // プログレスダイアログの設定
                //mProgressDialog.setMessage(getString(R.string.loading));

                // プログレスダイアログの表示
                //mProgressDialog.show();

                isActive = true;
                //
                // update UI
                handler.post(() -> {
                    if (mDataSet.isEmpty()) {
                        // レジューム状態の確認
                        if(!mState.isResumed()) {
                            return;
                        }

                        // バックアップ
                        mBackup.clear();

                        if (result.size() > MAX) {
                            mDataSet.addAll(result.subList(0, MAX));
                        } else {
                            mDataSet.addAll(result);
                        }
                        // 表示の更新
                        updateView(toList(mDataSet));

                        // 設定
                        mClient.setPeople(mDataSet);

                        String message = getString(R.string.sync_item);
                        // スナックバーの生成
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);

                    } else {
                        // バックアップ
                        mBackup.clear();
                        copyPeople(mBackup, mDataSet);

                        // プログレスバーの終了
                        mBinding.productImageLoading.setVisibility(View.INVISIBLE);

                        // 一覧表示の更新
                        mUndos.clear();
                        for (SimplePerson src : result) {
                            for (SimplePerson dest : mDataSet) {
                                if (dest.displayName.equals(src.displayName)) {
                                    dest.isChanged = false;
                                    // telを更新
                                    if (!src.call.equals(INVALID_STRING_VALUE)) {
                                        dest.call = src.call;
                                        dest.modifiedDate = src.modifiedDate;
                                        dest.isChanged = true;
                                    }
                                    // emailを更新
                                    if (!src.send.equals(INVALID_STRING_VALUE)) {
                                        dest.send = src.send;
                                        dest.modifiedDate = src.modifiedDate;
                                        dest.isChanged = true;
                                    }
                                    // 一覧表示の更新
                                    if (dest.isChanged) {
                                        dest.isChanged = false;
                                        if (!dest.isArchive && !dest.isTrash) {
                                            if (mCollectionView != null) {
                                                // 一覧表示の更新
                                                int position = mCollectionView.change(dest.toContact());
                                                mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Collections.reverse(mUndos);

                        // 設定
                        mClient.setPeople(mDataSet);

                        // スナックバーの生成
                        String message = getString(R.string.sync_item);
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);
                    }

                    // ダイアログを消去
                    //if (mProgressDialog != null && mProgressDialog.isShowing()) {
                     //   mProgressDialog.dismiss();
                    //}

                    isActive = false;
                });
            });
        }
    }

    /**
     * ダウンロード
     */
    @SuppressLint("StaticFieldLeak")
    private class FileAsyncTask {
        // ProgressDialog mProgressDialog;

        /**
         * @serial 実行状態
         */
        boolean isActive = false;

        /**
         * @serial アクティビティ
         */
        Activity mActivity;

        /**
         * @serial コンテキスト
         */
        private final Context context;

        /**
         * @serial URI
         */
        private final Uri uri;

        /**
         * コンストラクタ
         *
         * @param activity アクティビティ
         * @param context  コンテキスト
         * @param uri      URI
         */
        FileAsyncTask(Activity activity, Context context, Uri uri) {
            mActivity = activity;
            this.context = context;
            this.uri = uri;
        }

        private void execute() {
            final Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                        // do something in background
                        File cacheFile = new File(context.getExternalCacheDir(), "file_cache");
            try {
                InputStream is = context.getContentResolver().openInputStream(uri);

                int data;
                OutputStream os = new FileOutputStream(cacheFile);
                byte[] readBytes;
                if (is != null) {
                    readBytes = new byte[is.available()];
                    while ((data = is.read(readBytes)) != -1) {
                        os.write(readBytes, 0, data);
                    }
                }
                          // onPreExecute
                        //
                //mProgressDialog = new ProgressDialog(mActivity);

                // プログレスダイアログの設定
              //  mProgressDialog.setMessage(getString(R.string.loading));

                // プログレスダイアログの表示
                //mProgressDialog.show();

                isActive = true;
                        // update UI
                        handler.post(() -> {
                            if (cacheFile != null) {

                                ArrayList<SimplePerson> collection = readFile(cacheFile);

                                if (mDataSet.isEmpty()) {

                                    // レジューム状態の確認
                                    if(!mState.isResumed()) {
                                        return;
                                    }

                                    mBackup.clear();

                                    if (collection.size() > MAX) {
                                        mDataSet.addAll(collection.subList(0, MAX));
                                    } else {
                                        mDataSet.addAll(collection);
                                    }
                                    // 表示の更新
                                    updateView(toList(mDataSet));

                                    // 設定
                                    mClient.setPeople(mDataSet);

                                    // スナックバーの生成
                                    makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.sync_item));
                                } else {
                                    // バックアップ
                                    mBackup.clear();
                                    copyPeople(mBackup, mDataSet);

                                    // 履歴更新
                                    mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), PeopleHelper.toJSONString(mDataSet)));


                                    // プログレスの終了
                                    disableProgressBar();
                                    // 一覧表示の更新
                                    if (mCollectionView != null) {

                                        // 一覧表示の更新
                                        mUndos.clear();

                                        boolean isFound;
                                        ArrayList<SimplePerson> stack = new ArrayList<>();
                                        for (SimplePerson src : collection) {
                                            isFound = false;
                                            for (SimplePerson dest : mDataSet) {
                                                // 表示名が同じデータの確認
                                                if (dest.displayName.equals(src.displayName)) {
                                                    isFound = true;

                                                    if (!dest.isArchive && !dest.isTrash) {
                                                        boolean isMod = false;
                                                        // noteを更新
                                                        if (!src.note.equals(INVALID_STRING_VALUE)) {
                                                            dest.note = src.note;
                                                            isMod = true;
                                                        }
                                                        if (!src.call.equals(INVALID_STRING_VALUE)) {
                                                            dest.call = src.call;
                                                            isMod = true;
                                                        }
                                                        if (!src.send.equals(INVALID_STRING_VALUE)) {
                                                            dest.send = src.send;
                                                            isMod = true;
                                                        }
                                                        if(isMod) {
                                                            dest.modifiedDate = src.modifiedDate;
                                                            dest.isChanged = true;
                                                            // 一覧表示の更新
                                                            int position = mCollectionView.change(dest.toCall());
                                                            // 戻す処理に追加
                                                            mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                                        }
                                                    }

                                                }
                                            }
                                            // 新規項目を追加する
                                            if(!isFound) {
                                                stack.add(src);
                                            }
                                        }
                                        Collections.reverse(mUndos);

                                        // データ変更
                                        if(!stack.isEmpty()) {
                                            mDataSet.addAll(stack);

                                            // 表示の更新
                                            updateView(toList(mDataSet));
                                        }
                                    }

                                    // 設定
                                    mClient.setPeople(mDataSet);

                                    // スナックバーの生成
                                    String message = getString(R.string.sync_item);
                                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                                }


                                // ダイアログの消去
                          //      if (mProgressDialog != null && mProgressDialog.isShowing()) {
                           //         mProgressDialog.dismiss();
                            //    }

                                // アクティブ状態の無効
                                isActive = false;
                            }

                      //      if (mProgressDialog != null && mProgressDialog.isShowing()) {
                       //         mProgressDialog.dismiss();
                       //     }

                            isActive = false;
                        });
            } catch (Exception ex) {
                // something went wrong
                ex.printStackTrace();
            }


            });
        }
    }

    /**
     * 選択アーカイブ
     */
    private class SelectedArchiveRunner implements Runnable {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // 一覧表示が空でない場合
                if (mCollectionView != null) {

                    mState.changeSelection(UNSELECTED);

                    // バックアップ
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

                    for (SimplePerson src : mSelected) {
                        src.isSelected = false;
                        src.isArchive = true;
                    }
                    for (SimplePerson dest : mDataSet) {
                        for (SimplePerson src : mSelected) {
                            // 一致
                            if (dest.equal(src)) {
                                dest.copyPerson(src);
                            }
                        }
                    }

                    // プログレスバーの更新
                    disableProgressBar();

                    // 一覧表示の更新
                    mUndos.clear();
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src.isArchive) {
                            // 一覧表示の更新
                            position = mCollectionView.remove(src.toContact());
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else if (src.isTrash) {
                            // 一覧表示の更新
                            position = mCollectionView.remove(src.toContact());
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else {
                            // 一覧表示の更新
                            position = mCollectionView.change(src.toContact());
                            mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                        }
                    }
                    Collections.reverse(mUndos);

                    // 設定
                    mClient.setPeople(mDataSet);

                    mSelected.clear();

                    String message;
                    if (mUndos.size() == 1) {
                        message = getString(R.string.moved_to_archive_item);
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);
                    } else if (mUndos.size() > 1) {
                        message = getString(R.string.moved_to_archive_some_items, mUndos.size());
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 選択ゴミ箱
     */
    private class SelectedTrashRunner implements Runnable {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                mState.changeSelection(UNSELECTED);

                // バックアップ
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // データ設定
                for (SimplePerson src : mSelected) {
                    src.isSelected = false;
                    src.isTrash = true;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // 一致
                        if (dest.equal(src)) {
                            dest.copyPerson(src);
                        }
                    }
                }

                // プログレスバーの更新
                disableProgressBar();
                // 一覧表示の更新
                mUndos.clear();
                if (mCollectionView != null) {
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src != null) {
                            if (src.isArchive) {
                                // 一覧表示の更新
                                position = mCollectionView.remove(src.toContact());
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else if (src.isTrash) {
                                // 一覧表示の更新
                                position = mCollectionView.remove(src.toContact());
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else {
                                // 一覧表示の更新
                                position = mCollectionView.change(src.toContact());
                                mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                            }
                        }
                    }
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setPeople(mDataSet);

                // 選択項目のクリア
                mSelected.clear();

                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_trash_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_trash_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 非選択
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
     * コピー
     */
    private class CopyRunner implements Runnable {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                mState.changeSelection(UNSELECTED);

                // バックアップ
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // データ設定
                SimplePerson item = SimplePerson.createInstance();
                item.copyPerson(mSelected.get(0));

                // 項目を追加
                mDataSet.add(0, item);

                // プログレスバーの更新
                disableProgressBar();

                // 一覧表示の更新
                if (mCollectionView != null) {
                    mUndos.clear();
                    int position = 0;
                    mCollectionView.insert(position, item);
                    mUndos.add(new Action(REMOVE, position, item.toContact()));
                    Collections.reverse(mUndos);
                }

                // 設定
                mClient.setPeople(mDataSet);

                mSelected.clear();

                // スナックバーの生成
                makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.created_item));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
     * ファイル読み込み
     */
    private class ReadFileRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        String filename;

        /**
         * コンストラクタ
         *
         * @param filename ファイル名
         */
        ReadFileRunner(String filename) {
            this.filename = filename;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 履歴更新
                mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), PeopleHelper.toJSONString(mDataSet)));

                // データ設定
                mDataSet.clear();
                mDataSet.addAll(FileHelper.readFile(mSelf, filename + ".csv"));

                // 一覧表示の更新
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // 設定
                mClient.setPeople(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ファイル書き込み
     */
    private class WriteFileRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        String filename;

        /**
         * コンストラクタ
         *
         * @param filename ファイル名
         */
        WriteFileRunner(String filename) {
            this.filename = filename;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // ファイル保存
                writeFile(mSelf, filename + ".csv", mDataSet);
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
                disableProgressBar();
                // 一覧表示の更新
                if (mCollectionView != null) {
                    //mUndos.clear();
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

                    // プログレスバーの更新
                    disableProgressBar();

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


    /**
     * 履歴読み込み
     */
    private class ReadHistoryRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        History mHistory;

        /**
         * コンストラクタ
         *
         * @param history ファイル名
         */
        ReadHistoryRunner(History history) {
            mHistory = history;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // データ設定
                ArrayList<SimplePerson> people = PeopleHelper.toPeople(mHistory.message);
                mDataSet.clear();
                mDataSet.addAll(people);

                // 一覧表示の更新
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // 設定
                mClient.setPeople(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
