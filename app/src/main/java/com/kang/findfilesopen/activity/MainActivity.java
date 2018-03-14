package com.kang.findfilesopen.activity;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import com.kang.findfilesopen.R;
import com.kang.findfilesopen.adapter.SelectedFileListAdapter;
import com.kang.findfilesopen.adapter.TypeAdapter;
import com.kang.findfilesopen.bean.File_M;
import com.kang.findfilesopen.utils.TLog;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private RecyclerView main_type_recycler;//类型
    private RecyclerView main_files_recycler;//文件
    private List<File_M> fileList = new ArrayList<File_M>();//文件
    private List<String> typeList = new ArrayList<String>();//类型
    private SelectedFileListAdapter fileAdapter;
    private TypeAdapter typeAdapter;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();//初始化布局

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastRec);//取消注册
    }

    public void initViews(){
        typeList = new ArrayList<String>();//类型
        typeList.add("PDF");
        typeList.add("txt");
        typeList.add("pdf和txt");
        typeList.add("doc");
        typeList.add("jpg,png");
        main_type_recycler = (RecyclerView) findViewById(R.id.main_type_recycler);
        main_files_recycler = (RecyclerView) findViewById(R.id.main_files_recycler);

        typeAdapter = new TypeAdapter(MainActivity.this,typeList);
        typeAdapter.setOnItemClickListener(new TypeAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,FileListActivity.class);
                intent.putExtra("type",position);
                startActivityForResult(intent,1);
            }
        });
        main_type_recycler.setLayoutManager( new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        main_type_recycler.setAdapter(typeAdapter);

        fileList = new ArrayList<File_M>();//文件
        fileAdapter = new SelectedFileListAdapter(MainActivity.this,fileList);
        fileAdapter.setOnItemClickListener(new SelectedFileListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openFile(fileList.get(position).getFilePath());
            }
        });
        main_files_recycler.setLayoutManager(new GridLayoutManager(this,2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        main_files_recycler.setAdapter(fileAdapter);

    }

    /**
     * 打开一个文件
     *
     * @param filePath
     *            文件的绝对路径
     */
    private void openFile(final String filePath){
        TLog.e("filePath========"+filePath);
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(MainActivity.this, perms)) {
            EasyPermissions.requestPermissions(MainActivity.this, "需要访问手机存储权限！", 10086, perms);
        } else {
            FileDisplayActivity.show(MainActivity.this, filePath);
        }
//    {
//        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
//        try
//        {
//            File file = new File(filePath);
//            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//            String temp = ext.substring(1);
//            String mime = mimeTypeMap.getMimeTypeFromExtension(temp);
//
//            FileDisplayActivity.show(MainActivity.this, filePath);
//
////            Intent intent = new Intent();
////            intent.setAction(Intent.ACTION_VIEW);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            intent.addCategory(Intent.CATEGORY_DEFAULT);
////            // 判断是否是7.0
////            if(Build.VERSION.SDK_INT >= 24){
////                // 适配android7.0 ，不能直接访问原路径
////                // 需要对intent 授权
////                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                System.out.println(filePath);
////                Uri uri = FileProvider.getUriForFile(this,  "com.googosoft.findfilesquickly.fileprovider",file);
////                intent.setDataAndType(uri,mime);
////                FileDisplayActivity.show(MainActivity.this, filePath);
////            } else {
////                intent.setDataAndType(Uri.fromFile(file), mime);
////            }
//
////            startActivity(intent);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            Snackbar.make(main_files_recycler, "系统无法打开后缀名为" + ext + "的文件！", Snackbar.LENGTH_LONG)
//                    .setAction("OK", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        }
//                    })
//                    .show();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                fileList.clear();
                ArrayList<String> fileStrList = data.getStringArrayListExtra("files");
                for (int m=0;m<fileStrList.size();m++) {
                    String filrStr = fileStrList.get(m);
                    int index = filrStr.indexOf("*");
                    String typeName = "";
                    if (index>=0) {
                        typeName = filrStr.substring(0,index);
                    }

                    String titlePath = "";
                    if (filrStr.length()>(index+1)) {
                        titlePath = filrStr.substring(index+1);
                    }
                    File_M fileM = new File_M();
                    fileM.setFileType(typeName);
                    fileM.setFilePath(titlePath);
                    fileList.add(fileM);
                }
                fileAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
