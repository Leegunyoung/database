import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTest3 {
	private static final String url = "jdbc:mysql://localhost:3306/mydb?serverTimeZone=UTC";
	private static final String user = "root";
	private static final String password = "llgy88388!";
	private static JFrame jframe = new JFrame();
	private static String[] rangeOptions = { "전체", "부서", "성별", "연봉" };
	private static boolean foreignKeyConstraintsAdded = false;
	private static String[][] rangeOptionsDetail = { { "Research", "Administration", "Headquarters" }, { "M", "F" } };
	private static JCheckBox op2 = new JCheckBox("Name", true);
	private static JCheckBox op3 = new JCheckBox("Ssn", true);
	private static JCheckBox op4 = new JCheckBox("Bdate", true);
	private static JCheckBox op5 = new JCheckBox("Address", true);
	private static JCheckBox op6 = new JCheckBox("Sex", true);
	private static JCheckBox op7 = new JCheckBox("Salary", true);
	private static JCheckBox op8 = new JCheckBox("Supervisor", true);
	private static JCheckBox op9 = new JCheckBox("Department", true);
	private static JTable table;
	private static DefaultTableModel model;

	public static void main(String[] args) {
		try (Connection conn = getConnection()) {
			if (!foreignKeyConstraintsAdded) {
				addForeignKeyConstraintWithCascade(conn);
				foreignKeyConstraintsAdded = true;
			}
			processUserRequests(conn);
		} catch (SQLException e) {
			System.out.println("연결 불가");
			e.printStackTrace();
		}
	}

	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	private static void processUserRequests(Connection conn) {
		jframe.setTitle("Employee Retrieve System");
		jframe.setSize(1600, 900);
		jframe.setLocationRelativeTo(null);
		jframe.setResizable(false);
		jframe.setLayout(new BorderLayout());
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);

		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel searchPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JComboBox<String> rangeComboBox = new JComboBox<>(rangeOptions);
		JComboBox<String> rangeDetailComboBox = new JComboBox<>(rangeOptionsDetail[0]);
		JTextField salaryTextField = new JTextField(10);
		searchPanel.add(new JLabel("검색 범위:"));
		searchPanel.add(rangeComboBox);
		searchPanel.add(rangeDetailComboBox);
		searchPanel.add(salaryTextField);
		rangeDetailComboBox.setEnabled(false);
		rangeDetailComboBox.setVisible(false);
		salaryTextField.setVisible(false);
		searchPanel2.add(op2);
		searchPanel2.add(op3);
		searchPanel2.add(op4);
		searchPanel2.add(op5);
		searchPanel2.add(op6);
		searchPanel2.add(op7);
		searchPanel2.add(op8);
		searchPanel2.add(op9);

		rangeComboBox.addActionListener(e -> {
			rangeDetailComboBox.removeAllItems();
			switch (rangeComboBox.getSelectedIndex()) {
			case 0:
				rangeDetailComboBox.setEnabled(false);
				rangeDetailComboBox.setVisible(false);
				salaryTextField.setVisible(false);
				salaryTextField.setText("");
				break;
			case 1:
				int idx1 = rangeComboBox.getSelectedIndex();
				rangeDetailComboBox.setEnabled(true);
				rangeDetailComboBox.setVisible(true);
				salaryTextField.setVisible(false);
				salaryTextField.setText("");
				rangeDetailComboBox.setModel(new DefaultComboBoxModel<>(rangeOptionsDetail[idx1 - 1]));
				break;
			case 2:
				int idx2 = rangeComboBox.getSelectedIndex();
				rangeDetailComboBox.setEnabled(true);
				rangeDetailComboBox.setVisible(true);
				salaryTextField.setVisible(false);
				salaryTextField.setText("");
				rangeDetailComboBox.setModel(new DefaultComboBoxModel<>(rangeOptionsDetail[idx2 - 1]));
				break;
			case 3:
				rangeDetailComboBox.setEnabled(false);
				rangeDetailComboBox.setVisible(false);
				salaryTextField.setVisible(true);
			default:
				break;
			}
		});

		JButton searchBtn = new JButton("검색");
		searchBtn.addActionListener(e -> {
			searchEmployees((String) rangeComboBox.getSelectedItem(), (String) rangeDetailComboBox.getSelectedItem(),
					salaryTextField.getText());
		});
		searchPanel2.add(searchBtn);
		JPanel search = new JPanel(new GridLayout(2, 1));
		search.add(searchPanel);
		search.add(searchPanel2);
		jframe.add(search, BorderLayout.NORTH);

		JPanel btnPanel = new JPanel(new FlowLayout());
		JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(e -> deleteSelectedEmployees(rangeComboBox, rangeDetailComboBox, salaryTextField));
		btnPanel.add(deleteBtn);

		JButton addBtn = new JButton("직원 추가");
		addBtn.addActionListener(e -> createAddEmployeeForm(conn)); // Call the method for adding an employee
		btnPanel.add(addBtn);

		jframe.add(btnPanel, BorderLayout.SOUTH);

		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}

	public static void addForeignKeyConstraintWithCascade(Connection conn) {
		try (Statement stmt = conn.createStatement()) {
			String[] queries = {
					"ALTER TABLE PROJECT ADD CONSTRAINT Dnum_fk FOREIGN KEY (Dnum) REFERENCES DEPARTMENT(Dnumber) ON DELETE CASCADE;",
					"ALTER TABLE DEPENDENT ADD CONSTRAINT Essn_fk FOREIGN KEY (Essn) REFERENCES EMPLOYEE(Ssn) ON DELETE CASCADE;",
					"ALTER TABLE DEPT_LOCATIONS ADD CONSTRAINT Dnumber_fk FOREIGN KEY (Dnumber) REFERENCES DEPARTMENT(Dnumber) ON DELETE CASCADE;",
					"ALTER TABLE WORKS_ON ADD CONSTRAINT Essn_fk2 FOREIGN KEY (Essn) REFERENCES EMPLOYEE(Ssn) ON DELETE CASCADE;",
					"ALTER TABLE WORKS_ON ADD CONSTRAINT Pno_fk FOREIGN KEY (Pno) REFERENCES PROJECT(Pnumber) ON DELETE CASCADE;",
					"ALTER TABLE DEPARTMENT ADD CONSTRAINT Mgr_ssn_fk FOREIGN KEY (Mgr_ssn) REFERENCES EMPLOYEE(Ssn) ON DELETE CASCADE;",
					"ALTER TABLE EMPLOYEE ADD CONSTRAINT EMPSUPERFK FOREIGN KEY (Super_ssn) REFERENCES EMPLOYEE(Ssn) ON DELETE CASCADE;",
					"ALTER TABLE EMPLOYEE ADD CONSTRAINT EMPDNOFK FOREIGN KEY (Dno) REFERENCES DEPARTMENT(Dnumber) ON DELETE CASCADE;" };
			for (String query : queries) {
				stmt.executeUpdate(query);
			}
		} catch (SQLException e) {
			System.out.println("fk-pk가 이미 설정되어있으니까 추가하려면 쿼리문에 추가하시면 됩니당!");
		}
	}

	private static void searchEmployees(String range, String detail, String msal) {
		JPanel resultTable = new JPanel();
		resultTable.removeAll();
		resultTable.revalidate();
		List<String> headerList = new ArrayList<>();
		headerList.add("선택");
		String query = "SELECT ";
		boolean anySelected = false;

		if (op2.isSelected()) {
			query += " concat(E.fname, ' ', E.minit, ' ', E.lname) as Name, ";
			headerList.add("Name");
			anySelected = true;
		}
		if (op3.isSelected()) {
			query += "E.ssn, ";
			headerList.add("Ssn");
			anySelected = true;
		}
		if (op4.isSelected()) {
			query += "E.bdate, ";
			headerList.add("Bdate");
			anySelected = true;
		}
		if (op5.isSelected()) {
			query += "E.address, ";
			headerList.add("Address");
			anySelected = true;
		}
		if (op6.isSelected()) {
			query += "E.sex, ";
			headerList.add("Sex");
			anySelected = true;
		}
		if (op7.isSelected()) {
			query += "E.salary, ";
			headerList.add("Salary");
			anySelected = true;
		}
		if (op8.isSelected()) {
			query += "E.super_ssn, ";
			headerList.add("Supervisor");
			anySelected = true;
		}
		if (op9.isSelected()) {
			query += "D.dname, ";
			headerList.add("Department");
			anySelected = true;
		}

		if (anySelected) {
			query = query.substring(0, query.length() - 2);
			query += " FROM EMPLOYEE E LEFT JOIN DEPARTMENT D ON E.Dno = D.Dnumber";
		} else {
			query += " * ";
			query += " FROM EMPLOYEE E LEFT JOIN DEPARTMENT D ON E.Dno = D.Dnumber";
		}

		if ("부서".equals(range)) {
			query += " WHERE D.Dname = '" + detail + "'";
		} else if ("성별".equals(range)) {
			query += " WHERE E.Sex = '" + detail + "'";
		} else if (msal.length() != 0) {
			double dsal = Double.parseDouble(msal);
			query += " WHERE Salary > '" + dsal + "'";
		}

		try (Connection connection = DriverManager.getConnection(url, user, password);
				Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery(query);
			String[] header = headerList.toArray(new String[headerList.size()]);
			model = new DefaultTableModel(header, 0) {
				private static final long serialVersionUID = 1L;

				public Class<?> getColumnClass(int col) {
					if (col == 0) {
						return Boolean.class;
					} else {
						return String.class;
					}
				}

				public boolean isCellEditable(int row, int col) {
					if (col == 0) {
						return true;
					} else {
						return false;
					}
				}
			};
			model.setRowCount(0);

			String name = "";
			String ssn = "";
			String bdate = "";
			String addr = "";
			String sex = "";
			double salary = 0.0;
			String super_emp = "";
			String dept = "";

			while (resultSet.next()) {
				List<Object> rowData = new ArrayList<>();
				rowData.add(false);
				if (op2.isSelected()) {
					name = resultSet.getString("Name");
					rowData.add(name);
				}
				if (op3.isSelected()) {
					ssn = resultSet.getString("Ssn");
					rowData.add(ssn);
				}
				if (op4.isSelected()) {
					bdate = resultSet.getDate("Bdate").toString();
					rowData.add(bdate);
				}
				if (op5.isSelected()) {
					addr = resultSet.getString("Address");
					rowData.add(addr);
				}
				if (op6.isSelected()) {
					sex = resultSet.getString("Sex");
					rowData.add(sex);
				}
				if (op7.isSelected()) {
					salary = resultSet.getDouble("Salary");
					rowData.add(salary);
				}
				if (op8.isSelected()) {
					super_emp = resultSet.getString("Super_ssn");
					rowData.add(super_emp);
				}
				if (op9.isSelected()) {
					dept = resultSet.getString("Dname");
					rowData.add(dept);
				}

				Object[] rowArray = rowData.toArray();
				model.addRow(rowArray);

				if (table == null) {
					table = new JTable(model);
					JScrollPane scrollpane = new JScrollPane(table);
					scrollpane.setPreferredSize(new Dimension(1200, 500));
					resultTable.removeAll();
					resultTable.add(scrollpane);
					jframe.add(resultTable, BorderLayout.CENTER);
					jframe.revalidate();
				} else {
					table.setModel(model);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private static void createAddEmployeeForm(Connection conn) {
		JFrame addEmployeeFrame = new JFrame("직원 추가");
		addEmployeeFrame.setLayout(new GridLayout(11, 2));

		addEmployeeFrame.add(new JLabel("Name: "));
		JTextField nameField = new JTextField(); // Name을 받을 필드
		addEmployeeFrame.add(nameField);

		addEmployeeFrame.add(new JLabel("Ssn: "));
		JTextField SsnField = new JTextField();
		addEmployeeFrame.add(SsnField);

		addEmployeeFrame.add(new JLabel("Bdate: "));
		JTextField BdateField = new JTextField();
		addEmployeeFrame.add(BdateField);

		addEmployeeFrame.add(new JLabel("Address: "));
		JTextField AddressField = new JTextField();
		addEmployeeFrame.add(AddressField);

		addEmployeeFrame.add(new JLabel("Sex: "));
		JTextField SexField = new JTextField();
		addEmployeeFrame.add(SexField);

		addEmployeeFrame.add(new JLabel("Salary: "));
		JTextField SalaryField = new JTextField();
		addEmployeeFrame.add(SalaryField);

		addEmployeeFrame.add(new JLabel("Supervisor: "));
		JTextField SupervisorField = new JTextField();
		addEmployeeFrame.add(SupervisorField);


		addEmployeeFrame.add(new JLabel("Department: "));
		JTextField DepartmentField = new JTextField();
		addEmployeeFrame.add(DepartmentField);

		// ... Add the rest of the fields

		JButton addButton = new JButton("추가");
		addButton.addActionListener(e -> {
			String name = nameField.getText();
			String Ssn = SsnField.getText();
			String Bdate = BdateField.getText();
			String Address = AddressField.getText();
			String Sex = SexField.getText();
			String Salary = SalaryField.getText();
			String  Supervisor = SupervisorField.getText();
			String Department = DepartmentField.getText();

			String[] nameParts = name.split(" ");
			String Fname = nameParts[0]; // Fname에 해당
			String Minit = nameParts.length > 2 ? nameParts[1] : ""; // Minit
			String Lname = nameParts.length > 2 ? nameParts[2] : nameParts[1]; // Lname

			// Extract values from other fields
//            Integer ssn = Integer.parseInt(Ssn);
//            Date bDate = Date.valueOf(Bdate); // Assuming bDateField is a date in "YYYY-MM-DD" format
//            Double salary = Double.parseDouble(Salary);
//            Integer supervisor = Integer.parseInt(Supervisor); // Assuming supervisor ID is an Integer

			try {
				insertEmployee(conn, Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Supervisor, Department);
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
			addEmployeeFrame.dispose(); // Close the form
		});
		addEmployeeFrame.add(addButton);

		addEmployeeFrame.setSize(400, 300);
		addEmployeeFrame.setVisible(true);
	}


	private static void insertEmployee(
			Connection conn,
			String Fname,
			String Minit,
			String Lname,
			String Ssn,
			String Bdate,
			String Address,
			String Sex,
			String Salary,
			String Super_ssn,
			String Department
	) throws SQLException {
		String Dno = null; // Default value, assuming department IDs start from 1
		System.out.println(Department);
		Connection connection = DriverManager.getConnection(url, user, password);
		String departmentQuery = "SELECT Dnumber FROM department WHERE Dname = ?";
		try (PreparedStatement departmentPreparedStatement = connection.prepareStatement(departmentQuery)) {
			departmentPreparedStatement.setString(1, Department); // Set the Department name at the first position
			ResultSet departmentResultSet = departmentPreparedStatement.executeQuery();

			if (departmentResultSet.next()) {
				Dno = departmentResultSet.getString("Dnumber");
				System.out.println(Department);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		String insertSql = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		System.out.println("Values: " + Fname + ", " + Minit + ", " + Lname + ", " + Ssn +", " + Bdate +", " + Address +", " + Sex +", " + Salary +", " + Super_ssn + ", " + Dno); // 모든 값 출력

		try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
			insertStatement.setString(1, Fname);
			insertStatement.setString(2, Minit);
			insertStatement.setString(3, Lname);
			insertStatement.setString(4, Ssn);
			insertStatement.setString(5, Bdate);
			insertStatement.setString(6, Address);
			insertStatement.setString(7, Sex);
			insertStatement.setString(8, Salary);
			insertStatement.setString(9, Super_ssn);
			insertStatement.setString(10, Dno);

			int rowsAffected = insertStatement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Employee added successfully.");
			} else {
				System.out.println("Failed to add employee.");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}






	private static void deleteSelectedEmployees(JComboBox<String> rangeComboBox, JComboBox<String> rangeDetailComboBox, JTextField salaryTextField) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);

            if (isChecked) {
                String fname = (String) model.getValueAt(i, 1); // FNAME
                String fullName = fname;
                String ssn = (String) model.getValueAt(i, 2);

                try (Connection connection = DriverManager.getConnection(url, user, password);
                     PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM EMPLOYEE WHERE Ssn = ?")) {

                    // Get related employees
                    StringBuilder relatedEmployees = new StringBuilder();
                    try (PreparedStatement relatedStmt = connection.prepareStatement("SELECT Fname, Lname FROM EMPLOYEE WHERE Super_ssn = ?")) {
                        relatedStmt.setString(1, ssn);
                        ResultSet resultSet = relatedStmt.executeQuery();
                        while (resultSet.next()) {
                            relatedEmployees.append(resultSet.getString("Fname")).append(" ").append(resultSet.getString("Lname")).append(", ");
                        }
                    }

                    preparedStatement.setString(1, ssn);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        model.removeRow(i);
                        searchEmployees((String) rangeComboBox.getSelectedItem(), (String) rangeDetailComboBox.getSelectedItem(), salaryTextField.getText());

                        String message = fullName + "'employee 가 삭제 됩니다..";
                        if (relatedEmployees.length() > 0) {
                            message += "\npk-fk로 관련되서 삭제되는 employee들: " + relatedEmployees.substring(0, relatedEmployees.length() - 2);
                        }
                        System.out.println(message);

                        // GUI message
                        JOptionPane.showMessageDialog(jframe, message, "삭제 완료!", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(jframe, "삭제를 다시 시도해보세요!", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(jframe, "삭제를 다시 시도해보세요!", "Deletion Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}