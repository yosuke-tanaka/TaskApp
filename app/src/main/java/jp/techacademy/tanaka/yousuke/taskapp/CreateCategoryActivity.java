package jp.techacademy.tanaka.yousuke.taskapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreateCategoryActivity extends AppCompatActivity {

    private Category mCategory;
    private EditText mCategoryEdit;

    /**
     * 追加Buttonのリスナー
     */
    private View.OnClickListener mOnAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addCategory();
            finish();
        }
    };

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        findViewById(R.id.add_button).setOnClickListener(mOnAddClickListener);
        mCategoryEdit = (EditText)findViewById(R.id.category_edit_text);
    }

    /**
     * カテゴリ追加処理
     */
    private void addCategory() {
        Realm realm = Realm.getDefaultInstance();

        // 新規作成の場合
        mCategory = new Category();

        RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();

        // ID決定
        int identifier;
        if (categoryRealmResults.max("id") != null) {
            identifier = categoryRealmResults.max("id").intValue() + 1;
        } else {
            identifier = 0;
        }
        mCategory.setId(identifier);

        // editTextから取得した文字列をCategoryクラスに登録
        String category = mCategoryEdit.getText().toString();
        mCategory.setCategory(category);

        // Realm更新
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mCategory);
        realm.commitTransaction();

        realm.close();
    }
}
