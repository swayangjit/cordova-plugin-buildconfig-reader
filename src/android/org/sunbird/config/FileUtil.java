package org.sunbird.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 * Created by swayangjit on 17/3/19.
 */
public class FileUtil {
    public static boolean rm(File fileOrDirectory, String skippDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!skippDirectory.equals(child.getName())) {
                        rm(child, skippDirectory);
                    }
                }
            }
        }

        return fileOrDirectory.delete();
    }

    public static void write(String path, String fileName, String data) throws IOException {
        File manifestFile = new File(path, fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(manifestFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(data);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
