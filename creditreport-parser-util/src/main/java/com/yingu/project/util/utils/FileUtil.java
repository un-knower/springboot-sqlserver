package com.yingu.project.util.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;

/**
 * @Date: Created in 2018/3/26 10:48
 * @Author: wm
 */
@Slf4j
public class FileUtil {
    public static byte[] fileToByte(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 移动文件
     * @param sourceFile
     * @param targetFilePath
     * @param targetFileName
     * @return
     */
    public static Boolean fileMoveTo(File sourceFile,String targetFilePath,String targetFileName){
        try {
            File targetFile = new File(targetFilePath, targetFileName);
            if (targetFile.exists()) {
                int index = targetFileName.lastIndexOf(".");
                String name = targetFileName.substring(0, index);
                String extendName = targetFileName.substring(index);
                int i = 0;
                while (true) {
                    String newTargetFileName = MessageFormat.format("{0}({1}){2}", name, i, extendName);
                    targetFile = new File(targetFilePath,newTargetFileName);
                    if (!targetFile.exists()) {
                        break;
                    }
                    i++;
                }
            }
            boolean isSuccess = sourceFile.renameTo(targetFile);
            return isSuccess;
        }
        catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return false;
        }
    }
}
