package jp.techacademy.tanaka.yousuke.taskapp;

//5.3 Realmのモデルクラス
// モデルとはデータを表現するもので、TaskAapではタスクが相当することになります。
// Realmのモデルは、RealmObjectクラスを継承したJavaのクラスとして定義します。
// 対応しているデータ型はboolean、byte、short、int、long、float、double、String、Dateおよびbyte[]です。
// それではTaskクラスをMainActivityと同じところに新規作成しましょう。
// タスクのタイトル、内容、日時に該当するtitle、contents、dateを定義します。
// また、ユニークなIDを指定するidを定義し、@PrimaryKeyと付けます。
// @PrimaryKeyはRealmがプライマリーキーと判断するために必要なものです。
// プライマリーキーとは主キーとも呼ばれ、データーベースの一つのテーブルの中でデータを唯一的に確かめるための値です。

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject implements Serializable {
    private String title; // タイトル
    private String contents; // 内容
    private Date date; // 日時

    //private String category;  // カテゴリ
    private Category category;

    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryStr() {
        return category.getCategory();
    }

    public void setCategoryStr(String category) {
        this.category.setCategory(category);
    }

    public Category getCategoryCrass() {
        return category;
    }

    public void setCategoryCrass(Category category) {
        this.category = category;
    }
}