package com.mcapanel.web.controllers;

import com.mcapanel.web.handlers.Controller;

public class DynmapController extends Controller
{
	public boolean canView()
	{
		return bukkitServer.hasDynmap();
	}
	
	public boolean index()
	{
		return renderView();
	}
}