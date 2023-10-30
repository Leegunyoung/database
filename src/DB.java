import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DB {
    private static final String url = "jdbc:mysql://localhost:3306/mydb?serverTimeZone=UTC";
    private static final String user = "root";
    private static final String password = "llgy88388!"; // 사용자 비밀번호
    private static boolean foreignKeyConstraintsAdded = false;
    private static JFrame jframe = new JFrame();
    private static String[] rangeOptions = {"전체", "부서", "성별", "연봉"};
    private static String[][] rangeOptionsDetail = {
            {"Research", "Administration", "Headquarters"},
            {"M", "F"}
    };

    private static JCheckBox op1 = new JCheckBox("선택", true);
    private static JCheckBox op2 = new JCheckBox("Name", true);
    private static JCheckBox op3 = new JCheckBox("Ssn", true);
    private static JCheckBox op4 = new JCheckBox("Bdate", true);
    private static JCheckBox op5 = new JCheckBox("Address", true);
    private static JCheckBox op6 = new JCheckBox("Sex", true);
    private static JCheckBox op7 = new JCheckBox("Salary", true);
    private static JCheckBox op8 = new JCheckBox("Supervisor", true);
    private static JCheckBox op9 = new JCheckBox("Department", true);
    private static JButton search = new JButton("검색");

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

    public static void processUserRequests(Connection conn) {
        // 사용자로부터 요청을 처리할 함수들
        try {
            //listAllEmployees(conn);
            //searchEmployee(conn);
            deleteEmployee(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel1.add(new JLabel("검색 범위"));
        JComboBox<String> searchRange = new JComboBox<>(rangeOptions);
        JComboBox<String> searchRangeDetail = new JComboBox<>(rangeOptionsDetail[0]);
        searchRangeDetail.setEnabled(false);
        searchRangeDetail.setVisible(false);
        JTextField salaryTextField = new JTextField(10);
        salaryTextField.setVisible(false);

        searchRange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = searchRange.getSelectedIndex();
                if (idx == 0) {
                    searchRangeDetail.setEnabled(false);
                    searchRangeDetail.setVisible(false);
                    salaryTextField.setVisible(false);
                } else if (idx == 3) {
                    searchRangeDetail.setEnabled(false);
                    searchRangeDetail.setVisible(false);
                    salaryTextField.setVisible(true);
                } else {
                    searchRangeDetail.setModel(new DefaultComboBoxModel<>(rangeOptionsDetail[idx - 1]));
                    searchRangeDetail.setEnabled(true);
                    searchRangeDetail.setVisible(true);
                    salaryTextField.setVisible(false);
                }
            }
        });

        panel1.add(searchRange);
        panel1.add(searchRangeDetail);
        panel1.add(salaryTextField);
        panel2.add(new JLabel("검색 항목"));

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String header[] = {"선택", "Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
                DefaultTableModel tableModel = new DefaultTableModel(header, 0);

                StringBuilder sql = new StringBuilder("SELECT ");
                boolean anySelected = false;

                if (op2.isSelected()) {
                    sql.append("e.fname, ");
                    anySelected = true;
                }
                if (op3.isSelected()) {
                    sql.append("e.ssn, ");
                    anySelected = true;
                }
                if (op4.isSelected()) {
                    sql.append("e.bdate, ");
                    anySelected = true;
                }
                if (op5.isSelected()) {
                    sql.append("e.address, ");
                    anySelected = true;
                }
                if (op6.isSelected()) {
                    sql.append("e.sex, ");
                    anySelected = true;
                }
                if (op7.isSelected()) {
                    sql.append("e.salary, ");
                    anySelected = true;
                }
                if (op8.isSelected()) {
                    sql.append("e.super_ssn, ");
                    anySelected = true;
                }
                if (op9.isSelected()) {
                    sql.append("d.dname, ");
                    anySelected = true;
                }

                // Remove the trailing comma and add the rest of the SQL query
                if (anySelected) {
                    sql.setLength(sql.length() - 2); // Remove the last ", "
                    sql.append(" FROM EMPLOYEE e INNER JOIN DEPARTMENT d ON e.dno = d.dnumber");
                } else {
                    // If no columns are selected, select all columns
                    sql.append(" * "); // Select all columns
                    sql.append(" FROM EMPLOYEE e INNER JOIN DEPARTMENT d ON e.dno = d.dnumber");
                }

                String fname = ""; // Initialize with an empty string
                String ssn = "";
                String bdate = "";
                String addr = "";
                String sex = "";
                double salary = 0.0; // Initialize with a default value
                String super_emp = "";
                String dept = "";
                try (ResultSet rs = stmt.executeQuery(sql.toString())) {
                    while (rs.next()) {

                        int columnIndex = 0;

                        if (op2.isSelected()) {
                            fname = rs.getString(++columnIndex);
                        }
                        if (op3.isSelected()) {
                            ssn = rs.getString(++columnIndex);
                        }
                        if (op4.isSelected()) {
                            bdate = rs.getString(++columnIndex);
                        }
                        if (op5.isSelected()) {
                            addr = rs.getString(++columnIndex);
                        }
                        if (op6.isSelected()) {
                            sex = rs.getString(++columnIndex);
                        }
                        if (op7.isSelected()) {
                            salary = rs.getDouble(++columnIndex);
                        }
                        if (op8.isSelected()) {
                            super_emp = rs.getString(++columnIndex);
                        }
                        if (op9.isSelected()) {
                            dept = rs.getString(++columnIndex);
                        }

                        System.out.printf("%s / %s / %s / %s / %s / %f / %s / %s\n", fname, ssn, bdate, addr, sex, salary, super_emp, dept);

                        // Create an array for each row of data
                        String[] row = new String[9];  // 9 columns

                        // Check if each checkbox is selected and add the corresponding data to the row
                        if (op2.isSelected()) row[1] = fname;
                        if (op3.isSelected()) row[2] = ssn;
                        if (op4.isSelected()) row[3] = bdate;
                        if (op5.isSelected()) row[4] = addr;
                        if (op6.isSelected()) row[5] = sex;
                        if (op7.isSelected()) row[6] = String.valueOf(salary);
                        if (op8.isSelected()) row[7] = super_emp;
                        if (op9.isSelected()) row[8] = dept;

                        // Add the row to the DefaultTableModel
                        tableModel.addRow(row);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                JTable jtable = new JTable(tableModel);
                JScrollPane scrollpane = new JScrollPane(jtable);
                scrollpane.setPreferredSize(new Dimension(1200, 500));
                JPanel panel4 = new JPanel();
                panel4.add(scrollpane);
                jframe.add(scrollpane, BorderLayout.CENTER);
                jframe.revalidate();
                jframe.repaint();
            }
        });


        panel2.add(op2);
        panel2.add(op3);
        panel2.add(op4);
        panel2.add(op5);
        panel2.add(op6);
        panel2.add(op7);
        panel2.add(op8);
        panel2.add(op9);
        panel2.add(search);

        panel3.setLayout(new GridLayout(2, 1));
        panel3.add(panel1);
        panel3.add(panel2);

        jframe.add(panel3, BorderLayout.NORTH);
        jframe.setTitle("Employee Retrieve System");
        jframe.setVisible(true);
        jframe.setSize(1600, 900);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);

        return conn;
    };


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
            e.printStackTrace();
            // Handle exceptions as needed
        }
    }

    // 검색 조건을 입력 받아 조건을 만족하는 직원을 삭제합니다.
    public static void deleteEmployee(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("삭제할 직원의 조건을 다음줄에 입력하세요");
        String deleteCondition = scanner.nextLine();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT Fname,Lname FROM EMPLOYEE WHERE " + deleteCondition);
            List<String> deletedEmployees = new ArrayList<>();

            // 삭제할 직원의 이름을 가져옵니다.
            while (rs.next()) {
                String fname = rs.getString("Fname") + " " + rs.getString("Lname");
                deletedEmployees.add(fname);
                System.out.println(fname);
            }

            // 실제로 삭제된 직원 수 확인
            int actualDeletedCount = deletedEmployees.size();

            // 삭제된 직원 정보 출력

            if (actualDeletedCount > 0) {
                System.out.println("다음 직원이 삭제되었습니다:");
                for (String employee : deletedEmployees) {
                    System.out.println(employee);
                }
            } else {
                System.out.println("삭제된 직원이 없습니다.");
            }
        }
    }


}










