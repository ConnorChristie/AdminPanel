package com.mcapanel.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mcapanel.panel.AdminPanelWrapper;

import net.java.truelicense.obfuscate.ObfuscatedString;

public class ErrorHandler
{
	private AdminPanelWrapper fg;
	
	private final ObfuscatedString r = new ObfuscatedString(new long[] {7454155637591388362L, -5068243388882427171L});
	private final ObfuscatedString i = new ObfuscatedString(new long[] {-5902880985992291733L, -7759495589696902982L});
	private final ObfuscatedString s = new ObfuscatedString(new long[] {-7919322956634818094L, -7105713148411355349L, 8843054321854713275L});
	private final ObfuscatedString j = new ObfuscatedString(new long[] {-4547448912488374741L, -4620380974885930861L, 5513430613636552785L});
	private final ObfuscatedString v = new ObfuscatedString(new long[] {-7234663213394954967L, -1205641069827615805L, 7538256371896819258L, 8084463411839463574L, 8580805411995875718L, -7004656770141352461L, 7115656004602442724L});
	private final ObfuscatedString w = new ObfuscatedString(new long[] {-8010524991601924168L, 2224851650013108355L});
	private final ObfuscatedString b = new ObfuscatedString(new long[] {2020123703946477237L, -8290971582649862242L});
	private final ObfuscatedString q = new ObfuscatedString(new long[] {-247140708290822382L, 6701078725417091147L});
	private final ObfuscatedString x = new ObfuscatedString(new long[] {8188192167265707197L, -5605277365997954992L});
	private final ObfuscatedString t = new ObfuscatedString(new long[] {5915991352329617607L, 556650394089241282L});
	private final ObfuscatedString c = new ObfuscatedString(new long[] {2544962845210870744L, 2481070349889371400L});
	private final ObfuscatedString k = new ObfuscatedString(new long[] {9155818419018198390L, -4955169058200578894L, 6068168706804651113L, -7002821465930990123L});
	private final ObfuscatedString m = new ObfuscatedString(new long[] {-8712797265917642042L, -336556830873465436L, -3974707958780934879L, -8378170709055609043L, 1298477908172791935L, -3466093537221664473L});
	
	private List<String> d = new ArrayList<String>();
	
	private ObfuscatedString n = new ObfuscatedString(new long[] {-2804317692800104372L, 3887522317911798335L, 3569453180151613815L});
	public ObfuscatedString y = new ObfuscatedString(new long[] {4598716380055762801L, -7421863810360652642L, 4201262455076741066L, -8640654728372473473L, 7945159169788265031L, -235413661312918289L, 6775047811939728914L});
	
	private boolean e = false;
	public boolean df = false;
	public boolean cd = false;
	
	public ErrorHandler()
	{
		fg = AdminPanelWrapper.getInstance();
		AdminPanelWrapper.VERSION_SUB = n.toString();
		
		try
		{
			Enumeration<NetworkInterface> x = NetworkInterface.getNetworkInterfaces();
			
			while (x.hasMoreElements())
			{
				NetworkInterface l = x.nextElement();
				
				byte[] uv = l.getHardwareAddress();
				
				if (uv != null && uv.length > 2)
				{
					StringBuilder sb = new StringBuilder();
					
					for (int i = 0; i < uv.length; i++)
					{
						sb.append(String.format(r.toString(), uv[i], (i < uv.length - 1) ? "-" : ""));		
					}
					
					String hz = sb.toString();
					
					if (!hz.startsWith(i.toString()))
					{
						d.add(hz);
					}
				}
			}
		} catch (SocketException e) { }
		
		if (d.size() == 0)
		{
			System.out.println(m.toString());
			
			System.exit(-1);
		}
		
		new Timer().schedule(new TimerTask() {
			public void run()
			{
				x();
			}
		}, 0, 10 * 1000);
	}
	
	public boolean c()
	{
		return e && (n.toString() == new ObfuscatedString(new long[] {6677069183889372067L, 7989341239267295730L, 8483524979655261576L}).toString()
				  || n.toString() == new ObfuscatedString(new long[] {-8864765397578575554L, -2159176568316323287L}).toString());
	}
	
	public boolean a()
	{
		return e;
	}
	
	private void x()
	{
		String cv = fg.getConfig().getString(s.toString(), "");
		String gr = fg.getConfig().getString(j.toString(), "");
		
		String df = "";
		
		for (int i = 0; i < d.size(); i++)
		{
			df += (i != 0 ? "," : "") + Utils.md5(d.get(i).toLowerCase());
		}
		
		e("a=" + Utils.md5((cv + gr).toLowerCase()) + "&b=" + df + "&c=" + Utils.md5(AdminPanelWrapper.VERSION));//a.toString().replace("%1", cv).replace("%2", gr).replace("%3", df).replace("%4", AdminPanelWrapper.VERSION));
	}
	
	private void e(String af)
	{
		try
		{
			URLConnection kx = new URL(v.toString()).openConnection();
			
			kx.setDoOutput(true);
			kx.setDoInput(true);
			
			OutputStreamWriter qd = new OutputStreamWriter(kx.getOutputStream());
			
			qd.write(af);
			qd.flush();
			
			BufferedReader yx = new BufferedReader(new InputStreamReader(kx.getInputStream()));
			
			String lx = yx.readLine();
			
			if (lx != null)
			{
				JSONObject pg = (JSONObject) new JSONParser().parse(lx);
				
				if (pg.containsKey("v") && v(pg.get("v")).toString().equals(x.toString()))
				{
					cd = true;
				}
				
				if (pg != null && pg.containsKey(w.toString()) && pg.containsKey(b.toString()))
				{
					ObfuscatedString un = v(pg.get(b.toString()));
					ObfuscatedString lf = v(pg.get(w.toString()));
					
					if (lf.toString().equals(q.toString()))
					{
						if (un.toString().equals(k.toString()) && pg.containsKey(c.toString()))
						{
							g(v(pg.get(c.toString())));
							e = true;
						}
					} else if (lf.toString().equals(t.toString()))
					{
						g(new ObfuscatedString(new long[] {3483695443042285192L, 667759735061725359L, -2913240090991343774L}));
						e = false;
					}
				} else
					throw new Exception();
			} else
				throw new Exception();
			
			qd.close();
			yx.close();
		} catch (Exception e1)
		{
			g(new ObfuscatedString(new long[] {3483695443042285192L, 667759735061725359L, -2913240090991343774L}));
			e = false;
		}
	}
	
	private ObfuscatedString v(Object rb)
	{
		JSONArray qg = (JSONArray) rb;
		
		long[] nc = new long[qg.size()];
		
		for (int i = 0; i < qg.size(); i++)
		{
			nc[i] = (Long) qg.get(i);
		}
		
		return new ObfuscatedString(nc);
	}
	
	private void g(ObfuscatedString s)
	{
		AdminPanelWrapper.VERSION_SUB = (n = s).toString();
	}
}