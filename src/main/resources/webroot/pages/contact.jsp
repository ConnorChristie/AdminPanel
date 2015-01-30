<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="net.tanesha.recaptcha.ReCaptcha"%>
<%@page import="java.util.Properties"%>
<%@page import="net.tanesha.recaptcha.ReCaptchaFactory"%>

<script src="/js/viewjs/contact.js"></script>

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title">
			${language.localize("Contact Us")}
		</h3>
	</div>
	<div class="panel-body">
		<form id="contactform" method="post" action="/contact/send">
			<c:if test="${!loggedIn}">
				<div class="form-group">
					<label for="mcname">${language.localize("Minecraft Username")}</label>
					<input type="text" class="form-control" style="width: 380px;" id="mcname" name="mcname" placeholder="${language.localize('Enter Username')}" required>
				</div>
			</c:if>
			
			<c:if test="${loggedIn}">
				<input type="hidden" name="mcname" value="${user.getUsername()}" />
			</c:if>
			
			<div class="form-group">
				<label for="subject">${language.localize("Subject")}</label>
				<input type="text" class="form-control" style="width: 380px;" id="subject" name="subject" placeholder="${language.localize('Enter Subject')}" required>
			</div>
			
			<div class="form-group">
				<label for="message">${language.localize("Message")}</label>
				<textarea class="form-control" style="width: 380px; height: 100px;" id="message" name="message" placeholder="${language.localize('Enter Message')}" required></textarea>
			</div>
			
			<div class="form-group">
				<%
				ReCaptcha c = ReCaptchaFactory.newReCaptcha("6Lef1NYSAAAAAJKl-kM3Tlnw9pK6ewnsTe5krVQM", "6Lef1NYSAAAAAGuSgXGbIxyRzDffKiSn-rhEb3xL", false);
				Properties props = new Properties();
				
				props.put("theme", "white");
				%>
				
				<%= c.createRecaptchaHtml(null, props) %>
			</div>
						
			<p style="width: 700px; font-size: 15px; margin-top: 10px;">${language.localize("Please note that a response will not be instant, please allow up to 3 days for a response.")}</p>
			
			<p><button type="submit" id="submitapp" class="btn btn-primary btn-lg">${language.localize("Send Message")}</button></p>
		</form>
	</div>
</div>