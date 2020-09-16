package teacherAssistant;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mysql.cj.util.StringUtils;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

// error_flag keys:
// [0 : add success, 1 : DoesNotExist, 2: Duplicate, 3 : too short, 
//	4: function specific, 5: delete success, 6: edit success]

public class dbConnection {
	String name;
	String password;
	String url;
	String query;
	Connection conn;

	dbConnection() {
		try {
			this.name = "root";
			this.password = "SQLPassword";
			this.url = "jdbc:mysql://localhost:3306/teachasst";

			Connection conn = DriverManager.getConnection(url, name, password);
			this.conn = conn;
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public int connectUser(String userName, String userPW) {
		int privilege = 0;
		String new_query = "Select * from users where ID = '" + userName + "' AND Password = '" + userPW + "'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(new_query);

			if (result.next()) {
				privilege = (int) result.getInt(3);
				return privilege;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return privilege;

	} // end function

	public int createUser(String userName, String userPW, int Privilege) {
		String new_query = "Insert into users values ('" + userName + "', '" + userPW + "', " + Privilege + ");";

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

	public int editUser(String userName, String newName, String newPW, int newPriv) {

		String new_query = "update users set ID='" + newName + "', Password='" + newPW + "', Privilege=" + newPriv
				+ " where ID='" + userName + "';";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
			if (rowsAffected == 0) {
				return 1;
			} else {
				return 6;
			}
		}

		catch (SQLIntegrityConstraintViolationException e) {
			return 2;
		}

		catch (SQLException e) {
			System.out.println(e);
			return 3;
		}
	} // end edit user function

	public int removeRow(String table, String id) {
		String new_query = "Delete from " + table + " where ID='" + id + "';";

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

		String new_query = "";
		
		char first_letter = tbl.charAt(0);
		
		// built so user can override the default query 
		if(first_letter == '!') {
			new_query = tbl.substring(1, tbl.length());
		}
		else {
			new_query = "Select * from " + tbl + ";";
		}

		try {

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);

			Statement stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			ResultSet result = stmt.getResultSet();
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

			JTable table = new JTable(data, columnNames) {
				public Class getColumnClass(int column) {
					for (int row = 0; row < getRowCount(); row++) {
						Object o = getValueAt(row, column);
						if (o != null) {
							return o.getClass();
						}
					}
					return Object.class;
				}
			};

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
		String new_query = "insert into assignments values (" + id + ", '" + title + "', '" + details + "', " + points
				+ ", '" + dueDate + "');";

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
		String new_query = "select size from class_tables where ID=" + tblNum + ";";
		int size = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(new_query);
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
		String new_query = "select id, size from class_tables;";

		List<Integer> tblSizes = new ArrayList<>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(new_query);

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
		String new_query = "select * from students where tableID=" + tblNum + ";";
		ArrayList tblList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			ResultSet result = stmt.getResultSet();
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
	public ArrayList getAllStdnts() {
		String new_query = "select * from students;";
		ArrayList tblList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			ResultSet result = stmt.getResultSet();
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
		String resetTable = "update students set tableID=0 where tableID=" + tableID + ";";

		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(resetTable);

		} catch (SQLException e) {
			System.out.println(e);
			return 1;
		}

		for (int i = 0; i < seatList.size(); i++) {
			String name = seatList.get(i);
			String addName = "update students set tableID=" + tableID + " where ID='" + name + "';";

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

	public int addClsTbl(int tblID, int tblSize) {
		String new_query = "insert into class_tables values(" + tblID + "," + tblSize + ");";

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
		String new_query = "delete from class_tables where ID=" + tblID + ";";

		try {
			Statement stmt = conn.createStatement();
			int rowsAffected = stmt.executeUpdate(new_query);
			if (rowsAffected == 0) {
				return 1;
			} else {
				String resetTable = "update students set tableID=0 where tableID=" + tblID + ";";

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

	public void randomizeTables() {
		// list: {tblName1, tblSize1, tblName2, tblSize2..}
		List<Integer> tableContents = getClassTblSizes();

		ArrayList studentNames = getAllStdnts();
		Collections.shuffle(studentNames);

		int i = 0;
		int j = 0;
		int k = 1;

		// while k is less than the number of tables
		while (k <= (tableContents.size() / 2)) {

			// gets the capacity of the table i
			int tableCapacity = tableContents.get(i + 1);

			List<String> stdnts = new ArrayList<>();

			// makes a list of students the size of table i's capacity
			for (int l = 0; l < tableCapacity; l++) {
				if (j < studentNames.size()) {
					stdnts.add(studentNames.get(j).toString());
				}
				j++;
			}

			// updates table i with the listed students
			updateTableSeats(tableContents.get(i), stdnts);

			k++;
			i = i + 2;
		}
	}

	public int addGrades(int assignmentID, String assignmentTitle, String stdntName, double stdntGrade, String mode) {
		String new_query = "";

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
					+ "'," + stdntGrade + ");";

			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(new_query);

				updateGrade(stdntName);

				return 0;
			} catch (SQLIntegrityConstraintViolationException e) {
				return 2;
			} catch (SQLException e) {
				return 3;
			}
		}
	}

	public ArrayList getAllAsgnmts() {
		String new_query = "select * from assignments;";
		ArrayList asgnmtList = new ArrayList();

		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery(new_query);
			ResultSet result = stmt.getResultSet();
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

	public int updateGrade(String studentID) {

		double grade = getGrade(studentID);

		String new_query = "update students set grade=" + grade + " where ID='" + studentID + "';";

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
		String new_query = "select grades.studentID, grades.grade, assignments.points, assignments.id, "
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
			ResultSet result = stmt.executeQuery();

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
			System.out.println(e);
		}
		return 0;
	}
}
// end class
