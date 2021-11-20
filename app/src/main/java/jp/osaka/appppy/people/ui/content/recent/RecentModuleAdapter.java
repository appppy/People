package jp.osaka.appppy.people.ui.content.recent;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.osaka.appppy.people.R;
import jp.osaka.appppy.people.android.view.adapter.ItemListener;
import jp.osaka.appppy.people.android.view.adapter.RecyclerArrayAdapter;
import jp.osaka.appppy.people.databinding.ItemModuleBinding;
import jp.osaka.appppy.people.service.SimplePerson;

import static jp.osaka.appppy.people.constants.PeopleConstants.INVALID_STRING_VALUE;
import static jp.osaka.appppy.people.utils.BitmapHelper.decodeFile;
import static jp.osaka.appppy.people.utils.SelectHelper.isMultiSelected;
import static jp.osaka.appppy.people.utils.SelectHelper.isSelected;
import static jp.osaka.appppy.people.utils.ThemeHelper.getModuleColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getModuleDrawable;
import static jp.osaka.appppy.people.utils.ThemeHelper.getSelectedModuleColor;
import static jp.osaka.appppy.people.utils.ThemeHelper.getSelectedModuleDrawable;
import static jp.osaka.appppy.people.utils.ThemeHelper.setMargins;

/**
 * モジュールアダプタ
 */
class RecentModuleAdapter extends RecyclerArrayAdapter<SimplePerson, RecentModuleAdapter.BindingHolder> {
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
     * @serial アニメーション位置
     */
    private int mAnimatedPosition = RecyclerView.NO_POSITION;

    /**
     * @serial 項目タッチへルパ
     */
    private ItemTouchHelper mItemTouchHelper;

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
    RecentModuleAdapter(Context context, ItemListener<SimplePerson> listener, List<SimplePerson> collection) {
        super(collection);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    /**
     * タッチへルパの設定
     *
     * @param helper ヘルパ
     */
    void setItemTouchHelper(ItemTouchHelper helper) {
        mItemTouchHelper = helper;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public BindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BindingHolder(
                mInflater.inflate(R.layout.item_module, parent, false));
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
                    if (isSelected(getCollection())) {
                        for (View holdview : touchViewHolder) {
                            if (holdview.equals(view)) {
                                // 選択
                                select(view, holder, item);
                            }
                        }
                    } else {
                        for (View holdview : touchViewHolder) {
                            if (holdview.equals(view)) {
                                // 短押し
                                mListener.onClick(view, item);
                            }
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
                    // 選択
                    select(v, holder, item);
                    // タッチ表示保持を解除
                    touchViewHolder.clear();
                    return true;
                }
            });


            // 背景の設定
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.getBinding().cardView.setBackground(getModuleDrawable(mContext, item.color));
            } else {
                holder.getBinding().cardView.setBackgroundColor(getModuleColor(mContext, item.color));
            }
            holder.getBinding().foreground.setVisibility(View.INVISIBLE);
            // タイトル
            holder.getBinding().title.setText(item.title);
            // サブタイトル
            if(item.subtitle.equals(INVALID_STRING_VALUE)) {
                holder.getBinding().subtitle.setVisibility(View.GONE);
                holder.getBinding().subtitle2.setVisibility(View.GONE);
            } else {
                if(item.isSimpled) {
                    holder.getBinding().subtitle.setVisibility(View.GONE);
                    holder.getBinding().subtitle2.setVisibility(View.GONE);
                } else {
                    holder.getBinding().subtitle.setText(item.subtitle);
                    holder.getBinding().subtitle2.setText(item.subtitle);
                }
            }

