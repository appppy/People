
package jp.osaka.appppy.people.ui.content;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import jp.osaka.appppy.people.R;

/**
 * 基本モバイル広告アクティビティ
 */
public abstract class BaseAdmobActivity extends AppCompatActivity {
    /**
     * @serial モバイル広告の表示時間
     */
    static private final int TIMEOUT_ADMOB;

    static {
        TIMEOUT_ADMOB = 30 * 1000;
    }

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial タスク
     */
    private final Runnable mTask = () -> {
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        //MobileAds.initialize(this, "ca-app-pub-2133852417496011~2816942485");
        MobileAds.initialize(this, initializationStatus -> {
            //
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        // モバイル広告の表示
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.VISIBLE);
        }

        // 実行
        mHandler.postDelayed(mTask, TIMEOUT_ADMOB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // モバイル広告の非表示
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
