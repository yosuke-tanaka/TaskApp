package jp.techacademy.tanaka.yousuke.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "jp.techacademy.taro.kirameki.taskapp.TASK";

    // Realmクラスを保持
    private Realm mRealm;

    // データベースから取得した結果を保持
    private RealmResults<Task> mTaskRealmResults;   // Task用
    private RealmResults<Category> mCategoryRealmResults;   // Category用


    //Realmのデータベースに追加や削除など変化があった場合に呼ばれるリスナー
    private RealmChangeListener mTaskRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            reloadListView();
        }
    };
    private RealmChangeListener mCategoryRealmListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            UpdateSpinner2();
        }
    };

    private ListView mListView;
    private TaskAdapter mTaskAdapter;

    String mStrCategory;    // カテゴリ情報
    //EditText mEditText;

    // カテゴリ用スピナー
    Spinner mSpinner;

//    //検索Buttonのリスナー
//    View.OnClickListener mSearchClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v.getId() == R.id.search_button) {
//                // EditTextから検索文字列取得
//                mStrCategory = mEditText.getText().toString();
//                // ListView更新 (内部で検索結果が反映される)
//                reloadListView();
//            } else {
//                // Error
//                ErrorCommon("Iregal id at mSearchClickListener");
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // 検索ボタンのリスナー登録
//        findViewById(R.id.search_button).setOnClickListener(mSearchClickListener);
//        mEditText = (EditText) findViewById(R.id.editText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        // Realmの設定
        mRealm = Realm.getDefaultInstance();
        mTaskRealmResults = mRealm.where(Task.class).findAll();
        mTaskRealmResults.sort("date", Sort.DESCENDING);
        mRealm.addChangeListener(mTaskRealmListener);

        mCategoryRealmResults = mRealm.where(Category.class).findAll();
        mCategoryRealmResults.sort("id", Sort.DESCENDING);
        mRealm.addChangeListener(mCategoryRealmListener);

        // ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());

                startActivity(intent);
            }
        });

        //また、セルを長押しした時にタスクを削除する処理も合わせて実装しておきます。
        // 長押しするとLesson4で学んだAlertDialogを表示し、OKを押したら削除、CANCELを押したら何もしないという実装にします。
        // 選択したセルに該当するタスクと同じIDのものを検索し、その結果（RealmResults）に対してclearメソッドを呼ぶことで削除を行います。
        // 削除する際も追加するときと同様にbeginTransactionメソッドとcommitTransactionメソッドで囲む必要があります。

        // ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.clear();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        //タスクを削除したときに、ここで設定したアラームを解除する必要があります。
                        // MainActivityクラスのonCreateメソッドで設定したOnItemLongClickListenerの中で
                        // データベースからタスクを削除するタイミングでアラームを解除します。
                        // セットした時と同じIntent、PendingIntentを作成し、AlarmManagerクラスのcancelメソッドでキャンセルします。

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();

        // Spinner関連
        mSpinner = (Spinner) findViewById(R.id.spinner);
        // スピナー内容更新
        UpdateSpinner2();
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                Category item = (Category) spinner.getSelectedItem();
                Log.d("**MyTest**", item.getCategory());

                if(item.getId() == -1)
                {
                    mStrCategory = null; // 未選択に対応
                }
                else
                {
                    mStrCategory = item.getCategory();
                }
                // カテゴリ検索を行ってListView更新
                reloadListView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    // ListView更新
    // カテゴリが検索文字列に対応するもののみ表示
    private void reloadListView() {
        //データベースからデータを再取得
        if(TextUtils.isEmpty(mStrCategory) == true) {
            // nullもしくは空文字列の場合は検索なし
            mTaskRealmResults = mRealm.where(Task.class).findAll();
        }
        else
        {
            // mStrCategoryが指定されている場合は絞り込み
            // [方法1]
            //mTaskRealmResults = mRealm.where(Task.class).equalTo("category", mStrCategory).findAll();
            //Categoryクラスのメンバ変数categoryが文字列mStrCategoryに等しいか
            mTaskRealmResults = mRealm.where(Task.class).equalTo("category.category", mStrCategory).findAll();


            // [方法2]
            //RealmQuery<Task> query = mRealm.where(Task.class);
            //query.equalTo("category", mStrCategory);
            //mTaskRealmResults = query.findAll();
        }
        mTaskRealmResults.sort("date", Sort.DESCENDING);


        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (int i = 0; i < mTaskRealmResults.size(); i++) {
            Task task = new Task();

            task.setId(mTaskRealmResults.get(i).getId());
            task.setTitle(mTaskRealmResults.get(i).getTitle());
            task.setContents(mTaskRealmResults.get(i).getContents());
            task.setDate(mTaskRealmResults.get(i).getDate());
            task.setCategoryCrass(mTaskRealmResults.get(i).getCategoryCrass());

            taskArrayList.add(task);
        }

        mTaskAdapter.setTaskArrayList(taskArrayList);
        mListView.setAdapter(mTaskAdapter);
        mTaskAdapter.notifyDataSetChanged();
    }

    /**
     * スピナー内容更新
     * [参考]
     * http://mitoroid.com/category/android/android_listview1.php
     * http://mrstar-memo.hatenablog.com/entry/2013/08/17/213549
     *
     * [注意] 先頭に「(未選択)」用の項目を追加
     */
    private void UpdateSpinner2()
    {
        Category category;

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        // 先頭に項目「(未選択)」追加
        category = new Category();
        category.setId(-1);   // ID=-1が「未選択」を意味するようにする
        category.setCategory("(未選択)");
        adapter.add(category);

        for (int i = 0; i < mCategoryRealmResults.size(); i++) {
            category = new Category();

            category.setId(mCategoryRealmResults.get(i).getId());
            category.setCategory(mCategoryRealmResults.get(i).getCategory());

            adapter.add(category);
        }

        // アダプターを設定します
        mSpinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }


    // エラー共通処理
    private void ErrorCommon(String msg) {
        Log.d("**MyError**", msg);

        // エラーダイアログ表示
        showAlertDialog(msg);
    }

    // エラーダイアログ表示
    private void showAlertDialog(String msg) {
        // AlertDialog.Builderクラスを使ってAlertDialogの準備をする
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("エラー");
        alertDialogBuilder.setMessage(msg);

        // OKボタンに表示される文字列、押したときのリスナーを設定する
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //アクティビティ終了
                        Log.d("**MyError**", "End Activity");
                        finish();
                    }
                });

        // AlertDialogを作成して表示する
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
