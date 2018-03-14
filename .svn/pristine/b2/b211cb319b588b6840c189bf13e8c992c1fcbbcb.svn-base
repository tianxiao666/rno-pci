package com.hgicreate.rno.ltestrucanlsclient.tool;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipFileHandler {
	private static final int buffer = 1024;

	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (Objects.equals(subDirectory, "") && !fl.exists()) {
				fl.mkdir();
			} else if (!Objects.equals(subDirectory, "")) {
				dir = subDirectory.replace('\\', '/').split("/");
				for (String aDir : dir) {
					File subFile = new File(directory + File.separator + aDir);
					if (!subFile.exists()) {
						subFile.mkdir();
					}
					directory += File.separator + aDir;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean unZip(String zipFilePath, String outputDirectory) {
		if (new File(zipFilePath).exists()) {
			return false;
		}
		boolean flag = false;
		try (ZipFile zipFile = new ZipFile(zipFilePath, Charset.forName("GBK"))){
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			createDirectory(outputDirectory, "");
			while (e.hasMoreElements()) {
				ZipEntry zipEntry = e.nextElement();
				if (zipEntry.isDirectory()) {
					String name = new String(zipEntry.getName().getBytes("gbk"), "utf-8").trim();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					if (!f.exists()) {
						f.mkdir();
					}

				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					if (fileName.contains("/")) {
						createDirectory(outputDirectory, fileName.substring(0, fileName.lastIndexOf("/")));
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
		}
		return flag;
	}

    public static void zip(String targetPath, String resourcesPath) {
	    File resourcesFile = new File(resourcesPath);
	    File targetFile = new File(targetPath,resourcesFile.getName()+".zip");
        zip(targetFile,resourcesFile);
    }

	/**
	 * 将源文件/文件夹生成指定格式的压缩文件,格式zip
	 */
	public static void zip(File targetFile, File... resources) {
		if (!targetFile.exists()) {
			targetFile.getParentFile().mkdirs();
		} else if (!targetFile.delete()) {
			return;
		}
		try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)))) {
			add2ZipFile(out, null, resources);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void add2ZipFile(ZipOutputStream out, String dir, File... files) throws IOException {
		//如果当前的是文件夹，则进行进一步处理
		if (files != null) {
			for (File file : files) {
				String subFile = dir == null ? file.getName() : dir + file.getName();
				if (file.isDirectory()) {
					String subDir = subFile + "/";
					//将文件夹添加到下一级打包目录
					out.putNextEntry(new ZipEntry(subDir));
					// 递归处理,将文件夹中的文件打包,不用再做非空判断。
					add2ZipFile(out, subDir, file.listFiles());
				} else {
					//当前的是文件，打包处理
					out.putNextEntry(new ZipEntry(subFile));
					//文件输入流
					try (FileInputStream fis = new FileInputStream(file)) {
						//进行写操作
						byte[] bf = new byte[buffer];
						int j;
						while ((j = fis.read(bf)) > 0) {
							out.write(bf, 0, j);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
	
}
