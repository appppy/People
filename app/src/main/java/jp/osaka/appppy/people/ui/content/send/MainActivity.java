package jp.osaka.appppy.people.ui.content.send;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.appppy.people.Config;
import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.constants.ActivityTransition;
import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.databinding.ActivitySendAdmobBinding;
import jp.osaka.appppy.people.databinding.ActivitySendBinding;
import jp.osaka.appppy.people.service.PeopleClient;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.service.history.History;
import jp.osaka.appppy.people.service.history.HistoryProxy;
import jp.osaka.appppy.people.ui.Action;
import jp.osaka.appppy.people.ui.content.BaseAdmobActivity;
import jp.osaka.appppy.people.ui.content.EditDialog;
import jp.osaka.appppy.people.ui.content.ListFragment;
import jp.osaka.appppy.people.ui.content.ModuleFragment;
import jp.osaka.appppy.people.utils.PeopleHelper;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ACTION.CHANGE;
import static jp.osaka.appppy.people.constants.ACTION.CREATE;
import static jp.osaka.appppy.people.constants.ACTION.INSERT;
import static jp.osaka.appppy.people.constants.ACTION.MODIFY;
import static jp.osaka.appppy.people.constants.ACTION.REMOVE;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_DETAIL_ITEM;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_EDIT_ITEM;
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
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_SEND;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_CONTENT;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_FILE_NAME;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_LAYOUT_ID;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SORT_ID;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.constants.PeopleConstants.LAYOUT_GRID;
import static jp.osaka.appppy.people.constants.PeopleConstants.LAYOUT_LINEAR;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_DATE_CREATED;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_DATE_MODIFIED;
import static jp.osaka.appppy.people.constants.PeopleConstants.SORT_BY_NAME;
import static jp.osaka.appppy.people.constants.SELECTION.MULTI_SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTED_ALL;
import static jp.osaka.appppy.people.constants.SELECTION.SELECTING;
import static jp.osaka.appppy.people.constants.SELECTION.UNSELECTED;
import static jp.osaka.appppy.people.ui.content.send.SendHelper.toList;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.getStartActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Line;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Module;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startEditActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startFolderActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startHistoryActivity;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;
import static jp.osaka.appppy.people.utils.PeopleHelper.isMax;
import static jp.osaka.appppy.people.utils.SelectHelper.getSelectedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByDateCreatedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByDateModifiedCollection;
import static jp.osaka.appppy.people.utils.SortHelper.toSortByNameCollection;

/**
 * ??????????????????????????????
 */
