package com.yingu.project.rest.job;

public class SamplesConfig {
    // Folder with FRE dll
    // 与FRE DLL的文件夹
    public static String GetDllFolder() {
        if( is64BitJVMArchitecture() ) {
            return "C:\\Program Files\\ABBYY SDK\\11\\FineReader Engine\\Bin64";
        } else {
            return "Directory\\where\\x86\\dll\\resides";
        }
    }

    // Return developer serial number for FRE
    //免费返回开发者序列号
    public static String GetDeveloperSN() {
        return "SWTT11010006421097369005";
    }

    // Return full path to Samples directory
    //返回Samples目录的完整路径
    public static String GetSamplesFolder() {
        return "C:\\Program Files\\ABBYY SDK\\11\\FineReader Engine\\Samples";
    }

    // Determines whether the JVM architecture is a 64-bit architecture
    private static boolean is64BitJVMArchitecture()
    {
        String jvmKeys [] = {
                "sun.arch.data.model",
                "com.ibm.vm.bitmode",
                "os.arch"
        };
        for( String key : jvmKeys ) {
            String property = System.getProperty( key );
            if( property != null ) {
                if( property.indexOf( "64" ) >= 0 ) {
                    return true;
                } else if( property.indexOf( "32" ) >= 0 ) {
                    return false;
                } else if( property.indexOf( "86" ) >= 0 ) {
                    return false;
                }
            }
        }
        return false;
    }


}
