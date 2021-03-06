package jp.osaka.appppy.people.ui.content.note;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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
import jp.osaka.appppy.people.databinding.ActivityNoteSearchBinding;
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
import static jp.osaka.appppy.people.constants.ACTION.MODIFY;
import static jp.osaka.appppy.people.constants.ACTION.REMOVE;
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
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_COLOR_OF_NOTE;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_SIMPLE_PERSON;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.ui.content.note.NoteHelper.toList;
import static jp.osaka.appppy.people.ui.content.note.NoteHelper.toListOf;
import static jp.osaka.appppy.people.utils.ActivityTransitionHelper.startEditActivity;
import static jp.osaka.appppy.people.utils.PeopleHelper.copyPeople;
import static jp.osaka.appppy.people.utils.PeopleHelper.getPerson;
import static jp.osaka.appppy.people.utils.PeopleHelper.isModified;

/**
 * ??????????????????????????????
 */
public class SearchActivity extends BaseAdmobActivity implements
        PeopleClient.Callbacks,
        ISearchCollectionCallbacks<SimplePerson> {

    /**
     * @serial ??????
     */
    private final String TAG = "SearchActivity";

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
     * @serial ?????????????????????
     */
    private ActivityNoteSearchBinding mBinding;

    /**
     * @serial ????????????
     */
    private ISearchCollectionView<SimplePerson> mCollectionView;

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mRedos = new ArrayList<>();

    /**
     * @serial ??????
     */
    private final ArrayList<Action> mUndos = new ArrayList<>();

    /**
     * ????????????????????????
     *
     * @param context ??????????????????
     * @return ???????????????
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // ???????????????
        mSelf = this;

        // ??????????????????????????????
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // ??????????????????
        COLOR color = COLOR.valueOf(mPref.getString(EXTRA_COLOR_OF_NOTE, YELLOW.name()));
        setTheme(ThemeHelper.getTheme(color));

        // ?????????????????????
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_search);

        setSupportActionBar(mBinding.toolbar);
        initActionBar();

        mBinding.editSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // ????????????
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // ??????????????????????????????
                if(!mState.isResumed()) {
                    return;
                }
                // ?????????????????????
                updateView(toListOf(toList(mDataSet), charSequence.toString()));
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void afterTextChanged(Editable editable) {
                // ????????????
            }
        });


        //mProxy.connect();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
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
        if (requestCode == REQUEST_DETAIL_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // ??????????????????
                Bundle bundle = data.getExtras();
                assert bundle != null;
                SimplePerson item = bundle.getParcelable(EXTRA_SIMPLE_PERSON);
                // ????????????????????????????????????
                mRedos.clear();
                mRedos.add(new Action(MODIFY, 0, item));
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
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ??????????????????????????????
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        // ????????????????????????
        if (id == android.R.id.home) {
            finishActivity();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ??????????????????
     */
    private void finishActivity() {
        Intent intent = getIntent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
    }

    /**
     * ????????????
     *
     * @param collection ??????
     */
    private void updateView(ArrayList<SimplePerson> collection) {
        // ????????????????????????
        updateEmptyView(collection);
        // ?????????????????????
        updateCollectionView(collection);
    }

    /**
     * ???????????????
     *
     * @param collection ??????
     */
    private void updateEmptyView(List<SimplePerson> collection) {
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
     * ??????????????????
     *
     * @param collection ??????
     */
    private void updateCollectionView(ArrayList<SimplePerson> collection) {
        mCollectionView = SearchListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param collectionView ????????????
     * @param view           ????????????
     * @param item           ??????
     */
    @Override
    public void onSelectedMore(ISearchCollectionView<SimplePerson> collectionView, final View view, final SimplePerson item) {
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
                    // ??????????????????????????????
                    item.timestamp = System.currentTimeMillis();
                    for (SimplePerson dest : mDataSet) {
                        if (dest.equal(item)) {
                            dest.copyPerson(item);
                        }
                    }
                    mClient.setPeople(mDataSet);
                    NoteHelper.startDetailActivity(mSelf, mSelf, item);
                    return true;
                }
                case R.id.menu_edit: {
                    // ??????????????????????????????
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
                    // ????????????????????????????????????
                    item.isArchive = true;
                    mHandler.post(new ModifyRunner(item));
                    return true;
                }
                case R.id.menu_trash: {
                    // ????????????????????????????????????
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
                        intent.putExtra(Intent.EXTRA_TEXT, item.note);
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
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ISearchCollectionView<SimplePerson> collectionView, View view, SimplePerson item) {
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

        // ???????????????????????????
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                    view, 0, 0, view.getWidth(), view.getHeight());
            Intent intent = CustomDetailActivity.createIntent(this, item);
            ActivityCompat.startActivityForResult(this, intent, REQUEST_DETAIL_ITEM.ordinal(), opts.toBundle());
        } else {
            Intent intent = CustomDetailActivity.createIntent(this, item);
            startActivityForResult(intent, REQUEST_DETAIL_ITEM.ordinal());
        }
    }

    /**
     * ????????????
     *
     * @param view       ????????????
     * @param collection ??????
     */
    @Override
    public void onUpdated(ISearchCollectionView<SimplePerson> view, Collection<? extends SimplePerson> collection) {
        // ????????????
    }

    private void initActionBar() {
        // ToolBar??????????????????????????????????????????????????????????????????????????????
        ActionBar bar = getSupportActionBar();
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
                    updateView(toListOf(toList(mDataSet), Objects.requireNonNull(mBinding.editSearch.getText()).toString()));

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(mBinding.editSearch, 0);
                }

                // ?????????????????????
                for (Action doAction : mRedos) {
                    if (doAction.action.equals(MODIFY)) {
                        SimplePerson dest = getPerson(doAction.object.uuid, mDataSet);
                        if (!dest.equals(doAction.object)) {
                            if (!isModified(dest, doAction.object)) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

                        // ?????????????????????
                        if (mCollectionView != null) {
                            try {
                                for (Action undo : mUndos) {
                                    if (undo.action == INSERT) {
                                        // ?????????????????????
                                        mCollectionView.insert(undo.arg, undo.object.toNote());
                                    }
                                    if (undo.action == CHANGE) {
                                        // ?????????????????????
                                        mCollectionView.change(undo.object.toNote());
                                    }
                                    if (undo.action == REMOVE) {
                                        // ?????????????????????
                                        mCollectionView.remove(undo.object.toNote());
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                // ?????????????????????
                                updateCollectionView(mBackup);
                            }
                            mUndos.clear();
                        }

                        ArrayList<SimplePerson> list = toList(mBackup);
                        // ??????????????????
                        updateEmptyView(list);

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
                // ?????????????????????
                mUndos.clear();
                if (mCollectionView != null) {
                    int position;
                    if (mItem != null) {
                        if (mItem.isArchive) {
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toNote());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else if (mItem.isTrash) {
                            position = mCollectionView.remove(getPerson(mItem.uuid, mDataSet).toNote());
                            // ?????????????????????
                            mUndos.add(new Action(INSERT, position, getPerson(mItem.uuid, mBackup)));
                        } else {
                            position = mCollectionView.change(getPerson(mItem.uuid, mDataSet).toNote());
                            mUndos.add(new Action(CHANGE, position, getPerson(mItem.uuid, mBackup)));
                        }
                        Collections.reverse(mUndos);

                        // ??????????????????
                        updateEmptyView(toList(mDataSet));

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
                }
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

                    // ?????????????????????
                    mCollectionView.change(getPerson(mItem.uuid, mDataSet).toNote());

                    // ??????
                    mClient.setPeople(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
