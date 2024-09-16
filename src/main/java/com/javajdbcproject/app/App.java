package com.javajdbcproject.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

/*
 * Tacker to develop a jav-jdbc app
 * 1. import the package
 * 2. load and register the database drivers
 * 3. create a connection
 * 4. create a statement that will run the query on the database
 * 5. execute statement
 * 6. close the statement and connection
 */

//import the pages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.jdbc.ScriptRunner;

//Creating a record to store data as a List<Object>
record Student(int id, String fullName, int age){
	public int getId() {
		return id;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public int getAge() {
		return age;
	}
}


public class App 
{	
    public static void main(String[] args)
    {    
    	String url = "jdbc:mysql://localhost:3306/tables";
    	String userName = "root";
    	String password = "root";
    	
    	System.out.println("**************JDBC Connection****************");
    	
    	/*
    	 * Mysql | drop the table if exists
    	 */
    	String query0 = "drop table if exists Student;";
    	System.out.println("Mysql | drop the table if exists");
    	try{
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		Connection con = DriverManager.getConnection(url, userName, password);
    		Statement statement = con.createStatement();
    		boolean flag = statement.execute(query0);
    		System.out.println("Dropped table: "+ !flag);
    		statement.close();
    		con.close();
    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | create the table
    	 */
    	System.out.println();
    	String query1=
    			"""
				CREATE TABLE Student (id int PRIMARY KEY,
						    full_name varchar(255) not null,
						    age int default 0
						);
    			""";
    	System.out.println("Mysql | create the table");
    	try(Connection con = DriverManager.getConnection(url, userName, password);
    			PreparedStatement preparedStatemnet = con.prepareStatement(query1)){
    		boolean flag = preparedStatemnet.execute();
    		System.out.println("Created table: "+ !flag);
    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | Execute the db.sql
    	 */
    	System.out.println();
    	System.out.println("Mysql | Execute the db.sql");
    	try {
    		Connection con = DriverManager.getConnection(url, userName, password);
    		ScriptRunner scriptRunner = new ScriptRunner(con);
    		Reader reader = new BufferedReader(new FileReader("./db.sql"));
    		scriptRunner.runScript(reader);	
    		reader.close();
    		con.close();
		} catch (Exception exception) {
			System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | select all records
    	 */
    	String query2 = "select * from Student;";
    	System.out.println("Mysql | select all records");
    	try(Connection con = DriverManager.getConnection(url, userName, password);
    			PreparedStatement preparedStatemnet = con.prepareStatement(query2);
    			ResultSet resultantSet = preparedStatemnet.executeQuery()){
    		while (resultantSet.next()) {
    			System.out.println(String.format("id: %d full_name: %s age: %d", resultantSet.getInt("id"),resultantSet.getString("full_name"),resultantSet.getInt("age")));				
			}
    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | select a record by id
    	 */
    	System.out.println();
    	String query3 = "select * from Student where id = ?;";
    	System.out.println("Mysql | select a record by id");
    	try(Connection con = DriverManager.getConnection(url, userName, password);
    			PreparedStatement preparedStatemnet = con.prepareStatement(query3)
    			){
    		preparedStatemnet.setInt(1, 1);
			ResultSet resultantSet = preparedStatemnet.executeQuery();
    		while (resultantSet.next()) {
    			System.out.println(String.format("id: %d full_name: %s age: %d", resultantSet.getInt("id"),resultantSet.getString("full_name"),resultantSet.getInt("age")));				
			}
    		resultantSet.close();
    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | Insert the records in Batch
    	 */
    	System.out.println();
    	String query4 = "insert student(id,full_name,age) values (?,?,?);";
    	System.out.println("Mysql | Insert the records in Batch");
    	try(Connection con = DriverManager.getConnection(url, userName, password);
    			PreparedStatement preparedStatemnet = con.prepareStatement(query4)
    			){
    		
    		List<Student> studentList= List.of(new Student(3,"CC",30),new Student(4, "DD", 40), new Student(5, "EE", 50));
    		
    		for(Student s: studentList) {
    			preparedStatemnet.setInt(1, s.getId());
    			preparedStatemnet.setString(2, s.getFullName());
    			preparedStatemnet.setInt(3, s.getAge());
    			preparedStatemnet.addBatch();
    		}
    		
			int[] batchArray = preparedStatemnet.executeBatch();
    		
			for(int i: batchArray) {
    			System.out.println(i);
    		}
    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    	
    	/*
    	 * Mysql | update a record by id
    	 */
    	System.out.println();
    	String query5 = "update student set full_name = ? , age = ? where id = ?";
    	System.out.println("Mysql | update a record by id");
    	try(Connection con = DriverManager.getConnection(url, userName, password);
    			PreparedStatement preparedStatemnet = con.prepareStatement(query5)
    			){
    		preparedStatemnet.setString(1, "QQ");
    		preparedStatemnet.setInt(2, 50);
    		preparedStatemnet.setInt(3, 3);
    	    int flag = preparedStatemnet.executeUpdate();
    	    System.out.println(flag);

    	}catch (Exception exception) {
    		System.out.println(exception.getLocalizedMessage());
    		exception.printStackTrace();
		}
    }
}
