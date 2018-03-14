package com.iscreate.rno.microservice.pci.afp.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileHandler {
	private static final int buffer = 1024;
	private static int ziplevel = 7;
	private static File sourceFile = null;
	private static File zipFile = null;
	private static ZipOutputStream jos = null;
	private static ZipEntry sourEntry = null;
	private static byte[] buf = new byte[1024];

	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (subDirectory == "" && fl.exists() != true) {
				fl.mkdir();
			} else if (subDirectory != "") {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (subFile.exists() == false) {
						subFile.mkdir();
					}
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean unZip(String zipFilePath, String outputDirectory) {
		boolean flag = false;
		ZipFile zipFile = null;
		try {
			if (new File(zipFilePath).exists()) {
				zipFile = new ZipFile(zipFilePath, Charset.forName("GBK"));
			}
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			ZipEntry zipEntry = null;
			createDirectory(outputDirectory, "");
			while (e.hasMoreElements()) {
				zipEntry = e.nextElement();
				if (zipEntry.isDirectory()) {
					String name = new String(zipEntry.getName().getBytes("gbk"), "utf-8").trim();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					if (!f.exists()) {
						f.mkdir();
					}

				} else {
					String fileName = new String(zipEntry.getName());
					fileName = fileName.replace('\\', '/');
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0, fileName.lastIndexOf("/")));
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
					}
					File f = new File(outputDirectory + File.separator + zipEntry.getName());
					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);
					byte[] by = new byte[buffer];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					in.close();
					out.close();
				}
			}
			flag = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static String AddtoZip(List<String> sourcefilelist, String zipFileName) {
		if (sourcefilelist == null || sourcefilelist.size() < 1) {
			return null;
		}
		sourceFile = new File(sourcefilelist.get(0).toString());
		if (!sourceFile.isFile()) {
			return null;
		} else {
			// sourceFileName = sourceFile.getName();
			// this.setZipfileName(sourceFileName.substring(0,sourceFileName.lastIndexOf("."))+".zip");

			try {
				zipFile = new File(sourceFile.getParent(), zipFileName);

				if (zipFile.exists()) {
					int i = 1;
					while (true) {
						zipFile = new File(sourceFile.getParent(),
								zipFileName.substring(0, zipFileName.lastIndexOf(".zip")) + i + ".zip");
						if (!zipFile.exists())
							break;
						i++;
					}
				}
				// System.out.println(zipFile.getPath());
				if (zipFile.exists()) {
					zipFile.deleteOnExit();
				}
				zipFile.createNewFile();
				jos = new ZipOutputStream(new FileOutputStream(zipFile));
				jos.setLevel(ziplevel);
				for (int i = 0; i < sourcefilelist.size(); i++) {

					Stzip(jos, new File(sourcefilelist.get(i).toString()));
				}
				jos.finish();
			} catch (Exception e) {
				return null;
			}
			if (zipFile != null)
				return zipFile.getPath();
			else {
				return null;
			}
		}
	}

	private static void Stzip(ZipOutputStream jos, File file) throws IOException, FileNotFoundException {

		sourEntry = new ZipEntry(file.getName());
		FileInputStream fin = new FileInputStream(file);
		BufferedInputStream in = new BufferedInputStream(fin);
		jos.putNextEntry(sourEntry);
		int len;
		while ((len = in.read(buf)) >= 0)
			jos.write(buf, 0, len);
		in.close();
		jos.closeEntry();

	}
}
