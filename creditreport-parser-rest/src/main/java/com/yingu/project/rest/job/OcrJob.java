package com.yingu.project.rest.job;

import com.yingu.project.api.AbbyyDto;
import com.yingu.project.rest.rpc.ExampleService;
import com.yingu.project.rest.rpc.OcrService;
import com.yingu.project.util.config.ApiConfig;
import com.yingu.project.util.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class OcrJob {
    @Autowired
    ApiConfig apiConfig;
    @Autowired
    OcrService ocrService;


    @Autowired
    ExampleService example;

    @Scheduled(fixedDelay = 1000)
    public void test() {
        log.info("test.start");
        try {
            String ss = example.sayHiFromClientOne("sj");
            String bb = ss;
        } catch (Exception ex) {
            log.error("test:" + ex.getMessage(), ex);
        }
        log.info("test.end");
    }

    /**
     * 识别Pdf
     */
//    @Scheduled(fixedDelay = 10000)//上一次执行完毕后10秒再次执行；
    public void recognizePdf() {
        File directoryPdf = new File(apiConfig.getCreditpdf_source());
        if (!directoryPdf.exists() || !directoryPdf.isDirectory())
            return;
        File[] pdfs = directoryPdf.listFiles();
        log.info("directoryPdf.pdfsCount:" + pdfs.length);
        if (pdfs.length > 0) {
            for (int i = 0; i < pdfs.length; i++) {
                File pdf = pdfs[i];
                String namePdf = pdf.getName();
                log.info("directoryPdf:" + namePdf);
                String name = StringUtils.substringBeforeLast(namePdf, ".");
                byte[] bytesImport = null;
                try {
                    InputStream stream = new FileInputStream(pdf);
                    bytesImport = toByteArray(stream);
                    stream.close();
                } catch (Exception ex) {
                    log.error("directoryPdf.InputStream", ex);
                }
                if (bytesImport == null || bytesImport.length == 0) {
                    log.info("directoryPdf.bytes is empty:" + namePdf);
                    continue;
                }

                AbbyyDto aImport = new AbbyyDto();
                aImport.setName(name);
                aImport.setBytes(bytesImport);
                try {
                    AbbyyDto aExport = ocrService.process(aImport);
                    if (aExport.getCode() == 200) {
                        String wordName = name + ".docx";
                        byte2File(aExport.getBytes(), apiConfig.getCreditword_path(), wordName);

                        Boolean moveSucess = FileUtil.fileMoveTo(pdf, apiConfig.getCreditpdf_success(), namePdf);
                        log.info(String.format("directoryPdf.move:{0},{1}", namePdf, moveSucess));
                    }
                } catch (Exception ex) {
                    log.error("directoryPdf:" + ex.getMessage(), ex);
                }
            }
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

    /**
     * 根据byte数组，生成文件
     *
     * @param bfile    文件数组
     * @param filePath 文件存放路径
     * @param fileName 文件名称
     */
    public static void byte2File(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
