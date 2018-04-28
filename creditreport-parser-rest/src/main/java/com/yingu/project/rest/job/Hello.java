package com.yingu.project.rest.job;

import com.abbyy.FREngine.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Hello {

    public static void main(String[] args) {
        try {
            Hello application = new Hello();
            application.Run();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void Run() throws Exception {
        // Load ABBYY FineReader Engine
        loadEngine();
        try {
            // Process with ABBYY FineReader Engine
            processWithEngine();
        } finally {
            // Unload ABBYY FineReader Engine
            unloadEngine();
        }
    }

    private void loadEngine() throws Exception {
        log.info("Initializing Engine...");
        String DllFolder = "/opt/ABBYY/FREngine11/Bin";
        String DeveloperSN = "SWTT-1101-1006-4222-4068-3677";
        engine = Engine.GetEngineObject(DllFolder, DeveloperSN);
    }

    private void processWithEngine() {
        try {
            // Setup FREngine
            setupFREngine();

            // Process sample image
            processImage();
        } catch (Exception ex) {
            displayMessage(ex.getMessage());
        }
    }

    private void setupFREngine() {
        displayMessage("Loading predefined profile...");


        //选择必要的预定义配置
        //DocumentConversion_Accuracy  继续文档处理
        engine.LoadPredefinedProfile("DocumentConversion_Accuracy");
        //engine.LoadPredefinedProfile( "TextExtraction_Accuracy" );

        //engine.setMessagesLanguage(MessagesLanguageEnum.ML_ChinesePRC);
        // Possible profile names are:
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

    private void processImage() {
        String imagePath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\zhengxin000002.pdf";

        try {
            // Don't recognize PDF file with a textual content, just copy it
            //if( engine.IsPdfWithTextualContent( imagePath, null ) ) {
            //displayMessage( "Copy results..." );
            //String resultPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\test000003.pdf";
            //Files.copy( Paths.get( imagePath ), Paths.get( resultPath ), StandardCopyOption.REPLACE_EXISTING );
            //return;
            //}

            // Create document
            IFRDocument document = engine.CreateFRDocument();

            try {
                // Add image file to document
                displayMessage("Loading image...");
                document.AddImageFile(imagePath, null, null);

                // Process document
                displayMessage("Process...");
                //document.Process( null );
                IDocumentProcessingParams processParam = engine.CreateDocumentProcessingParams();
                processParam.getPageProcessingParams().getRecognizerParams().SetPredefinedTextLanguage("English,ChinesePRC");// 中英文
                document.Process(processParam);

                // Save results
                displayMessage("Saving results...");

                // Save results to rtf with default parameters
                String docxExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001.docx";
                document.Export(docxExportPath, FileExportFormatEnum.FEF_DOCX, null);

//                // Save results to rtf with default parameters
//                String rtfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001.rtf";
//                document.Export(rtfExportPath, FileExportFormatEnum.FEF_RTF, null);
//
//
//                String htmlExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001..html";
//                document.Export(htmlExportPath, FileExportFormatEnum.FEF_HTMLUnicodeDefaults, null);

                // Save results to pdf using 'balanced' scenario
                IPDFExportParams pdfParams = engine.CreatePDFExportParams();
                pdfParams.setScenario(PDFExportScenarioEnum.PES_MaxQuality);

                String pdfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\doc_4.pdf";
                //document.Export( pdfExportPath, FileExportFormatEnum.FEF_PDF, pdfParams );
            } finally {
                // Close document
                document.Close();
            }
        } catch (Exception ex) {
            displayMessage(ex.getMessage());
        }
    }

    private void processImage(String namePdf, String nameWord) {
        String imagePath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\zhengxin000002.pdf";

        try {
            // Don't recognize PDF file with a textual content, just copy it
            //if( engine.IsPdfWithTextualContent( imagePath, null ) ) {
            //displayMessage( "Copy results..." );
            //String resultPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\test000003.pdf";
            //Files.copy( Paths.get( imagePath ), Paths.get( resultPath ), StandardCopyOption.REPLACE_EXISTING );
            //return;
            //}

            // Create document
            IFRDocument document = engine.CreateFRDocument();

            try {
                // Add image file to document
                displayMessage("Loading image...");
                document.AddImageFile(imagePath, null, null);

                // Process document
                displayMessage("Process...");
                //document.Process( null );
                IDocumentProcessingParams processParam = engine.CreateDocumentProcessingParams();
                processParam.getPageProcessingParams().getRecognizerParams().SetPredefinedTextLanguage("English,ChinesePRC");// 中英文
                document.Process(processParam);

                // Save results
                displayMessage("Saving results...");

                // Save results to rtf with default parameters
                String docxExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001.docx";
                document.Export(docxExportPath, FileExportFormatEnum.FEF_DOCX, null);

//                // Save results to rtf with default parameters
//                String rtfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001.rtf";
//                document.Export(rtfExportPath, FileExportFormatEnum.FEF_RTF, null);
//
//
//                String htmlExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\pdf001..html";
//                document.Export(htmlExportPath, FileExportFormatEnum.FEF_HTMLUnicodeDefaults, null);

                // Save results to pdf using 'balanced' scenario
                IPDFExportParams pdfParams = engine.CreatePDFExportParams();
                pdfParams.setScenario(PDFExportScenarioEnum.PES_MaxQuality);

                String pdfExportPath = SamplesConfig.GetSamplesFolder() + "\\SampleImages\\doc_4.pdf";
                //document.Export( pdfExportPath, FileExportFormatEnum.FEF_PDF, pdfParams );
            } finally {
                // Close document
                document.Close();
            }
        } catch (Exception ex) {
            displayMessage(ex.getMessage());
        }
    }

    private void unloadEngine() throws Exception {
        displayMessage("Deinitializing Engine...");
        engine = null;
        Engine.DeinitializeEngine();
    }

    private static void displayMessage(String message) {
        System.out.println(message);
    }

    private IEngine engine = null;
}