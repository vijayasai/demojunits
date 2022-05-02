package com.externalize.mock.utils;

import java.io.*;
import java.util.regex.Pattern;

/**
 * This util class is to read file and write file to local path
 */
public class FileUtil {
    private static String validFileNameRegEx = "^[a-zA-Z0-9._-]+$";
    public static String readFile(String filename) throws Exception{
        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        BufferedReader buf = null;
        try {
            is = new FileInputStream(filename);
            buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
        }finally {
            if(is!=null){
                is.close();
            }
            if(buf!=null){
                buf.close();
            }
        }
        String fileAsString = sb.toString();
        return fileAsString;
    }

    public static boolean isFilenameValid(String filename) {
        Pattern pattern = Pattern.compile(validFileNameRegEx);
        return pattern.matcher(filename).matches();
    }

    public static void writeFile(String content, String filename) throws Exception{
        FileWriter fileWriter = new FileWriter(filename);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(content);
        printWriter.close();
    }
}