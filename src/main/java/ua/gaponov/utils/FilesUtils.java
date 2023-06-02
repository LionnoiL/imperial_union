package ua.gaponov.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class FilesUtils {

    public static void saveTextFile(String filePath, String text) {
        checkFileDirAndCreateDir(filePath);
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(text);
        } catch (IOException e) {
            Logger.getLogger(FilesUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void checkFileDirAndCreateDir(String filePath) {
        File dir = new File(new File(filePath).getParent());
        if (!dir.exists() && !dir.mkdirs()) {
            Logger.getLogger(FilesUtils.class.getName()).log(Level.SEVERE, "Error create dir = {}", dir.getAbsolutePath());
        }
    }

    public static void deleteFile(String fileName) throws IOException {
        File file = new File(fileName);
        Files.delete(file.toPath());
    }

    public static boolean fileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static FileInputStream getFileInputStream(String fileName)
            throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            FilesUtils.checkFileDirAndCreateDir(fileName);
            FilesUtils.saveTextFile(fileName, "");
        }
        return new FileInputStream(file);
    }

    public static List<String> getFilesLines(String fileName) {
        List<String> result = new ArrayList<>();
        try {
            result = Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            log.error("Error read file {}", fileName);
        }
        return result;
    }
}
