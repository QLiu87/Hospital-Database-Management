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
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

	public static boolean test_dept_id(DBproject esql, int id) {
		String query_exist = "Select * FROM Department WHERE dept_ID = ";
		query_exist += String.valueOf(id);
		try {
			List<List<String>> res = esql.executeQueryAndReturnResult(query_exist);
			if(res.size() > 0)
				return true;
		} catch(Exception e) {
			System.out.print(e);
		}
		return false;
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
		boolean dept_check = false;
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
			if(test_dept_id(esql, did) == false){
				System.out.println("This department id does not exist, Please try again!");
				continue;
			}
			dept_check = true;
		} while (did <= 0 || dept_check == false);
		
		try {
			query += "VALUES (" + String.valueOf(doc_id) + ", '" + doc_name + "', '" + specialty + "', " + String.valueOf(did) + ")";
            esql.executeUpdate(query);
			String query2 = "select * from Doctor where doctor_ID = " + String.valueOf(doc_id);
			int rowcount = esql.executeQueryAndPrintResult(query2);
			//System.out.println(rowcount);
		} catch (Exception e) {
			System.out.println("Error adding a doctor!");
			System.err.println (e.getMessage());
		}
		System.out.print("New doctor added!\n");
		
		return;
	}

	public static void AddPatient(DBproject esql) {//2

        int patient_ID;

        do {
            System.out.print("Input Patient ID Number: ");
            try {
                patient_ID = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        String name;

        do {
            System.out.print("Input Patient Name: ");
            try {
                name = in.readLine();
                if(name.length() <= 0 || name.length() > 128) {
                    throw new RuntimeException("Patient Name cannot be null or exceed 128 characters");
                }
                break;
            }catch (Exception e) {
                System.out.println(e);
                continue;
            }
        }while (true);

        String gtype;

        do {
            System.out.print("Input Patient Gender: ");
            try {
                gtype = in.readLine();
                if(gtype.length() <= 0 || gtype.length() >= 2) {
                    throw new RuntimeException("Patient Gender cannot be null or exceed 2 characters");
                }
                break;
            }catch (Exception e) {
                System.out.println(e);
                continue;
            }
        }while (true);

        int age;

        do {
            System.out.print("Input Patient Age: ");
            try {
                age = Integer.parseInt(in.readLine());
                if(age < 0) {
                    throw new RuntimeException("Patient Age cannot be negative");
                }
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        String address;

        do {
            System.out.print("Input Patient Address: ");
            try {
                address = in.readLine();
                if(address.length() <= 0 || address.length() > 256) {
                    throw new RuntimeException("Patient Address cannot be null or exceed 256 characters");
                }
                break;
            }catch (Exception e) {
                System.out.println(e);
                continue;
            }
        }while (true);

        int number_of_appts;

        do {
            System.out.print("Input Number of Appointments: ");
            try {
                number_of_appts = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        try {
            String query = "INSERT INTO Patient (patient_ID, name, gtype, age, address, number_of_appts) VALUES (" + patient_ID + ", \'" + name + "\', \'" + gtype + "\', " + age + ", \'" + address + "\', " + number_of_appts + ");";

            esql.executeUpdate(query);

            System.out.print("New patient added!\n");

        }catch (Exception e) {
            System.err.println (e.getMessage());
        }
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
					//System.out.println("Input status is: " + appnt_status);
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
        int pid;

        do {
            System.out.print("Input Patient ID: ");
            try {
                pid = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        int aid;

        do {
            System.out.print("Input Appointment ID: ");
            try {
                aid = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        int did;

        do {
            System.out.print("Input Doctor ID: ");
            try {
                did = Integer.parseInt(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        try {
            String query = "SELECT A.status\nFROM Appointment A, has_appointment H, searches S\nWHERE A.appnt_ID = H.appt_id AND S.aid = A.appnt_ID AND S.aid = H.appt_id AND S.pid = " + pid + " AND doctor_id = " + did + " AND A.appnt_ID = " + aid + ";";

            List<List<String>> res = new ArrayList<>();
            res = esql.executeQueryAndReturnResult(query);

            if(res.get(0).get(0).equals("AC")){
                //do {


                int patient_ID;

                do {
                    System.out.print("Input Patient ID Number: ");
                    try {
                        patient_ID = Integer.parseInt(in.readLine());
                        break;
                    }catch (Exception e) {
                        System.out.println("Your input is invalid!");
                        continue;
                    }
                }while (true);

                String name;

                do {
                    System.out.print("Input Patient Name: ");
                    try {
                        name = in.readLine();
                        if(name.length() <= 0 || name.length() > 128) {
                            throw new RuntimeException("Patient Name cannot be null or exceed 128 characters");
                        }
                        break;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }while (true);

                String gtype;

                do {
                    System.out.print("Input Patient Gender: ");
                    try {
                        gtype = in.readLine();
                        if(gtype.length() <= 0 || gtype.length() >= 2) {
                            throw new RuntimeException("Patient Gender cannot be null or exceed 2 characters");
                        }
                        break;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }while (true);

                int age;

                do {
                    System.out.print("Input Patient Age: ");
                    try {
                        age = Integer.parseInt(in.readLine());
                        if(age < 0) {
                            throw new RuntimeException("Patient Age cannot be negative");
                        }
                        break;
                    }catch (NumberFormatException e) {
                        System.out.println("Your input is invalid!");
                        continue;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }while (true);

                String address;

                do {
                    System.out.print("Input Patient Address: ");
                    try {
                        address = in.readLine();
                        if(address.length() <= 0 || address.length() > 256) {
                            throw new RuntimeException("Patient Address cannot be null or exceed 256 characters");
                        }
                        break;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }while (true);

                int number_of_appts;

                do {
                    System.out.print("Input Number of Appointments: ");
                    try {
                        number_of_appts = Integer.parseInt(in.readLine());
                        break;
                    }catch (Exception e) {
                        System.out.println("Your input is invalid!");
                        continue;
                    }
                }while (true);

                try {
                    query = "INSERT INTO Patient (patient_ID, name, gtype, age, address, number_of_appts) VALUES (" + patient_ID + ", \'" + name + "\', \'" + gtype + "\', " + age + ", \'" + address + "\', " + number_of_appts + ");";

                    esql.executeUpdate(query);
                }catch (Exception e) {
                    System.err.println (e.getMessage());
                }

                //}while(true);

                do {
                    System.out.print("Update Appointment Status to WaitListed: ");

                    String status;
                    try {
                        status = in.readLine();
                        if(!status.equals("PA") && !status.equals("AC") && !status.equals("AV") && !status.equals("WL")) {
                            throw new RuntimeException("Input only accepts the following inputs: PA, AC, AV, WL");
                        }
                        break;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                } while (true);
                try {
                    query = "UPDATE Appointment SET status = 'WL' WHERE appnt_ID = " + aid + ";";


                    esql.executeUpdate(query);
                }catch (Exception e) {
                    System.err.println (e.getMessage());
                }

            }else if (res.get(0).get(0).equals("AV")){
                do{
                    System.out.print("Input Update Appointment Status to Active: ");

                    String status;
                    try {
                        status = in.readLine();
                        if(!status.equals("PA") && !status.equals("AC") && !status.equals("AV") && !status.equals("WL")) {
                            throw new RuntimeException("Input only accepts the following inputs: PA, AC, AV, WL");
                        }
                        break;
                    }catch (Exception e) {
                        System.out.println(e);
                        continue;
                    }
                }while (true);
                try {
                    query = "UPDATE Appointment SET status = 'AC' WHERE appnt_ID = " + aid + ";";


                    esql.executeUpdate(query);
                }catch (Exception e) {
                    System.err.println (e.getMessage());

                }

            } else if(res.get(0).get(0).equals("WL")){
                do{
                    int patient_ID;

                    do {
                        System.out.print("Input Patient ID Number: ");
                        try {
                            patient_ID = Integer.parseInt(in.readLine());
                            break;
                        }catch (Exception e) {
                            System.out.println("Your input is invalid!");
                            continue;
                        }
                    }while (true);

                    String name;

                    do {
                        System.out.print("Input Patient Name: ");
                        try {
                            name = in.readLine();
                            if(name.length() <= 0 || name.length() > 128) {
                                throw new RuntimeException("Patient Name cannot be null or exceed 128 characters");
                            }
                            break;
                        }catch (Exception e) {
                            System.out.println(e);
                            continue;
                        }
                    }while (true);

                    String gtype;

                    do {
                        System.out.print("Input Patient Gender: ");
                        try {
                            gtype = in.readLine();
                            if(gtype.length() <= 0 || gtype.length() >= 2) {
                                throw new RuntimeException("Patient Gender cannot be null or exceed 2 characters");
                            }
                            break;
                        }catch (Exception e) {
                            System.out.println(e);
                            continue;
                        }
                    }while (true);

                    int age;

                    do {
                        System.out.print("Input Patient Age: ");
                        try {
                            age = Integer.parseInt(in.readLine());
                            if(age < 0) {
                                throw new RuntimeException("Patient Age cannot be negative");
                            }
                            break;
                        }catch (NumberFormatException e) {
                            System.out.println("Your input is invalid!");
                            continue;
                        }catch (Exception e) {
                            System.out.println(e);
                            continue;
                        }
                    }while (true);

                    String address;

                    do {
                        System.out.print("Input Patient Address: ");
                        try {
                            address = in.readLine();
                            if(address.length() <= 0 || address.length() > 256) {
                                throw new RuntimeException("Patient Address cannot be null or exceed 256 characters");
                            }
                            break;
                        }catch (Exception e) {
                            System.out.println(e);
                            continue;
                        }
                    }while (true);

                    int number_of_appts;

                    do {
                        System.out.print("Input Number of Appointments: ");
                        try {
                            number_of_appts = Integer.parseInt(in.readLine());
                            break;
                        }catch (Exception e) {
                            System.out.println("Your input is invalid!");
                            continue;
                        }
                    }while (true);

                    try {
                        query = "INSERT INTO Patient (patient_ID, name, gtype, age, address, number_of_appts) VALUES (" + patient_ID + ", \'" + name + "\', \'" + gtype + "\', " + age + ", \'" + address + "\', " + number_of_appts + ");";

                        esql.executeUpdate(query);
                    }catch (Exception e) {
                        System.err.println (e.getMessage());
                    }

                }while(true);

            }else{

                System.out.println("No need to do anything");

            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
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
        String name;

        do {
            System.out.print("Input Department Name : ");
            try {
                name = in.readLine();
                if(name.length() <= 0 || name.length() > 32) {
                    throw new RuntimeException("Department Name cannot be null or exceed 32 characters");
                }
                break;
            }catch (Exception e) {
                System.out.println(e);
                continue;
            }
        }while (true);

        Date adate;

        do {
            System.out.print("Input Appointment Date (mm/dd/yyyy): ");
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
                adate = dateFormat.parse(in.readLine());
                break;
            }catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }while (true);

        try {
            String query = "SELECT A.appnt_ID AS available_appointments, A.time_slot\nFROM Appointment A, Department De, searches S\nWHERE S.hid = De.hid AND A.appnt_ID = S.aid AND A.status = 'AV' AND De.name = \'" + name + "\' AND A.adate = \'" + adate + "\'\nGROUP BY \"available_appointments\";";


            esql.executeQueryAndPrintResult(query);
        }catch (Exception e) {
            System.err.println (e.getMessage());
        }


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
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		try {
			List<String> mapKeys = new ArrayList<>(passedMap.keySet());
			List<Integer> mapValues = new ArrayList<>(passedMap.values());
			Collections.sort(mapValues, Collections.reverseOrder());
			Collections.sort(mapKeys, Collections.reverseOrder());
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
		} catch (Exception e) {
			System.out.println("Error in sorting function.");
			System.out.println(e);
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
			String name_query = "SELECT doctor_ID, name FROM Doctor;";
			res = esql.executeQueryAndReturnResult(name_query);
			for(int i = 0; i < res.size();i++){
				doctor_id_to_name.put(res.get(i).get(0), res.get(i).get(1));
			}
			//System.out.println("Number of doctor = " + num_doc);
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
				for (int j = 0; j < res.size(); j++) { 
					//doc_status_list. get(each doc id)
					//then set(each doc id)'s status count 
					int curr_doc_id = Integer.parseInt(res.get(j).get(0));
					String curr_doc_status_count = res.get(j).get(2);
					doc_status_list.get(curr_doc_id).set(i, curr_doc_status_count);	
				}
			}
		} catch (Exception e) {
			System.out.println("Error in getting status count.");
			System.out.println(e);
		}

		//System.out.println("Starting bag of doctors");
		ArrayList<HashMap<String, Integer>> bag_of_docs = new ArrayList<HashMap<String, Integer>>();
		try {
			for (int each_doc = 0; each_doc < doc_status_list.size(); each_doc++) {
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

		System.out.printf("%10s%20s\n", "Doctor ID", "Doctor Name");
		try {
			for (int i = 0; i < num_doc; i++) { // Print all values in our format
				System.out.printf("%10s%20s     ", i, doctor_id_to_name.get(String.valueOf(i)));
				for (Map.Entry<String, Integer> mapElement : doctor.get(i).entrySet()) {
					String key = mapElement.getKey();
					int value = mapElement.getValue();
					System.out.printf("%2s:%1s ",key, value);
				}
				System.out.print("\n");
			}
		} catch (Exception e) {
			System.out.println("Error in final output");
			System.out.println(e);
		}
		
	}

	public static void FindPatientsCountWithStatus(DBproject esql) {//8
        String status;

        do {
            System.out.print("Input Appointment Status: ");
            try {
                status = in.readLine();
                if(!status.equals("PA") && !status.equals("AC") && !status.equals("AV") && !status.equals("WL")) {
                    throw new RuntimeException("Input only accepts the following inputs: PA, AC, AV, WL");
                }
                break;
            }catch (Exception e) {
                System.out.println(e);
                continue;
            }
        }while (true);

        try {
            String query = "SELECT COUNT(DISTINCT P.patient_ID) AS num_of_patient\nFROM Appointment A, has_appointment H, Doctor D, searches S, Patient P\nWHERE A.appnt_ID = H.appt_id AND H.doctor_id = D.doctor_ID AND S.aid = A.appnt_ID AND S.pid = P.patient_ID AND A.status = \'" + status + "\';";

            esql.executeQueryAndPrintResult(query);
        }catch (Exception e) {
            System.err.println (e.getMessage());
        }
        // Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
    }
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.

}
