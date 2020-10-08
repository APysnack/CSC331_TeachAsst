package teacherAssistant;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.util.StringUtils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

// error_flag keys:
// [0 : add success, 1 : DoesNotExist, 2: Duplicate, 3 : too short, 
//	4: function specific, 5: delete success, 6: edit success]

public class dbConnection {
	boolean trueAll = false;
	String tchrName;
	String usrName;
	String name;
	String password;
	String url;
	String new_query;
	ResultSet result;
	String emailDomain = "@hogwarts.com";
	Connection conn;

	dbConnection() { 
		try {
			this.name = "pp_admin";
			this.password = "oshkoshprakash";
			this.url = "jdbc:mysql://pied-piper-rds.cpdiz6ijxbup.us-east-2.rds.amazonaws.com/teachasst";

			Connection conn = DriverManager.getConnection(url, name, password);
			this.conn = conn;
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public int connectUser(String userName, String userPW) {
		int privilege = 0;
		new_query = "Select * from users where ID = '" + userName + "' AND Password = '" + userPW + "'";
		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);

			if (result.next()) {
				privilege = (int) result.getInt(3);
				return privilege;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return privilege;

	} // end function

	public int createUser(String userName, String userPW, int Privilege, String arg0) {
		new_query = "Insert into users values ('" + userName + "', '" + userPW + "', " + Privilege + ");";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(new_query);

			if (Privilege == 2) {
				createTeacher(userName, arg0);
			}

			if (Privilege == 3) {
				createStudent(userName, arg0);
			}

			return 0;
		}

		catch (SQLIntegrityConstraintViolationException e) {
			return 2;
		}

		catch (SQLException e) {
			System.out.println(e);
			return 3;
		}
	}

	public int createStudent(String userName, String teacherName) {
		new_query = "insert into students values ('" + userName + "', '" + teacherName + "', 0, 0, 0);";

		try {
			Statement stmt = conn.createStatement();
			stmt.execute(new_query);
		} catch (SQLException e) {
			System.out.println(e);
		}

		return 0;
	}

	public int createTeacher(String userName, String subject) {
		String email = userName + emailDomain;
		new_query = "insert into teachers values ('" + userName + "', '" + email + "', '" + subject + "');";

		try {
			Statement stmt = conn.createStatement();
			stmt.execute(new_query);
		} catch (SQLException e) {
			System.out.println(e);
		}

		return 0;
	}

	public int editUser(String userName, String newName, String newPW, int newPriv, String arg0) {
		int flag = -1;
		
		// if the user was successfully deleted
		flag = removeRow("Users", userName);
		if (flag == 5) {
			// attempt to create a new user with this data
			flag = createUser(newName, newPW, newPriv, arg0);

			if (flag == 0) {
				return 6;
			} else {
				return 3;
			}
		}

		else {
			return 1;
		}
	} // end edit user function

	public int getPrivilege(String userName) {

		new_query = "select privilege from Users where ID='" + userName + "';";
		Statement stmt;
		int i = 1;
		int privilege = 0;

		try {
			stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);
			if (result.next()) {
				privilege = result.getInt(i);
			}

			return privilege;
		} catch (SQLException e) {
			System.out.println(e);
		}

		return 0;
	}

	public int removeRow(String table, String id) {
		new_query = "Delete from " + table + " where ID='" + id + "';";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
			if (rowsAffected == 0) {
				// if no rows affected, returns 1 for error flag
				return 1;
			} else {
				// otherwise, command was successful
				return 5;
			}
		}

