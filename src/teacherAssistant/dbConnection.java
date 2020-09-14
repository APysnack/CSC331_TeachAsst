package teacherAssistant;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.mysql.cj.util.StringUtils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

// error_flag keys:
// [0 : success, 1 : UsrDoesNotExist, 2: Duplicate User, 3 : too short, 4: function specific]

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
		} 
		catch (SQLException e) {
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
				return 0;
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
				return 0;
			}
		}

		catch (SQLException e) {
			System.out.println(e);
			return 3;
		}
	} // end remove user function

	public JTable getJTable(String tbl) {

		String new_query = "Select * from " + tbl + ";";

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

} // end class
