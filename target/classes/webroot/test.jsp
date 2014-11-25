<%@page import="com.mcapanel.panel.PluginSocket"%>
<%@page import="com.mcapanel.panel.AdminPanelWrapper"%>

<%
	PluginSocket pluginSocket = AdminPanelWrapper.getInstance().getPluginServer().getPluginSocket();
%>

<%= pluginSocket.connected() ? pluginSocket.callMethod("getVersion(BukkitVer)") : "Not connected to plugin..." %>