package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;
import java.lang.management.ManagementFactory;

public class EngineHolder {

    public EngineHolder( String projectId ) throws Exception {
        try {
            engineLoader = Engine.GetEngineOutprocLoader();

            // When a process finishes normally, all COM objects are released and work processes hosting these objects also finish
            // normally. If the process terminates abnormally (hard exception or manually killed) the COM objects are not released
            // and work processes will remain loaded. To address this issue and make your server more robust you can make
            // each work process watch if its parent process is still alive and terminate if not.
            //当一个进程正常完成时，所有COM对象都被释放，并且托管这些对象的工作进程也完成
            // 一般。 如果进程异常终止（硬异常或手动终止），COM对象不会被释放
            //并且工作进程将保持加载状态。 为了解决这个问题，使你的服务器更强大，你可以做出
            //每个工作进程监视它的父进程是否仍然活着，如果没有则终止。
            IHostProcessControl processControl = engineLoader.GetHostProcessControl();
            processControl.SetClientProcessId( getCurrentProcessId() );

            engine = engineLoader.GetEngineObject( projectId );
        } catch( Exception ex ) {
            if( ex instanceof EngineException ) {
                int hResult = ( ( EngineException )ex ).getHResult();
                if( hResult == 0x80070005 ) {
                    // To use LocalServer under a special account you must add this account to
                    // the COM-object's launch permissions (using DCOMCNFG or OLE/COM object viewer)
                    throw new Exception( "Launch permission for the work-process COM-object is not granted. " +
                            "Use DCOMCNFG to change security settings for the object (" + ex.getMessage() + ")" );
                }
            }
            throw ex;
        }
        isEngineLocked = false;
        engineUsageCount = 0;
    }

    public IEngine GetLockedEngine() {
        isEngineLocked = true;
        engineUsageCount++;
        return engine;
    }

    public void UnlockEngine() {
        isEngineLocked = false;
    }

    public boolean IsEngineLocked() {
        return isEngineLocked;
    }

    public boolean ContainsEngine( IEngine value ) {
        return engine == value;
    }

    public int GetEngineUsageCount() {
        return engineUsageCount;
    }

    public void UnloadEngine() {
        IHostProcessControl processControl = engineLoader.GetHostProcessControl();
        int processId = processControl.getProcessId();

        engine = null;
        System.gc();
        System.runFinalization();
        engineLoader.ExplicitlyUnload();
        engineLoader = null;
        System.gc();
        System.runFinalization();

        try {
            Thread.sleep( 500 );
            killProcess( processId );
        } catch( Exception ex ) {
            // The process could exit between a decision to kill it and this code
            // Skip this error
        }
    }

    private static int getCurrentProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return new Integer( name.substring( 0, name.indexOf( "@" ) ) ).intValue();
    }

    private static void killProcess( int processId ) throws Exception {
        Runtime.getRuntime().exec( "taskkill /F /PID " + processId );
    }

    private IEngineLoader engineLoader;
    private IEngine engine;
    private boolean isEngineLocked;
    private int engineUsageCount;
}
