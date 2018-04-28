package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;
import java.io.*;

public class WorkerThread implements Runnable {

    public WorkerThread( int id, EnginesPool pool, ImageSource source, String folder ) {
        enginesPool = pool;
        imageSource = source;
        resultFolder = folder;
        threadId = id;
    }

    @Override
    public void run() {
        try {
            String id = Integer.toString( threadId );
            displayMessage( "Thread " + id + " started" );

            ImageFile imageFile = imageSource.GetNextImageFile();
            while( imageFile != null ) {
                displayMessage( "Thread " + id + ": " + imageFile.FilePath );
                try {
                    processImageFile( imageFile );
                    imageSource.IncreaseProcessedImageFilesCount();
                } catch( Exception ex ) {
                    displayMessage( "Thread " + id + " ERROR: \"" + ex.getMessage() +
                            "\" during processing image file: " + imageFile.FilePath );
                }
                imageFile = imageSource.GetNextImageFile();
            }
            displayMessage( "Thread " + id + " finished" );
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
    }

    private void processImageFile( ImageFile imageFile ) throws Exception {
        IEngine engine = enginesPool.GetEngine();
        boolean isRecycleRequired = false;
        try {
            IFRDocument frDocument = engine.CreateFRDocument();
            try {
                frDocument.AddImageFile( imageFile.FilePath, null, null );
                frDocument.Process( null );

                String resultFilePath = Main.CombinePaths( resultFolder, new File( imageFile.FilePath ).getName() + ".pdf" );
                frDocument.Export( resultFilePath, FileExportFormatEnum.FEF_PDF, null );
            } finally {
                frDocument.Close();
            }
        } catch( Exception ex ) {
            isRecycleRequired = shouldRestartEngine( ex );
            if( isRecycleRequired ) {
                imageFile.FailedRunsCount++;
                if( imageFile.FailedRunsCount > 1 ) {
                    throw ex;
                }
                imageSource.AddImageFile( imageFile );
            } else {
                throw ex;
            }
        } catch( OutOfMemoryError ex ) {
            isRecycleRequired = true;
            imageFile.FailedRunsCount++;
            if( imageFile.FailedRunsCount > 1 ) {
                throw ex;
            }
            imageSource.AddImageFile( imageFile );
        } finally {
            enginesPool.ReleaseEngine( engine, isRecycleRequired );
        }
    }

    private static boolean shouldRestartEngine( Exception ex )
    {
        if( ex instanceof EngineException ) {
            int hResult = ( ( EngineException )ex ).getHResult();
            // The RPC server is unavailable because of a crash of the surrogate process
            // You should restart the engine
            if( hResult == 0x800706BA ) {
                return true;
            }
            // This is RPC_E_SERVERFAULT because of a structural exception in the surrogate process
            // Most probably now the engine is in the undefined state and you should restart it
            if( hResult == 0x80010105 ) {
                return true;
            }
        }
        return false;
    }

    private static void displayMessage( String message ) {
        System.out.println( message );
    }

    private EnginesPool enginesPool;
    private ImageSource imageSource;
    private String resultFolder;
    private int threadId;
}
