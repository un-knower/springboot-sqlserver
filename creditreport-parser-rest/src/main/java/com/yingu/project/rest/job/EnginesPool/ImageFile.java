package com.yingu.project.rest.job.EnginesPool;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

import com.abbyy.FREngine.*;

public class ImageFile {

    public ImageFile( String filePath ) {
        FilePath = filePath;
        FailedRunsCount = 0;
    }

    public String FilePath;
    public int FailedRunsCount;
}
