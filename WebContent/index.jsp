<%@page import="com.databloks.ormgen.GenDashboard"%>
<%@page import="java.io.PrintWriter" %>
<%
	PrintWriter pw = new PrintWriter(out);
	GenDashboard genDashboard = new GenDashboard();
	genDashboard.loadDashboard(pw);
%>