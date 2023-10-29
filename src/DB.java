import java.sql.*;
import java.util.Scanner;

public class DB {
    private static final String url= "jdbc:mysql://localhost:3306/mydb?serverTimeZone=UTC";
    private static final String user="root";
    private static final String password="llgy88388!"; // 사용자 비밀번호

    public static void main(String[] args){
        try(Connection conn=getConnection()){
            System.out.println("정상 연결");
            listAllEmployees(conn);
            searchEmployee(conn);
            deleteEmployee(conn);
            addEmployee(conn);
        } catch(SQLException e){
            System.out.println("연결 불가");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }

    // EMPLOYEE 테이블의 모든 직원 정보를 출력합니다.
    public static void listAllEmployees(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT e.Fname, e.Salary, d.Dname FROM EMPLOYEE e JOIN DEPARTMENT d ON e.Dno = d.Dnumber");
            while(rs.next()){
                String fname = rs.getString("Fname");
                double salary = rs.getDouble("Salary");
                String department = rs.getString("Dname");
                System.out.printf("이름: %s, 급여: %.2f, 부서: %s\n", fname, salary, department);
            }
        }
    }

    // 사용자 입력을 받아 조건을 만족하는 직원 정보를 출력합니다.
    public static void searchEmployee(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("검색 조건을 입력하세요 (예: Salary > 30000): ");
        String condition = scanner.nextLine();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT Fname, Salary, Dno FROM EMPLOYEE WHERE " + condition);
            while(rs.next()){
                String fname = rs.getString("Fname");
                double salary = rs.getDouble("Salary");
                int dno = rs.getInt("Dno");
                System.out.printf("이름: %s, 급여: %.2f, 부서: %d\n", fname, salary, dno);
            }
        }
    }

    // 검색 조건을 입력 받아 조건을 만족하는 직원을 삭제합니다.
    public static void deleteEmployee(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("삭제할 직원의 조건을 입력하세요 (예: Fname = 'John'): ");
        String deleteCondition = scanner.nextLine();

        try (Statement stmt = conn.createStatement()) {
            int deletedRows = stmt.executeUpdate("DELETE FROM EMPLOYEE WHERE " + deleteCondition);
            System.out.println(deletedRows + " 개의 행이 삭제되었습니다.");
        }
    }

    // 새로운 직원 정보를 EMPLOYEE 테이블에 삽입합니다.
    public static void addEmployee(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("새 직원 정보를 입력하세요 (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno): ");
        String newEmployeeDetails = scanner.nextLine();

        String[] details = newEmployeeDetails.split(",");
        String query = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < details.length; i++) {
                pstmt.setString(i + 1, details[i].trim());
            }
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("새 직원이 성공적으로 삽입되었습니다!");
            }
        }
    }
}
