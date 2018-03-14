package com.kang.findfilesopen.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kang.findfilesopen.R;
import com.kang.findfilesopen.adapter.FileListAdapter;
import com.kang.findfilesopen.bean.File_M;
import com.kang.findfilesopen.utils.CustomProgressDialog;
import com.kang.findfilesopen.utils.MyDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    private LinearLayout top_layout_back_layout;//返回
    private TextView top_layout_title;//标题
    private Button top_layout_right_btn;//确定按钮
    private FloatingActionButton file_list_ok;//确定按钮
    private ImageView file_list_nodata;//没有数据
    private CustomProgressDialog progressDialog = null;

    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RecyclerView file_list_recycler;
    private List<File_M> list = new ArrayList<File_M>();
    private FileListAdapter adapter;
    private final int READ_EXTERNAL_INT = 1;//读取权限
    private int type;//0pdf  1txt   2pdf和txt   3 doc   4jpg,png

    private ArrayList<String> fileList = new ArrayList<String>();;

    private AppBarLayout appbarlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        appbarlayout = (AppBarLayout) findViewById(R.id.appbarlayout);


//        startProgress("正在查找，请稍候...");

        initViews();//初始化布局

        if (ContextCompat.checkSelfPermission(FileListActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FileListActivity.this,
                    PERMISSIONS_STORAGE,READ_EXTERNAL_INT);
        } else {

            queryFiles();//查找文件
//            queryMyFiles();//查找文件

        }


        appbarlayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset<0){
                    //上去了
//                    top_layout_back_layout.setVisibility(View.INVISIBLE);
                    top_layout_right_btn.setVisibility(View.VISIBLE);
                } else {
                    //正常
//                    top_layout_back_layout.setVisibility(View.VISIBLE);
                    top_layout_right_btn.setVisibility(View.INVISIBLE);
                }

                top_layout_back_layout.setAlpha((100+verticalOffset)/100.0f);
                top_layout_right_btn.setAlpha(0-verticalOffset/100.0f);


                System.out.println(verticalOffset);
            }
        });
    }




    /**
     * 授权之后的操作
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_INT:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    queryMyFiles();
                } else {
                    if (progressDialog!=null) {
                        progressDialog.dismiss();
                    }
                    Snackbar.make(top_layout_title,"您拒绝了开启权限！",Snackbar.LENGTH_LONG).setAction("去设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * 获取应用详情页面intent
                             * 去设置中修改权限设置
                             * @return
                             */
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                            }
                            startActivity(localIntent);
                        }
                    }).show();;
                }
                break;
            default:
                break;
        }
    }



    public void initViews(){
        type = getIntent().getIntExtra("type",0);

        top_layout_back_layout = (LinearLayout) findViewById(R.id.top_layout_back_layout);//返回
        top_layout_back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        top_layout_right_btn = (Button) findViewById(R.id.top_layout_right_btn);
        top_layout_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(top_layout_title,"测试样式",Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();;
            }
        });

        top_layout_title = (TextView) findViewById(R.id.top_layout_title);//标题
        top_layout_title.setText("文件列表");

        file_list_ok = (FloatingActionButton) findViewById(R.id.file_list_ok);

        file_list_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileList = new ArrayList<String>();
                for (int i=0;i<list.size();i++){
                    if (list.get(i).isChecked()) {
                        fileList.add(list.get(i).getFileType()+"*"+list.get(i).getFilePath());
                    }
                }
                if (fileList.size()>0){
                    final MyDialog builder = new MyDialog(FileListActivity.this).builder();
                    builder.setMsg("确定选择这些文件吗？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra("files",fileList);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Snackbar.make(top_layout_title,"请选择文件!",Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            }
        });
        file_list_nodata = (ImageView) findViewById(R.id.file_list_nodata);//没有数据


        file_list_recycler = (RecyclerView) findViewById(R.id.file_list_recycler);
        list = new ArrayList<File_M>();
        adapter = new FileListAdapter(this,list);
        file_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new FileListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                list.get(position).toggle();
                adapter.notifyDataSetChanged();
            }
        });
        file_list_recycler.setAdapter(adapter);
    }

    public void queryMyFiles(){
        Collection<File> listFiles;
        if (type==2) {// 2 pdf和txt
            listFiles = FileUtils.listFiles(Environment.getExternalStorageDirectory().getAbsoluteFile(), new String[]{"pdf","txt"}, true);
        } else if (type==4) {//4jpg png
            listFiles = FileUtils.listFiles(Environment.getExternalStorageDirectory().getAbsoluteFile(), new String[]{"jpg","png"}, true);
        } else if (type==3) {//3doc
            listFiles = FileUtils.listFiles(Environment.getExternalStorageDirectory().getAbsoluteFile(), new String[]{"doc","docx","wps"}, true);
        } else if (type==1) {//1txt
            listFiles = FileUtils.listFiles(Environment.getExternalStorageDirectory().getAbsoluteFile(), new String[]{"txt"}, true);
        } else {
            listFiles = FileUtils.listFiles(Environment.getExternalStorageDirectory().getAbsoluteFile(), new String[]{"pdf"}, true);
        }

        for (File file : listFiles) {
            File_M file_m = new File_M();
            file_m.setFilePath(file.getAbsolutePath());
            file_m.setFileTitle(file.getName());
            int pointIndex = file.getName().lastIndexOf(".");
            if (pointIndex>=0&&file.getName().length()>(pointIndex+1)) {
                file_m.setFileType(file.getName().substring(pointIndex+1));
            }

            list.add(file_m);
        }
        adapter.notifyDataSetChanged();
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
        if (list.size()>0){
            file_list_nodata.setVisibility(View.GONE);
        } else {
            file_list_nodata.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 查找文件
     * */
    public void queryFiles() {
        String[] projection = new String[] {
                MediaStore.Files.FileColumns._ID,//文件id
                MediaStore.Files.FileColumns.DATA,//路径（带后缀名）
                MediaStore.Files.FileColumns.MIME_TYPE,//文件类型
                MediaStore.Files.FileColumns.TITLE,//文件类名称（不带后缀名）
                MediaStore.Files.FileColumns.SIZE//文件大小（不带后缀名）
        };
        String pdfType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf".toLowerCase());
        String txtType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt".toLowerCase());
        String docType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc".toLowerCase());
        String jpgType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg".toLowerCase());

        String[] selectionArgsPdf = new String[]{ pdfType };
        String[] selectionArgsTxt = new String[]{ txtType };
        String[] selectionArgsDoc = new String[]{ docType };
        String[] selectionArgsJpg = new String[]{ jpgType };
        String[] selectionArgsPdfTxt = new String[]{ pdfType,txtType };



        Cursor cursor = null;
        if (type==2) {// 2 pdf和txt
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/file"),//查询资源路径
                    projection,//查询哪些信息
                    MediaStore.Files.FileColumns.MIME_TYPE  + " = ? or "+MediaStore.Files.FileColumns.MIME_TYPE  + " = ? ",//查询条件
                    selectionArgsPdfTxt,//查询条件对应的值的集合
//                null//排序依据
                    MediaStore.Files.FileColumns.MIME_TYPE+" ASC"//排序依据,如果不想排序直接写null
            );
        } else if (type==4) {//4png
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/file"),//查询资源路径
                    projection,//查询哪些信息
                    MediaStore.Files.FileColumns.MIME_TYPE  + " = ? ",//查询条件
                    selectionArgsJpg,//查询条件对应的值的集合
                    null//排序依据
            );
        } else if (type==3) {//3doc
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/file"),//查询资源路径
                    projection,//查询哪些信息
                    MediaStore.Files.FileColumns.MIME_TYPE  + " = ? ",//查询条件
                    selectionArgsDoc,//查询条件对应的值的集合
                    null//排序依据
            );
        } else if (type==1) {//1txt
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/file"),//查询资源路径
                    projection,//查询哪些信息
                    MediaStore.Files.FileColumns.MIME_TYPE  + " = ? ",//查询条件
                    selectionArgsTxt,//查询条件对应的值的集合
                    null//排序依据
            );
        } else {
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/file"),//查询资源路径
                    projection,//查询哪些信息
                    MediaStore.Files.FileColumns.MIME_TYPE  + " = ? ",//查询条件
                    selectionArgsPdf,//查询条件对应的值的集合
                    null//排序依据
            );
        }

        if (cursor != null) {
            boolean flag = cursor.moveToFirst();
            if (flag) {

                int idindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns._ID);
                int dataindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATA);

                int typea = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);
                int titlea = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                int sizeindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                do {
                    File_M file_m = new File_M();
                    String id = cursor.getString(idindex);
                    String path = cursor.getString(dataindex);
                    String size = cursor.getString(sizeindex);
                    String type = cursor.getString(typea);
                    String title = cursor.getString(titlea);
                    file_m.setFileId(id);
                    file_m.setFilePath(path);
                    file_m.setFileSize(size);
                    file_m.setFileType(type);
                    file_m.setFileTitle(title);
                    list.add(file_m);
                } while (cursor.moveToNext());
                adapter.notifyDataSetChanged();
            }
        }
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
        if (list.size()>0){
            file_list_nodata.setVisibility(View.GONE);
        } else {
            file_list_nodata.setVisibility(View.VISIBLE);
        }

    }

    private void startProgress(String str) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new CustomProgressDialog(FileListActivity.this);
        progressDialog = CustomProgressDialog.createDialog(FileListActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    progressDialog.dismiss();
                }
                return true;
            }
        });
        progressDialog.setMessage(str);
        progressDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
