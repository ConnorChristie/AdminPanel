package com.mcapanel.web.controllers;

import com.mcapanel.web.handlers.Controller;

public class ElementController extends Controller
{
	public boolean index()
	{
		includeIndex(false);
		mimeType("text/html");
		
		return renderView("/elements/" + arguments.get(0) + ".jsp");
	}
}