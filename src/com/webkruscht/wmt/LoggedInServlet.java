package com.webkruscht.wmt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

/**
 * Servlet implementation class LoggedInServlet
 */
@WebServlet("/LoggedIn")
public class LoggedInServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoggedInServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String token = AuthSubUtil.getTokenFromReply(request.getQueryString());
			String sessionToken = AuthSubUtil.exchangeForSessionToken(token, null);
			response.getOutputStream().println("Logged In: " + sessionToken);
		} catch (Exception e) {
			// TODO proper error Handling
			throw new ServletException(e);
		}
	}

}