            // 画像
            if (item.imagePath.equals(INVALID_STRING_VALUE)) {
                holder.getBinding().image.setVisibility(View.GONE);
                holder.getBinding().subtitle2.setVisibility(View.GONE);
                holder.getBinding().layoutTitle.setVisibility(View.VISIBLE);
            } else {
                holder.getBinding().image.setVisibility(View.VISIBLE);
                try {
                    holder.getBinding().imageView.setColorFilter(null);
                    // 画像を縮小して取得
                    Bitmap bitmap = decodeFile(item.imagePath);
                    // imageViewの初期化
                    holder.getBinding().imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.getBinding().imageView.setImageURI(null);
                    // ImageViewにセット
                    holder.getBinding().imageView.setImageBitmap(bitmap);
                } catch (OutOfMemoryError | Exception e) {
                    e.printStackTrace();
                }
                holder.getBinding().layoutTitle.setVisibility(View.GONE);
            }

            // 項目の選択状態の取得
            if (item.isSelected) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.getBinding().cardView.setBackground(getSelectedModuleDrawable(mContext, item.color));
                } else {
                    holder.getBinding().cardView.setBackgroundColor(getSelectedModuleColor(mContext, item.color));
                }
                holder.getBinding().foreground.setVisibility(View.VISIBLE);

                //マージンを設定
                setMargins(holder.getBinding().imageView, 4);

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.getBinding().cardView.setBackground(getModuleDrawable(mContext, item.color));
                } else {
                    holder.getBinding().cardView.setBackgroundColor(getModuleColor(mContext, item.color));
                }
                holder.getBinding().foreground.setVisibility(View.INVISIBLE);

                //マージンを設定
                setMargins(holder.getBinding().imageView, 0);
            }

            // グリッド表示のときのみアニメーションを行う
            if (mAnimatedPosition < holder.getBindingAdapterPosition()) {
                Animator animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_slide_in);
                animator.setTarget(holder.getBinding().cardView);
                animator.start();
                mAnimatedPosition = holder.getBindingAdapterPosition();
            }
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
     * 選択
     *
     * @param view   表示
     * @param holder 保持
     * @param item   項目
     */
    private void select(View view, BindingHolder holder, SimplePerson item) {

        if (mItemTouchHelper != null) {
            // 選択状態を変更
            //item.isSelected = !item.isSelected;

            // 複数選択の場合、
            if (isMultiSelected(getCollection())) {

                if (item.isSelected) {

                    // 選択状態を表示
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.getBinding().cardView.setBackground(getSelectedModuleDrawable(mContext, item.color));
                    } else {
                        holder.getBinding().cardView.setBackgroundColor(getSelectedModuleColor(mContext, item.color));
                    }
                    holder.getBinding().foreground.setVisibility(View.VISIBLE);

                    //マージンを設定
                    setMargins(holder.getBinding().imageView, 4);

                } else {
                    // 選択状態を非表示
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.getBinding().cardView.setBackground(getModuleDrawable(mContext, item.color));
                    } else {
                        holder.getBinding().cardView.setBackgroundColor(getModuleColor(mContext, item.color));
                    }
                    holder.getBinding().foreground.setVisibility(View.INVISIBLE);

                    //マージンを設定
                    setMargins(holder.getBinding().imageView, 0);
                }

            } else {
                if (item.isSelected) {

                    // ドラッグを開始する
                    mItemTouchHelper.startDrag(holder);

                } else {

                    // 選択状態を非表示
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.getBinding().cardView.setBackground(getModuleDrawable(mContext, item.color));
                    } else {
                        holder.getBinding().cardView.setBackgroundColor(getModuleColor(mContext, item.color));
                    }
                    holder.getBinding().foreground.setVisibility(View.INVISIBLE);

                    //マージンを設定
                    setMargins(holder.getBinding().imageView, 0);

                }
            }

            // 長押し
            mListener.onLongClick(view, item);
        }
    }

    /**
     * 表示保持
     */
    static class BindingHolder extends RecyclerView.ViewHolder {

        /**
         * @serial バインディング
         */
        private final ItemModuleBinding mBinding;

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
        public ItemModuleBinding getBinding() {
            return mBinding;
        }
    }

}

