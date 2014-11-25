package com.mcapanel.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.mcapanel.bukkit.BukkitServer;
import com.mcapanel.panel.AdminPanelWrapper;
import com.mcapanel.panel.ServerStatus;
import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.Backup;
import com.mcapanel.web.database.BackupSchedule;

public class BackupHandler
{
	private AdminPanelWrapper ap;
	
	private BukkitServer server;
	
	private File mainDir;
	private File backupsDir;
	
	public BackupHandler(BukkitServer server)
	{
		this.ap = AdminPanelWrapper.getInstance();
		this.server = server;
		
		mainDir = server.getServerJar().getParentFile();
		
		backupsDir = new File(mainDir, "backups");
		
		List<Backup> backups = ap.getDatabase().find(Backup.class).where().eq("server_id", server.getId()).findList();
		
		for (Backup b : backups)
		{
			File f = new File(backupsDir, b.getFilename());
			
			if (!f.exists())
			{
				ap.getDatabase().delete(b);
				
				println("Could not find backup, removing from db");
			}
		}
	}
	
	public void runSchedules()
	{
		List<BackupSchedule> scheds = ap.getDatabase().find(BackupSchedule.class).where().eq("server_id", server.getId()).findList();
		
		for (BackupSchedule bs : scheds)
		{
			BackupInterval.Interval i = BackupInterval.Interval.getFromString(bs.getIntervalString());
			
			long nextBackup = bs.getLastBackup() + (bs.getInterval() * i.getMult());
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm a");
			
			if (System.currentTimeMillis() >= nextBackup)
			{
				println("Backup '" + bs.getDescription() + "' is overdue, backing up");
				println("Supposed to be on " + sdf.format(new Date(nextBackup)));
				
				backupNow(bs);
				
				return;
			}
			
			scheduleBackup(bs);
		}
	}
	
	private void scheduleBackup(final BackupSchedule bsTemp)
	{
		BackupInterval.Interval i = BackupInterval.Interval.getFromString(bsTemp.getIntervalString());
		
		long nextBackup = bsTemp.getLastBackup() + (bsTemp.getInterval() * i.getMult());
		long backupTime = nextBackup - System.currentTimeMillis();
		
		backupTime = backupTime >= 0 ? backupTime : 0;
		
		new Timer().schedule(new TimerTask() {
			public void run()
			{
				BackupSchedule bs = ap.getDatabase().find(BackupSchedule.class, bsTemp.getId());
				
				if (bs != null)
				{
					backupNowRaw(bs);
				}
			}
		}, backupTime);
	}
	
	public void backupNow(final BackupSchedule schedule)
	{
		new Thread(new Runnable() {
			public void run()
			{
				backupNowRaw(schedule);
			}
		}).start();
	}
	
	public void backupNowRaw(BackupSchedule schedule)
	{
		println("Backing up " + schedule.getDescription());
		
		boolean backedUp = false;
		
		String filename = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_hh-mm-a");
		Date date = new Date();
		
		File file = null;
		
		if (schedule.isBackupEverything())
		{
			try
			{
				filename = "everything_" + sdf.format(date) + ".zip";
				
				Utils.zipDir(mainDir, file = new File(backupsDir, filename), new File[] { mainDir });
				
				backedUp = true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else if (schedule.isBackupPlugins())
		{
			try
			{
				filename = "plugins_" + sdf.format(date) + ".zip";
				
				Utils.zipDir(mainDir, file = new File(backupsDir, filename), new File[] { new File(mainDir, "plugins") });
				
				backedUp = true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else if (schedule.isBackupWorlds())
		{
			List<File> worlds = new ArrayList<File>();
			
			String[] split = schedule.getWorlds().split(";");
			
			String ws = "";
			
			for (String s : split)
			{
				if (!s.isEmpty())
				{
					worlds.add(new File(mainDir, s));
					
					ws += s + ",";
				}
			}
			
			try
			{
				filename = ws + sdf.format(date) + ".zip";
				
				Utils.zipDir(mainDir, file = new File(backupsDir, filename), worlds.toArray(new File[worlds.size()]));
				
				backedUp = true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (backedUp && file != null)
		{
			schedule.setLastBackup(date.getTime());
			
			Backup backup = new Backup(schedule.getId(), server.getId(), schedule.getDescription(), filename, file.length());
			
			ap.getDatabase().save(backup);
			ap.getDatabase().update(schedule);
			
			println("Successfully backed up " + schedule.getDescription());
		} else
			println("Could not backup " + schedule.getDescription());
		
		scheduleBackup(schedule);
	}
	
	public void restoreBackup(final Backup backup)
	{
		new Thread(new Runnable() {
			public void run()
			{
				boolean stoppedServer = false;
				
				println("Restoring backup " + backup.getDescription());
				
				File backupFile = new File(backupsDir, backup.getFilename());
				
				if (backupFile.exists())
				{
					if (server.getStatus() != ServerStatus.STOPPED)
					{
						server.stopServerRaw(false);
						
						stoppedServer = true;
					}
					
					if (server.getStatus() == ServerStatus.STOPPED)
					{
						try
						{
							BackupSchedule bs = ap.getDatabase().find(BackupSchedule.class, backup.getSchedule());
							
							if (bs != null)
							{
								println("Backing up now, just in case...");
								
								backupNowRaw(bs);
							}
							
							Utils.unzipFile(backupFile, mainDir);
							
							println("Successfully restored " + backup.getDescription());
						} catch (IOException e)
						{
							e.printStackTrace();
						}
					} else
						println("Invalid server state for restoring backup, please try again");
					
					if (stoppedServer)
					{
						server.startServer();
					}
				} else
					println("Could not find backup " + backup.getDescription() + ", aborting restore");
			}
		}).start();
	}
	
	public boolean deleteBackup(Backup backup)
	{
		File backupFile = new File(backupsDir, backup.getFilename());
		
		if (backupFile.exists())
		{
			boolean deleted = backupFile.delete();
			
			if (deleted)
			{
				println("Deleted backup " + backup.getFilename());
				
				return true;
			}
		}
		
		return false;
	}
	
	private void println(String line)
	{
		System.out.println("[Backup] " + line);
	}
}