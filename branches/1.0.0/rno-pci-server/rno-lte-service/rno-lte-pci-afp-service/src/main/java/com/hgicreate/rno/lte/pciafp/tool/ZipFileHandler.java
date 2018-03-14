package com.hgicreate.rno.lte.pciafp.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileHandler {
    private static final int zipLevel = 7;

    public static boolean unZip(String zipFilePath, String outputDirectory) {
        if (!Files.exists(Paths.get(zipFilePath))) {
            return false;
        }
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(Paths.get(outputDirectory, zipEntry.getName()));
                } else {
                    Path path = Paths.get(outputDirectory, zipEntry.getName());
                    Files.createDirectories(path.getParent());
                    Files.copy(zipFile.getInputStream(zipEntry), path, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static String add2Zip(List<String> sourceFileList, String zipFileName) {
        if (sourceFileList == null || sourceFileList.size() < 1) {
            return "";
        }
        Path sourceFile = Paths.get(sourceFileList.get(0));
        if (Files.isDirectory(sourceFile)) {
            return "";
        } else {
            Path zipFile;
            try {
                zipFile = Paths.get(sourceFile.getParent().toString(), zipFileName);
                if (Files.exists(zipFile)) {
                    int i = 1;
                    while (true) {
                        zipFile = Paths.get(sourceFile.getParent().toString(), zipFileName.substring(0, zipFileName.lastIndexOf(".zip")) + i + ".zip");
                        if (!Files.exists(zipFile)) {
                            break;
                        }
                        i++;
                    }
                }
                Files.createDirectories(zipFile.getParent());
                ZipOutputStream jos = new ZipOutputStream(Files.newOutputStream(zipFile));
                jos.setLevel(zipLevel);
                for (String aSourceFileList : sourceFileList) {
                    stZip(jos, Paths.get(aSourceFileList));
                }
                jos.finish();
            } catch (Exception e) {
                return "";
            }
            if (Files.exists(zipFile)) {
                return zipFile.toAbsolutePath().toString();
            } else {
                return "";
            }
        }
    }

    private static void stZip(ZipOutputStream jos, Path file) {
        ZipEntry sourEntry = new ZipEntry(file.getFileName().toString());
        try {
            jos.putNextEntry(sourEntry);
            Files.copy(file, jos);
            jos.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
