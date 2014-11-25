package com.mcapanel.web.controllers;

import com.mcapanel.web.handlers.Controller;

public class ErrorController extends Controller
{
	public void index()
	{
		request.setAttribute("error", "404");
	}
}