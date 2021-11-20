package jp.osaka.appppy.people.ui.history;

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
import jp.osaka.appppy.people.android.view.ISimpleCollectionView;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.databinding.FragmentBinding;
import jp.osaka.appppy.people.service.history.History;

import static jp.osaka.appppy.people.Config.LOG_I;
import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_HISTORY;

/**
 * 一覧フラグメント
 */
public class ListFragment extends Fragment implements
        ItemListener<History>,
        ISimpleCollectionView<History> {

    /**
     * @serial 自身
     */
    private final ListFragment mSelf = this;

    /**
     * @serial コールバック
     */
    private Callbacks<History> mCallbacks;

    /**
     * @serial アダプタ
     */
    private ListAdapter mAdapter;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static ListFragment newInstance(ArrayList<History> collection) {
        // フラグメントの生成
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_HISTORY, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public ListFragment() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "ListFragment";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }
        // 再生成を抑止
        setRetainInstance(true);
        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mCallbacks = (Callbacks<History>) getActivity();
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
        FragmentBinding binding = DataBindingUtil.bind(requireView());

        assert getArguments() != null;
        ArrayList<History> files = getArguments().getParcelableArrayList(EXTRA_HISTORY);

        // アダプタの設定
        mAdapter = new ListAdapter(getActivity(), this, files);

        // レイアウトの設定
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        assert binding != null;
        binding.collection.setLayoutManager(layoutManager);
        binding.collection.setAdapter(mAdapter);
        binding.collection.setItemAnimator(new DefaultItemAnimator());
        binding.collection.setVerticalScrollBarEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remove(@NonNull History item) {
        int location = 0;
        if (mAdapter != null) {
            location = mAdapter.getCollection().indexOf(item);
            if (location >= 0) {
                mAdapter.remove(location);
            }
        }
        return location;
    }

    /**
     * クリック
     *
     * @param view 表示
     * @param item 項目
     */
    @Override
    public void onClickMore(View view, History item) {
        if (mCallbacks != null) {
            mCallbacks.onSelectedMore(mSelf, view, item);
        }
    }

    /**
     * クリック
     *
     * @param view 表示
     * @param item 項目
     */
    @Override
    public void onClick(View view, History item) {
        if (mCallbacks != null) {
            mCallbacks.onSelected(mSelf, view, item);
        }
    }

    /**
     * ロングクリック
     *
  　 * @param view 表示
     * @param item 項目
     */
    @Override
    public void onLongClick(View view, History item) {
        // 処理なし
    }
}