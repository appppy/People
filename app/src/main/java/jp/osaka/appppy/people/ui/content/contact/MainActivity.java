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
 * ??????????????????????????????
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
     * @serial ??????
     */
    private final String TAG = "MainActivity";

    /**
     * @serial ?????????????????????
     */
    private ActivityContactBinding mBinding;

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
     * @serial ????????????????????????
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
     * @serial URI
     */
    private Uri mUri;

    /**
     * @serial ??????????????????????????????
     */
    private boolean isFile = false;


    /**
     * @serial ????????????????????????
     */
    private boolean isHistory = false;

    /**
     * @serial ??????
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

        // ???????????????
        mSelf = this;

        // ??????????????????????????????
        mPref = getDefaultSharedPreferences(this);

        // ??????????????????
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_CONTACT, COLOR.LIGHT_BLUE.name()));
        setTheme(ThemeHelper.getTheme(color));

        // ?????????????????????
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact);

        // ????????????????????????
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.contact);
        }

        // ??????????????????????????????????????????????????????
        setupFloatingActionButton();

        // ??????????????????????????????
        enableNavigationDrawer();

        // ??????
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
                // ???????????????
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
                // ????????????????????????????????????
                mRedos.clear();
                mRedos.add(new Action(CREATE, 0, item));
            }
        }
        if(!mRedos.isEmpty()) {
            mPref.edit().putInt(EXTRA_CONTENT, R.id.contact).apply();
        }

        // ???????????????????????????????????????
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestContactsPermission();
//        } else {
//            // ?????????????????????????????????
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
    protected void onStop() {
   //     mProxy.disconnect();

        super.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // ??????
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

        // ????????????
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case REQUEST_CREATE_ITEM: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                    // ????????????????????????????????????
                    mRedos.clear();
                    mRedos.add(new Action(CREATE, 0, item));
                }
                break;
            }
            case REQUEST_DETAIL_ITEM: {
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
            case REQUEST_EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                    // ????????????????????????????????????
                    mRedos.clear();
                    mRedos.add(new Action(MODIFY, 0, item));
                }
                break;
            }
            case REQUEST_SYNC_FILE: {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        // ????????????????????????????????????
                        mUri = data.getData();
                    }
                }
                break;
            }
            case REQUEST_OPEN_FILE: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
                    mPref.edit().putString(EXTRA_FILE_NAME, data.getStringExtra(EXTRA_FILE_NAME)).apply();

                    // ????????????????????????????????????
                    isFile = true;
                }
                break;
            }
            case REQUEST_OPEN_HISTORY: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    mHistory = bundle.getParcelable(EXTRA_HISTORY);
                    // ????????????????????????????????????
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
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

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
                mClient.setPeople(mDataSet);
                //mHandler.post(new DiselectRunner());
                startEditActivity(mSelf, mSelf, person);
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
                // ?????????????????????
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
                startSearchActivity(this);
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
                // ????????????????????????
                String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);

                // ??????????????????????????????
                EditDialog fragment = EditDialog.newInstance(filename);
                fragment.show(getSupportFragmentManager(), "EditDialog");

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
                    // ??????????????????????????????
                    if (!mState.isResumed()) {
                        return;
                    }
                    // ???????????????
                    mDataSet.clear();
                    mDataSet.addAll(people);
                    // ???????????????
                    updateView(toList(mDataSet));
                    // FAB??????
                    setupFloatingActionButton();
                    mBinding.fab.show();
                }

                // ?????????????????????
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
                    // ????????????????????????
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
        // ?????????????????????????????????
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // ????????????
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // ????????????
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
        // ????????????
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

    /**
     * ????????????????????????????????????
     *
     * @param view ??????
     * @param item ??????
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
            // ???????????????
            mSelected.clear();
            mSelected.add(item);

            // ??????????????????
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
                dest.copyPerson(item);
            }
        }
        mClient.setPeople(mDataSet);

        // ?????????????????????
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
        // ??????????????????
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // ?????????????????????
        if (mSelected.size() == collection.size()) {
            // ?????????
            if (!mState.changeSelection(SELECTED_ALL)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // ???????????????
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // ??????????????????
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // ??????
            if (!mState.changeSelection(SELECTED)) {
                // ?????????????????????
                // ??????????????????
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
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
    public void onSwiped(ICollectionView<SimplePerson> collectionView, SimplePerson item) {
        // ??????????????????
        mBackup.clear();
        copyPeople(mBackup, mDataSet);

        // ???????????????
        item.isArchive = true;
        // ??????
        for (SimplePerson dest : mDataSet) {
            // ??????
            if (dest.equal(item)) {
                dest.copyPerson(item);
            }
        }

        // ??????????????????????????????
        disableProgressBar();
        // ?????????????????????
        if (mCollectionView != null) {
            mUndos.clear();
            // ?????????????????????
            int position = mCollectionView.remove(item.toContact());
            mUndos.add(new Action(INSERT, position, getPerson(item.uuid, mBackup)));
            Collections.reverse(mUndos);
        }

        // ??????
        mClient.setPeople(mDataSet);

        // ???????????????????????????
        String message = getString(R.string.moved_to_archive_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // ????????????
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
        // ??????????????????
        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.light_blue_800));
        enableNavigationDrawer();
        invalidateOptionsMenu();

        //???????????????
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
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_600));
                        // ??????????????????????????????
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));
                        }

                        // ??????????????????
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));

                        // ??????????????????????????????????????????????????????
                        setupFloatingActionButton();
                        mBinding.fab.show();

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
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }

                        // ??????????????????
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));

                        // ?????????????????????????????????????????????????????????
                        //mBinding.fab.hide();
                        setupFloatingActionButton();

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
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }

                        // ??????????????????
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));

                        // ?????????????????????????????????????????????????????????
                        //mBinding.fab.hide();
                        setupFloatingActionButton();

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
     * ??????????????????????????????????????????????????????
     */
    private void setupFloatingActionButton() {
        if (mState.getSelection() == UNSELECTED) {
            mBinding.fab.setImageResource(R.drawable.ic_add_white_36dp);
            mBinding.fab.setOnClickListener(v -> {
                // ??????????????????????????????
                if (!mState.isResumed()) {
                    return;
                }
                if (isMax(mDataSet)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.warning_message)).append("  ").append(mDataSet.size()).append("\n");
                    // ????????????
                    new AlertDialog.Builder(mSelf)
                            .setTitle(getString(R.string.warning))
                            .setMessage(sb.toString())
                            .show();
                    sb.delete(0, sb.length());
                } else {
                    // ????????????
                    startCreateActivity(mSelf, mSelf, v, createInstance());
                }
            });
            mBinding.fab.hide();
            mBinding.fab.show();
        } else {
            if (mSelected.size() == 1) {
                mBinding.fab.setImageResource(R.drawable.ic_create_white_36dp);
                mBinding.fab.setOnClickListener(v -> {
                    // ??????????????????????????????
                    if (!mState.isResumed()) {
                        return;
                    }

                    // ??????????????????????????????
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
     * ??????????????????????????????
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
     * ??????????????????????????????
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
     * ????????????????????????
     *
     * @return ?????????
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
//     * ???????????????????????????
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
     * ???????????????????????????
     */
    private void downloadPicker(Uri uri) {
        new FileAsyncTask(this, this, uri).execute();
    }

    /**
     * ID???????????????????????????
     *
     * @param resolver ??????
     * @param id       ?????????
     * @return ????????????
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
     * ID????????????????????????????????????
     *
     * @param resolver ??????
     * @param id       ?????????
     * @return ?????????????????????
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
        // ??????????????????
        updateTitle(collection);
    }

    /**
     * ??????????????????????????????
     */
    private void disableProgressBar() {
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * ??????????????????
     *
     * @param collection ??????
     */
    private void updateEmptyView(List<SimplePerson> collection) {
        // ??????????????????
        ImageView view = mBinding.emptyView;
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            view.setVisibility(View.VISIBLE);
        } else {

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
     * @param id         ?????????
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
            sb.append(this.getString(R.string.contact));
        } else {
            sb.append(this.getString(R.string.contact)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
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
                        // ?????????????????????????????????
                        if (mCollectionView != null) {
                            // ?????????????????????
                            try {
                                for (Action undo : mUndos) {
                                    switch (undo.action) {
                                        case INSERT: {
                                            // ?????????????????????
                                            mCollectionView.insert(undo.arg, undo.object.toContact());
                                            break;
                                        }
                                        case CHANGE: {
                                            // ?????????????????????
                                            mCollectionView.change(undo.object.toContact());
                                            break;
                                        }
                                        case REMOVE: {
                                            // ?????????????????????
                                            mCollectionView.remove(undo.object.toContact());
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

                        // ???????????????
                        ArrayList<SimplePerson> list = toList(mBackup);
                        // ??????????????????
                        updateEmptyView(list);
                        // ?????????????????????
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
     * ??????????????????????????????.
     *
     * @param dialog ???????????????
     * @param name   ?????????
     */
    @Override
    public void onPositiveButtonClicked(IDialog dialog, String name) {
        if (name.equals(PeopleConstants.INVALID_STRING_VALUE)) {
            Toast.makeText(mSelf, R.string.failed_save_file, Toast.LENGTH_LONG).show();
        } else {
            // ??????????????????????????????
            mHandler.post(new WriteFileRunner(name));
        }
    }

    /**
     * ??????????????????????????????.
     *
     * @param dialog ???????????????
     * @param name   ?????????
     */
    @Override
    public void onNegativeButtonClicked(IDialog dialog, String name) {

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
                // ???????????????????????????
                // ???????????????????????????
                if ((mCollectionView != null) && (mItem != null)) {

                    // ??????????????????
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

                    // ???????????????
                    mDataSet.add(0, mItem);

                    // ??????????????????????????????
                    disableProgressBar();

                    // ?????????????????????
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

                    // ??????
                    mClient.setPeople(mDataSet);

                    // ???????????????????????????
                    makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.created_item));
                }

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
                // ?????????????????????????????????????????????????????????
                if ((mCollectionView != null) && (mItem != null)) {

                    // ??????????????????
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

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
                    mUndos.clear();
                    if (mItem.isArchive) {
                        // ?????????????????????
                        int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    } else if (mItem.isTrash) {
                        // ?????????????????????
                        int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    } else {
                        // ?????????????????????
                        int position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());
                        mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                    }
                    for (SimplePerson dest : mDataSet) {
                        if (!dest.displayName.equals(INVALID_STRING_VALUE)) {
                            if (dest.displayName.equals(mItem.displayName)) {
                                if (dest.imagePath.equals(mItem.imagePath)) {
                                    dest.imagePath = mItem.imagePath;
                                    if (!dest.isTrash && !dest.isArchive) {
                                        // ?????????????????????
                                        int position = mCollectionView.change(dest.toContact());
                                        mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                    }
                                }
                            }
                        }
                    }
                    Collections.reverse(mUndos);

                    // ??????
                    mClient.setPeople(mDataSet);

                    // ???????????????????????????
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
     * ??????
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
     * ??????????????????
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

                // ???????????????????????????????????????
                //mProgressDialog.setMessage(getString(R.string.loading));

                // ???????????????????????????????????????
                //mProgressDialog.show();

                isActive = true;
                //
                // update UI
                handler.post(() -> {
                    if (mDataSet.isEmpty()) {
                        // ??????????????????????????????
                        if(!mState.isResumed()) {
                            return;
                        }

                        // ??????????????????
                        mBackup.clear();

                        if (result.size() > MAX) {
                            mDataSet.addAll(result.subList(0, MAX));
                        } else {
                            mDataSet.addAll(result);
                        }
                        // ???????????????
                        updateView(toList(mDataSet));

                        // ??????
                        mClient.setPeople(mDataSet);

                        String message = getString(R.string.sync_item);
                        // ???????????????????????????
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);

                    } else {
                        // ??????????????????
                        mBackup.clear();
                        copyPeople(mBackup, mDataSet);

                        // ??????????????????????????????
                        mBinding.productImageLoading.setVisibility(View.INVISIBLE);

                        // ?????????????????????
                        mUndos.clear();
                        for (SimplePerson src : result) {
                            for (SimplePerson dest : mDataSet) {
                                if (dest.displayName.equals(src.displayName)) {
                                    dest.isChanged = false;
                                    // tel?????????
                                    if (!src.call.equals(INVALID_STRING_VALUE)) {
                                        dest.call = src.call;
                                        dest.modifiedDate = src.modifiedDate;
                                        dest.isChanged = true;
                                    }
                                    // email?????????
                                    if (!src.send.equals(INVALID_STRING_VALUE)) {
                                        dest.send = src.send;
                                        dest.modifiedDate = src.modifiedDate;
                                        dest.isChanged = true;
                                    }
                                    // ?????????????????????
                                    if (dest.isChanged) {
                                        dest.isChanged = false;
                                        if (!dest.isArchive && !dest.isTrash) {
                                            if (mCollectionView != null) {
                                                // ?????????????????????
                                                int position = mCollectionView.change(dest.toContact());
                                                mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Collections.reverse(mUndos);

                        // ??????
                        mClient.setPeople(mDataSet);

                        // ???????????????????????????
                        String message = getString(R.string.sync_item);
                        makeUndoSnackbar(mBinding.coordinatorLayout, message);
                    }

                    // ????????????????????????
                    //if (mProgressDialog != null && mProgressDialog.isShowing()) {
                     //   mProgressDialog.dismiss();
                    //}

                    isActive = false;
                });
            });
        }
    }

    /**
     * ??????????????????
     */
    @SuppressLint("StaticFieldLeak")
    private class FileAsyncTask {
        // ProgressDialog mProgressDialog;

        /**
         * @serial ????????????
         */
        boolean isActive = false;

        /**
         * @serial ?????????????????????
         */
        Activity mActivity;

        /**
         * @serial ??????????????????
         */
        private final Context context;

        /**
         * @serial URI
         */
        private final Uri uri;

        /**
         * ?????????????????????
         *
         * @param activity ?????????????????????
         * @param context  ??????????????????
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

                // ???????????????????????????????????????
              //  mProgressDialog.setMessage(getString(R.string.loading));

                // ???????????????????????????????????????
                //mProgressDialog.show();

                isActive = true;
                        // update UI
                        handler.post(() -> {
                            if (cacheFile != null) {

                                ArrayList<SimplePerson> collection = readFile(cacheFile);

                                if (mDataSet.isEmpty()) {

                                    // ??????????????????????????????
                                    if(!mState.isResumed()) {
                                        return;
                                    }

                                    mBackup.clear();

                                    if (collection.size() > MAX) {
                                        mDataSet.addAll(collection.subList(0, MAX));
                                    } else {
                                        mDataSet.addAll(collection);
                                    }
                                    // ???????????????
                                    updateView(toList(mDataSet));

                                    // ??????
                                    mClient.setPeople(mDataSet);

                                    // ???????????????????????????
                                    makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.sync_item));
                                } else {
                                    // ??????????????????
                                    mBackup.clear();
                                    copyPeople(mBackup, mDataSet);

                                    // ????????????
                                    mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), PeopleHelper.toJSONString(mDataSet)));


                                    // ????????????????????????
                                    disableProgressBar();
                                    // ?????????????????????
                                    if (mCollectionView != null) {

                                        // ?????????????????????
                                        mUndos.clear();

                                        boolean isFound;
                                        ArrayList<SimplePerson> stack = new ArrayList<>();
                                        for (SimplePerson src : collection) {
                                            isFound = false;
                                            for (SimplePerson dest : mDataSet) {
                                                // ????????????????????????????????????
                                                if (dest.displayName.equals(src.displayName)) {
                                                    isFound = true;

                                                    if (!dest.isArchive && !dest.isTrash) {
                                                        boolean isMod = false;
                                                        // note?????????
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
                                                            // ?????????????????????
                                                            int position = mCollectionView.change(dest.toCall());
                                                            // ?????????????????????
                                                            mUndos.add(new Action(CHANGE, position, getPerson(dest.uuid, mBackup)));
                                                        }
                                                    }

                                                }
                                            }
                                            // ???????????????????????????
                                            if(!isFound) {
                                                stack.add(src);
                                            }
                                        }
                                        Collections.reverse(mUndos);

                                        // ???????????????
                                        if(!stack.isEmpty()) {
                                            mDataSet.addAll(stack);

                                            // ???????????????
                                            updateView(toList(mDataSet));
                                        }
                                    }

                                    // ??????
                                    mClient.setPeople(mDataSet);

                                    // ???????????????????????????
                                    String message = getString(R.string.sync_item);
                                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                                }


                                // ????????????????????????
                          //      if (mProgressDialog != null && mProgressDialog.isShowing()) {
                           //         mProgressDialog.dismiss();
                            //    }

                                // ??????????????????????????????
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
     * ?????????????????????
     */
    private class SelectedArchiveRunner implements Runnable {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // ?????????????????????????????????
                if (mCollectionView != null) {

                    mState.changeSelection(UNSELECTED);

                    // ??????????????????
                    mBackup.clear();
                    copyPeople(mBackup, mDataSet);

                    for (SimplePerson src : mSelected) {
                        src.isSelected = false;
                        src.isArchive = true;
                    }
                    for (SimplePerson dest : mDataSet) {
                        for (SimplePerson src : mSelected) {
                            // ??????
                            if (dest.equal(src)) {
                                dest.copyPerson(src);
                            }
                        }
                    }

                    // ??????????????????????????????
                    disableProgressBar();

                    // ?????????????????????
                    mUndos.clear();
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src.isArchive) {
                            // ?????????????????????
                            position = mCollectionView.remove(src.toContact());
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else if (src.isTrash) {
                            // ?????????????????????
                            position = mCollectionView.remove(src.toContact());
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        } else {
                            // ?????????????????????
                            position = mCollectionView.change(src.toContact());
                            mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                        }
                    }
                    Collections.reverse(mUndos);

                    // ??????
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
     * ???????????????
     */
    private class SelectedTrashRunner implements Runnable {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ???????????????
                for (SimplePerson src : mSelected) {
                    src.isSelected = false;
                    src.isTrash = true;
                }
                for (SimplePerson dest : mDataSet) {
                    for (SimplePerson src : mSelected) {
                        // ??????
                        if (dest.equal(src)) {
                            dest.copyPerson(src);
                        }
                    }
                }

                // ??????????????????????????????
                disableProgressBar();
                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (src != null) {
                            if (src.isArchive) {
                                // ?????????????????????
                                position = mCollectionView.remove(src.toContact());
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else if (src.isTrash) {
                                // ?????????????????????
                                position = mCollectionView.remove(src.toContact());
                                mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                            } else {
                                // ?????????????????????
                                position = mCollectionView.change(src.toContact());
                                mUndos.add(new Action(CHANGE, position, getPerson(src.uuid, mBackup)));
                            }
                        }
                    }
                }
                Collections.reverse(mUndos);

                // ??????
                mClient.setPeople(mDataSet);

                // ????????????????????????
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
     * ?????????
     */
    private class DiselectRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
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
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ???????????????
                SimplePerson item = SimplePerson.createInstance();
                item.copyPerson(mSelected.get(0));

                // ???????????????
                mDataSet.add(0, item);

                // ??????????????????????????????
                disableProgressBar();

                // ?????????????????????
                if (mCollectionView != null) {
                    mUndos.clear();
                    int position = 0;
                    mCollectionView.insert(position, item);
                    mUndos.add(new Action(REMOVE, position, item.toContact()));
                    Collections.reverse(mUndos);
                }

                // ??????
                mClient.setPeople(mDataSet);

                mSelected.clear();

                // ???????????????????????????
                makeUndoSnackbar(mBinding.coordinatorLayout, getString(R.string.created_item));

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
     * ????????????????????????
     */
    private class ReadFileRunner implements Runnable {
        /**
         * @serial ???????????????
         */
        String filename;

        /**
         * ?????????????????????
         *
         * @param filename ???????????????
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
                // ????????????
                mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), PeopleHelper.toJSONString(mDataSet)));

                // ???????????????
                mDataSet.clear();
                mDataSet.addAll(FileHelper.readFile(mSelf, filename + ".csv"));

                // ?????????????????????
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // ??????
                mClient.setPeople(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????
     */
    private class WriteFileRunner implements Runnable {
        /**
         * @serial ???????????????
         */
        String filename;

        /**
         * ?????????????????????
         *
         * @param filename ???????????????
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
                // ??????????????????
                writeFile(mSelf, filename + ".csv", mDataSet);
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
                        mCollectionView.change(src.toContact());
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
                    mCollectionView.change(getPerson(mItem.uuid, mDataSet).toContact());

                    // ??????
                    mClient.setPeople(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ??????????????????
     */
    private class ReadHistoryRunner implements Runnable {
        /**
         * @serial ???????????????
         */
        History mHistory;

        /**
         * ?????????????????????
         *
         * @param history ???????????????
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
                // ???????????????
                ArrayList<SimplePerson> people = PeopleHelper.toPeople(mHistory.message);
                mDataSet.clear();
                mDataSet.addAll(people);

                // ?????????????????????
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // ??????
                mClient.setPeople(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
