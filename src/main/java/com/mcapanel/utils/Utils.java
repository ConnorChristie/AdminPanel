package com.mcapanel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class Utils
{
	public static String md5(String text)
	{
		String md5 = "";
		
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			byte[] array = messageDigest.digest(text.getBytes());
			
			for (int i = 0; i < array.length; ++i)
			{
				md5 += Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3);
			}
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		
		return md5;
	}
	
	public static void zipDir(File mainDir, File zipFile, File[] dir) throws Exception
	{
		zipFile.getParentFile().mkdirs();
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
		for (File f : dir)
		{
			addDir(mainDir, f, out);
		}
		
		out.close();
	}
	
	public static void addDir(File mainDir, File dirObj, ZipOutputStream out) throws IOException
	{
		File[] files = dirObj.listFiles();
		byte[] tmpBuf = new byte[1024];
		
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isDirectory())
			{
				if (!files[i].getName().equals("backups") && !files[i].getName().equals("logs") && !files[i].getName().equals("target") && !files[i].getName().equals("McAdminPanel"))
					addDir(mainDir, files[i], out);
				
				continue;
			}
			
			if (!files[i].exists())
				continue;
			
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			
			out.putNextEntry(new ZipEntry(files[i].getAbsolutePath().replace(mainDir.getAbsolutePath().replace(".", "") + File.separator, "")));
			
			int len;
			while ((len = in.read(tmpBuf)) > 0)
			{
				out.write(tmpBuf, 0, len);
			}
			
			out.closeEntry();
			in.close();
		}
	}
	
	public static void unzipFile(File zipFile, File dir) throws IOException
	{
		ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
		
		ZipEntry ze = in.getNextEntry();
		
		byte[] buffer = new byte[1024];
		
		while (ze != null)
		{
			File f = new File(dir, ze.getName());
			
			if (f.isDirectory() && f.exists())
			{
				FileUtils.deleteDirectory(f);
			} else if (f.exists())
			{
				f.delete();
			}
			
			FileOutputStream fos = new FileOutputStream(f);
			
			int len;
			
			while ((len = in.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}
			
			fos.close();
			
			ze = in.getNextEntry();
		}
		
		in.closeEntry();
		in.close();
	}
	
	public static JarFile jarForClass(Class<?> clazz, JarFile defaultJar)
	{
		String path = "/" + clazz.getName().replace('.', '/') + ".class";
		URL jarUrl = clazz.getResource(path);
		if (jarUrl == null)
		{
			return defaultJar;
		}
		
		String url = jarUrl.toString();
		int bang = url.indexOf("!");
		String JAR_URI_PREFIX = "jar:file:";
		if (url.startsWith(JAR_URI_PREFIX) && bang != -1)
		{
			try
			{
				return new JarFile(url.substring(JAR_URI_PREFIX.length(), bang));
			} catch (IOException e)
			{
				throw new IllegalStateException("Error loading jar file.", e);
			}
		} else
		{
			return defaultJar;
		}
	}
	
	/**
	 * Copies a directory from a jar file to an external directory.
	 */
	public static void copyResourcesToDirectory(JarFile fromJar, String jarDir, String destDir) throws IOException
	{
		for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements();)
		{
			JarEntry entry = entries.nextElement();
			
			//System.out.println("");
			
			if (entry.getName().startsWith(jarDir + "/") && !entry.isDirectory())
			{
				File dest = new File(destDir + "/" + entry.getName().substring(jarDir.length() + 1));
				File parent = dest.getParentFile();
				if (parent != null)
				{
					parent.mkdirs();
				}
				
				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = fromJar.getInputStream(entry);
				
				try
				{
					byte[] buffer = new byte[8 * 1024];
					
					int s = 0;
					while ((s = in.read(buffer)) > 0)
					{
						out.write(buffer, 0, s);
					}
				} catch (IOException e)
				{
					throw new IOException("Could not copy asset from jar file", e);
				} finally
				{
					try
					{
						in.close();
					} catch (IOException ignored)
					{
					}
					try
					{
						out.close();
					} catch (IOException ignored)
					{
					}
				}
			}
		}
		
	}
}