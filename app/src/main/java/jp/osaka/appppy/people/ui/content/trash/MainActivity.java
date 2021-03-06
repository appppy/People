package jp.osaka.appppy.people.ui.content.trash;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
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

import jp.osaka.appppy.people.PeopleState;
import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.constants.COLOR;
import jp.osaka.appppy.people.databinding.ActivityTrashBinding;
import jp.osaka.appppy.people.service.PeopleClient;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.service.history.HistoryProxy;
import jp.osaka.appppy.people.ui.Action;
import jp.osaka.appppy.people.ui.content.BaseAdmobActivity;
import jp.osaka.appppy.people.ui.content.ListFragment;
import jp.osaka.appppy.people.ui.content.ModuleFragment;
import jp.osaka.appppy.people.utils.PeopleHelper;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static androidx.core.view.GravityCompat.START;
import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.ACTION.INSERT;
import static jp.osaka.appppy.people.constants.ACTION.MODIFY;
import static jp.osaka.appppy.people.constants.ActivityTransition.REQUEST_DETAIL_ITEM;
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
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_TRASH;
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
import static jp.osaka.appppy.people.ui.content.trash.TrashHelper.toEmptyList;
import static jp.osaka.appppy.people.ui.content.trash.TrashHelper.toList;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.getStartActivity;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Line;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startDetailActivity_from_Module;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;
import static jp.osaka.appppy.people.utils.SelectHelper.getSelectedCollection;

/**
 * ???????????????
 */
