package jp.osaka.appppy.people.ui.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.appppy.people.constants.PeopleConstants;
import jp.osaka.appppy.people.databinding.ItemLineBinding;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.utils.BitmapHelper.decodeFile;
import static jp.osaka.appppy.people.utils.ThemeHelper.getImageColor;

/**
 * 検索一覧アダプタ
 */
class SearchListAdapter extends RecyclerArrayAdapter<SimplePerson, SearchListAdapter.BindingHolder> {

    /***
     * @serial コンテキスト
     */
    private final Context mContext;

    /**
     * @serial レイアウトインフレータ
     */
    private final LayoutInflater mInflater;

    /**
     * @serial リスナ
     */
    private final ItemListener<SimplePerson> mListener;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param listener   リスナ
     * @param collection 一覧
     */
    SearchListAdapter(Context context, ItemListener<SimplePerson> listener, List<SimplePerson> collection) {
        super(collection);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BindingHolder(
                mInflater.inflate(R.layout.item_line, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {
        // 項目
        final SimplePerson item = getCollection().get(position);
        if (item != null) {
            // タッチの設定
            // 短押しの設定
            holder.getBinding().cardView.setOnClickListener(new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View view) {
                    // 短押し
                    mListener.onClick(view, item);
                }
            });

            // 背景の設定
            //holder.getBinding().cardView.setBackgroundColor(PeopleHelper.getModuleColor(mContext, item.color));
            // アイコン
            if (item.imagePath.equals(PeopleConstants.INVALID_STRING_VALUE)) {
                holder.getBinding().iconView.setVisibility(View.GONE);
                holder.getBinding().icon2.setVisibility(View.VISIBLE);
                holder.getBinding().icon2.setColorFilter(getImageColor(mContext, item.color));
            } else {
                holder.getBinding().icon2.setVisibility(View.GONE);
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

            // タイトル
            holder.getBinding().title.setText(item.displayName);
            // サブタイトル
            holder.getBinding().subtitle.setVisibility(View.GONE);
//            ViewGroup.LayoutParams lp = holder.getBinding().layoutText.getLayoutParams();
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
//            mlp.setMargins(mlp.leftMargin, 16, mlp.rightMargin, mlp.bottomMargin);
//            holder.getBinding().layoutText.setLayoutParams(mlp);

            // Moreの設定
            holder.getBinding().buttonPopup.setOnClickListener(new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View v) {
                    // 短押し
                    mListener.onClickMore(v, item);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return getCollection().size();
    }

    /**
     * 表示保持
     */
    static class BindingHolder extends RecyclerView.ViewHolder {
        /**
         * @serial バインディング
         */
        private final ItemLineBinding mBinding;

        BindingHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public ItemLineBinding getBinding() {
            return mBinding;
        }
    }
}

