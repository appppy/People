package jp.osaka.appppy.people.ui.files;

import android.content.Context;
import android.os.Bundle;
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

import static jp.osaka.appppy.people.constants.PeopleConstants.EXTRA_FILE;

/**
 * 一覧フラグメント
 */
public class ListFragment extends Fragment implements
        ItemListener<SimpleFile>,
        ISimpleCollectionView<SimpleFile> {

    /**
     * @serial 自身
     */
    private final ListFragment mSelf;

    /**
     * @serial コールバック
     */
    private Callbacks<SimpleFile> mCallbacks;

    /**
     * @serial アダプタ
     */
    private ListAdapter mAdapter;

    /**
     * インスタンス取得
     *
     * @param collection カード
     */
    public static ListFragment newInstance(ArrayList<SimpleFile> collection) {
        // フラグメントの生成
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_FILE, collection);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * コンストラクタ
     */
    public ListFragment() {
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 再生成を抑止
        setRetainInstance(true);

    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mCallbacks = (Callbacks<SimpleFile>) getActivity();
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
        ArrayList<SimpleFile> files = getArguments().getParcelableArrayList(EXTRA_FILE);

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
    public int remove(@NonNull SimpleFile item) {
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
    public void onClickMore(View view, SimpleFile item) {
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
    public void onClick(View view, SimpleFile item) {
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
    public void onLongClick(View view, SimpleFile item) {

    }
}