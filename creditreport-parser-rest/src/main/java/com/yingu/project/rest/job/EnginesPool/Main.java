package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;
import java.io.*;

public class Main {

    public static void main( String[] args ) {
        try {
            Main application = new Main();
            application.Run();
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
    }

    public void Run() throws Exception {
        // Initializing
        // 初始化
        initialize();
        try {
            // Process with ABBYY FineReader Engine
            processWithEngine();
        } finally {
            // Deinitializing
            deinitialize();
        }
    }

    private int GetAllowedCoresNumber( String projectId ) throws Exception {
        int cores = 0;
        // It's need to load FREngine instance to get information about license
        // 需要加载FREngine实例才能获取有关许可证的信息
        IEngine engine = null;
        try {
            engine = Engine.GetEngineObject( SamplesConfig.GetDllFolder(), "" );
            ILicense license = engine.GetAvailableLicenses( projectId, "" ).Find( projectId );
            if( license == null ) {
                throw new Exception( "License " + projectId + " not found!" );
            }
            cores = license.getAllowedCoresCount();
        } finally {
            engine = null;
            Engine.DeinitializeEngine();
        }
        return cores;
    }

    private void initialize() throws Exception {
        // Configure the number of worker processes depending on the number of processors
        // 返回java虚拟机可用处理器数量
        int availableCoresNumber = Runtime.getRuntime().availableProcessors();
        int allowedCoresNumber = GetAllowedCoresNumber( SamplesConfig.GetDeveloperSN() );

        int optimalNumberOfThreads = 0;
        // 判断    allowedCoresNumber返回0    不限制引擎数量
        if( allowedCoresNumber == 0 ) {
            // Zero value means for no limitation to the engines count
            optimalNumberOfThreads = availableCoresNumber;
        } else {
            //返回  最小值 做引擎数量
            optimalNumberOfThreads = Math.min( allowedCoresNumber, availableCoresNumber );
        }

        displayMessage( "Initializing engines pool..." );
        enginesPool = new EnginesPool( optimalNumberOfThreads, SamplesConfig.GetDeveloperSN(), 60 );
        enginesPool.SetAutoRecycleUsageCount( 100 );

        workerThreads = null;
    }

    private void processWithEngine() {
        try {
            String imagesFolder = CombinePaths( SamplesConfig.GetSamplesFolder(), "SampleImages" );
            String resultFolder = CombinePaths( SamplesConfig.GetSamplesFolder(), "SampleImages\\ExportResults" );

            // Check source folder existence
            if( !isDirectoryExist( imagesFolder ) ) {
                throw new Exception( "Cannot find " + imagesFolder );
            }
            imageSource = new ImageSource( imagesFolder );

            // Create result folder if it doesn't exist
            createDirectory( resultFolder );

            runThreads( Runtime.getRuntime().availableProcessors(), imageSource, resultFolder );
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
    }

    private void runThreads( int numberOfThreads, ImageSource imageSource, String resultFolder ) {
        workerThreads = new Thread [numberOfThreads];

        displayMessage( "Running " + Integer.toString( numberOfThreads ) + " threads..." );
        for( int i = 0; i < numberOfThreads; i++ ) {
            workerThreads[i] = new Thread( new WorkerThread( i, enginesPool, imageSource, resultFolder ) );
            workerThreads[i].start();
        }

        boolean isRunning = true;
        while( isRunning ) {
            isRunning = false;
            for( Thread workerThread : workerThreads ) {
                isRunning = isRunning || workerThread.isAlive();
            }
            try {
                Thread.sleep( 500 );
            } catch( Exception ex ) {
                displayMessage( ex.getMessage() );
            }
        }
    }

    private void deinitialize() throws Exception {
        displayMessage( "Deinitializing engines pool..." );
        try {
            for( Thread workerThread : workerThreads ) {
                workerThread.join();
            }
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
        if( enginesPool != null ) {
            enginesPool.UnloadEngines();
            enginesPool = null;
        }
    }

    public static String CombinePaths( String path1, String path2 ) {
        File file1 = new File( path1 );
        File file2 = new File( file1, path2 );
        return file2.getPath();
    }

    private static void displayMessage( String message ) {
        System.out.println( message );
    }

    private static boolean isDirectoryExist( String path ) {
        File file = new File( path );
        return file.exists();
    }

    private static void createDirectory( String path ) {
        File file = new File( path );
        if( !file.exists() ) {
            file.mkdir();
        }
    }

    private EnginesPool enginesPool;
    private ImageSource imageSource;
    private Thread[] workerThreads;
}
