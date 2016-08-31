package jp.techacademy.tanaka.yousuke.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yosuke.tanaka on 2016/08/31.
 */
public class Category extends RealmObject implements Serializable {
    //id
    @PrimaryKey
    private int id;
    //カテゴリ名
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
