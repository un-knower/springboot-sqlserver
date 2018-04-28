package com.yingu.project.rest.job;
// (c) 2013 ABBYY Production LLC
// SAMPLES code is property of ABBYY, exclusive rights are reserved.
//
// DEVELOPER is allowed to incorporate SAMPLES into his own APPLICATION and modify it under
// the  terms of  License Agreement between  ABBYY and DEVELOPER.


// ABBYY FineReader Engine 11 Sample

// This sample shows how to create batch processor and use it for recognition.

import com.abbyy.FREngine.*;
import java.io.*;

public class BatchProcessing {

    public static void main( String[] args ) {
        try {
            BatchProcessing application = new BatchProcessing();
            application.Run();
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
    }

    public static String CombinePaths( String path1, String path2 ) {
        File file1 = new File( path1 );
        File file2 = new File( file1, path2 );
        return file2.getPath();
    }

    public void Run() throws Exception {
        // Load ABBYY FineReader Engine
        // 加载ABBYY FineReader光学文字识别 引擎
        loadEngine();
        try{
            // Process with ABBYY FineReader Engine
            // 使用ABBYY FineReader引擎进行处理
            processWithEngine();
        } finally {
            // Unload ABBYY FineReader Engine
            // 卸载ABBYY FineReader引擎    释放
            unloadEngine();
        }
    }

    private void loadEngine() throws Exception {
        displayMessage( "Initializing Engine..." );
        engine = Engine.GetEngineObject( SamplesConfig.GetDllFolder(), SamplesConfig.GetDeveloperSN() );
    }

    private void processWithEngine() {
        try {
            // Setup FREngine
            // 安装FREngine
            setupFREngine();

            // Batch processing
            batchProcessing();
        } catch( Exception ex ) {
            displayMessage( ex.getMessage() );
        }
    }

    private void setupFREngine() {
        displayMessage( "Loading predefined profile..." );
        engine.LoadPredefinedProfile( "DocumentConversion_Accuracy" );
        // 可能的配置文件名称  Possible profile names are:
        //   "DocumentConversion_Accuracy", "DocumentConversion_Speed",
        //   "DocumentArchiving_Accuracy", "DocumentArchiving_Speed",
        //   "BookArchiving_Accuracy", "BookArchiving_Speed",
        //   "TextExtraction_Accuracy", "TextExtraction_Speed",
        //   "FieldLevelRecognition",
        //   "BarcodeRecognition_Accuracy", "BarcodeRecognition_Speed",
        //   "HighCompressedImageOnlyPdf",
        //   "BusinessCardsProcessing",
        //   "EngineeringDrawingsProcessing",
        //   "Version9Compatibility",
        //   "Default"
    }

    private void batchProcessing() throws Exception {
        displayMessage( "Batch processing..." );
        //多处理识别文件夹路径   /*"SampleImages\\MultiProcessingRecognition"*/
        String sourceFolder = CombinePaths( SamplesConfig.GetSamplesFolder(), "SampleImages" );
        //得到处理结果路径
        String resultFolder = CombinePaths( SamplesConfig.GetSamplesFolder(), "SampleImages\\ExportResults" );

        // Check source folder existence
        // 检查源文件夹的存在
        if( !isDirectoryExist( sourceFolder ) ) {
            throw new Exception( "Cannot find " + sourceFolder );
        }
        // Create result folder if it doesn't exist
        // 创建结果文件夹，如果它不存在   存在不创建
        createDirectory( resultFolder );

        // Create ImageSourceImpl for accessing to images files in source folder
        // 创建图片迭代器 以访问源文件夹中的图片文件
        ImageSourceImpl imageSource = new ImageSourceImpl( sourceFolder );
        if( imageSource.IsEmpty() ) {
            throw new Exception( "No images in specified folder." );
        }
        //创建批量处理器
        IBatchProcessor batchProcessor = engine.CreateBatchProcessor();

        // Start batch processor for specified image source
        //  开始批处理
        batchProcessor.Start( imageSource, null, null, null, null );

        // Obtain recognized pages and export them to RTF format
        // 获取认可的页面并将其导出为RTF格式
        IFRPage page = batchProcessor.GetNextProcessedPage();
        while( page != null ) {
            // Synthesize page before export
            // 导出前合成页面
            page.Synthesize( null );

            // Export page to file with the same name and pdf extension
            // 将页面导出为具有相同名称和pdf扩展名的文件
            String resultFilePath = CombinePaths( resultFolder, new File( page.getSourceImagePath() ).getName() + ".pdf" );
            // 导出
            page.Export( resultFilePath, FileExportFormatEnum.FEF_PDF, null );
            // 得到下一个页面
            page = batchProcessor.GetNextProcessedPage();
            // 回收垃圾
            System.gc();
        }
    }

    private void unloadEngine() throws Exception {
        displayMessage( "Deinitializing Engine..." );
        engine = null;
        Engine.DeinitializeEngine();
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
        //exists() 测试此抽象路径名定义的文件或目录是否存在
        if( !file.exists() ) {
            //  创建内级目录   成功可以返回 true 失败可以返回false
            file.mkdir();
        }
    }

    private IEngine engine = null;
}
