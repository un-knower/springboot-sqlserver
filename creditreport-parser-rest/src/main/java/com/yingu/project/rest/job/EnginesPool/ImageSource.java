package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;
import java.io.*;

public class ImageSource {

    public ImageSource( String imagesFolder ) {
        String extensionsMask = "bmp|dcx|pcx|png|jpg|jpeg|jp2|jpc|jfif|pdf|tif|tiff|gif|djvu|djv|jb2";
        // Get all files from source folder
        String[] fileNames = new File( imagesFolder ).list();
        // Select files with appropriate extensions
        imageFiles = new ArrayList<ImageFile>();
        for( String fileName : fileNames ) {
            if( extensionsMask.contains( getFileExtension( fileName ).toLowerCase() ) ) {
                imageFiles.add( new ImageFile( Main.CombinePaths( imagesFolder, fileName ) ) );
            }
        }
        // Get iterator for image files
        iterator = imageFiles.iterator();
        processedImageFilesLock = new ReentrantLock();
        processedImageFilesCount = 0;
    }

    public synchronized ImageFile GetNextImageFile() {
        if( iterator.hasNext() ) {
            return iterator.next();
        }
        return null;
    }

    public synchronized void AddImageFile( ImageFile imageFile ) {
        imageFiles.add( imageFile );
    }

    public void IncreaseProcessedImageFilesCount() {
        processedImageFilesLock.lock();
        try {
            processedImageFilesCount++;
        } finally {
            processedImageFilesLock.unlock();
        }
    }

    public int GetProcessedImageFilesCount() {
        processedImageFilesLock.lock();
        try {
            return processedImageFilesCount;
        } finally {
            processedImageFilesLock.unlock();
        }
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

    private List<ImageFile> imageFiles;
    private Iterator<ImageFile> iterator;
    private ReentrantLock processedImageFilesLock;
    private int processedImageFilesCount;
}
