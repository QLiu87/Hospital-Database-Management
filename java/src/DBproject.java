/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
//scanner, date
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

/*
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		
		ResultSet rs = stmt.executeQuery (query);
		//System.out.print("After executing the qeuery");
		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		//System.out.print("Before printing");
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static int test_add_doc_id(DBproject esql, int id) {
		String query_exist = "Select * FROM Doctor WHERE doctor_ID = ";
		query_exist += String.valueOf(id);
		int res_int = id;
		try {
			List<List<String>> res = esql.executeQueryAndReturnResult(query_exist);
			List<List<String>> res1 = esql.executeQueryAndReturnResult("SELECT MAX(doctor_ID) FROM Doctor");
			if(res.size() > 0)
				res_int = Integer.parseInt(res1.get(0).get(0)) + 1;
		} catch(Exception e) {
			System.out.print(e);
		}
		return res_int;
	}

	public static int test_add_appnt_id(DBproject esql, int id) {
		String query_exist = "Select * FROM Appointment WHERE appnt_ID = ";
		query_exist += String.valueOf(id);
		int res_int = id;
		try {
			List<List<String>> res = esql.executeQueryAndReturnResult(query_exist);
			List<List<String>> res1 = esql.executeQueryAndReturnResult("SELECT MAX(appnt_ID) FROM appointment");
			if(res.size() > 0)
				res_int = Integer.parseInt(res1.get(0).get(0)) + 1;
		} catch(Exception e) {
			System.out.print(e);
		}
		return res_int;
	}

	public static void AddDoctor(DBproject esql) {//1
		//doctor ID, name, hid
		Scanner sc = new Scanner(System.in);
		int doc_id;
		String doc_name = "";
		String specialty = "";
		int did;
		String query = "INSERT INTO Doctor (doctor_ID, name, specialty, did) ";
		do {
			System.out.print("Input Doctor's ID:");
			while (!sc.hasNextInt()) {
				System.out.println("That's not a number!");
				sc.next(); 
			}
			int temp_doc_id = sc.nextInt();
			doc_id = test_add_doc_id(esql, temp_doc_id);
			if(doc_id != temp_doc_id){
				System.out.print("Your input doctor is already in use, the next available id (" + String.valueOf(doc_id) +") is assigned to you.\n");
			}
		} while (doc_id <= 0);
		
		do {
			try {
				System.out.print("Input Doctor's Name:");
				doc_name = in.readLine();
			} catch (Exception e) {
				System.out.print("Your Input for doctor's Name is incorrect!");
			}
		} while (doc_name.length() > 128 || doc_name.length() == 0);

		do {
			System.out.print("Input Doctor's Specialty:");
			try {
				specialty = in.readLine();
			} catch (Exception e) {
				System.out.print("Your Input for doctor's Specialty is incorrect!");
			}
		} while (specialty.length() > 24 || specialty.length() == 0);

		do {
			System.out.print("Input Doctor's Department ID:");
			while (!sc.hasNextInt()) {
				System.out.println("That's not a number!");
				sc.next();
			}
			did = sc.nextInt();
		} while (did <= 0);
		
		try {
			query += "VALUES (" + String.valueOf(doc_id) + ", '" + doc_name + "', '" + specialty + "', " + String.valueOf(did) + ")";
            esql.executeUpdate(query);
			String query2 = "select * from Doctor where doctor_ID = " + String.valueOf(doc_id);
			int rowcount = esql.executeQueryAndPrintResult(query2);
			System.out.println(rowcount);
			try {
				System.in.read();
			} catch (Exception e) {
				System.out.print("error at the pause");
			}
		} catch (Exception e) {
			System.out.println("Error adding a doctor!");
			System.err.println (e.getMessage());
		}
		System.out.print("New doctor added!\n");
		
		return;
	}

	public static void AddPatient(DBproject esql) {//2
	}

	public static void AddAppointment(DBproject esql) {//3
		//appnt_ID INTEGER,	adate DATE,time_slot VARCHAR(11), status _STATUS
		Scanner sc = new Scanner(System.in);
		int appnt_id;
		String adate = "";
		String temp_date_input = "";
		boolean date_check = false;
		boolean hour_check = false;
		String appnt_time = "";
		final String DATE_FORMAT = "MM-dd-yyyy";
		String appnt_status = "";
		String query = "";
		boolean status_check = false;
		
		try{
			//get aid
			query += "INSERT INTO Appointment VALUES (";
			do {
				System.out.print("Input Your Appointment's ID:");
				while (!sc.hasNextInt()) {
					System.out.println("That's not a number!");
					sc.next(); 
				}
				int temp_appnt_id = sc.nextInt();
				appnt_id = test_add_appnt_id(esql, temp_appnt_id);
				if(appnt_id != temp_appnt_id){
					System.out.print("Your input appointment ID is already in use, the next available id (" + String.valueOf(appnt_id) +") is assigned to you.\n");
				}
			} while (appnt_id <= 0);	

			//get adate
			do {
				try {
					System.out.print("Input Your Appointment's date in (MM-DD-YYYY) format:");
					temp_date_input = in.readLine();
					//System.out.print(temp_date_input);
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
					df.setLenient(false);
					Date date = df.parse(temp_date_input);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					if(temp_date_input.matches("^\\d+\\-\\d+\\-\\d+")){
						adate = temp_date_input.substring(6) + '-' +  temp_date_input.substring(0,2) + '-' +  temp_date_input.substring(3,5) ;
						date_check = true;

						continue;
					}  //added my method
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
				catch (Exception e) {
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
			} while (date_check == false || adate.length() <= 0);

			//get appointment time range
			do {
				try {
					System.out.println("Input Your Appointment's start time in (HH:MM) format:");
				String start_time = in.readLine();
				if(start_time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]") == false){
					System.out.println("Your input for Appointment's start time is wrong, Please follow the given format:");
					continue;
				}
				System.out.println("Input Your Appointment's end time in (HH:MM) format:");
				String end_time = in.readLine();
				if(end_time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]") == false){
					System.out.println("Your input for Appointment's end time is wrong, Please follow the given format:");
					continue;
				}
				int h1 = Integer.parseInt(start_time.substring(0, 2));
				int m1 = Integer.parseInt(start_time.substring(3, 5));
				int h2 = Integer.parseInt(end_time.substring(0, 2));
				int m2 = Integer.parseInt(end_time.substring(3, 5));
				if(h1 >= h2){
					if(h1 == h2){
						if(m1 >= m2){
							System.out.println("Your input for Appointment's start time is later than the end time:");
							continue;
						}
					}
					else{
						System.out.println("Your input for Appointment's start time is later than the end time:");
						continue;
					}
				}
				appnt_time = start_time + "-" + end_time;
				//System.out.println(appnt_time);
				hour_check = true;
				} catch (Exception e) {
					System.out.print("\tPlease enter a valid time slot: ");
				}
			} while (hour_check == false || appnt_time.length() <= 0);

			do { // status
				try {
					System.out.print("Input Your Appointment's Status, Choose one from (PA, AC, AV, WL):");
					appnt_status = in.readLine().toUpperCase();
					System.out.println("Input status is: " + appnt_status);
					if(appnt_status.equals("PA") || appnt_status.equals("AC") || appnt_status.equals("AV") || appnt_status.equals("WL")){
						status_check = true;
						continue;
					}

					System.out.println("Your input for Appointment's status is wrong, Please follow the given format:");
				} catch (Exception e) {
					System.out.println("Your input is invalid!");
					continue;
				} // end try
			} while (appnt_status.length() <= 0 || status_check == false);

			query += appnt_id + ", \'" + adate + "\', \'" + appnt_time + "\', \'" + appnt_status + "\');";
			//System.out.println(query);
			esql.executeUpdate(query);
			System.out.println("New appointment added!\n");
		} catch (Exception e) {
			System.out.println("Error adding an Appointment!");
			System.err.println (e.getMessage());
		} 

		
	}


	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
		Scanner sc = new Scanner(System.in);
		final String DATE_FORMAT = "MM-dd-yyyy";
		int doc_id;
		boolean date_check1 = false;
		boolean date_check2 = false;
		String start_date = "";
		String end_date = "";
		try {
			do {
				System.out.print("Input Doctor's ID:");
				while (!sc.hasNextInt()) {
					System.out.println("That's not a number!");
					sc.next(); 
				}
				doc_id = sc.nextInt();
			} while (doc_id <= 0);
	
			do {
				try {
					System.out.print("Input Your doctor's date 1 in (MM-DD-YYYY) format:");
					String temp_date_input = in.readLine();
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
					df.setLenient(false);
					Date date = df.parse(temp_date_input);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					if(temp_date_input.matches("^\\d+\\-\\d+\\-\\d+")){
						start_date = temp_date_input.substring(6) + '-' +  temp_date_input.substring(0,2) + '-' +  temp_date_input.substring(3,5) ;
						date_check1 = true;
						continue;
					}  //added my method
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
				catch (Exception e) {
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
			} while (date_check1 == false || start_date.length() <= 0);
	
			do {
				try {
					System.out.print("Input Your doctor's date 2 in (MM-DD-YYYY) format:");
					String temp_date_input = in.readLine();
					SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
					df.setLenient(false);
					Date date = df.parse(temp_date_input);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					if(temp_date_input.matches("^\\d+\\-\\d+\\-\\d+")){
						end_date = temp_date_input.substring(6) + '-' +  temp_date_input.substring(0,2) + '-' +  temp_date_input.substring(3,5) ;
						date_check2 = true;
						continue;
					}  //added my method
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
				catch (Exception e) {
					System.out.print("Your input for Appointment date is wrong, Please follow the given format:");
				}
			} while (date_check2 == false || end_date.length() <= 0);

			String query = "SELECT appnt_ID AS Appointment_ID, adate AS Date, time_slot, status FROM Appointment JOIN has_appointment ";
			query +=       "ON Appointment.appnt_ID = has_appointment.appt_id WHERE (Appointment.adate >= '" + start_date + "' AND Appointment.adate <= '" + end_date + "')";
			query +=  " AND (Appointment.status = 'AC' OR Appointment.status = 'AV') AND has_appointment.doctor_id = " + doc_id;
			//System.out.println(query);
			int rows = esql.executeQueryAndPrintResult(query);
			
			if(rows == 0) {
				System.out.print("No active or available appointments for this doctor. Please double check your doctor id\n");
			}
			else{
				System.out.println(" PA --> Past | AC --> Active | AV --> Available | WL --> Waitlisted"); 
			}
		} catch (Exception e) {
			System.out.println("Your query is incorrect, Please try again!");
			System.out.println(e);
		}	
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
	}

	public static List<List<String>> count_status(DBproject esql, String status){
		List<List<String>> res = new ArrayList<List<String>>();
		try { 
			String query = "SELECT D.doctor_ID, D.name, COUNT(A.appnt_ID)FROM Doctor D, Appointment A, has_appointment H";
			query +=      " WHERE D.doctor_ID = H.doctor_ID AND H.appt_ID = A.appnt_ID AND A.status = '" + status + "' GROUP BY D.doctor_ID ORDER BY D.doctor_ID ASC;";
			res = esql.executeQueryAndReturnResult(query);
		} catch (Exception e) {
			System.out.println("Query error in " + status + " query.");
		}
		return res;
	}

	//reference: https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values by Sandeep Pathak
	public static LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
		List<String> mapKeys = new ArrayList<>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<>(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

		Iterator<Integer> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			int val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();
		
			while (keyIt.hasNext()) {
				String key = keyIt.next();
				int comp1 = passedMap.get(key);
				int comp2 = val;
			
				if (comp1 == comp2) {
					mapKeys.remove(key);
					sortedMap.put(key, val);
					break;
				}
			}
		}
    	return sortedMap;
	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
		List<List<String>> res = new ArrayList<List<String>>();
		int num_doc = 0;
		HashMap<String, String> doctor_id_to_name = new HashMap<String, String>();
		List<String> status_list= new ArrayList<String>();
		status_list.add("PA");
		status_list.add("AC");
		status_list.add("AV");
		status_list.add("WL");

		try { 
			String num_doc_query = "SELECT COUNT(doctor_ID) FROM Doctor;";
			res = esql.executeQueryAndReturnResult(num_doc_query);
			num_doc = Integer.parseInt(res.get(0).get(0));
			System.out.println("Number of doctor = " + num_doc);
		} catch (Exception e) {
			System.out.println("Error in finding total number of doctors.");
		}

		List<List<String>> doc_status_list = new ArrayList<List<String>>(); 
		for (int i = 0; i < num_doc; i++) {
			//initialization
			List<String> index = new ArrayList<String>();
			//initialize the counter for each status
			for (int j = 0; j < 4; j++) {
				index.add("0");
			}
			doc_status_list.add(index);
		}
		try {
			for(int i = 0; i < status_list.size(); i++){
				res = count_status(esql, status_list.get(i));
				System.out.println("size of res =: " + res.size());
				System.out.println("After count_status function for " + status_list.get(i));
				for (int j = 0; j < res.size(); j++) { 
					//doc_status_list. get(each doc id)
					//then set(each doc id)'s status count 
					int curr_doc_id = Integer.parseInt(res.get(j).get(0));
					System.out.println("After 701");
					String curr_doc_name = res.get(j).get(1);
					System.out.println("After 703");
					String curr_doc_status_count = res.get(j).get(2);
					System.out.println("After 705");
					doctor_id_to_name.put(res.get(j).get(0), curr_doc_name);
					System.out.println("After 706");
					doc_status_list.get(curr_doc_id).set(i, curr_doc_status_count);
					System.out.println("After 708");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in getting status count.");
			System.out.println(e);
		}

		ArrayList<HashMap<String, Integer>> bag_of_docs = new ArrayList<HashMap<String, Integer>>();
		try {
			
			for (int each_doc = 0; each_doc < res.size(); each_doc++) {
				HashMap<String, Integer> map=new HashMap<String, Integer>();
				for(int each_status = 0; each_status < 4; each_status++){
					//     map(status, its_count)
					map.put(status_list.get(each_status), Integer.parseInt(doc_status_list.get(each_doc).get(each_status)));
				}
				bag_of_docs.add(map);
			}
		} catch (Exception e) {
			System.out.println("Error in putting hashmap in list");
			System.out.println(e);
		}

		List<LinkedHashMap<String, Integer>> doctor = new ArrayList<LinkedHashMap<String, Integer>>();
		try {
			
			for (HashMap<String,Integer> stats : bag_of_docs) {
				LinkedHashMap<String, Integer> sorted_status = new LinkedHashMap<String, Integer>();
				sorted_status = sortHashMapByValues(stats);
				doctor.add(sorted_status);
			}
		} catch (Exception e) {
			System.out.println("Error in sorting the hashmap");
			System.out.println(e);
		}
		
		System.out.printf("%s-10%s-20%\n", "Doctor ID", "Doctor Name");
		try {
			for (int i = 0; i <= num_doc; i++) { // Print all values in our format
				System.out.printf("%s-10%s-20", i, doctor_id_to_name.get(String.valueOf(i)));
				for (Map.Entry<String, Integer> mapElement : doctor.get(i).entrySet()) {
	  
					String key = mapElement.getKey();
		  
					// Finding the value
					int value = mapElement.getValue();
		  
					// print the key : value pair
					System.out.printf("%s:-1%s-5",key, value);
				}
			}
		} catch (Exception e) {
			System.out.println("Error in final output");
			System.out.println(e);
		}
		
	}

	
	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
	}
}
