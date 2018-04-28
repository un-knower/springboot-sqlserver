package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

public class EnginesPool implements Runnable {

    public EnginesPool( int enginesCount, String developerSN, int waitingEngineTimeout ) throws Exception {
        projectId = developerSN; //开发者序列号
        engineHolders = new EngineHolder [enginesCount];
        waitingTimeout = waitingEngineTimeout;
        semaphore = new Semaphore( enginesCount );
        autoRecycleUsageCount = 0; // Zero means that auto recycling will not be performed
        action = null;
        actionEvent = new Object();
        responseEvent = new Object();
        enginesMap = new HashMap<IEngine, IEngine>();
        autoRecycleUsageCountLock = new Object();
        isActionEventNotified = false;
        isResponseEventNotified = false;
        thread = new Thread( this );
        thread.start();
        waitResponseEvent();
        if( threadException != null ) {
            throw threadException;
        }
    }

    @Override
    public void run() {
        try {
            for( int i = 0; i < engineHolders.length; i++ ) {
                engineHolders[i] = new EngineHolder( projectId );
            }
            notifyResponseEvent();

            boolean isRunning = true;
            while( isRunning ) {
                try {
                    waitActionEvent();
                    threadException = null;

                    switch (action) {
                        case UnloadEngines:
                            isRunning = false;
                            unloadEngines();
                            break;
                        case GetEngine:
                            currentEngine = getEngine();
                            currentEngineHandle = currentEngine.MarshalInterface();
                            break;
                        case ReleaseEngine:
                            releaseEngine( engineToRelease, isEngineRecycleRequired );
                            break;
                        default:
                            throw new Exception( "Unknown action" );
                    }

                } catch( Exception ex ) {
                    threadException = ex;
                } finally {
                    action = null;
                    notifyResponseEvent();
                }
            }
        } catch( Exception ex ) {
            threadException = ex;
            notifyResponseEvent();
        }
    }

    public IEngine GetEngine() throws Exception {
        if( !semaphore.tryAcquire( waitingTimeout, TimeUnit.SECONDS ) ) {
            throw new Exception( "Waiting engine timeout exceeded" );
        }
        synchronized( this ) {
            action = EngineAction.GetEngine;
            notifyActionEvent();

            waitResponseEvent();
            if( threadException != null ) {
                throw threadException;
            }
            IEngine engine = IEngine.UnmarshalInterface( currentEngineHandle );
            enginesMap.put( engine, currentEngine );
            return engine;
        }
    }

    public void ReleaseEngine( IEngine engine, boolean isRecycleRequired ) throws Exception {
        synchronized( this ) {
            action = EngineAction.ReleaseEngine;
            if( !enginesMap.containsKey( engine ) ) {
                throw new Exception( "The engine haven't been found" );
            }
            engineToRelease = ( IEngine )enginesMap.get( engine );
            enginesMap.remove( engine );
            isEngineRecycleRequired = isRecycleRequired;
            notifyActionEvent();

            waitResponseEvent();
            if( threadException != null ) {
                throw threadException;
            }
        }
    }

    public int GetAutoRecycleUsageCount() {
        synchronized( autoRecycleUsageCountLock ) {
            return autoRecycleUsageCount;
        }
    }

    public void SetAutoRecycleUsageCount( int value ) {
        synchronized( autoRecycleUsageCountLock ) {
            autoRecycleUsageCount = value;
        }
    }

    public void UnloadEngines() throws Exception {
        synchronized( this ) {
            try {
                action = EngineAction.UnloadEngines;
                notifyActionEvent();

                waitResponseEvent();
                if( threadException != null ) {
                    throw threadException;
                }
            } finally {
                thread.join();
            }
        }
    }

    private IEngine getEngine() throws Exception {
        for( int i = 0; i < engineHolders.length; i++ ) {
            if( !engineHolders[i].IsEngineLocked() ) {
                return engineHolders[i].GetLockedEngine();
            }
        }
        throw new Exception( "The free engine haven't been found" );
    }

    private void releaseEngine( IEngine engine, boolean isRecycleRequired ) throws Exception {
        for( int i = 0; i < engineHolders.length; i++ ) {
            if( engineHolders[i].ContainsEngine( engine ) ) {
                try {
                    boolean isAutoRecycleRequired = GetAutoRecycleUsageCount() != 0
                            && GetAutoRecycleUsageCount() <= engineHolders[i].GetEngineUsageCount();

                    engineHolders[i].UnlockEngine();

                    if( isRecycleRequired || isAutoRecycleRequired ) {
                        engineHolders[i].UnloadEngine();
                        engineHolders[i] = new EngineHolder( projectId );
                    }
                } finally {
                    semaphore.release();
                }
                return;
            }
        }
        throw new Exception( "The engine haven't been found" );
    }

    public void unloadEngines() {
        if( engineHolders != null ) {
            for( int i = 0; i < engineHolders.length; i++ ) {
                engineHolders[i].UnloadEngine();
                engineHolders[i] = null;
            }
            engineHolders = null;
        }
    }
    // 线程进入等待
    private void waitActionEvent() throws Exception {
        synchronized( actionEvent ) {
            while( !isActionEventNotified ) {
                actionEvent.wait();
            }
            isActionEventNotified = false;
        }
    }
    // 唤醒线程
    private void notifyActionEvent() {
        synchronized( actionEvent ) {
            isActionEventNotified = true;
            actionEvent.notify();
        }
    }
    // 线程进入等待
    private void waitResponseEvent() throws Exception {
        synchronized( responseEvent ) {
            while( !isResponseEventNotified ) {
                responseEvent.wait();
            }
            isResponseEventNotified = false;
        }
    }
    // 唤醒线程
    private void notifyResponseEvent() {
        synchronized( responseEvent ) {
            isResponseEventNotified = true;
            responseEvent.notify();
        }
    }

    private Thread thread;
    private Exception threadException;
    private String projectId;
    private EngineHolder[] engineHolders;
    private int waitingTimeout;
    private Semaphore semaphore;
    private int autoRecycleUsageCount;
    private EngineAction action;
    private Object actionEvent;
    private long currentEngineHandle;
    private IEngine currentEngine;
    private Object responseEvent;
    private IEngine engineToRelease;
    private boolean isEngineRecycleRequired;
    private HashMap<IEngine, IEngine> enginesMap;
    private Object autoRecycleUsageCountLock;
    private boolean isActionEventNotified;
    private boolean isResponseEventNotified;
}
