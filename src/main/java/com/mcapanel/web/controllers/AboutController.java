package com.mcapanel.web.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.mcapanel.web.handlers.Controller;

public class AboutController extends Controller
{
	public boolean index() throws FileNotFoundException, IOException
	{
		request.setAttribute("aboutpage", IOUtils.toString(new FileReader(new File("McAdminPanel", "webpages/aboutpage.html"))));
		
		return renderView();
	}
}