public class MainActivity extends BaseAdmobActivity implements
        PeopleClient.Callbacks,
        ICollectionView.Callbacks<SimplePerson>,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        PeopleState.Callbacks {

    /**
     * @serial ??????
     */
    private final String TAG = "MainActivity";

    /**
     * @serial ?????????????????????
     */
    private SharedPreferences mPref;

    /**
     * @serial ??????
     */
    private Activity mSelf;

    /**
     * @serial ??????
     */
    private final PeopleState mState = new PeopleState();

    /**
     * @serial ??????????????????
     */
    private final ArrayList<SimplePerson> mDataSet = new ArrayList<>();

    /**
     * @serial ??????????????????
     */
    private final ArrayList<SimplePerson> mBackup = new ArrayList<>();

    /**
     * @serial ???????????????
     */
    private final ArrayList<SimplePerson> mSelected = new ArrayList<>();

    /**
     * @serial ??????????????????
     */
    private final PeopleClient mClient = new PeopleClient(this, this);

    /**
     * @serial ??????????????????
     */
    private final HistoryProxy mProxy = new HistoryProxy(this);

    /**
     * @serial ????????????
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mUndos = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mRedos = new ArrayList<>();

    /**
     * @serial ????????????
     */
    private ICollectionView<SimplePerson> mCollectionView;

    /**
     * @serial ?????????
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial ???????????????
     */
    private Toolbar mToolbar;

    /**
     * @serial ??????????????????????????????
     */
    private DrawerLayout mDrawerLayout;

    /**
     * @serial ???????????????????????????
     */
    private NavigationView mNavigationView;

    /**
     * @serial ???????????????
     */
    private CoordinatorLayout mCoordinatorLayout;

    /**
     * @serial ?????????????????????
     */
    private ProgressBar mProgressBar;

    /**
     * @serial ?????????
     */
    private ImageView mEmptyView;

    /**
     * @serial ???????????????
     */
    private FrameLayout mLayout;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // ???????????????
        mSelf = this;

        // ??????????????????????????????
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // ??????????????????
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_SEND, COLOR.RED.name()));
        setTheme(ThemeHelper.getTheme(color));

        // ?????????????????????
        if(Config.DEBUG || Build.MODEL.equals(PeopleConstants.MODEL)) {
            ActivitySendBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_send);
            mToolbar = binding.toolbar;
            mDrawerLayout = binding.drawerLayout;
            mNavigationView = binding.navView;
            mCoordinatorLayout = binding.coordinatorLayout;
            mProgressBar = binding.productImageLoading;
            mEmptyView = binding.emptyView;
            mLayout = binding.mainContainer;
        } else {
            ActivitySendAdmobBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_send_admob);
            mToolbar = binding.toolbar;
            mDrawerLayout = binding.drawerLayout;
            mNavigationView = binding.navView;
            mCoordinatorLayout = binding.coordinatorLayout;
            mEmptyView = binding.emptyView;
            mLayout = binding.mainContainer;
        }

        // ????????????????????????
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.send);
        }

        // ??????????????????????????????
        enableNavigationDrawer();

        // ??????
        mState.setId(R.id.send);
        mState.registerCallacks(this);

        // ???????????????????????????????????????
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestContactsPermission();
//        } else {
//            // ?????????????????????????????????
//            mState.setContacts(PERMISSSION.GRANTED);
//        }

        //mProxy.connect();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * ????????????
     *
     * @param object ??????????????????
     * @param people ????????????
     */
    @Override
    public void onUpdated(Object object, final List<SimplePerson> people) {
        mHandler.post(() -> {

            try {
                boolean result;
                result = mDataSet.isEmpty() || mDataSet.size() != people.size() || !mDataSet.equals(people);
                if (result) {
                    // ??????????????????????????????
                    if (!mState.isResumed()) {
                        return;
                    }
                    // ???????????????
                    mDataSet.clear();
                    mDataSet.addAll(people);
                    // ???????????????
                    updateView(toList(mDataSet));
                }

                // ?????????????????????
                for (Action doAction : mRedos) {
                    if (doAction.action.equals(CREATE)) {
                        CreateRunner runner = new CreateRunner(doAction.object);
                        mHandler.post(runner);
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

                // ?????????????????????
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
    protected void onResume() {
        super.onResume();
        // ??????????????????????????????
        mState.setResumed(true);

        // ?????????????????????
        mClient.connect();
        mProxy.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {

        // ??????????????????????????????
        mState.setResumed(false);

        // ????????????????????????
        mProxy.disconnect();
        mClient.disconnect();

        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        //mProxy.disconnect();

        // ??????
        mState.unregisterCallacks();

        super.onDestroy();
    }


    /**
     * ????????????????????????????????????????????????
     */
    private void enableNavigationDrawer() {
        // ?????????????????????????????????????????????
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    mToolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ?????????????????????????????????????????????
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mDrawerLayout.addDrawerListener(mToggle);
        mDrawerLayout.addDrawerListener(this);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void disableNavigationDrawer() {
        // ???????????????????????????????????????????????????
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mDrawerLayout.removeDrawerListener(mToggle);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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

        // ????????????
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case REQUEST_DETAIL_ITEM:
            case REQUEST_EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
                    Bundle bundle = data.getExtras();
                    SimplePerson item = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_PERSON);
                    // ????????????????????????????????????
                    mRedos.clear();
                    mRedos.add(new Action(MODIFY, 0, item));
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // ?????????????????????
            switch (mState.getSelection()) {
                case SELECTED_ALL:
                case MULTI_SELECTED:
                case SELECTING:
                case SELECTED: {
                    // ????????????
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
        // ??????????????????
        // ?????????????????????
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                switch (mState.getDisplay()) {
                    case EMPTY: {
                        if (mState.getContacts() == GRANTED) {
                            getMenuInflater().inflate(R.menu.main_empty_download, menu);
                        } else {
                            getMenuInflater().inflate(R.menu.main_empty, menu);
                        }
                        break;
                    }
                    default:
                    case LINEAR: {
                        if (toList(mDataSet).size() == 1) {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.main_linear_download_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_linear_one, menu);
                            }
                        } else {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.main_linear_download, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_linear, menu);
                            }
                        }
                        break;
                    }
                    case GRID: {
                        if (toList(mDataSet).size() == 1) {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.main_grid_download_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_grid_one, menu);
                            }
                        } else {
                            if (mState.getContacts() == GRANTED) {
                                getMenuInflater().inflate(R.menu.main_grid_download, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_grid, menu);
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
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // ????????????????????????
        switch (id) {
            case R.id.menu_edit: {
                // ??????????????????????????????
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
                // ??????????????????????????????
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
            // ????????????
            case R.id.menu_linear: {
                mHandler.post(() -> {
                    if(mState.isResumed()) {
                        mPref.edit().putInt(EXTRA_LAYOUT_ID, LAYOUT_LINEAR).apply();
                        updateView(toList(mDataSet));
                    }
                });
                return true;
            }
            // ?????????????????????
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
                mHandler.post(() -> SendHelper.startSearchActivity(mSelf));
                return true;
            }
            // ??????
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
                // ?????????
                mHandler.post(new SelectedAllRunner());
                return true;
            }
            case R.id.menu_share: {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TITLE, mSelected.get(0).displayName);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mSelected.get(0).displayName);
                    intent.putExtra(Intent.EXTRA_EMAIL, mSelected.get(0).send);
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
                // ????????????????????????
                String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);

                // ??????????????????????????????
                EditDialog fragment = EditDialog.newInstance(filename);
                fragment.show(getSupportFragmentManager(), EditDialog.class.getSimpleName());

                return true;
            }
            case R.id.menu_open_history: {
                startHistoryActivity(this);
                return true;
            }
            case R.id.menu_leave_history: {
                // ????????????
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
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        mState.setId(item.getItemId());
        // ?????????????????????????????????
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called when a drawer's arg changes.
     *
     * @param drawerView  The child view that was moved
     * @param slideOffset The new offset of this drawer within its range, from 0-1
     */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (mState.getId() != R.id.send) {
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
    }

    /**
     * ???????????????
     *
     * @param collection ??????
     */
    private void updateView(ArrayList<SimplePerson> collection) {
        // ????????????????????????
        disableProgressBar();
        // ??????????????????
        updateEmptyView(collection);
        // ?????????????????????
        updateMenu(collection);
        // ?????????????????????
        int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
        updateCollectionView(layout, collection);
        // ?????????????????????
        updateTitle(collection);
    }

    private void disableProgressBar() {
        // ??????????????????????????????
        ProgressBar bar = mProgressBar;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * ??????????????????
     *
     * @param collection ??????
     */
    private void updateEmptyView(List<SimplePerson> collection) {
        // ??????????????????
        ImageView view = mEmptyView;
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            // ????????????
            view.setVisibility(View.VISIBLE);
        } else {
            // ??????????????????
            view.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????
     *
     * @param collection ??????
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
     * ?????????????????????
     *
     * @param collection ??????
     */
    private void updateCollectionView(int id, ArrayList<SimplePerson> collection) {
        // ?????????????????????
        mCollectionView = getCollectionView(id, collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * ?????????????????????
     *
     * @param collection ??????
     */
    private void updateTitle(ArrayList<SimplePerson> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.send));
        } else {
            sb.append(this.getString(R.string.send)).append("  ").append(collection.size());
        }
        mToolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * ?????????????????????
     *
     * @param id         ?????????
     * @param collection ??????
     * @return ????????????
     */
    private ICollectionView<SimplePerson> getCollectionView(int id, ArrayList<SimplePerson> collection) {
        if (id == LAYOUT_GRID) {
            return ModuleFragment.newInstance(collection);
        }
        return ListFragment.newInstance(collection);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param collectionView ????????????
     * @param view           ????????????
     * @param item           ??????
     */
    @Override
    public void onSelectedMore(ICollectionView<SimplePerson> collectionView, final View view, final SimplePerson item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }


    // BEGIN_INCLUDE(show_popup)
    /**
     * ????????????????????????????????????
     *
     * @param view ??????
     * @param item ??????
     */
    private void showPopupMenu(final View view, final SimplePerson item) {
//        final PopupAdapter adapter = (PopupAdapter) getListAdapter();
//
//        // Retrieve the clicked item from view's tag
//        final String item = (String) view.getTag();

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

            // ???????????????
            mSelected.clear();
            mSelected.add(item);

            // ????????????????????????????????????
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
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return;
        }

        // ??????????????????????????????
        item.timestamp = System.currentTimeMillis();
        for (SimplePerson dest : mDataSet) {
            if (dest.equal(item)) {
                // ??????????????????
                dest.copyPerson(item);
            }
        }
        // ??????
        mClient.setPeople(mDataSet);

        // ??????????????????????????????
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{item.send});
            intent.putExtra(Intent.EXTRA_TEXT, item.note);
            intent.setType("text/plain");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimplePerson> collectionView, SimplePerson item) {
        // ??????????????????
        mBackup.clear();
        copyPeople(mBackup, mDataSet);

        // ???????????????
        item.isArchive = true;
        for (SimplePerson dest : mDataSet) {
            // ??????
            if (dest.equal(item)) {
                dest.copyPerson(item);
            }
        }

        // ????????????????????????
        disableProgressBar();
        // ?????????????????????
        if (mCollectionView != null) {
            mUndos.clear();
            int position = mCollectionView.remove(item.toSend());
            // ?????????????????????
            mUndos.add(new Action(INSERT, position, getPerson(item.uuid, mBackup)));
            Collections.reverse(mUndos);
        }

        // ??????
        mClient.setPeople(mDataSet);

        // ???????????????????????????
        String message = getString(R.string.moved_to_archive_item);
        makeUndoSnackbar(mCoordinatorLayout, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimplePerson> view) {
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimplePerson> view) {
        // ????????????
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
                // ?????????????????????
                switch (mState.getSelection()) {
                    case UNSELECTED: {
                        // ??????????????????????????????
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(false);
                        }

                        // ???????????????
                        if (mCollectionView != null) {
                            mCollectionView.diselect();
                        }

                        // ?????????????????????????????????????????????
                        enableNavigationDrawer();

                        // ????????????????????????
                        updateTitle(toList(mDataSet));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_600));
                        // ??????????????????????????????
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.red_800));
                        }

                        // ??????????????????
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_800));

                        // ?????????????????????
                        invalidateOptionsMenu();

                        break;
                    }
                    // ???????????????
                    case SELECTED_ALL: {

                        // ????????????????????????????????????
                        disableNavigationDrawer();

                        // ??????????????????????????????
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ?????????
                        if (mCollectionView != null) {
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());
                        }

                        // ????????????????????????
                        mToolbar.setTitle(String.valueOf(mSelected.size()));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.teal_800));
                        }

                        // ??????????????????
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_800));

                        // ??????????????????
                        invalidateOptionsMenu();

                        break;
                    }
                    case MULTI_SELECTED:
                    case SELECTED: {

                        // ????????????????????????????????????
                        disableNavigationDrawer();

                        // ??????????????????????????????
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ????????????????????????
                        mToolbar.setTitle(String.valueOf(mSelected.size()));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.teal_800));
                        }

                        // ??????????????????
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_800));

                        // ??????????????????
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
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimplePerson> collectionView, View view, SimplePerson item, Collection<? extends SimplePerson> collection) {
        // ??????????????????
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // ?????????????????????
        if (mSelected.size() == collection.size()) {
            // ?????????
            if (!mState.changeSelection(SELECTED_ALL)) {
                // ?????????????????????
                // ??????????????????
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // ???????????????
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // ??????
            if (!mState.changeSelection(SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else {
            // ?????????
            mState.changeSelection(UNSELECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {

        // ????????????
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);

        // ?????????
        mState.changeSelection(SELECTING);

        // ??????????????????????????????
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }
        // ????????????????????????
        updateTitle(arrayList);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.red_600));

        // ??????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red_800));
            //getWindow().setBackgroundDrawable(DevelopHelper.getBackgroundDrawble(mSelf, mState));
        }
        // ??????????????????
        mLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red_800));
        // ?????????????????????????????????????????????


        // ?????????????????????
        invalidateOptionsMenu();


        // ???????????????
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

        // ??????
        mClient.setPeople(mDataSet);
    }

    /**
     * ????????????
     *
     * @param view       ????????????
     * @param collection ??????
     */
    @Override
    public void onUpdated(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // ????????????
        ArrayList<SimplePerson> arrayList = new ArrayList<>(collection);

        // ??????????????????
        updateEmptyView(arrayList);
        // ??????????????????
        updateTitle(arrayList);
        // ?????????????????????
        updateMenu(arrayList);
    }

    /**
     * ???????????????????????????
     *
     * @param layout  ???????????????
     * @param message ???????????????
     */
    private void makeUndoSnackbar(CoordinatorLayout layout, String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(View v) {
                        // ??????????????????????????????
                        if(!mState.isResumed()) {
                            return;
                        }

                        // ??????????????????????????????
                        disableProgressBar();
                        // ?????????????????????
                        if (mCollectionView != null) {
                            try {
                                for (Action undo : mUndos) {
                                    switch (undo.action) {
                                        case INSERT: {
                                            // ?????????????????????
                                            mCollectionView.insert(undo.arg, undo.object.toSend());
                                            break;
                                        }
                                        case CHANGE: {
                                            // ?????????????????????
                                            mCollectionView.change(undo.object.toSend());
                                            break;
                                        }
                                        case REMOVE: {
                                            // ?????????????????????
                                            mCollectionView.remove(undo.object.toSend());
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // ?????????????????????
                                int layout = mPref.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT);
                                updateCollectionView(layout, mBackup);
                            }
                            mUndos.clear();
                        }

                        ArrayList<SimplePerson> list = toList(mBackup);
                        // ??????????????????
                        updateEmptyView(list);
                        // ??????????????????
                        updateTitle(list);
                        // ?????????????????????
                        updateMenu(list);

                        // ???????????????
                        mDataSet.clear();
                        copyPeople(mDataSet, mBackup);

                        // ??????
                        mClient.setPeople(mDataSet);
                    }
                })
                .show();
    }

    /**
     * ??????
     */
    private class CreateRunner implements Runnable {

        /**
         * @serial ??????
         */
        SimplePerson mItem;

        /**
         * ?????????????????????
         *
         * @param item ??????
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

                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ??????????????????
                mDataSet.add(0, mItem);

                // ????????????????????????
                disableProgressBar();
                // ?????????????????????
                if (mCollectionView != null) {
                    mUndos.clear();
                    // ???????????????????????????
                    mCollectionView.insert(0, mItem.toSend());
                    // ?????????????????????
                    mUndos.add(new Action(REMOVE, 0, mItem));
                }
                // ??????????????????
                if (mItem != null) {
                    // ???????????????????????????????????????
                    if (mItem.imagePath.equals(INVALID_STRING_VALUE)) {
                        for (SimplePerson dest : mDataSet) {
                            // ?????????????????????
                            if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                                // ?????????????????????
                                if (dest.displayName.equals(mItem.displayName)) {
                                    // ????????????????????????????????????
                                    if (!dest.imagePath.equals(INVALID_STRING_VALUE)) {
                                        mItem.imagePath = dest.imagePath;
                                        // ?????????????????????
                                        if (mCollectionView != null) {
                                            // ???????????????
                                            int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toSend());
                                            // ?????????????????????
                                            mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ???????????????????????????
                        for (SimplePerson dest : mDataSet) {
                            // ?????????????????????
                            if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                                // ?????????????????????
                                if (dest.displayName.equals(mItem.displayName)) {
                                    // ???????????????
                                    dest.imagePath = mItem.imagePath;
                                    // ?????????????????????
                                    if (mCollectionView != null) {
                                        // ???????????????
                                        int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toSend());
                                        // ?????????????????????
                                        mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                                    }
                                }
                            }
                        }
                    }
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                // ????????????????????????
                String message = getString(R.string.created_item);
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ??????
     */
    private class ModifyRunner implements Runnable {

        /**
         * @serial ??????
         */
        SimplePerson mItem;

        /**
         * ?????????????????????
         *
         * @param item ??????
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
                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ??????????????????
                for (SimplePerson dest : mDataSet) {
                    // ??????
                    if (dest.equal(mItem)) {
                        // ??????????????????
                        dest.copyPerson(mItem);
                    }
                }

                // ????????????????????????
                disableProgressBar();

                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    int position;
                    if (mItem != null) {
                        if (mItem.isArchive) {
                            // ????????????????????????
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toSend());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else if (mItem.isTrash) {
                            // ??????????????????
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toSend());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else {
                            // ???????????????
                            position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toSend());
                            mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                        }
                    }
                }
                // ??????????????????
                if (mItem != null) {
                    for (SimplePerson dest : mDataSet) {
                        // ?????????????????????
                        if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                            // ?????????????????????
                            if (dest.displayName.equals(mItem.displayName)) {
                                // ?????????????????????????????????
                                if (dest.imagePath.equals(mItem.imagePath)) {
                                    dest.imagePath = mItem.imagePath;
                                    if (!dest.isTrash && !dest.isArchive) {
                                        // ???????????????
                                        assert mCollectionView != null;
                                        int position = Objects.requireNonNull(mCollectionView).change(dest.toSend());
                                        mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                                    }
                                }
                            }
                        }
                    }
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                String message = getString(R.string.modified_item);
                if (mItem != null) {
                    if (mItem.isArchive) {
                        message = getString(R.string.moved_to_archive_item);
                    } else if (mItem.isTrash) {
                        message = getString(R.string.moved_to_trash_item);
                    }
                }
                // ???????????????????????????
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ?????????????????????
     */
    private class SelectedArchiveRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {

                // ?????????????????????
                mState.changeSelection(UNSELECTED);

                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ??????????????????
                for (SimplePerson src : mSelected) {
                    // ?????????????????????
                    src.isSelected = false;
                    // ??????????????????????????????
                    src.isArchive = true;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // ??????
                        if (dest.equal(src)) {
                            // ??????????????????
                            dest.copyPerson(src);
                        }
                    }
                }
                // ????????????????????????
                disableProgressBar();
                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src.isArchive) {
                            // ?????????????????????
                            position = mCollectionView.remove(src.toSend());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else if (src.isTrash) {
                            // ?????????????????????
                            position = mCollectionView.remove(src.toSend());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else {
                            // ?????????????????????
                            position = mCollectionView.change(src.toSend());
                            mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                        }
                    }
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                mSelected.clear();

                // ???????????????????????????
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_archive_item);
                    makeUndoSnackbar(mCoordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_archive_some_items, mUndos.size());
                    makeUndoSnackbar(mCoordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????
     */
    private class SelectedTrashRunner implements Runnable {

        @Override
        public void run() {

            try {

                // ?????????????????????
                mState.changeSelection(UNSELECTED);

                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ??????????????????
                for (SimplePerson src : mSelected) {
                    // ?????????????????????
                    src.isSelected = false;
                    // ??????????????????
                    src.isTrash = true;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // ??????
                        if (dest.equal(src)) {
                            // ??????????????????
                            dest.copyPerson(src);
                        }
                    }
                }
                // ????????????????????????
                disableProgressBar();
                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src != null) {
                            if (src.isArchive) {
                                // ?????????????????????
                                position = mCollectionView.remove(src.toSend());
                                // ?????????????????????
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else if (src.isTrash) {
                                // ?????????????????????
                                position = mCollectionView.remove(src.toSend());
                                // ?????????????????????
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else {
                                // ?????????????????????
                                position = mCollectionView.change(src.toSend());
                                mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                            }
                        }
                    }
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                mSelected.clear();

                // ???????????????????????????
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_trash_item);
                    makeUndoSnackbar(mCoordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_trash_some_items, mUndos.size());
                    makeUndoSnackbar(mCoordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????
     */
    private class DiselectRunner implements Runnable {
        @Override
        public void run() {

            try {

                // ????????????
                mState.changeSelection(UNSELECTED);

            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }

    /**
     * ?????????
     */
    private class CopyRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {

                // ?????????????????????
                mState.changeSelection(UNSELECTED);

                mBackup.clear();
                copyPeople(mBackup, mDataSet);
                SimplePerson item = SimplePerson.createInstance();
                item.setParams(mSelected.get(0));

                // ???????????????
                mDataSet.add(0, item);

                // ????????????????????????
                disableProgressBar();
                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    // ?????????????????????
                    mCollectionView.insert(0, item.toSend());
                    // ?????????????????????
                    mUndos.add(new Action(REMOVE, 0, item));
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                mSelected.clear();

                // ????????????????????????
                String message = getString(R.string.created_item);
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ?????????
     */
    private class SelectedAllRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // ?????????
                mState.changeSelection(SELECTED_ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ???????????????????????????????????????
     */
    private class SelectedChangeColorRunner implements Runnable {

        COLOR mCOLOR;

        /**
         * ?????????????????????
         *
         * @param color ???
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
                // ?????????????????????
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                for (SimplePerson src : mSelected) {
                    // ?????????????????????
                    src.isSelected = false;
                    // ????????????
                    src.color = mCOLOR;
                    // ???????????????
                    src.imagePath = INVALID_STRING_VALUE;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // ??????
                        if (dest.equal(src)) {
                            // ??????????????????
                            dest.copyPerson(src);
                        }
                    }
                }

                // ??????????????????????????????
                disableProgressBar();
                // ?????????????????????
                if (mCollectionView != null) {
                    //mUndos.clear();
                    for (SimplePerson src : mSelected) {
                        // ?????????????????????
                        mCollectionView.change(src.toSend());
                    }
                }

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                mSelected.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ??????
     */
    private class ChangeRunner implements Runnable {

        /**
         * @serial ??????
         */
        SimplePerson mItem;

        /**
         * ?????????????????????
         *
         * @param item ??????
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
                // ???????????????????????????
                // ???????????????????????????
                if ((mCollectionView != null) && (mItem != null)) {

                    // ??????
                    for (SimplePerson dest : mDataSet) {
                        // ??????
                        if (dest.equal(mItem)) {
                            dest.copyPerson(mItem);
                        }
                    }

                    // ??????????????????????????????
                    disableProgressBar();

                    // ?????????????????????
                    mCollectionView.change(getPerson(mItem.uuid, mDataSet).toSend());

                    // ??????
                    mClient.setPeople(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
