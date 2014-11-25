package com.mcapanel.web.controllers;

import java.io.IOException;

import org.json.simple.JSONObject;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import com.mcapanel.web.database.Message;
import com.mcapanel.web.handlers.Controller;

public class ContactController extends Controller
{
	public boolean index()
	{
		return renderView();
	}
	
	@SuppressWarnings("unchecked")
	public boolean send() throws IOException
	{
		if (isMethod("POST"))
		{
			includeIndex(false);
			mimeType("application/json");
			
			JSONObject out = new JSONObject();
			
			String mcname = request.getParameter("mcname");
			String subject = request.getParameter("subject");
			String message = request.getParameter("message");
			
			String remoteAddr = request.getRemoteAddr();
	        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
	        reCaptcha.setPrivateKey("6Lef1NYSAAAAAGuSgXGbIxyRzDffKiSn-rhEb3xL");
	        
	        String challenge = request.getParameter("recaptcha_challenge_field");
	        String uresponse = request.getParameter("recaptcha_response_field");
	        
	        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);
	
	        if (reCaptchaResponse.isValid())
	        {
	        	Message msg = new Message(mcname, subject, message);
	        	
	        	ap.getDatabase().save(msg);
	        	bukkitServer.getPluginConnector().sendMethod("doMessage", mcname, subject, message);
	        	
	        	out.put("good", "Successfully sent your message!");
	        } else
	        {
	        	out.put("error", "Incorrect captcha entered.");
	        }
	        
        	response.getWriter().println(out.toJSONString());
	        
			return true;
		}
		
		return error();
	}
}