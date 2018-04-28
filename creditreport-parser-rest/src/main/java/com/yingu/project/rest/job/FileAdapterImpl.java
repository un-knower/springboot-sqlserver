package com.yingu.project.rest.job;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

// Implementation of IFileAdapter interfaces.

import com.abbyy.FREngine.*;

public class FileAdapterImpl implements IFileAdapter {

    public FileAdapterImpl( String _fileName ) {
        fileName = _fileName;
    }

    // IFileAdapter methods implementation

    public String GetFileName() {
        return fileName;
    }

    public IIntsCollection GetPagesToProcess() {
        // Process all pages
        return null;
    }

    public String GetPassword() {
        return "";
    }

    private String fileName;
}
