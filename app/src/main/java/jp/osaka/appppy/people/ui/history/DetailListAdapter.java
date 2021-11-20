package jp.osaka.appppy.people.ui.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.appppy.people.databinding.ItemLineBinding;
import jp.osaka.appppy.people.service.SimplePerson;
import jp.osaka.appppy.people.utils.ThemeHelper;

import static android.view.View.GONE;
import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.utils.BitmapHelper.decodeFile;



/**
 * 一覧アダプタ
 */
class DetailListAdapter extends RecyclerArrayAdapter<SimplePerson, DetailListAdapter.BindingHolder> {

    /**
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
     * @serial タッチ表示保持
     */
    private final Collection<View> touchViewHolder = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param context    コンテキスト
     * @param listener   リスナ
     * @param collection 一覧
     */
    DetailListAdapter(Context context, ItemListener<SimplePerson> listener, List<SimplePerson> collection) {
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
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final BindingHolder holder, int position) {
        // 項目
        final SimplePerson item = getCollection().get(position);
        if (item != null) {
            // タッチの設定
            holder.getBinding().cardView.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    touchViewHolder.clear();
                    touchViewHolder.add(view);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    for (View holdview : touchViewHolder) {
                        if (holdview.equals(view)) {
                            // 短押し
                            mListener.onClick(view, item);
                        }
                    }
                    touchViewHolder.clear();
                }
                return false;
            });
            // 短押しの設定
            holder.getBinding().cardView.setOnClickListener(new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View v) {
                    // 処理なし
                }
            });
            // 長押しの設定
            holder.getBinding().cardView.setOnLongClickListener(new View.OnLongClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean onLongClick(View v) {
                    // タッチ表示保持を解除
                    touchViewHolder.clear();
                    return true;
                }
            });

            // 項目の選択状態の取得
            if (item.isSelected) {
                // アイコンの設定
                holder.getBinding().iconView.setVisibility(GONE);
                holder.getBinding().icon2.setImageResource(R.drawable.ic_check_circle_black_24dp);
                holder.getBinding().icon2.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_600));
                holder.getBinding().icon2.setVisibility(View.VISIBLE);
            } else {
                // アイコンの設定
                if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                    holder.getBinding().iconView.setVisibility(GONE);
                    holder.getBinding().icon2.setImageResource(R.drawable.ic_lens_black_24dp);
                    holder.getBinding().icon2.setColorFilter(ThemeHelper.getImageColor(mContext, item.color));
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
                        holder.getBinding().iconView.setVisibility(GONE);
                        holder.getBinding().icon2.setImageResource(R.drawable.ic_lens_black_24dp);
                        holder.getBinding().icon2.setColorFilter(ThemeHelper.getImageColor(mContext, item.color));
                        holder.getBinding().icon2.setVisibility(View.VISIBLE);
                    }
                }
            }

            // タイトル
            holder.getBinding().title.setText(item.title);
            // サブタイトル
            if (item.subtitle.equals(INVALID_STRING_VALUE)) {
                holder.getBinding().subtitle.setVisibility(GONE);
            } else {
                holder.getBinding().subtitle.setText(item.subtitle);
            }

            // Moreの設定
            holder.getBinding().buttonPopup.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total number of items in the data change hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return getCollection().size();
    }

    /**
     * 表示の保持
     */
    static class BindingHolder extends RecyclerView.ViewHolder {
        /**
         * @serial バインディング
         */
        private final ItemLineBinding mBinding;

        /**
         * コンストラクタ
         *
         * @param itemView 項目表示
         */
        BindingHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        /**
         * バインディング取得
         *
         * @return バインディング
         */
        public ItemLineBinding getBinding() {
            return mBinding;
        }
    }

}

