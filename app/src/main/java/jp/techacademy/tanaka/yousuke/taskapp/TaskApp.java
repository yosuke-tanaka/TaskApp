package jp.techacademy.tanaka.yousuke.taskapp;

/**
 5.4 Realm のデータベースファイル

 モデルクラスを用意したら、データベースの準備をします。
 Realmは1データベースにつき、1ファイルとなります。
 今回はシンプルなタスク管理アプリなのでファイル名を意識しなくて良いデフォルトのデータベースを使用します。
 大規模なアプリになると複数のファイル、つまり複数のデータベースを使うこともあります。
 どのファイルを使うかなどの設定を行う必要があります。
 アプリケーション起動時（Applicationオブジェクトが作成されたとき）にデフォルトとして設定することで、
 アプリケーション全体で同じ設定のRealmを使用することができます。


 Applicationクラスを継承したクラスを作成する

 Applicationクラスを継承したTaskAppクラスを作成します。onCreateメソッドをオーバーライドします。
 その中でRealmConfigurationクラスを生成し、RealmクラスのsetDefaultConfigurationメソッドで設定します。
 特別な設定を行わずデフォルトの設定を使う場合はこのように記述します。


 Applicationクラスを継承したTaskAppクラスを作成するだけではこのクラスは使われることはないため、
 AndroidManifest.xmlに１行追加する必要があります。application要素に android:name=".TaskApp"を追記します。
 これはこのアプリのApplicationクラスはこれですよ、と指定するためのものです。
 ここで指定したクラスのonCreateメソッドがアプリ起動時に呼ばれます。
 */
import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
