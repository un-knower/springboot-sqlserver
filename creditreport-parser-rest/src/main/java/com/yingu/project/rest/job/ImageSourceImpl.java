package com.yingu.project.rest.job;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

// Implementation of IImageSource interfaces.

import com.abbyy.FREngine.*;
import java.util.*;
import java.io.*;

public class ImageSourceImpl implements IImageSource {

    public ImageSourceImpl( String sourceDir ) {
        String extensionsMask = "bmp|dcx|pcx|png|jpg|jpeg|jp2|jpc|jfif|pdf|tif|tiff|gif|djvu|djv|jb2";
        // Get all files from source folder
        //  从源文件夹获取所有文件
        String[] fileNames = new File( sourceDir ).list();
        // Select files with appropriate extensions
        // 选择适当扩展名的文件
        List<String> imagesNames = new ArrayList<String>();
        for( String fileName : fileNames ) {
            //判断 定义的字符串中是否包含得到的文件后缀名
            if( extensionsMask.contains( getFileExtension( fileName ).toLowerCase() ) ) {
                //得到文件的路径添加到list
                imagesNames.add( BatchProcessing.CombinePaths( sourceDir, fileName ) );
            }
        }
        // Get iterator for images
        // 获取图像迭代器
        iterator = imagesNames.iterator();
        // 检查是否还有元素
        isEmpty = !iterator.hasNext();
    }

    // IImageSource methods implementation
    //  图片容器实现
    public IFileAdapter GetNextImageFile() {
        if( isEmpty ) {
            // Return null if there are no more files in source folder
            return null;
        }
        // Create adapter for a current file name
        // 为当前文件名创建适配器
        FileAdapterImpl fileAdapter = new FileAdapterImpl( iterator.next() );
        isEmpty = !iterator.hasNext();
        return fileAdapter;
    }

    public boolean IsEmpty() {
        return isEmpty;
    }

    private static String getFileExtension( String fileName ) {
        String extension = "";
        int pointIndex = fileName.lastIndexOf( '.' );
        int slashIndex = Math.max( fileName.lastIndexOf( '/' ), fileName.lastIndexOf( '\\' ) );

        if( pointIndex > slashIndex ) {
            extension = fileName.substring( pointIndex + 1 );
        }
        return extension;
    }

    private Iterator<String> iterator;
    private boolean isEmpty;
}