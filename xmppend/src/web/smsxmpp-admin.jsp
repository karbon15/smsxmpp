<%@ page import="java.io.IOException,
                 java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.ParamUtils,
                 org.jivesoftware.util.JiveGlobals,
                 name.theberge.smsxmpp.xmppserver.User,
                 name.theberge.smsxmpp.xmppserver.AuthorizationManager"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%
    boolean saveSettings = request.getParameter("saveSettings") != null;
	boolean manageUser = request.getParameter("manageUser") != null;
	boolean addUser = request.getParameter("addUser") != null;
	boolean update = request.getParameter("update") != null;
	boolean delete = request.getParameter("delete") != null;
    boolean success = false;
    
    String message = "";
    
    Map<String, String> errors = new HashMap<String, String>();
    if (saveSettings) {
    	//TODO: Validation
    	JiveGlobals.setProperty("smsxmpp.subdomain", request.getParameter("subdomain"));
    	JiveGlobals.setProperty("smsxmpp.inboundq", request.getParameter("inboundq"));
    	JiveGlobals.setProperty("smsxmpp.outboundq", request.getParameter("outboundq"));
    	success = true;
    	message = "Settings saved successfully";
    }	
    else if(manageUser)	{
    	if(delete)	{
    		AuthorizationManager.getInstance().deleteUser(Integer.parseInt(request.getParameter("id")));
    		success = true;
        	message = "User deleted successfully";
    	}	else if(update)	{
    		AuthorizationManager.getInstance().updateUser(Integer.parseInt(request.getParameter("id")),request.getParameter("jid"), request.getParameter("number"));
    		success = true;
        	message = "User updated successfully";
    	}
    }	
    else if(addUser)	{
    	AuthorizationManager.getInstance().createUser(request.getParameter("jid"), request.getParameter("number"));
    	success = true;
    	message = "User created successfully";
    }
    
    String subdomain = JiveGlobals.getProperty("smsxmpp.subdomain");
    String inboundq = JiveGlobals.getProperty("smsxmpp.inboundq");
    String outboundq = JiveGlobals.getProperty("smsxmpp.outboundq");
%>

<html>
    <head>
        <title>SMSXMPP Settings</title>
        <meta name="pageID" content="smsxmpp-settings"/>
    </head>
    <body>

<% if (errors.size() > 0) { %>

    <div class="jive-error">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr>
            <td class="jive-icon"><img src="images/error-16x16.gif" width="16" height="16" border="0"></td>
            <td class="jive-icon-label">Something went wrong!</td>
        </tr>
    </tbody>
    </table>
    </div>
    <br>

<% } else if (success) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr>
            <td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
            <td class="jive-icon-label">Settings saved successfully</td>
        </tr>
    </tbody>
    </table>
    </div>
    <br>
    
<% } %>

<form action="smsxmpp-admin.jsp?saveSettings" method="post">

<div class="jive-contentBoxHeader">General Settings</div>
<div class="jive-contentBox">
    <p>
    These are settings specific to this instance of SMSXMPP
    </p>
    
    <table cellpadding="3" cellspacing="0" border="0" width="100%">
    <tbody>
        <tr>
            <td width="200"><input type="text" name="subdomain" value="<%= subdomain %>"  id="subdomain"></td>
            <td><label for="subdomain">Enter the subdomain you want for your instance, without the domain part (ex: sms for sms.example.org)</label></td>
        </tr>
        <tr>
        	<td width="200"><input type="text" name="inboundq" value="<%= inboundq %>"  id="inboundq"></td>
        	<td><label for="inboundq">Entter the name of the inbound queue</label></td>
        </tr>
        <tr>
    		<td width="200"><input type="text" name="outboundq" value="<%= outboundq %>"  id="outboundq"></td>
    		<td><label for="outboundq">Entter the name of the outbound queue</label></td>
    	</tr>
    </tbody>
    </table>
    <input type="submit" value="Save">
</div>
</form>

<div class="jive-contentBoxHeader">Authorized Users</div>
<div class="jive-contentBox">
    <p>
    Here is a confgurable list of the JID that will be authorized and their phone number mapping
    </p>
    <table cellpadding="3" cellspacing="0" border="0" width="100%">
    <thead><th width="200">JID</th><th  width="200">Phone number</th><th></th><th></th></thead>
    <tbody>
    <% for (User u : AuthorizationManager.getInstance().getAllUsers()) { %>
        <tr>
        	<form action="smsxmpp-admin.jsp?manageUser" method="post">
            	<td><input type="hidden" name="id" value="<%= u.getId() %>"><input type="text" name="jid" value="<%= u.getJid() %>" id="jid"></td>
            	<td><input type="text" name="number" value="<%= u.getNumber() %>" id="number"></td>
            	<td width="80"><input type="submit" name="update" value="Update" id="update"></td>
            	<td width="80"><input type="submit" name="delete" value="Delete" id="delete"></td>
            </form>
        </tr>
    <% } %>
    </tbody>
    </table>
    <p>Add authorized JID</p>
    <form action="smsxmpp-admin.jsp?addUser" method="post">
	    JID: <input type="text" name="jid" id="jid">
	    Phone Number: <input type="text" name="number" id="number">
	    <input type="submit" name="add" value="Add" id="add">
    </form>
</div>
</form>

</body>
</html>