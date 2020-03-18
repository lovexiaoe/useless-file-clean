package org.colosseumer.java.fileupload.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;

/**
 * @Description: 图片压缩工具类
 * @Author: zhaoyu
 * @Date: 2019/10/19
 */
public class ImageThumbUtil {

    // 缩略图后缀-小图
    private static final int[] SMALL_SIZE=new int[]{300,400};
    private static final String SMALL_SIZE_SUFFIX = "-small";


    /**
     * @Description 按比例缩放成小图
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param file
     * @Return java.lang.String
     */
    public static String sacleSmallBySize ( String file) throws IOException {
        return sacleBySize(SMALL_SIZE[0],SMALL_SIZE[1],file,SMALL_SIZE_SUFFIX);
    }

    /**
     * @Description 指定大小缩放，不变形，保存到当前目录
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param wideth 宽
     * @Param highth 高
     * @Param fileName 图片路径
     * @Return String 缩略图路径。
     */
    public static String sacleBySize(int wideth,int highth, String fileName,String suffix) throws IOException {
        String newPath=appendSuffix(fileName,suffix);
        Thumbnails.of(fileName)
                .size(wideth,highth)
                .toFile(newPath);
        return newPath;
    }

    /**
     * @Description 指定大小缩放 （宽高比会变，图片变形），保存到当前目录
     * @Author zhaoyu
     * @Date 2019/10/21
     * @Param wideth 宽
     * @Param highth 高
     * @Param fileName 图片路径
     * @Return String 缩略图路径。
     */
    public static String sacleToSize(int wideth,int highth, String fileName,String suffix) throws IOException {
        String newPath=appendSuffix(fileName,suffix);
        Thumbnails.of(fileName)
                .size(wideth,highth)
                .keepAspectRatio(false)
                .toFile(newPath);
        return newPath;
    }

    /**
     * 文件追加后缀
     *
     * @param fileName 原文件名
     * @param suffix   文件后缀
     * @return
     */
    public static String appendSuffix(String fileName, String suffix) {
        String newFileName = "";

        int indexOfDot = fileName.lastIndexOf('.');

        if (indexOfDot != -1) {
            newFileName = fileName.substring(0, indexOfDot);
            newFileName += suffix;
            newFileName += fileName.substring(indexOfDot);
        } else {
            newFileName = fileName + suffix;
        }

        return newFileName;
    }

}
