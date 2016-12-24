package com.outlook.sftjun.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vpro_004.app01.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.vpro_004.app01.R.id.img;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;
    private ImageView imageView;
    private Uri imageUri;
    private String imageFileName;
    protected ProgressDialog progressDialog;

    protected static final int REQUEST_ACTION_TAKE_A_PICTURE = 2;
    protected static final int REQUEST_ACTION_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        btn6 = (Button) findViewById(R.id.btn6);
        btn7 = (Button) findViewById(R.id.btn7);
        imageView = (ImageView) findViewById(img);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFileName = System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, imageFileName);
                values.put(MediaStore.Images.Media.MIME_TYPE,
                        "image/jpeg");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);

                Intent localintent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                localintent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(localintent, REQUEST_ACTION_TAKE_A_PICTURE);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localIntent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                localIntent.setType("image/*");
                startActivityForResult(localIntent, REQUEST_ACTION_PICK);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageURI(Uri.parse("/storage/emulated/0/Download/Browser/0.jpg"));
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = "/storage/emulated/0/Pictures/1481199486049.jpg";
                Bitmap bitmap;
                File file = new File(path);
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    FileChannel fileChannel = fileInputStream.getChannel();
                    Long fileSize = fileChannel.size();
                    bitmap = BitmapFactory.decodeStream(fileInputStream);
                    imageView.setImageBitmap(bitmap);
                    System.out.println("文件的大小是:" + fileSize);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showProgress(null, ProgressDialog.STYLE_SPINNER);
                final Message message = new Message();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 初始化
                        OkHttpClient okHttpClient = new OkHttpClient();
                        File file = new File("/storage/emulated/0/PIT-IMG/1481783829845.jpg");
                        // 发起新的请求,获取返回信息
                        RequestBody requestBody = new MultipartBody.Builder()
                                // 设置请求的 Content-Type
                                .setType(MultipartBody.FORM)// 预设类型
                                // 添加一般参数
                                .addFormDataPart("remarks", "abc")//添加键值对
                                .addFormDataPart("goods_name", "ソフト００１")
                                .addFormDataPart("assets_no", "SOFT0001-001")
                                .addFormDataPart("set_location", "10003")
                                .addFormDataPart("im_secure_token", "cde76fd2d8e3e5e680c684ca733f89db0a20e18eb6bf70d573343a51d94b3ce0")
                                .addFormDataPart("status", "2")
                                .addFormDataPart("load_department_cd", "kaifa_one")
                                .addFormDataPart("final_check_user", "ProShip")
                                .addFormDataPart("final_check_date", "2016-12-12 10:07:09")
                                .addFormDataPart("check_type", "100000")
                                // 添加图像
                                .addFormDataPart("file", "/storage/emulated/0/PIT-IMG/1481783829845.jpg", RequestBody.create(MediaType.parse("image/png"), file))
                                .build();
                        Headers headers = new Headers.Builder()
                                .add("path", "path=/")
                                .add("cookie", "JSESSIONID=aaaFCbZNf3EgCdpquNMHv")
                                .build();
                        Request request = new Request.Builder()
                                .url("http://54.222.164.166:8089/imart/updateAssetsInfo")
                                .headers(headers)
                                .post(requestBody)
                                .build();
//                        try {
//                            // 同步执行代码
//                            Response response = okHttpClient.newCall(request).execute();
//                            if(response.isSuccessful()){
//                                message.obj = response.body().string();
//                                netHandler.sendMessage(message);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        // 异步执行代码
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                System.out.println("异步执行后的结果是:" + response.body().string());
                                runOnUiThread(new Runnable() {//这个方法可以在子线和中操作UI线程
                                    @Override
                                    public void run() {
                                        imageView.setImageURI(Uri.parse("/storage/emulated/0/PIT-IMG/1481783829845.jpg"));
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SqliteActivity.class);
                startActivity(intent);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,BluetoothActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String[] proj = {MediaStore.Images.Media.DATA};
            //好像是Android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = null;
            int column_index;
            String imgPath = null;
            switch (requestCode) {
                case REQUEST_ACTION_TAKE_A_PICTURE:
                    imageView.setImageURI(imageUri);
                    System.out.println("========>" + imageUri.toString());
                    cursor = managedQuery(imageUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    imgPath = cursor.getString(column_index);
                    break;
                case REQUEST_ACTION_PICK:
                    if (data != null) {
                        Uri uri = data.getData();
                        Log.e("uri", uri.toString());
                        ContentResolver cr = this.getContentResolver();
                        try {
                            Bitmap bitmap2 = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            imageView.setImageBitmap(bitmap2);
                        } catch (FileNotFoundException e) {
                            Log.e("Exception", e.getMessage(), e);
                        }
                        //好像是Android多媒体数据库的封装接口，具体的看Android文档
                        cursor = managedQuery(uri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        imgPath = cursor.getString(column_index);
                    } else {
                        //TODO
                    }
                    break;
            }
            System.out.println("图片的地址是:" + imgPath);
        } else {
            System.out.println("==============>没有数据");
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void showProgress(String message, final int style) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            return;
        }
        if (message == null) {
            message = getString(R.string.Loading);
        }
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(message);
        this.progressDialog.setProgressStyle(style);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    public void closeDialog() {
        if (this.progressDialog != null) {
            this.progressDialog.cancel();
        }
    }

    Handler netHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeDialog();
            Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
            System.out.println("=============>>" + msg.obj.toString());
        }
    };
}
