package jp.techacademy.tanaka.yousuke.taskapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputActivity extends AppCompatActivity {
    //タスクの日時を保持するInt型のmYear、mMonth、mDay、mHour、mMinute
    private int mYear, mMonth, mDay, mHour, mMinute;
    //日付を設定するButtonと時間を設定するButton、タイトルを入力するEditText、内容を入力するEditTextの保持する変数
    private Button mDateButton, mTimeButton;
    private EditText mTitleEdit, mContentEdit;
    // Taskクラスのオブジェクト
    private Task mTask;

    //日付を設定するButtonのリスナー
    //日付をユーザに入力させる場合はLesson4で学んだDatePickerDialogを使います。mYear、mMonth、mDayを引数に与えて生成し、
    //onDateSetメソッドでそれらの値を入力された日付で更新します。
    //その際にmDateButtonのテキストも新しい日付で更新します。
    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(InputActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
                            mDateButton.setText(dateString);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };
    //時間を設定するButtonのリスナー
    //時間をユーザに入力させる場合はLesson4で学んだTimePickerDialogを使います。
    // mHour、mMinuteを引数に与えて生成し、onTimeSetでそれらの値を入力された時間で更新します。
    // その際にmTimeButtonのテキストも新しい日付で更新します。
    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
                            mTimeButton.setText(timeString);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };
    //決定Buttonのリスナー
    //mOnDoneClickListenerではRealmに保存/更新したあと、
    // finishメソッドを呼び出すことでInputActivityを閉じて前の画面（MainActivity）に戻ります。
    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTask();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mDateButton = (Button)findViewById(R.id.date_button);
        mDateButton.setOnClickListener(mOnDateClickListener);
        mTimeButton = (Button)findViewById(R.id.times_button);
        mTimeButton.setOnClickListener(mOnTimeClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mTitleEdit = (EditText)findViewById(R.id.title_edit_text);
        mContentEdit = (EditText)findViewById(R.id.content_edit_text);

        Intent intent = getIntent();
        mTask = (Task) intent.getSerializableExtra(MainActivity.EXTRA_TASK);

        if (mTask == null) {
            //新規作成の場合は遷移元であるMainActivityからTaskは渡されないのでnullになります。
            // nullであれば現在時刻からmYear、mMonth、mDay、mHour、mMinutに値を設定します。

            // 新規作成の場合
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            //タスクが渡ってきた場合は更新のため渡ってきたタスクの時間を設定します。
            // 合わせて、mTitleEditにタイトル、mContentEditに内容を設定し、
            // mDateButtonに日付、mTimeButtonに時間をそれぞれ文字列に変換して設定します。

            // 更新の場合
            mTitleEdit.setText(mTask.getTitle());
            mContentEdit.setText(mTask.getContents());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getDate());
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
            mDateButton.setText(dateString);
            mTimeButton.setText(timeString);
        }
    }

//    private void addTask() {
//        // Realmオブジェクトを取得
//        Realm realm = Realm.getDefaultInstance();
//
//        if (mTask == null) {
//            //mTaskがnull、つまり新規作成の場合はTaskクラスを生成し、
//            // 保存されているタスクの中の最大のidの値に1を足したものを設定します。
//            // こうすることでユニークなIDを設定することが可能となります。
//            // そしてタイトル、内容、日時をmTaskに設定し、データベースに保存します。
//
//            // 新規作成の場合
//            mTask = new Task();
//
//            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();
//
//            int identifier;
//            if (taskRealmResults.max("id") != null) {
//                identifier = taskRealmResults.max("id").intValue() + 1;
//            } else {
//                identifier = 0;
//            }
//            mTask.setId(identifier);
//        }
//
//        String title = mTitleEdit.getText().toString();
//        String content = mContentEdit.getText().toString();
//
//        mTask.setTitle(title);
//        mTask.setContents(content);
//        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
//        Date date = calendar.getTime();
//        mTask.setDate(date);
//
//
//        // Realmでデータを追加、削除など変更を行う場合はbeginTransactionメソッドを呼び出し、削除などの処理、
//        // そして最後にcommitTransactionメソッドを呼び出す必要があります。
//        // データの保存・更新はcopyToRealmOrUpdateメソッドを使います。
//        // これは引数で与えたオブジェクトが存在していれば更新、なければ追加を行うメソッドです。
//        // 最後にcloseメソッドを呼び出します。
//
//
//        // Realmでデータを追加、削除など変更
//        realm.beginTransaction();
//        // データの保存・更新
//        realm.copyToRealmOrUpdate(mTask);
//        realm.commitTransaction();
//
//        realm.close();
//    }

    private void addTask() {
        Realm realm = Realm.getDefaultInstance();

        if (mTask == null) {
            // 新規作成の場合
            mTask = new Task();

            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mTask.setId(identifier);
        }

        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();

        mTask.setTitle(title);
        mTask.setContents(content);
        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
        Date date = calendar.getTime();
        mTask.setDate(date);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mTask);
        realm.commitTransaction();

        realm.close();


        // まずTaskAlarmReceiverを起動するIntentを作成します。
        // そしてそのExtraにタスクを設定します。
        // これはTaskAlarmReceiverがブロードキャストを受け取った後、
        // タスクのタイトルなどを表示する通知を発行するためにタスクの情報が必要になるからです。
        // そしてPendingIntentを作成します。第2引数にタスクのIDを指定しています。
        // タスクを削除する際に指定したアラームも合わせて削除する必要があります。
        // アラームを削除しないとタスクを削除したにも関わらず通知を表示してしまうことになるからです。
        // そしそのPendingIntentを一意に識別するためにタスクのIDを設定します。

        // PendingIntent.FLAG_UPDATE_CURRENTは既存のPendingIntentがあれば、
        // それはそのままでextraのデータだけ置き換えるという指定です。
        // タスクを更新した際にはextraのデータだけ、
        // つまりタスクのデータだけ置き換えたいのでこの指定にします。
        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
        resultIntent.putExtra(MainActivity.EXTRA_TASK, mTask);
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                this,
                mTask.getId(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        //AlarmManagerを使うことで指定した時間に任意の処理をさせることができます。addTaskメソッドに追加した処理を見てみましょう。
        //AlarmManagerはActivityのgetSystemServiceメソッドに引数ALARM_SERVICEを与えて取得します。setメソッドの第一引数のRTC_WAKEUPは「UTC時間を指定する。画面スリープ中でもアラームを発行する」という指定です。
        //第二引数でタスクの時間をUTC時間で指定しています。
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);
    }
}