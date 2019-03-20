package org.sunbird.config;

import java.io.File;

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
}
