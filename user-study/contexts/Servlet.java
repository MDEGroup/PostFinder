package com.test.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class TestDataPlaceHolder
 */
/**
 * @author preetham
 *
 */
public class TestDataPlaceHolderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDataPlaceHolderServlet() {
        super();
        System.out.println("TestDataPlaceHolderServlet servlet constructor called");
    }

    /**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println("TestDataPlaceHolderServlet \"Init\" method called");
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		System.out.println("TestDataPlaceHolderServlet \"Destroy\" method called");
	}


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
    	System.out.println("TestDataPlaceHolderServlet doGet method called");
    	doPost(request,response);
    	
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
}