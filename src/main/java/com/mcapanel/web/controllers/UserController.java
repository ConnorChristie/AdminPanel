package com.mcapanel.web.controllers;

import java.io.IOException;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;

import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.Application;
import com.mcapanel.web.database.Group;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class UserController extends Controller
{
	@SuppressWarnings("unchecked")
	public void login() throws IOException
	{
		if (isMethod("POST"))
		{
			JSONObject obj = new JSONObject();
			
			includeIndex(false);
			mimeType("application/json");
			
			User u = db.find(User.class).where().ieq("username", request.getParameter("username")).findUnique();
			
			if (u != null)
			{
				String saltedPass = Utils.md5(request.getParameter("password") + Utils.md5(u.getPassSalt()));
				
				if (u.getPassHash().equals(saltedPass))
				{
					String ip = request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr();
					
					u.setIpAddress(ip);
					
					db.save(u);
					
					loginUser(u);
					
					obj.put("good", "good");
				} else
					obj.put("error", "The password that you entered is incorrect.<br />If you have forgotten your password you can reset it <a href='/password/'>here</a>.");
			} else
				obj.put("error", "There doesn't seem to be a user with that username.");
			
			response.getWriter().println(obj.toJSONString());
		} else
			error();
	}
	
	public void logout() throws IOException
	{
		request.getSession().setAttribute("loggedIn", false);
		request.getSession().removeAttribute("userId");
		
		response.sendRedirect("/");
	}
	
	@SuppressWarnings({ "unchecked" })
	public boolean whitelist() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			String mcname = request.getParameter("mcname");
			String mcpass = request.getParameter("mcpass");
			String mcpassconf = request.getParameter("mcpassconf");
			String mcdesc = request.getParameter("mcdesc");
			
			String remoteAddr = request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr();
	        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
	        reCaptcha.setPrivateKey("6Lef1NYSAAAAAGuSgXGbIxyRzDffKiSn-rhEb3xL");
	        
	        String challenge = request.getParameter("recaptcha_challenge_field");
	        String uresponse = request.getParameter("recaptcha_response_field");
	        
	        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

	        if (reCaptchaResponse.isValid())
	        {
				if (mcpass.equals(mcpassconf))
				{
					User u = db.find(User.class).where().ieq("username", mcname).findUnique();
					
					if (u == null)
					{
						u = new User(mcname, mcpass, RandomStringUtils.randomAlphanumeric(8), remoteAddr);
						
						boolean createApp = false;
						
						if (mcname.equalsIgnoreCase("ChillerCraft"))
						{
							u.setGroupId(db.find(Group.class).where().ieq("group_name", "Admin").findUnique().getId());
							u.setWhitelisted(true);
						} else
						{
							u.setGroupId(db.find(Group.class).where().eq("is_whitelist_default", true).findUnique().getId());
							
							createApp = true;
						}
						
						db.save(u);
						
						loginUser(u);
						
						if (createApp)
						{
							Application a = new Application(u.getId(), mcdesc);
							
							db.save(a);
							
							bukkitServer.getPluginConnector().sendMethod("doAppNotice",
									"" + a.getId(),
									ap.getDatabase().find(User.class, a.getUserId()).getUsername(),
									a.getDescription());
							
							out.put("good", "Your whitelist application has successfully been submitted!<br />You will now be able to join the server but you will be a ghost until your application has been approved.");
						} else
							out.put("good", "Your whitelist application has been approved!");
					} else
					{
						if (!u.isWhitelisted() && !u.isBlacklisted())
							out.put("error", "It appears that you have already submitted an application!<br />No need to submit another one.");
						else if (u.isWhitelisted())
							out.put("error", "It appears that you are already whitelisted!<br />No need to submit another application.");
						else
							out.put("error", "It appears that your application has been denied.<br />You are not allowed to re-apply, sorry.");
					}
				} else
					out.put("error", "The passwords that you entered do not appear to match.");
	        } else
				out.put("error", "The captcha that you entered is incorrect.");
			
	        response.getWriter().println(out.toJSONString());
	        
			return true;
		}
		
		return error();
	}
}