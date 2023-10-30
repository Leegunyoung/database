//////import javax.swing.*;
//////import java.sql.Connection;
//////import java.sql.ResultSet;
//////import java.sql.SQLException;
//////import java.sql.Statement;
//////import java.util.Scanner;
//////
//////public class Swing extends JFrame{
//////    public Swing(){
//////        setTitle("java swing 만들기");
//////        setSize(300,300);
//////        setVisible(true);
//////    }
//////    public static void main(String[] args){
//////        Swing frame=new Swing();
//////    }
//////}
//////    // 사용자 입력을 받아 조건을 만족하는 직원 정보를 출력합니다.
//////    public static void searchEmployee(Connection conn) throws SQLException {
//////        Scanner scanner = new Scanner(System.in);
//////        System.out.println("검색 조건을 입력하세요 (예: Salary > 30000): ");
//////        String condition = scanner.nextLine();
//////
//////        try (Statement stmt = conn.createStatement()) {
//////            ResultSet rs = stmt.executeQuery("SELECT Fname, Salary, Dno FROM EMPLOYEE WHERE " + condition);
//////            while(rs.next()){
//////                String fname = rs.getString("Fname");
//////                double salary = rs.getDouble("Salary");
//////                int dno = rs.getInt("Dno");
//////                System.out.printf("이름: %s, 급여: %.2f, 부서: %d\n", fname, salary, dno);
//////            }
//////        }
//////    }
////
////public static String findReferencedTable(Connection conn, String tableName, String deleteCondition) throws SQLException {
////        try (PreparedStatement pstmt = conn.prepareStatement(
////        "SELECT TABLE_NAME " +
////        "FROM information_schema.REFERENTIAL_CONSTRAINTS " +
////        "WHERE CONSTRAINT_SCHEMA = ? " +
////        "AND TABLE_NAME = ? " +
////        "AND DELETE_RULE = 'RESTRICT'")) {
////        pstmt.setString(1, conn.getCatalog());
////        pstmt.setString(2, tableName);
////        ResultSet rs = pstmt.executeQuery();
////        if (rs.next()) {
////        return rs.getString("TABLE_NAME");
////        }
////        }
////        return null;
////        }
////
//
//// 새로운 직원 정보를 EMPLOYEE 테이블에 삽입합니다.
//public static void addEmployee(Connection conn) throws SQLException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("새 직원 정보를 입력하세요 (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno): ");
//        String newEmployeeDetails = scanner.nextLine();
//
//        String[] details = newEmployeeDetails.split(",");
//        String query = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
//        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
//        for (int i = 0; i < details.length; i++) {
//        pstmt.setString(i + 1, details[i].trim());
//        }
//        int rowsInserted = pstmt.executeUpdate();
//        if (rowsInserted > 0) {
//        System.out.println("새 직원이 성공적으로 삽입되었습니다!");
//        }
//        }
//        }
//        }