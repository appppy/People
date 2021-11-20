package jp.osaka.appppy.people.ui.content.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import static android.view.View.GONE;
import static jp.osaka.appppy.people.constants.PeopleConstants.TIMEOUT_FLOATING_ACTION_BUTTON_HIDE;
import static jp.osaka.appppy.people.utils.BitmapHelper.decodeFile;
import static jp.osaka.appppy.people.utils.SelectHelper.isMultiSelected;
import static jp.osaka.appppy.people.utils.SelectHelper.isSelected;
import static jp.osaka.appppy.people.utils.ThemeHelper.getImageColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getLineColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getLineDrawable;
import static jp.osaka.appppy.people.utils.ThemeHelper.getSelectedLineColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getSelectedLineDrawable;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.createTimer;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.startTimer;
import static jp.osaka.appppy.people.utils.timer.TimerHelper.stopTimer;

/**
 * 一覧フラグメント
 */
public class RecentListFragment extends Fragment implements
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
    private final RecentListFragment mSelf;

    /**
     * @serial アダプタ
     */
    private RecentListAdapter mAdapter;

    /**
     * @serial バインディング
     */
    private FragmentBinding mBinding;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static RecentListFragment newInstance(ArrayList<SimplePerson> collection) {
        // フラグメントの生成
        RecentListFragment fragment = new RecentListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PeopleConstants.EXTRA_PEOPLE, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public RecentListFragment() {
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

        // タイマ生成
        mTimer = createTimer(mTimer, TIMEOUT_FLOATING_ACTION_BUTTON_HIDE, this);
        // 再生成を抑止
        setRetainInstance(true);
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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding = DataBindingUtil.bind(requireView());

        assert getArguments() != null;
        ArrayList<SimplePerson> people = getArguments().getParcelableArrayList(PeopleConstants.EXTRA_PEOPLE);

        // アダプタの設定
        mAdapter = new RecentListAdapter(getContext(), this, people);


        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.collection.addItemDecoration(new DividerItemDecoration(requireActivity()));
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
                try {
                    // タイマ開始
                    startTimer(mTimer);
                    if (mCallbacks != null) {
                        mCallbacks.onScroll(mSelf);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            /**
             * {@inheritDoc}
             */
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // 複数選択の場合、ドラッグ無効、スワイプ無効
                if (isMultiSelected(mAdapter.getCollection())) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, 0) |
                            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, 0);
                }
                // 単数選択の場合、
                if (isSelected(mAdapter.getCollection())) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END) |
                            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, 0);
                }

                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP) |
                        makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                try {
                    int from = viewHolder.getBindingAdapterPosition();
                    int to = target.getBindingAdapterPosition();
                    if (from >= 0 && to >= 0) {

                        // 選択状態を解除
                        SimplePerson person = mAdapter.getCollection().get(from);
                        person.isSelected = false;

                        mAdapter.move(from, to);
                        mCallbacks.onMoveChanged(mSelf, mAdapter.getCollection());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                try {
                    switch (actionState) {
                        case ItemTouchHelper.ACTION_STATE_IDLE:
                        case ItemTouchHelper.ACTION_STATE_SWIPE: {
                            break;
                        }
                        default: {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                float elevation = 8 * getResources().getDisplayMetrics().density;
                                viewHolder.itemView.setElevation(elevation);
                            }

                            RecentListAdapter.BindingHolder holder = (RecentListAdapter.BindingHolder) viewHolder;
                            int position = viewHolder.getBindingAdapterPosition();
                            SimplePerson item = mAdapter.getCollection().get(position);

                            // 背景の設定
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.getBinding().cardView.setBackground(getSelectedLineDrawable(getContext(), item.color));
                            } else {
                                holder.getBinding().cardView.setBackgroundColor(getSelectedLineColor(getContext(), item.color));
                            }
                            // アイコンの設定
                            holder.getBinding().iconView.setVisibility(GONE);
                            holder.getBinding().icon2.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            holder.getBinding().icon2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey_600));
                            holder.getBinding().icon2.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        viewHolder.itemView.setElevation(0);
                    }

                    RecentListAdapter.BindingHolder holder = (RecentListAdapter.BindingHolder) viewHolder;

                    // 項目の選択状態の取得
                    int position = viewHolder.getBindingAdapterPosition();
                    if (position >= 0) {
                        SimplePerson item = mAdapter.getCollection().get(position);
                        // 項目が選択状態でなければ、選択表示を解除
                        if (!item.isSelected) {
                            // 背景の設定
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                holder.getBinding().cardView.setBackground(getLineDrawable(getContext(), item.color));
                            } else {
                                holder.getBinding().cardView.setBackgroundColor(getLineColor(getContext(), item.color));
                            }
                            // アイコンの設定
                            if (item.imagePath.equals(PeopleConstants.INVALID_STRING_VALUE)) {
                                holder.getBinding().iconView.setVisibility(GONE);
                                holder.getBinding().icon2.setImageResource(R.drawable.ic_account_circle_black_24dp);
                                holder.getBinding().icon2.setColorFilter(getImageColor(getContext(), item.color));
                                holder.getBinding().icon2.setVisibility(View.VISIBLE);
                            } else {
                                holder.getBinding().icon2.setVisibility(GONE);
                                try {
                                    holder.getBinding().iconView.setColorFilter(null);
                                    // 画像を縮小して取得
                                    Bitmap bitmap = decodeFile(item.imagePath);
                                    // imageViewの初期化
                                    holder.getBinding().iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    holder.getBinding().iconView.setImageURI(null);
                                    // ImageViewにセット
                                    holder.getBinding().iconView.setImageBitmap(bitmap);
                                    holder.getBinding().iconView.setVisibility(View.VISIBLE);
                                } catch (OutOfMemoryError | Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mCallbacks.onSelectedChanged(mSelf, holder.getBinding().cardView, item, mAdapter.getCollection());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    int position = viewHolder.getBindingAdapterPosition();
                    if (position >= 0) {
                        if (mCallbacks != null) {
                            mCallbacks.onSwiped(mSelf, mAdapter.getCollection().get(position));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.6f;
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.collection);
        mAdapter.setItemTouchHelper(itemTouchHelper);
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