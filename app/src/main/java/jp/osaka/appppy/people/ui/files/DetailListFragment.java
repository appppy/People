package jp.osaka.appppy.people.ui.files;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Collection;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.DividerItemDecoration;
import jp.osaka.appppy.people.android.view.ICollectionView;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.databinding.FragmentBinding;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.utils.timer.SimpleTimer;
import jp.osaka.appppy.people.utils.timer.TimerListener;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_PEOPLE;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.createTimer;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.startTimer;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.stopTimer;


/**
 * 一覧フラグメント
 */
public class DetailListFragment extends Fragment implements
        ICollectionView<SimplePerson>,
        ItemListener<SimplePerson>,
        TimerListener {

    /**
     * @serial コールバック
     */
    private Callbacks<SimplePerson> mCallbacks;

    /**
     * @serial フローティングアクションボタン表示タイマー
     */
    private SimpleTimer mTimer;

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 自身
     */
    private final DetailListFragment mSelf;

    /**
     * @serial アダプタ
     */
    private DetailListAdapter mAdapter;

    /**
     * @serial バインディング
     */
    private FragmentBinding mBinding;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static DetailListFragment newInstance(ArrayList<SimplePerson> collection) {
        // フラグメントの生成
        DetailListFragment fragment = new DetailListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_PEOPLE, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public DetailListFragment() {
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = ((Callbacks<SimplePerson>) getActivity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "DetailListFragment";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // タイマ生成
        mTimer = createTimer(mTimer, PeopleConstants.TIMEOUT_FLOATING_ACTION_BUTTON_HIDE, this);
        // 再生成を抑止
        setRetainInstance(true);
        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        // タイマ停止
        stopTimer(mTimer);
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    /**
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding = DataBindingUtil.bind(requireView());

        ArrayList<SimplePerson> assets = getArguments().getParcelableArrayList(EXTRA_PEOPLE);

        // アダプタの設定
        mAdapter = new DetailListAdapter(getContext(), this, assets);


        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.collection.addItemDecoration(new DividerItemDecoration(getActivity()));
        mBinding.collection.setLayoutManager(layoutManager);
        mBinding.collection.setAdapter(mAdapter);
        mBinding.collection.setItemAnimator(new DefaultItemAnimator());
        mBinding.collection.setVerticalScrollBarEnabled(false);
        mBinding.collection.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // タイマ開始
                startTimer(mTimer);
                if (mCallbacks != null) {
                    mCallbacks.onScroll(mSelf);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTimer(Object timer, int timeOutCount, boolean timerState) {
        mHandler.post(() -> {
            if (null != mCallbacks) {
                mCallbacks.onScrollFinished(mSelf);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void diselect() {
        if (mAdapter != null) {
            Collection<SimplePerson> collection = mAdapter.getCollection();
            for (SimplePerson item : collection) {
                item.isSelected = false;
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends SimplePerson> selectedAll() {
        Collection<? extends SimplePerson> result = null;
        if (mAdapter != null) {
            Collection<SimplePerson> collection = mAdapter.getCollection();
            for (SimplePerson item : collection) {
                item.isSelected = true;
            }
            mAdapter.notifyDataSetChanged();
            result = mAdapter.getCollection();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(int index, SimplePerson item) {
        if (mAdapter != null) {
            mAdapter.insert(index, item);
            if (index == 0) {
                mBinding.collection.scrollToPosition(index);
            }
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(@NonNull SimplePerson item) {
        if (mAdapter != null) {
            mAdapter.insert(0, item);
            mBinding.collection.scrollToPosition(0);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(@NonNull SimplePerson item) {
        int location = 0;
        if (mAdapter != null) {
            location = mAdapter.getCollection().indexOf(item);
            if (location >= 0) {
                mAdapter.remove(location);
            }
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int change(@NonNull SimplePerson item) {
        int position = 0;
        if (mAdapter != null) {
            position = mAdapter.set(item);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeAll(Collection<? extends SimplePerson> collection) {
        if (mAdapter != null) {
            mAdapter.setAll(collection);
            if (mCallbacks != null) {
                mCallbacks.onUpdated(this, mAdapter.getCollection());
            }
        }
    }

    /**
     * クリック
     *
     * @param view 表示
     * @param item 項目
     */
    @Override
    public void onClickMore(View view, SimplePerson item) {
        if (mCallbacks != null) {
            mCallbacks.onSelectedMore(this, view, item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view, SimplePerson item) {
        if (mCallbacks != null) {
            mCallbacks.onSelected(this, view, item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLongClick(View view, SimplePerson item) {
        if (mCallbacks != null) {
            mCallbacks.onSelectedChanged(this, view, item, mAdapter.getCollection());
        }
    }
}