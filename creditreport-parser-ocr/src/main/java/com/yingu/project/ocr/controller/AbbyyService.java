package com.yingu.project.ocr.controller;

import com.abbyy.FREngine.*;
import com.yingu.project.util.config.ApiConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AbbyyService {
    public byte[] processImage(byte[] bytesImport) throws Exception {
        if (bytesImport == null || bytesImport.length == 0) {
            throw new IllegalArgumentException();
        }
        try {
            IFRDocument document = engine.CreateFRDocument();
            try {
                document.AddImageFileFromMemory(bytesImport, null, null, null, "");
                document.Process(processParam);
                FileWriterAbbyy fileWriter = new FileWriterAbbyy();
                document.ExportToMemory(fileWriter, FileExportFormatEnum.FEF_DOCX, null);
                byte[] bytesExport = fileWriter.getBytes();
                return bytesExport;
            } finally {
                document.Close();
            }
        } catch (Exception ex) {
            log.error("processImage.AddImage", ex);
            throw ex;
        }
    }

    private void loadEngine() throws Exception {
        String DllFolder = apiConfig.getAbbyy_dll_folder();
        String DeveloperSN = apiConfig.getAbbyy_serial_number();
        log.info(String.format("loadEngine,%s,%s", DllFolder, DeveloperSN));
        engine = Engine.GetEngineObject(DllFolder, DeveloperSN);
    }

    private void setupFREngine() {
        log.info("setupFREngine");
        engine.LoadPredefinedProfile("DocumentConversion_Accuracy");

        processParam = engine.CreateDocumentProcessingParams();
        processParam.getPageProcessingParams().getRecognizerParams().SetPredefinedTextLanguage("English,ChinesePRC");// 中英文
    }

    public void initialize() {
        try {
            loadEngine();
            setupFREngine();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void unloadEngine() throws Exception {
        log.info("unloadEngine");
        engine = null;
        Engine.DeinitializeEngine();
    }

    private IDocumentProcessingParams processParam;
    private IEngine engine = null;
    @Autowired
    ApiConfig apiConfig;
}
