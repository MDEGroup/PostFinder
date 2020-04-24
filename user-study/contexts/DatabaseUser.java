package com.dumiduh;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DatabaseUser {
private Connection connectionObj;
private Statement statementObj;
private ResultSet resultSet;
private final String dbString = "jdbc:mysql://localhost:3306/sakila";
private final String userName = "root";
private final String password = "sql@1234";
private String SQLString ="SELECT * FROM film LIMIT 0,20";

	public void run()
	{
		try
		{
			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			connectionObj = DriverManager.getConnection(dbString,"root","sql@1234");
			statementObj = connectionObj.createStatement();
			resultSet = statementObj.executeQuery(SQLString);
			
			while(resultSet.next())
			{
				System.out.print("Name of Film :"+resultSet.getString("title")+"\t\t Released Year :"+resultSet.getDate("release_year"));
				System.out.println("");
			}
			
}}}