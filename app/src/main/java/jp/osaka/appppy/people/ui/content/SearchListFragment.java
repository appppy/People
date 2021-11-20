package jp.osaka.appppy.people.ui.content;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.DividerItemDecoration;
import jp.osaka.appppy.people.android.view.ISearchCollectionCallbacks;
import jp.osaka.appppy.people.android.view.ISearchCollectionView;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.databinding.FragmentBinding;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.Config.LOG_I;

/**
 * 検索一覧フラグメント
 */
public class SearchListFragment extends Fragment implements
        ISearchCollectionView<SimplePerson>,
        ItemListener<SimplePerson> {

    /**
     * @serial タグ
     */
    private final String TAG = "SearchListFragment";

    /**
     * @serial データバインディング
     */
    private FragmentBinding mBinding;

    /**
     * @serial コールバック
     */
    private ISearchCollectionCallbacks<SimplePerson> mCallbacks;

    /**
     * @serial アダプタ
     */
    private SearchListAdapter mAdapter;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static SearchListFragment newInstance(ArrayList<SimplePerson> collection) {
        // フラグメントの生成
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PeopleConstants.EXTRA_PEOPLE, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof ISearchCollectionCallbacks)) {
            throw new ClassCastException(requireActivity().toString()
                    + " must implement Callbacks");
        }

        mCallbacks = (ISearchCollectionCallbacks<SimplePerson>) getActivity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 再生成を抑止
        setRetainInstance(true);

        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }
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
        if (LOG_I) {
            Log.i(TAG, "onActivityCreated#enter");
        }

        // データ取得
        ArrayList<SimplePerson> people = requireArguments().getParcelableArrayList(PeopleConstants.EXTRA_PEOPLE);
        // データバインディング取得
        mBinding = DataBindingUtil.bind(requireView());
        // アダプタの設定
        mAdapter = new SearchListAdapter(getContext(), this, people);
        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        if (mBinding != null) {
            mBinding.collection.addItemDecoration(new DividerItemDecoration(requireActivity()));
            mBinding.collection.setLayoutManager(layoutManager);
            mBinding.collection.setAdapter(mAdapter);
            mBinding.collection.setItemAnimator(new DefaultItemAnimator());
            mBinding.collection.setVerticalScrollBarEnabled(false);
        }

        if (LOG_I) {
            Log.i(TAG, "onActivityCreated#leave");
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
        // 処理なし
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

}