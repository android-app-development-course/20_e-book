package com.example.gecko.ereader.util;

public class Fileutil {

    public static String name ;


    /** 去掉文件扩展名
     *
     *
     * */
    public static String getFileNameNoEx (String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
