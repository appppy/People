# People
Last State: Nov 2021

# Peopleとは何か？

## 種類
Javaで記述したAndroidアプリです。

## 内容
以下、Google Pixel 3で動作確認済みです。
- 「連絡先一覧画面」では、住所の編集や保存が可能です。
- 「アーカイブ済みの連絡先一覧画面」では、アーカイブした住所を確認できます。
- 「ゴミ箱にある住所一覧画面」では、削除が可能です。

# だれが、「People」を使うか？

Androidアプリを作成したい方が参考として使用することを想定します。

# どのように、「People」を使うか？

以下のapi_keyの値(value)を取得し、設定してください。

・AndroidManifest.xml
```
         <meta-data
            android:name="com.google.android.backup.api_key"
            android:value="キーを設定してください" />
```

以下のAPPLICATION_IDの値(value)を取得し、設定してください。

・AndroidManifest.xml

```
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="キーを設定してください"/>

```

・AdMobFragmentImpl.java

```
    @Override
    protected String getUnitId() {
        return "キーを設定してください";
    }
```