		catch (SQLException e) {
			System.out.println(e);
			return 3;
		}
	} // end remove user function

	public JTable getJTable(String tbl) {

		new_query = "";

		char first_letter = tbl.charAt(0);

		// built so user can override the default query
		if (first_letter == '!') {
			new_query = tbl.substring(1, tbl.length());
		} else if (trueAll == true) {
			new_query = "Select * from " + tbl + ";";
			resetTrueAll();
		} else {
			new_query = "Select * from " + tbl + " where teacherID='" + usrName + "';";
		}

		try {

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);

			Statement stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			result = stmt.getResultSet();
			ResultSetMetaData metadata = result.getMetaData();
			int colCount = metadata.getColumnCount();

			Vector columnNames = new Vector();
			Vector data = new Vector();

			for (int i = 1; i <= colCount; i++) {
				columnNames.addElement(metadata.getColumnName(i));
			}

			while (result.next()) {
				Vector row = new Vector();
				for (int i = 1; i <= colCount; i++) {
					row.addElement(result.getObject(i));
				}

				data.addElement(row);
			}

			DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {

				public Class getColumnClass(int column) {
					for (int row = 0; row < getRowCount(); row++) {
						Object o = getValueAt(row, column);
						if (o != null) {
							return o.getClass();
						}
					}

					return Object.class;
				}

				@Override
				public boolean isCellEditable(int row, int column) {
					// all cells false
					return false;
				}
			};

			JTable table = new JTable();
			table.setModel(tableModel);

			for (int i = 0; i < colCount; i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}

			return table;

		} catch (SQLException e) {
			System.out.println(e);
		}

		JTable table = new JTable();
		return table;
	}

	public int addAssignment(String id, String title, String details, String points, String dueDate) {
		new_query = "insert into assignments values (" + id + ", '" + title + "', '" + details + "', " + points + ", '"
				+ dueDate + "', '" + usrName + "');";

		if (!StringUtils.isStrictlyNumeric(id) || !StringUtils.isStrictlyNumeric(points) || dueDate.length() < 10) {
			return 4;
		}

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(new_query);
			return 0;
		}

		catch (SQLIntegrityConstraintViolationException e) {
			return 2;
		}

		catch (SQLException e) {
			System.out.println(e);
			return 3;
		}

	}

	// gets the size of an individual table (arg0)
	public int getClassTblSize(int tblNum) {
		new_query = "select size from class_tables where ID=" + tblNum + " and teacherID='" + usrName + "';";
		int size = 0;
		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);
			if (result.next()) {
				size = result.getInt(1);
			}
			return size;

		} catch (SQLException e) {
			System.out.println(e);
		}

		return 0;
	}

	// gets the names/sizes of each table and stores in list {tblName1, tblSize1,
	// tblName2, tblSize2..}
	public List<Integer> getClassTblSizes() {
		new_query = "select id, size from class_tables where teacherID='" + usrName + "';";

		List<Integer> tblSizes = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);

			while (result.next()) {
				tblSizes.add(result.getInt(1));
				tblSizes.add(result.getInt(2));
			}

		} catch (SQLException e) {
			System.out.println(e);
		}

		return tblSizes;
	}

	// gets all students that are assigned to table arg0
	public ArrayList getTblStdnts(int tblNum) {
		new_query = "select * from students where tableID=" + tblNum + " and teacherID='" + usrName + "';";
		ArrayList tblList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			result = stmt.getResultSet();
			ResultSetMetaData metadata = result.getMetaData();

			while (result.next()) {
				tblList.add(result.getString(1));
			}

			return tblList;

		} catch (SQLException e) {
			System.out.println(e);
			return tblList;
		}
	}

	// gets ID's of all students in the database
	public ArrayList getAllNames(String tableName) {
		new_query = "select * from " + tableName + " where teacherID='" + usrName + "';";

		if (trueAll == true) {
			new_query = "select * from " + tableName + ";";
			resetTrueAll();
		}

		ArrayList tblList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			result = stmt.getResultSet();
			ResultSetMetaData metadata = result.getMetaData();

			while (result.next()) {
				tblList.add(result.getString(1));
			}

			return tblList;

		} catch (SQLException e) {
			System.out.println(e);
			return tblList;
		}
	}

	// takes a list of names and assigns them all to the table in arg0
	public int updateTableSeats(int tableID, List<String> seatList) {
		String resetTable = "update students set tableID=0 where tableID=" + tableID + " and teacherID='" + usrName
				+ "';";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(resetTable);

		} catch (SQLException e) {
			System.out.println(e);
			return 1;
		}

		for (int i = 0; i < seatList.size(); i++) {
			String name = seatList.get(i);
			String addName = "update students set tableID=" + tableID + " where ID='" + name + "' and teacherID='"
					+ usrName + "';";

			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(addName);

			} catch (SQLException e) {
				System.out.println(e);
				return 1;
			}
		}

		return 0;
	}

	public int updateSingleSeat(int tableID, String name) {
		String addName = "update students set tableID=" + tableID + " where ID='" + name + "' and teacherID='" + usrName
				+ "';";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(addName);

		} catch (SQLException e) {
			System.out.println(e);
			return 1;
		}

		return 0;
	}

	public int addClsTbl(int tblID, int tblSize) {
		new_query = "insert into class_tables values(" + tblID + "," + tblSize + ", '" + usrName + "');";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(new_query);
			return 0;
		} catch (SQLIntegrityConstraintViolationException e) {
			return 2;

		} catch (SQLException e) {
			return 3;
		}

	}

	public int delClsTbl(int tblID) {
		new_query = "delete from class_tables where ID=" + tblID + " and teacherID='" + usrName + "';";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
			if (rowsAffected == 0) {
				return 1;
			} else {
				String resetTable = "update students set tableID=0 where tableID=" + tblID + " and teacherID='"
						+ usrName + "';";

				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(resetTable);
					return 0;

				} catch (SQLException e) {
					System.out.println(e);
					return 3;
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
			return 3;
		}

	}

	public void randomizeTables(String tchrName) {
		new_query = "update students set tableID=0 where teacherID = '" + usrName + "';";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
		} catch (SQLException e) {
			System.out.println(e);
		}

		// list: {tblName1, tblSize1, tblName2, tblSize2..}
		List<Integer> tableContents = getClassTblSizes();
		List<Integer> stsFilled = new ArrayList();

		int k = 0;

		// initializes seats filled to be the same size as tableContents with all 0
		// values
		while (k <= (tableContents.size())) {
			stsFilled.add(k, 0);
			k++;
		}

		// a randomized list of all the student names
		ArrayList studentNames = getAllNames("students");
		Collections.shuffle(studentNames, new Random());

		List<String> stdnts = new ArrayList<>();

		// converts the list of student names to a list of string representations
		// ("Table X: Y Seats")
		for (int i = 0; i < studentNames.size(); i++) {
			stdnts.add(studentNames.get(i).toString());
		}

		int i = 0;
		int j = 0;
		k = 0;

		while (j < stdnts.size()) {
			// if j becomes greater than the number of elements in the array, reset i to 0
			// (start at table 1 again)
			if (k > (tableContents.size() / 2) - 1) {
				i = 0;
				k = 0;
			}

			// if the number of seats already assigned to table i > table i's capacity,
			// check next table
			if (stsFilled.get(k) >= tableContents.get(i + 1)) {
				i++;
				k++;
			}

			updateSingleSeat(tableContents.get(i), stdnts.get(j));

			// increments the number of seats filled at index i by 1
			stsFilled.add(k, stsFilled.get(k) + 1);
			i = i + 2;
			j++;
			k++;
		}
	}

	public int addGrades(int assignmentID, String assignmentTitle, String stdntName, double stdntGrade, String mode) {

		new_query = "";

		if (mode.equalsIgnoreCase("Edit")) {
			new_query = "delete from grades where assignmentID=" + assignmentID + " and studentID='" + stdntName + "';";

			try {
				Statement stmt = conn.createStatement();
				int rowsAffected = stmt.executeUpdate(new_query);

				if (rowsAffected == 0) {
					return 1;
				}

				int returnValue = addGrades(assignmentID, assignmentTitle, stdntName, stdntGrade, "Add");
				return returnValue;

			} catch (SQLException e) {
				System.out.println(e);
				return 3;
			}

		}

		else {
			new_query = "insert into grades values(" + assignmentID + ", '" + assignmentTitle + "', '" + stdntName
					+ "'," + stdntGrade + ", '" + usrName + "');";

			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(new_query);
				updateGrade(stdntName);
				return 0;
			} catch (SQLIntegrityConstraintViolationException e) {
				return 2;
			} catch (SQLException e) {
				// most likely cause of error is too many digits
				return -2;
			}
		}
	}

	public ArrayList getAllAsgnmts() {
		new_query = "select * from assignments where teacherID = '" + usrName + "';";
		ArrayList asgnmtList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			result = stmt.getResultSet();
			ResultSetMetaData metadata = result.getMetaData();

			while (result.next()) {
				asgnmtList.add(result.getString(1));
				asgnmtList.add(result.getString(2));
			}

			return asgnmtList;

		} catch (SQLException e) {
			System.out.println(e);
			return asgnmtList;
		}
	}

	public String getAssgnmtDtl(String idNum, int priv) {

		// foo
		if (priv == 3) {
			new_query = "Select details from assignments where ID =" + idNum + " and teacherID='" + tchrName + "'";
		} else {
			new_query = "Select details from assignments where ID =" + idNum + " and teacherID='" + usrName + "';";
		}
		String details = "";

		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);
			if (result.next()) {
				details = result.getString(1);
			}
			return details;
		} catch (SQLException e) {
			System.out.println(e);
			return details;
		}
	}

	public int getAbsences(String studentID) {
		int absCount = 0;
		new_query = "select absences from students where ID='" + studentID + "';";

		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);

			if (result.next()) {
				absCount = result.getInt(1);
				return absCount;
			} else {
				return absCount;
			}

		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}

	public int updateGrade(String studentID) {
		double grade = getGrade(studentID);

		new_query = "update students set grade=" + grade + " where ID='" + studentID + "';";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
			if (rowsAffected == 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			System.out.println(e);

			return 3;
		}
	}

	public double getGrade(String studentID) {
		new_query = "select grades.studentID, grades.grade, assignments.points, assignments.id, "
				+ "assignments.title from grades inner join assignments on assignments.ID = grades.assignmentid"
				+ " where studentID=?";

		PreparedStatement stmt;
		try {

			double scoreSum = 0;
			double ptsPossible = 0;
			double total = 0.0;

			// uses a prepared statement to execute query
			stmt = conn.prepareStatement(new_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// sets first ? value in prepared statement to studentID.
			stmt.setString(1, studentID);
			result = stmt.executeQuery();

			if (result.next()) {
				result.beforeFirst();
				while (result.next()) {
					scoreSum += result.getFloat(2);
					ptsPossible += result.getFloat(3);
				}

				if (ptsPossible > 0) {
					total = ((scoreSum / ptsPossible) * 100);
					return total;
				}
			}
			return total;
		} catch (SQLException e) {
			return -2;
		}

	}

	public int recordAttendance(String studentID, String selectedDate, boolean is_present) {
		new_query = "insert into attendance values('" + studentID + "', '" + selectedDate + "', " + is_present + ", '"
				+ usrName + "');";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);

			if (is_present == false) {
				addAbsence(studentID);
			}

			if (rowsAffected == 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			return 3;
		}

	}

	public void addAbsence(String studentID) {
		new_query = "update students set absences=(absences+1) where ID='" + studentID + "';";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(new_query);
			return;
		} catch (SQLException e) {
			return;
		}
	}

	public ArrayList getEnumFields(String tableName, String fieldName) {
		new_query = "Show columns from " + tableName + " where field=?";
		ArrayList numList = new ArrayList<>();
		int i = 1;
		String str = "";

		try {
			PreparedStatement stmt;
			// uses a prepared statement to execute query
			stmt = conn.prepareStatement(new_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			// sets first ? value in prepared statement to studentID.
			stmt.setString(1, fieldName);
			result = stmt.executeQuery();
			result.beforeFirst();
			if (result.next()) {
				str = result.getString(i + 1);
				i++;
			}

			String[] enumVals = str.split("'");
			for (i = 1; i < enumVals.length; i++) {
				enumVals[i] = enumVals[i].replaceAll("[^A-Za-z0-9 \n]", "");
				if (enumVals[i].length() > 0) {
					numList.add(enumVals[i]);
				}
			}

			return numList;

		}

		catch (SQLException e) {
			System.out.println(e);
		}

		return numList;
	}

	public int logBehavior(String name, String date, String type, String comment) {
		new_query = "insert into behavior values('" + name + "', '" + date + "', '" + type + "', '" + comment + "', '"
				+ usrName + "');";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(new_query);
		} catch (SQLException e) {
			System.out.println(e);
		}

		return 0;
	}

	public String getTeacher(String usrName) {
		String tchrName = "";

		new_query = "select teacherID from students where ID='" + usrName + "';";

		try {
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(new_query);

			if (result.next()) {
				tchrName = result.getString(1);
			}
			return tchrName;
		} catch (SQLException e) {
			System.out.println(e);
		}

		return tchrName;
	}
	
	public void setTeacher(String teacherName) {
		this.tchrName = teacherName;
	}

	public void setUser(String usrName) {
		this.usrName = usrName;
	}

	public void resetTrueAll() {
		this.trueAll = false;
	}

	public void setTrueAll(boolean trueAll) {
		this.trueAll = trueAll;
	}
}
// end class
