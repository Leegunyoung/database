import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DB {
    private static final String url = "jdbc:mysql://localhost:3306/mydb?serverTimeZone=UTC";
    private static final String user = "root";
    private static final String password = "llgy88388!";
    private static JFrame jframe = new JFrame();
    private static String[] rangeOptions = {"전체", "부서", "성별", "연봉"};
    private static boolean foreignKeyConstraintsAdded = false;
    private static String[][] rangeOptionsDetail = {
            {"Research", "Administration", "Headquarters"},
            {"M", "F"}

    };

    private static JTable table;


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
        jframe.setTitle("Employee Manager");
        jframe.setSize(1000, 700);
        jframe.setLayout(new BorderLayout());

        // Table
        String[] columnNames = {"선택", "Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                } else {
                    return String.class;
                }
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        jframe.add(scrollPane, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        JComboBox<String> rangeComboBox = new JComboBox<>(rangeOptions);
        JComboBox<String> rangeDetailComboBox = new JComboBox<>();
        searchPanel.add(new JLabel("검색 범위:"));
        searchPanel.add(rangeComboBox);
        searchPanel.add(rangeDetailComboBox);

        // Update range detail options dynamically
        rangeComboBox.addActionListener(e -> {
            rangeDetailComboBox.removeAllItems();
            switch (rangeComboBox.getSelectedIndex()) {
                case 1:  // 부서
                    for (String s : rangeOptionsDetail[0]) rangeDetailComboBox.addItem(s);
                    break;
                case 2:  // 성별
                    for (String s : rangeOptionsDetail[1]) rangeDetailComboBox.addItem(s);
                    break;
                default:
                    break;
            }
        });

        JButton searchBtn = new JButton("검색");
        searchBtn.addActionListener(e -> {
            // TODO: Implement search logic based on range and detail options
            searchEmployees((String) rangeComboBox.getSelectedItem(), (String) rangeDetailComboBox.getSelectedItem());
        });
        searchPanel.add(searchBtn);
        jframe.add(searchPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(e -> deleteSelectedEmployees(rangeComboBox, rangeDetailComboBox));
        btnPanel.add(deleteBtn);

        JButton addBtn = new JButton("직원 추가");
        addBtn.addActionListener(e -> {
            // TODO: Implement add employee function
            System.out.println("직원 추가 기능 구현 필요");
        });
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
                    "ALTER TABLE EMPLOYEE ADD CONSTRAINT EMPDNOFK FOREIGN KEY (Dno) REFERENCES DEPARTMENT(Dnumber) ON DELETE CASCADE;"
            };
            for (String query : queries) {
                stmt.executeUpdate(query);
            }
        } catch (SQLException e) {
            System.out.println("fk-pk가 이미 설정되어있으니까 추가하려면 쿼리문에 추가하시면 됩니당!");
            // 예외 처리
        }
    }

    private static void searchEmployees(String range, String detail) {
        String query = "SELECT * FROM EMPLOYEE E LEFT JOIN DEPARTMENT D ON E.Dno = D.Dnumber";
        if ("부서".equals(range)) {
            query += " WHERE D.Dname = '" + detail + "'";
        } else if ("성별".equals(range)) {
            query += " WHERE E.Sex = '" + detail + "'";
        }
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            while (resultSet.next()) {
                model.addRow(new Object[]{
                        false, resultSet.getString("Fname"), resultSet.getString("Ssn"), resultSet.getDate("Bdate").toString(),
                        resultSet.getString("Address"), resultSet.getString("Sex"), resultSet.getDouble("Salary"),
                        resultSet.getString("Super_ssn"), resultSet.getString("Dname")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void deleteSelectedEmployees(JComboBox<String> rangeComboBox, JComboBox<String> rangeDetailComboBox) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);

            if (isChecked) {
                String fname = (String) model.getValueAt(i, 1); // FNAME
                String fullName = fname; // FNAME 변수만 fullName 변수에 할당
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
                        searchEmployees((String) rangeComboBox.getSelectedItem(), (String) rangeDetailComboBox.getSelectedItem());

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

