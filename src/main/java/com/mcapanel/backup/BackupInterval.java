package com.mcapanel.backup;

public class BackupInterval
{
	private Interval i;
	private int n;
	
	private BackupInterval(Interval i, int n)
	{
		this.i = i;
		this.n = n;
	}
	
	public int getInterval()
	{
		return n * i.getMult();
	}
	
	public static BackupInterval EVERY_N_MINUTES(int n)
	{
		return new BackupInterval(Interval.EVERY_N_MINUTES, n);
	}
	
	public static BackupInterval EVERY_N_HOURS(int n)
	{
		return new BackupInterval(Interval.EVERY_N_HOURS, n);
	}
	
	public static BackupInterval EVERY_N_DAYS(int n)
	{
		return new BackupInterval(Interval.EVERY_N_DAYS, n);
	}
	
	public static String toText(String nText, int n)
	{
		Interval i = Interval.getFromString(nText);
		
		return i.getText(n);
	}
	
	public enum Interval
	{
		EVERY_N_MINUTES("Every %n Minute(s)", 1000 * 60),
		EVERY_N_HOURS("Every %n Hour(s)", 1000 * 60 * 60),
		EVERY_N_DAYS("Every %n Day(s)", 1000 * 60 * 60 * 60);
		
		private String text;
		
		private int mult;
		
		private Interval(String text, int mult)
		{
			this.text = text;
			this.mult = mult;
		}
		
		public String getText(int n)
		{
			return text.replace("%n", "" + n);
		}
		
		public int getMult()
		{
			return mult;
		}
		
		public static Interval getFromString(String txt)
		{
			Interval i = EVERY_N_MINUTES;
			
			if (txt.equalsIgnoreCase("minutes"))
				i = EVERY_N_MINUTES;
			else if (txt.equalsIgnoreCase("hours"))
				i = EVERY_N_HOURS;
			else if (txt.equalsIgnoreCase("days"))
				i = EVERY_N_DAYS;
			
			return i;
		}
	}
}