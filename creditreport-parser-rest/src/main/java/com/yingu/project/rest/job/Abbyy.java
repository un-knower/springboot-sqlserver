package com.yingu.project.rest.job;

import com.abbyy.FREngine.*;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class Abbyy {
    private void processImage(File pdf) {
        String namePdf = pdf.getName();
        log.info("processImage:" + namePdf);
        String name = StringUtils.substringBeforeLast(namePdf, ".");
        byte[] bytes = {};
        try {
            InputStream stream = new FileInputStream(pdf);
            bytes = toByteArray(stream);
            stream.close();
        } catch (Exception ex) {
            log.error("processImage.InputStream", ex);
        }
        if (bytes == null || bytes.length == 0) {
            log.info("processImage.bytes is empty:" + namePdf);
            return;
        }
        try {
            IFRDocument document = engine.CreateFRDocument();
            try {
                document.AddImageFileFromMemory(bytes, null, null, null, "");
                IDocumentProcessingParams processParam = engine.CreateDocumentProcessingParams();
                processParam.getPageProcessingParams().getRecognizerParams().SetPredefinedTextLanguage("English,ChinesePRC");// 中英文
                document.Process(processParam);
                String docxExportPath = apiConfig.getCreditword_path() + "\\" + name + ".docx";
                document.Export(docxExportPath, FileExportFormatEnum.FEF_DOCX, null);
                Boolean moveSucess = FileUtil.fileMoveTo(pdf, apiConfig.getCreditpdf_success(), namePdf);
                log.info(String.format("processImage.move:{0},{1}", namePdf, moveSucess));
            } finally {
                document.Close();
            }
        } catch (Exception ex) {
            log.error("processImage.AddImage", ex);
        }
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    @Autowired
    ApiConfig apiConfig;

    public void Run(File[] pdfs) throws Exception {
        loadEngine();
        try {
            processWithEngine(pdfs);
        } finally {
            unloadEngine();
        }
    }

    private void loadEngine() throws Exception {
        String DllFolder = apiConfig.getAbbyy_dll_folder();
        String DeveloperSN = apiConfig.getAbbyy_serial_number();
        log.info(String.format("loadEngine,%s,%s", DllFolder, DeveloperSN));
        engine = Engine.GetEngineObject(DllFolder, DeveloperSN);
    }

    private void processWithEngine(File[] pdfs) {
        try {
            setupFREngine();
            for (int i = 0; i < pdfs.length; i++) {
                processImage(pdfs[i]);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void setupFREngine() {
        log.info("setupFREngine");
        engine.LoadPredefinedProfile("DocumentConversion_Accuracy");
    }

    private void unloadEngine() throws Exception {
        log.info("unloadEngine");
        engine = null;
        Engine.DeinitializeEngine();
    }

    private IEngine engine = null;
}