public class MainActivity extends BaseAdmobActivity implements
        PeopleClient.Callbacks,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        ICollectionView.Callbacks<SimplePerson>,
        View.OnClickListener,
        PeopleState.Callbacks {

    /**
     * @serial ??????
     */
    public PeopleState mState = new PeopleState();

    /**
     * @serial ??????
     */
    private Activity mSelf;

    /**
     * @serial ?????????????????????
     */
    private SharedPreferences mPref;

    /**
     * @serial ????????????
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial ??????????????????
     */
    private final PeopleClient mClient = new PeopleClient(this, this);

    /**
     * @serial ??????????????????
     */
    private final HistoryProxy mProxy = new HistoryProxy(this);

    /**
     * @serial ??????????????????
     */
    private ArrayList<SimplePerson> mDataSet = new ArrayList<>();

    /**
     * @serial ??????????????????
     */
    private final ArrayList<SimplePerson> mBackup = new ArrayList<>();

    /**
     * @serial ???????????????
     */
    private final ArrayList<SimplePerson> mSelected = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mRedos = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mUndos = new ArrayList<>();

    /**
     * @serial ?????????????????????
     */
    private ActivityTrashBinding mBinding;

    /**
     * @serial ?????????
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial ????????????
     */
    private ICollectionView<SimplePerson> mCollectionView;

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
                }
                // ?????????????????????
                for (Action doAction : mRedos) {
                    if (doAction.action.equals(MODIFY)) {
                        if (!doAction.object.isTrash) {
                            UnTrashRunner runner = new UnTrashRunner(doAction.object);
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
                        // ???????????????????????????
                        if (mCollectionView != null) {
                            // ??????????????????????????????
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(false);
                            }

                            // ???????????????
                            mCollectionView.diselect();

                            // ?????????????????????????????????????????????
                            enableNavigationDrawer();

                            // ????????????????????????
                            updateTitle(toList(mDataSet));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            // ??????????????????????????????
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }

                            // ??????????????????
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // ?????????????????????
                            invalidateOptionsMenu();
                        }
                        break;
                    }
                    // ???????????????
                    case SELECTED_ALL: {
                        // ???????????????????????????
                        if (mCollectionView != null) {

                            // ????????????????????????????????????
                            disableNavigationDrawer();

                            // ??????????????????????????????
                            ActionBar bar = getSupportActionBar();
                            if (bar != null) {
                                bar.setDisplayHomeAsUpEnabled(true);
                            }

                            // ?????????
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());

                            // ????????????????????????
                            mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                            mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                            }

                            // ??????????????????
                            mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

                            // ??????????????????
                            invalidateOptionsMenu();
                        }
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
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.grey_800));
                        }

                        // ??????????????????
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.grey_800));

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

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.trash_selected_one_more, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {

            // ?????????????????????
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

        // ?????????????????????
        startDetailActivity_from_Line(this, this, view, item);
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
        item.isTrash = false;
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
            // ?????????????????????
            int position = mCollectionView.remove(item.toContact());
            // ?????????????????????
            mUndos.add(new Action(INSERT, position, getPerson(item.uuid, mBackup)));
            Collections.reverse(mUndos);
        }

        // ??????
        mClient.setPeople(mDataSet);

        // ???????????????????????????
        String message = getString(R.string.restored_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
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
        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_600));

        // ??????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_800));
            //getWindow().setBackgroundDrawable(DevelopHelper.getBackgroundDrawble(mSelf, mState));
        }
        // ??????????????????
        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_800));
        // ?????????????????????????????????????????????
        enableNavigationDrawer();

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
        // ?????????????????????
        updateTitle(arrayList);
        // ?????????????????????
        updateMenu(arrayList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ???????????????
        mSelf = this;

        // ??????????????????????????????
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // ??????????????????
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_TRASH, COLOR.GREY.name()));
        setTheme(ThemeHelper.getTheme(color));

        // ????????????????????????
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_trash);

        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.trash);
        }
        // ??????????????????????????????
        enableNavigationDrawer();

        // ??????
        mState.setId(R.id.trash);
        mState.registerCallacks(this);
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
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ?????????????????????????????????????????????
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void disableNavigationDrawer() {
        // ???????????????????????????????????????????????????
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mBinding.drawerLayout.removeDrawerListener(mToggle);
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(START)) {
            mBinding.drawerLayout.closeDrawer(START);
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
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // ??????????????????
        // ?????????????????????
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                if (mState.getDisplay() == EMPTY) {
                    getMenuInflater().inflate(R.menu.trash_empty, menu);
                } else {
                    getMenuInflater().inflate(R.menu.trash, menu);
                }
                break;
            }
            case SELECTED:
            case MULTI_SELECTED: {
                getMenuInflater().inflate(R.menu.trash_selected, menu);
                break;
            }
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    getMenuInflater().inflate(R.menu.trash_selected_all_one, menu);
                } else {
                    getMenuInflater().inflate(R.menu.trash_selected_all, menu);
                }
                break;
            }
        }
        return super.onCreateOptionsMenu(menu);
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
            case R.id.menu_empty: {

                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                mDataSet = toEmptyList(mDataSet);

                // ????????????????????????
                disableProgressBar();
                // ?????????????????????
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                String message = getString(R.string.empty_trash_is_done);
                Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), v -> {
                            // ??????????????????????????????
                            if(!mState.isResumed()) {
                                return;
                            }
                            // ???????????????
                            mDataSet.clear();
                            copyPeople(mDataSet, mBackup);
                            // ???????????????
                            updateView(toList(mDataSet));
                            // ??????
                            mClient.setPeople(mDataSet);
                        })
                        .show();

                return true;
            }
            case R.id.menu_untrash: {
                mHandler.post(new SelectedUnTrashRunner());
                return true;
            }
            case R.id.menu_selected_all: {
                // ?????????
                mHandler.post(new SelectedAllRunner());
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
     * ???????????????
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

    /**
     * ??????????????????????????????
     */
    private void disableProgressBar() {
        // ??????????????????????????????
        mBinding.productImageLoading.setVisibility(View.INVISIBLE);
    }

    /**
     * ??????????????????
     */
    private void updateEmptyView(ArrayList<SimplePerson> collection) {
        // ??????????????????
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            // ????????????
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            // ??????????????????
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????
     *
     * @param collection ??????
     */
    private void updateMenu(ArrayList<SimplePerson> collection) {
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
     */
    private void updateCollectionView(int id, ArrayList<SimplePerson> collection) {
        if (id == LAYOUT_GRID) {
            mCollectionView = ModuleFragment.newInstance(collection);
        } else {
            mCollectionView = ListFragment.newInstance(collection);
        }
        // ?????????????????????
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
            sb.append(this.getString(R.string.trash));
        } else {
            sb.append(this.getString(R.string.trash)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "MainActivity";
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }

        // ????????????
        if (requestCode == REQUEST_DETAIL_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // ??????????????????
                Bundle bundle = data.getExtras();
                SimplePerson item = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_PERSON);
                // ????????????????????????????????????
                mRedos.clear();
                mRedos.add(new Action(MODIFY, 0, item));
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
        if (mState.getId() != R.id.trash) {
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
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // ?????????????????????????????????
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(START);
        return true;
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

                        ArrayList<SimplePerson> list = toList(mBackup);
                        // ??????????????????
                        updateEmptyView(list);
                        // ??????????????????
                        updateTitle(list);
                        // ?????????????????????
                        updateMenu(list);

                        // ??????????????????
                        mDataSet.clear();
                        copyPeople(mDataSet, mBackup);

                        // ??????
                        mClient.setPeople(mDataSet);
                    }
                })
                .show();
    }

    /**
     * ????????????
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
     * ??????????????????
     */
    private class UnTrashRunner implements Runnable {

        /**
         * @serial ??????
         */
        SimplePerson mItem;

        /**
         * ?????????????????????
         *
         * @param item ??????
         */
        UnTrashRunner(SimplePerson item) {
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
                if (mCollectionView != null) {
                    mUndos.clear();
                    // ?????????????????????
                    int position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toContact());
                    // ?????????????????????
                    mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                    Collections.reverse(mUndos);
                }

                // ??????
                mClient.setPeople(mDataSet);

                // ???????????????????????????
                String message = getString(R.string.restored_item);
                makeUndoSnackbar(mBinding.coordinatorLayout, message);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????????????????
     */
    private class SelectedUnTrashRunner implements Runnable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // ????????????
                mState.changeSelection(UNSELECTED);

                // ??????????????????
                mBackup.clear();
                copyPeople(mBackup, mDataSet);

                // ??????????????????
                for (SimplePerson src : mSelected) {
                    // ?????????????????????
                    src.isSelected = false;
                    // ????????????????????????
                    src.isTrash = false;
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
                // ??????????????????????????????????????????
                if (mCollectionView != null) {
                    mUndos.clear();
                    for (SimplePerson src : mSelected) {
                        int position;
                        if (!src.isTrash) {
                            // ?????????????????????
                            position = mCollectionView.remove(src.toContact());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(src.uuid, mBackup)));
                        }
                    }
                    Collections.reverse(mUndos);
                }

                // ??????
                mClient.setPeople(mDataSet);

                // ?????????????????????
                mSelected.clear();

                // ???????????????????????????
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.restored_item);
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.restored_some_items, mUndos.size());
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

}
