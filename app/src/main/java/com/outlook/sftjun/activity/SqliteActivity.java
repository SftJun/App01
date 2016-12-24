package com.outlook.sftjun.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vpro_004.app01.R;
import com.outlook.sftjun.greendao.db.DBHelper;
import com.outlook.sftjun.greendao.entity.User;
import com.outlook.sftjun.greendao.gen.UserDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SqliteActivity extends AppCompatActivity {

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.txt)
    TextView textView;
    //
    private static String DB_NAME = "GreenDaoDbTest";
    private UserDao userDao;
    private StringBuilder txt = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        ButterKnife.bind(this);
        //
        userDao = DBHelper.getDaoSession(this,DB_NAME).getUserDao();
    }

    @OnClick({R.id.btn1, R.id.btn2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                //
                User saveUser = new User(System.currentTimeMillis() + "", "殷俊");
                userDao.insert(saveUser);
                System.out.println("Saved");
                break;
            case R.id.btn2:
                //
                List<User> users = userDao.loadAll();
                System.out.println("有"+users.size()+"条数据");
                for (User user : users) {
                    txt.append("ID:" + user.getId() + "    name:" + user.getName() + "\r\n");
                }
                textView.setText(txt);
                break;
        }
    }
}
