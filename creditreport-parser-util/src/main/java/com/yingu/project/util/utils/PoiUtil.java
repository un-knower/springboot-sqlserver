package com.yingu.project.util.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * poi工具类
 */
@Slf4j
public class PoiUtil {
    /**
     * 读取word
     * @param filePath
     * @return
     */
    public static XWPFDocument readWord(String filePath) throws Exception{
        XWPFDocument xdoc=null;
        FileInputStream fis=null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            xdoc = new XWPFDocument(fis);
        }
        catch (Exception e) {
            log.error("readWord error,filePath:"+filePath);
            throw e;
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (Exception e){
                log.error("readWord close error,filePath:"+filePath);
                throw e;
            }
        }
        return xdoc;
    }
}
