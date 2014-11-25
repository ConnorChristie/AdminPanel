package com.mcapanel.bukkit.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

public class UUIDFetcher
{
	private static final double PROFILES_PER_REQUEST = 100;
	private static final String NAME_PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private static final String UUID_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	
	private JSONParser jsonParser = new JSONParser();
	
	private List<String> names = new ArrayList<String>();
	private List<UUID> uuids = new ArrayList<UUID>();
	
	private Map<String, UUID> playerToUUID = new HashMap<String, UUID>();
	private Map<UUID, String> uuidToPlayer = new HashMap<UUID, String>();
	
	public UUIDFetcher()
	{
		System.setProperty("http.agent", "McAdminPanel-Minecraft");
	}
	
	public UUIDFetcher setNames(List<String> names)
	{
		this.names = names;
		
		return this;
	}
	
	public UUIDFetcher setUUIDs(List<UUID> uuids)
	{
		this.uuids = uuids;
		
		return this;
	}
	
	public void runNames()
	{
		try
		{
			int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
			
			for (int i = 0; i < requests; i++)
			{
				HttpURLConnection connection = createConnection();
				
				String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
				
				writeBody(connection, body);
				
				JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
				
				for (Object profile : array)
				{
					JSONObject jsonProfile = (JSONObject) profile;
					String id = (String) jsonProfile.get("id");
					String name = (String) jsonProfile.get("name");
					
					UUID uuid = getUUID(id);
					
					playerToUUID.put(name, uuid);
					uuidToPlayer.put(uuid, name);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		names.clear();
	}
	
	public void runUUIDs()
	{
		try
		{
			for (UUID uuid : uuids)
			{
				HttpURLConnection connection = (HttpURLConnection) new URL(UUID_PROFILE_URL + uuid.toString().replace("-", "")).openConnection();
				JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
				
				String name = (String) response.get("name");
				
				if (name == null) continue;
				
				String cause = (String) response.get("cause");
				String errorMessage = (String) response.get("errorMessage");
				
				if (cause != null && cause.length() > 0)
				{
					throw new IllegalStateException(errorMessage);
				}
				
				playerToUUID.put(name, uuid);
				uuidToPlayer.put(uuid, name);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		uuids.clear();
	}
	
	private void writeBody(HttpURLConnection connection, String body) throws Exception
	{
		OutputStream stream = connection.getOutputStream();
		
		stream.write(body.getBytes());
		stream.flush();
		stream.close();
	}
	
	private HttpURLConnection createConnection() throws Exception
	{
		URL url = new URL(NAME_PROFILE_URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		
		return connection;
	}
	
	private UUID getUUID(String id)
	{
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
	}
	
	public byte[] toBytes(UUID uuid)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		
		return byteBuffer.array();
	}
	
	public UUID fromBytes(byte[] array)
	{
		if (array.length != 16)
		{
			throw new IllegalArgumentException("Illegal byte array length: " + array.length);
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		
		long mostSignificant = byteBuffer.getLong();
		long leastSignificant = byteBuffer.getLong();
		
		return new UUID(mostSignificant, leastSignificant);
	}
	
	public UUID getUUIDOf(String name)
	{
		if (!playerToUUID.containsKey(name))
		{
			names.add(name);
			
			runNames();
		}
		
		return playerToUUID.get(name);
	}
	
	public String getNameOf(UUID uuid)
	{
		if (!uuidToPlayer.containsKey(uuid))
		{
			uuids.add(uuid);
			
			runUUIDs();
		}
		
		return uuidToPlayer.get(uuid);
	}
}