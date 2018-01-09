package com.example.searcher.searchengine;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.example.searcher.util.FileSearcherUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileItem{
    private static String storagePath = Environment.getExternalStorageDirectory().getPath() + File.separator;
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm");



    private String name,detail,path;
    private File file;
    private boolean isChecked;

    FileItem(File file){
        this.file = file;
        this.name = file.getName();
        this.detail = FileSearcherUtil.byteSizeFormatter(file.length())+","+FileItem.formatter.format(new Date(file.lastModified()));
        this.path = file.getPath().replace(storagePath,"");
        isChecked = false;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }
}