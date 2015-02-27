package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.mcapanel.utils.Utils;
import com.mcapanel.web.database.PasswordReset;
import com.mcapanel.web.database.User;
import com.mcapanel.web.handlers.Controller;

public class PasswordController extends Controller
{
	public boolean canView()
	{
		return !isLoggedIn();
	}
	
	public boolean index()
	{
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	public boolean reset() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			if (!isLoggedIn())
			{
				String mcname = request.getParameter("mcname");
				String seccode = request.getParameter("seccode");
				
				String password = request.getParameter("password");
				String confpassword = request.getParameter("confpassword");
				
				if (!mcname.isEmpty() && !seccode.isEmpty() && !password.isEmpty() && !confpassword.isEmpty())
				{
					if (password.equals(confpassword))
					{
						PasswordReset pr = db.find(PasswordReset.class).where().ieq("username", mcname).eq("reset_code", seccode).findUnique();
						
						if (pr != null)
						{
							User us = db.find(User.class).where().ieq("username", mcname).findUnique();
							
							us.setPassHash(Utils.md5(password + Utils.md5(us.getPassSalt())));
							
							db.save(us);
							db.delete(pr);
							
							out.put("success", "Successfully updated your password!");
						} else
							out.put("error", "The username or reset code you entered was invalid.");
					} else
						out.put("error", "The passwords you entered do not match.");
				} else
					out.put("error", "Please fill out all of the fields.");
			} else
				out.put("error", "You already know your password.");
			
			response.getWriter().println(out.toJSONString());
			
			return true;
		}
		
		return error();
	}
}