import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBTest3 {
	private static final String url = "jdbc:mysql://localhost:3306/company?serverTimeZone=UTC";
	private static final String user = "root";
	private static final String password = "1234";
	private static JFrame jframe = new JFrame();
	private static String[] rangeOptions = { "전체", "부서", "성별", "연봉" };
	private static String[] addrangeOptions = { "전체", "부서", "성별", "연봉" };
	private static boolean foreignKeyConstraintsAdded = false;
	private static String[][] rangeOptionsDetail = { { "Research", "Administration", "Headquarters" }, { "M", "F" } };
	private static String[][] addrangeOptionsDetail = { { "Research", "Administration", "Headquarters" },
			{ "M", "F" } };
	private static JCheckBox op1 = new JCheckBox("Name", true);
	private static JCheckBox op2 = new JCheckBox("Ssn", true);
	private static JCheckBox op3 = new JCheckBox("Bdate", true);
	private static JCheckBox op4 = new JCheckBox("Address", true);
	private static JCheckBox op5 = new JCheckBox("Sex", true);
	private static JCheckBox op6 = new JCheckBox("Salary", true);
	private static JCheckBox op7 = new JCheckBox("Supervisor", true);
	private static JCheckBox op8 = new JCheckBox("Department", true);
	private static JTable table;
	private static DefaultTableModel model;
	private static JLabel selectedEmp = new JLabel("선택한 직원 : ");
	private static JLabel selectedEmpNum = new JLabel("선택한 직원 수 : 0");

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
		jframe.setSize(1200, 800);
		jframe.setLocationRelativeTo(null);
		jframe.setResizable(false);
		jframe.setLayout(new BorderLayout());

		JPanel searchPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel searchPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel searchPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JComboBox<String> rangeComboBox = new JComboBox<>(rangeOptions);
		JComboBox<String> rangeDetailComboBox = new JComboBox<>(rangeOptionsDetail[0]);
		JComboBox<String> addrangeComboBox = new JComboBox<>(addrangeOptions);
		JComboBox<String> addrangeDetailComboBox = new JComboBox<>(addrangeOptionsDetail[0]);
		JTextField salaryTextField1 = new JTextField(10);
		JTextField salaryTextField2 = new JTextField(10);

		searchPanel1.add(new JLabel("검색 범위:"));
		searchPanel1.add(rangeComboBox);
		searchPanel1.add(rangeDetailComboBox);
		searchPanel1.add(salaryTextField1);
		rangeDetailComboBox.setEnabled(false);
		rangeDetailComboBox.setVisible(false);
		salaryTextField1.setVisible(false);

		searchPanel2.add(new JLabel("추가 검색 범위:"));
		searchPanel2.add(addrangeComboBox);
		searchPanel2.add(addrangeDetailComboBox);
		searchPanel2.add(salaryTextField2);
		addrangeDetailComboBox.setEnabled(false);
		addrangeDetailComboBox.setVisible(false);
		salaryTextField2.setVisible(false);
		searchPanel2.setVisible(false);

		searchPanel3.add(op1);
		searchPanel3.add(op2);
		searchPanel3.add(op3);
		searchPanel3.add(op4);
		searchPanel3.add(op5);
		searchPanel3.add(op6);
		searchPanel3.add(op7);
		searchPanel3.add(op8);

		rangeComboBox.addActionListener(e -> {
			rangeDetailComboBox.removeAllItems();
			switch (rangeComboBox.getSelectedIndex()) {
			case 0:
				rangeDetailComboBox.setEnabled(false);
				rangeDetailComboBox.setVisible(false);
				salaryTextField1.setVisible(false);
				salaryTextField1.setText("");
				searchPanel2.setVisible(false);
				break;
			case 1:
				int idx1 = rangeComboBox.getSelectedIndex();
				rangeDetailComboBox.setEnabled(true);
				rangeDetailComboBox.setVisible(true);
				salaryTextField1.setVisible(false);
				salaryTextField1.setText("");
				rangeDetailComboBox.setModel(new DefaultComboBoxModel<>(rangeOptionsDetail[idx1 - 1]));
				searchPanel2.setVisible(true);
				break;
			case 2:
				int idx2 = rangeComboBox.getSelectedIndex();
				rangeDetailComboBox.setEnabled(true);
				rangeDetailComboBox.setVisible(true);
				salaryTextField1.setVisible(false);
				salaryTextField1.setText("");
				rangeDetailComboBox.setModel(new DefaultComboBoxModel<>(rangeOptionsDetail[idx2 - 1]));
				searchPanel2.setVisible(true);
				break;
			case 3:
				rangeDetailComboBox.setEnabled(false);
				rangeDetailComboBox.setVisible(false);
				salaryTextField1.setVisible(true);
				searchPanel2.setVisible(true);
			default:
				break;
			}
		});

		addrangeComboBox.addActionListener(e -> {
			addrangeDetailComboBox.removeAllItems();
			switch (addrangeComboBox.getSelectedIndex()) {
			case 0:
				addrangeDetailComboBox.setEnabled(false);
				addrangeDetailComboBox.setVisible(false);
				salaryTextField2.setVisible(false);
				salaryTextField2.setText("");
				break;
			case 1:
				int idx1 = addrangeComboBox.getSelectedIndex();
				addrangeDetailComboBox.setEnabled(true);
				addrangeDetailComboBox.setVisible(true);
				salaryTextField2.setVisible(false);
				salaryTextField2.setText("");
				addrangeDetailComboBox.setModel(new DefaultComboBoxModel<>(addrangeOptionsDetail[idx1 - 1]));
				break;
			case 2:
				int idx2 = addrangeComboBox.getSelectedIndex();
				addrangeDetailComboBox.setEnabled(true);
				addrangeDetailComboBox.setVisible(true);
				salaryTextField2.setVisible(false);
				salaryTextField2.setText("");
				addrangeDetailComboBox.setModel(new DefaultComboBoxModel<>(addrangeOptionsDetail[idx2 - 1]));
				break;
			case 3:
				addrangeDetailComboBox.setEnabled(false);
				addrangeDetailComboBox.setVisible(false);
				salaryTextField2.setVisible(true);
			default:
				break;
			}
		});

		JButton searchBtn = new JButton("검색");
		searchBtn.addActionListener(e -> {
			selectedEmpNum.setText("선택한 직원 수 : 0");
			selectedEmp.setText("선택한 직원 : ");
			searchEmployees((String) rangeComboBox.getSelectedItem(), (String) rangeDetailComboBox.getSelectedItem(),
					salaryTextField1.getText(), (String) addrangeComboBox.getSelectedItem(),
					(String) addrangeDetailComboBox.getSelectedItem(), salaryTextField2.getText());
		});
		searchPanel3.add(searchBtn);

		JButton minButton = new JButton("최솟값");
		minButton.addActionListener(e -> {
			String range = (String) rangeComboBox.getSelectedItem();
			String detail = (String) rangeDetailComboBox.getSelectedItem();
			String salary = salaryTextField1.getText();
			String addrange = (String) addrangeComboBox.getSelectedItem();
			String adddetail = (String) addrangeDetailComboBox.getSelectedItem();
			String addsalary = salaryTextField2.getText();
			double minSalary = getMinSalary(range, detail, salary, addrange, adddetail, addsalary);
			JOptionPane.showMessageDialog(jframe, "최솟값: " + minSalary);
		});
		searchPanel3.add(minButton);

		JButton maxButton = new JButton("최댓값");
		maxButton.addActionListener(e -> {
			String range = (String) rangeComboBox.getSelectedItem();
			String detail = (String) rangeDetailComboBox.getSelectedItem();
			String salary = salaryTextField1.getText();
			String addrange = (String) addrangeComboBox.getSelectedItem();
			String adddetail = (String) addrangeDetailComboBox.getSelectedItem();
			String addsalary = salaryTextField2.getText();
			double maxSalary = getMaxSalary(range, detail, salary, addrange, adddetail, addsalary);
			JOptionPane.showMessageDialog(jframe, "최댓값: " + maxSalary);
		});
		searchPanel3.add(maxButton);

		JPanel searchPanel = new JPanel(new GridLayout(2, 1));
		searchPanel1.add(searchPanel2);
		searchPanel.add(searchPanel1);
		searchPanel.add(searchPanel3);
		jframe.add(searchPanel, BorderLayout.NORTH);

		JPanel btnPanel = new JPanel(new FlowLayout());
		JButton deleteBtn = new JButton("삭제");
		deleteBtn.addActionListener(e -> deleteSelectedEmployees(rangeComboBox, rangeDetailComboBox, salaryTextField1,
				addrangeComboBox, addrangeDetailComboBox, salaryTextField2));
		btnPanel.add(deleteBtn);

		JButton addBtn = new JButton("직원 추가");
		addBtn.addActionListener(e -> createAddEmployeeForm(conn));
		btnPanel.add(addBtn);

		JButton modifyBtn = new JButton("직원 수정");
		modifyBtn.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				Object[][] selectedData = new Object[1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					Object value = table.getValueAt(selectedRow, i);
					if (value instanceof Double) {
						selectedData[0][i] = String.valueOf(value);
					} else {
						selectedData[0][i] = value;
					}
				}
				modifyEmployeeForm(conn, selectedData);
				jframe.add(btnPanel, BorderLayout.SOUTH);
				jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jframe.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(jframe, "수정할 직원을 선택해 주세요.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		});

		btnPanel.add(modifyBtn);
		JPanel btmPanel = new JPanel(new GridLayout(3, 1));
		btmPanel.add(selectedEmpNum);
		btmPanel.add(selectedEmp);
		btmPanel.add(btnPanel);
		jframe.add(btmPanel, BorderLayout.SOUTH);
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

//	private static String[] getMinSalaryAndEmployeeName() {
//		String[] minSalaryAndEmployee = new String[2]; // 인덱스 0: 최소 급여, 인덱스 1: 해당 급여를 받는 직원
//
//		try (Connection connection = DriverManager.getConnection(url, user, password);
//				Statement statement = connection.createStatement()) {
//			String minQuery = "SELECT MIN(Salary) AS MinSalary, concat(E.fname, ' ', E.minit, ' ', E.lname) as Name "
//					+ "FROM EMPLOYEE E";
//			ResultSet minResult = statement.executeQuery(minQuery);
//
//			if (minResult.next()) {
//				minSalaryAndEmployee[0] = String.valueOf(minResult.getDouble("MinSalary"));
//				minSalaryAndEmployee[1] = minResult.getString("Name");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return minSalaryAndEmployee;
//	}

	private static double getMinSalary(String range, String detail, String salary, String addrange, String adddetail,
			String addsalary) {
		double minSalary = 0.0;
		try (Connection connection = DriverManager.getConnection(url, user, password);
				Statement statement = connection.createStatement()) {
			StringBuilder minQuery = new StringBuilder(
					"SELECT MIN(E.Salary) AS MinSalary FROM EMPLOYEE E, DEPARTMENT D");
			List<String> conditions = new ArrayList<>();
			if ("부서".equals(range)) {
				conditions.add("D.Dname = '" + detail + "'");
			} else if ("성별".equals(range)) {
				conditions.add("E.Sex = '" + detail + "'");
			} else if ("연봉".equals(range) && !salary.isEmpty()) {
				conditions.add("E.Salary > " + salary);
			}

			if ("부서".equals(addrange)) {
				conditions.add("D.Dname = '" + adddetail + "'");
			} else if ("성별".equals(addrange)) {
				conditions.add("E.Sex = '" + adddetail + "'");
			} else if ("연봉".equals(addrange) && !addsalary.isEmpty()) {
				conditions.add("E.Salary > " + addsalary);
			}

			if (!conditions.isEmpty()) {
				minQuery.append(" WHERE E.Dno = D.Dnumber AND ");
				minQuery.append(String.join(" AND ", conditions));
			}
			ResultSet minResult = statement.executeQuery(minQuery.toString());

			if (minResult.next()) {
				minSalary = minResult.getDouble("MinSalary");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return minSalary;
	}

	private static double getMaxSalary(String range, String detail, String salary, String addrange, String adddetail,
			String addsalary) {
		double maxSalary = 0.0;
		try (Connection connection = DriverManager.getConnection(url, user, password);
				Statement statement = connection.createStatement()) {
			StringBuilder maxQuery = new StringBuilder(
					"SELECT MAX(E.Salary) AS MaxSalary FROM EMPLOYEE E, DEPARTMENT D");
			List<String> conditions = new ArrayList<>();
			if ("부서".equals(range)) {
				conditions.add("D.Dname = '" + detail + "'");
			} else if ("성별".equals(range)) {
				conditions.add("E.Sex = '" + detail + "'");
			}

			if ("부서".equals(addrange)) {
				conditions.add("D.Dname = '" + adddetail + "'");
			} else if ("성별".equals(addrange)) {
				conditions.add("E.Sex = '" + adddetail + "'");
			}

			if (!conditions.isEmpty()) {
				maxQuery.append(" WHERE E.Dno = D.Dnumber AND ");
				maxQuery.append(String.join(" AND ", conditions));
			}
			ResultSet maxResult = statement.executeQuery(maxQuery.toString());

			if (maxResult.next()) {
				maxSalary = maxResult.getDouble("MaxSalary");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxSalary;
	}

	private static void searchEmployees(String range, String detail, String msal, String addrange, String adddetail,
			String addmsal) {
		JPanel resultTable = new JPanel();
		resultTable.removeAll();
		resultTable.revalidate();
		List<String> headerList = new ArrayList<>();
		headerList.add("선택");
		String query = "SELECT ";
		boolean anySelected = false;

		if (op1.isSelected()) {
			query += " concat(E.fname, ' ', E.minit, ' ', E.lname) as Name, ";
			headerList.add("Name");
			anySelected = true;
		}
		if (op2.isSelected()) {
			query += "E.ssn, ";
			headerList.add("Ssn");
			anySelected = true;
		}
		if (op3.isSelected()) {
			query += "E.bdate, ";
			headerList.add("Bdate");
			anySelected = true;
		}
		if (op4.isSelected()) {
			query += "E.address, ";
			headerList.add("Address");
			anySelected = true;
		}
		if (op5.isSelected()) {
			query += "E.sex, ";
			headerList.add("Sex");
			anySelected = true;
		}
		if (op6.isSelected()) {
			query += "E.salary, ";
			headerList.add("Salary");
			anySelected = true;
		}
		if (op7.isSelected()) {
			query += "E.super_ssn, ";
			headerList.add("Supervisor");
			anySelected = true;
		}
		if (op8.isSelected()) {
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

		if ("부서".equals(addrange)) {
			query += " AND D.Dname = '" + adddetail + "'";
		} else if ("성별".equals(addrange)) {
			query += " AND E.Sex = '" + adddetail + "'";
		} else if (addmsal.length() != 0) {
			double dsal = Double.parseDouble(addmsal);
			query += " AND Salary > '" + dsal + "'";
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
				if (op1.isSelected()) {
					name = resultSet.getString("Name");
					rowData.add(name);
				}
				if (op2.isSelected()) {
					ssn = resultSet.getString("Ssn");
					rowData.add(ssn);
				}
				if (op3.isSelected()) {
					bdate = resultSet.getDate("Bdate").toString();
					rowData.add(bdate);
				}
				if (op4.isSelected()) {
					addr = resultSet.getString("Address");
					rowData.add(addr);
				}
				if (op5.isSelected()) {
					sex = resultSet.getString("Sex");
					rowData.add(sex);
				}
				if (op6.isSelected()) {
					salary = resultSet.getDouble("Salary");
					rowData.add(salary);
				}
				if (op7.isSelected()) {
					super_emp = resultSet.getString("Super_ssn");
					rowData.add(super_emp);
				}
				if (op8.isSelected()) {
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
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				String empTxt = "선택한 직원 : ";
				String numTxt = "선택한 직원 수 : ";
				int cnt = 0;
				if (e.getType() == TableModelEvent.UPDATE) {
					for (int row = 0; row < model.getRowCount(); row++) {
						boolean isChecked = (Boolean) model.getValueAt(row, 0);
						String name = (String) model.getValueAt(row, 1);
						if (isChecked) {
							cnt += 1;
							empTxt += name;
							empTxt += ",  ";
						}
					}
					numTxt += String.valueOf(cnt);
					selectedEmpNum.setText(numTxt);
					empTxt = empTxt.substring(0, empTxt.length() - 3);
					selectedEmp.setText(empTxt);
				}
			}
		});
	}

	private static void createAddEmployeeForm(Connection conn) {
		JFrame addEmployeeFrame = new JFrame("직원 추가");
		addEmployeeFrame.setLayout(new GridLayout(11, 2));

		addEmployeeFrame.add(new JLabel("Name: "));
		JTextField nameField = new JTextField();
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

		JButton addButton = new JButton("추가");
		addButton.addActionListener(e -> {
			String name = nameField.getText();
			String Ssn = SsnField.getText();
			String Bdate = BdateField.getText();
			String Address = AddressField.getText();
			String Sex = SexField.getText();
			String Salary = SalaryField.getText();
			String Supervisor = SupervisorField.getText();
			String Department = DepartmentField.getText();
			String[] nameParts = name.split(" ");
			String Fname = nameParts[0];
			String Minit = nameParts.length > 2 ? nameParts[1] : "";
			String Lname = nameParts.length > 2 ? nameParts[2] : nameParts[1];

			try {
				insertEmployee(Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Supervisor, Department);
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
			addEmployeeFrame.dispose();
		});
		addEmployeeFrame.add(addButton);

		addEmployeeFrame.setSize(400, 300);
		addEmployeeFrame.setVisible(true);
	}

	private static void insertEmployee(String Fname, String Minit, String Lname, String Ssn, String Bdate,
			String Address, String Sex, String Salary, String Super_ssn, String Department) throws SQLException {
		String Dno = null;
		System.out.println(Department);
		Connection connection = DriverManager.getConnection(url, user, password);
		String departmentQuery = "SELECT Dnumber FROM department WHERE Dname = ?";
		try (PreparedStatement departmentPreparedStatement = connection.prepareStatement(departmentQuery)) {
			departmentPreparedStatement.setString(1, Department);
			ResultSet departmentResultSet = departmentPreparedStatement.executeQuery();
			if (departmentResultSet.next()) {
				Dno = departmentResultSet.getString("Dnumber");
				System.out.println(Department);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		String insertSql = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		System.out.println("Values: " + Fname + ", " + Minit + ", " + Lname + ", " + Ssn + ", " + Bdate + ", " + Address
				+ ", " + Sex + ", " + Salary + ", " + Super_ssn + ", " + Dno);

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

	private static void deleteSelectedEmployees(JComboBox<String> rangeComboBox, JComboBox<String> rangeDetailComboBox,
			JTextField salaryTextField, JComboBox<String> addrangeComboBox, JComboBox<String> addrangeDetailComboBox,
			JTextField salaryTextField1) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			Boolean isChecked = (Boolean) model.getValueAt(i, 0);
			if (isChecked) {
				String fname = (String) model.getValueAt(i, 1);
				String fullName = fname;
				String ssn = (String) model.getValueAt(i, 2);
				try (Connection connection = DriverManager.getConnection(url, user, password);
						PreparedStatement preparedStatement = connection
								.prepareStatement("DELETE FROM EMPLOYEE WHERE Ssn = ?")) {
					StringBuilder relatedEmployees = new StringBuilder();
					try (PreparedStatement relatedStmt = connection
							.prepareStatement("SELECT Fname, Lname FROM EMPLOYEE WHERE Super_ssn = ?")) {
						relatedStmt.setString(1, ssn);
						ResultSet resultSet = relatedStmt.executeQuery();
						while (resultSet.next()) {
							relatedEmployees.append(resultSet.getString("Fname")).append(" ")
									.append(resultSet.getString("Lname")).append(", ");
						}
					}
					preparedStatement.setString(1, ssn);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						model.removeRow(i);
						searchEmployees((String) rangeComboBox.getSelectedItem(),
								(String) rangeDetailComboBox.getSelectedItem(), salaryTextField.getText(),
								(String) addrangeComboBox.getSelectedItem(),
								(String) addrangeDetailComboBox.getSelectedItem(), salaryTextField1.getText());
						String message = fullName + "'employee 가 삭제 됩니다..";
						if (relatedEmployees.length() > 0) {
							message += "\npk-fk로 관련되서 삭제되는 employee들: "
									+ relatedEmployees.substring(0, relatedEmployees.length() - 2);
						}
						System.out.println(message);
						JOptionPane.showMessageDialog(jframe, message, "삭제 완료!", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(jframe, "삭제를 다시 시도해보세요!", "Deletion Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(jframe, "삭제를 다시 시도해보세요!", "Deletion Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private static void modifyEmployeeForm(Connection conn, Object[][] selectedData) {
		JFrame modifyEmployeeFrame = new JFrame("직원 수정");
		modifyEmployeeFrame.setLayout(new GridLayout(11, 2));

		JTextField nameField = new JTextField((String) selectedData[0][1]);
		modifyEmployeeFrame.add(nameField);

		JTextField SsnField = new JTextField((String) selectedData[0][2]);
		modifyEmployeeFrame.add(SsnField);

		JTextField BdateField = new JTextField((String) selectedData[0][3]);
		modifyEmployeeFrame.add(BdateField);

		JTextField AddressField = new JTextField((String) selectedData[0][4]);
		modifyEmployeeFrame.add(AddressField);

		JTextField SexField = new JTextField((String) selectedData[0][5]);
		modifyEmployeeFrame.add(SexField);

		JTextField SalaryField = new JTextField((String) selectedData[0][6]);
		modifyEmployeeFrame.add(SalaryField);

		JTextField SupervisorField = new JTextField((String) selectedData[0][7]);
		modifyEmployeeFrame.add(SupervisorField);

		JTextField DepartmentField = new JTextField((String) selectedData[0][8]);
		modifyEmployeeFrame.add(DepartmentField);

		JButton modifyButton = new JButton("수정");
		modifyButton.addActionListener(e -> {
			String name = nameField.getText();
			String Ssn = SsnField.getText();
			String Bdate = BdateField.getText();
			String Address = AddressField.getText();
			String Sex = SexField.getText();
			String Salary = SalaryField.getText();
			String Supervisor = SupervisorField.getText();
			String Department = DepartmentField.getText();
			String[] nameParts = name.split(" ");
			String Fname = nameParts[0];
			String Minit = nameParts.length > 2 ? nameParts[1] : "";
			String Lname = nameParts.length > 2 ? nameParts[2] : nameParts[1];
			List<String> selectedSSNs = new ArrayList<>();
			selectedSSNs.add(Ssn);
			try {
				modifyEmployee(conn, selectedSSNs, Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Supervisor,
						Department);
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
			modifyEmployeeFrame.dispose();
		});
		modifyEmployeeFrame.add(modifyButton);
		modifyEmployeeFrame.setSize(400, 300);
		modifyEmployeeFrame.setVisible(true);
	}

	private static void modifyEmployee(Connection conn, List<String> selectedSSNs, String Fname, String Minit,
			String Lname, String Ssn, String Bdate, String Address, String Sex, String Salary, String Super_ssn,
			String Department) throws SQLException {
		String Dno = null;
		Connection connection = DriverManager.getConnection(url, user, password);
		String departmentQuery = "SELECT Dnumber FROM department WHERE Dname = ?";
		try (PreparedStatement departmentPreparedStatement = connection.prepareStatement(departmentQuery)) {
			departmentPreparedStatement.setString(1, Department);
			ResultSet departmentResultSet = departmentPreparedStatement.executeQuery();
			if (departmentResultSet.next()) {
				Dno = departmentResultSet.getString("Dnumber");
				System.out.println(Department);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		String updateSql = "UPDATE EMPLOYEE SET Fname = ?, Minit = ?, Lname = ?, Ssn=?,Bdate = ?, Address = ?, Sex = ?, Salary = ?, Super_ssn = ?, Dno = ? WHERE Ssn = ?";
		try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
			for (String ssn : selectedSSNs) {
				updateStatement.setString(1, Fname);
				updateStatement.setString(2, Minit);
				updateStatement.setString(3, Lname);
				updateStatement.setString(4, Ssn);
				updateStatement.setString(5, Bdate);
				updateStatement.setString(6, Address);
				updateStatement.setString(7, Sex);
				updateStatement.setString(8, Salary);
				updateStatement.setString(9, Super_ssn);
				updateStatement.setString(10, Dno);
				updateStatement.setString(11, ssn);
				updateStatement.addBatch();
			}
			int[] rowsAffected = updateStatement.executeBatch();
			int totalRowsAffected = 0;
			for (int rows : rowsAffected) {
				totalRowsAffected += rows;
			}
			if (totalRowsAffected == selectedSSNs.size()) {
				JOptionPane.showMessageDialog(jframe, "선택된 직원 정보가 성공적으로 수정되었습니다.", "수정 완료!", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(jframe, "수정에 실패했습니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}