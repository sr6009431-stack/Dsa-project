import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Employee {
    private final int empId;
    private final String name;
    private final String department;
    private final String designation;
    private final double salary;

    public Employee(int empId, String name, String department, String designation, double salary) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
    }

    public int getEmpId() { return empId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }
    public double getSalary() { return salary; }

    // One row of the employee table
    @Override
    public String toString() {
        return String.format("%-8d %-16s %-12s %-16s %10.0f",
                empId, name, department, designation, salary);
    }

    // Single-line detail used by the search options
    public String toDetail() {
        return String.format("Emp ID: %d, Name: %s, Dept: %s, Designation: %s, Salary: %.0f",
                empId, name, department, designation, salary);
    }
}

class Node {
    Employee data;
    Node next;

    public Node(Employee data) {
        this.data = data;
        this.next = null;
    }
}

class EmployeeLinkedList {
    private Node head;
    private Node tail;
    private int size;

    // Insert at the end - O(1) because of the tail pointer
    public void addLast(Employee e) {
        Node n = new Node(e);
        if (head == null) {
            head = tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    // Unlink the node with the given ID - O(n)
    public boolean removeById(int id) {
        Node prev = null;
        Node cur = head;
        while (cur != null && cur.data.getEmpId() != id) {
            prev = cur;
            cur = cur.next;
        }
        if (cur == null) return false;          // not found
        if (prev == null) head = cur.next;      // deleting the head
        else prev.next = cur.next;
        if (cur == tail) tail = prev;           // deleting the tail
        size--;
        return true;
    }

    // Linear Search by name - O(n)
    public Employee findByName(String name) {
        String key = name.trim();
        Node cur = head;
        while (cur != null) {
            if (cur.data.getName().equalsIgnoreCase(key)) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    // Bubble Sort by salary, highest first - O(n^2), stable
    public void sortBySalary() {
        if (head == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node cur = head;
            while (cur.next != null) {
                if (cur.data.getSalary() < cur.next.data.getSalary()) {
                    Employee temp = cur.data;
                    cur.data = cur.next.data;
                    cur.next.data = temp;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }

    // Selection Sort by name, A to Z - O(n^2)
    public void sortByName() {
        for (Node i = head; i != null && i.next != null; i = i.next) {
            Node min = i;
            for (Node j = i.next; j != null; j = j.next) {
                if (j.data.getName().compareToIgnoreCase(min.data.getName()) < 0) {
                    min = j;
                }
            }
            if (min != i) {
                Employee temp = i.data;
                i.data = min.data;
                min.data = temp;
            }
        }
    }

    public void displayAll() {
        if (head == null) {
            System.out.println("No employees to display.");
            return;
        }
        System.out.println(String.format("%-8s %-16s %-12s %-16s %10s",
                "Emp ID", "Name", "Department", "Designation", "Salary"));
        System.out.println("-".repeat(66));
        for (Node cur = head; cur != null; cur = cur.next) {
            System.out.println(cur.data);
        }
    }

    public int getSize() { return size; }
}

class EmployeeManager {
    private final EmployeeLinkedList list = new EmployeeLinkedList();
    private final HashMap<Integer, Employee> empMap = new HashMap<>();
    private final HashMap<String, Integer> deptCount = new HashMap<>();

    public boolean addEmployee(Employee e) {
        if (empMap.containsKey(e.getEmpId())) return false;   // duplicate ID
        list.addLast(e);
        empMap.put(e.getEmpId(), e);
        deptCount.merge(e.getDepartment(), 1, Integer::sum);
        return true;
    }

    public boolean deleteEmployee(int id) {
        Employee e = empMap.get(id);
        if (e == null) return false;
        list.removeById(id);
        empMap.remove(id);
        int left = deptCount.merge(e.getDepartment(), -1, Integer::sum);
        if (left == 0) deptCount.remove(e.getDepartment());
        return true;
    }

    public Employee searchById(int id) { return empMap.get(id); }          // O(1) average

    public Employee searchByName(String name) { return list.findByName(name); }  // O(n)

    public void sortBySalary() { list.sortBySalary(); }

    public void sortByName() { list.sortByName(); }

    public void displayAll() { list.displayAll(); }

    public void showDeptReport() {
        if (deptCount.isEmpty()) {
            System.out.println("No employees to report.");
            return;
        }
        System.out.println(String.format("%-14s %s", "Department", "Employees"));
        System.out.println("-".repeat(25));
        for (Map.Entry<String, Integer> en : deptCount.entrySet()) {
            System.out.println(String.format("%-14s %d", en.getKey(), en.getValue()));
        }
    }
}

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final EmployeeManager manager = new EmployeeManager();

    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ");
            System.out.println();
            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> manager.displayAll();
                case 3 -> searchById();
                case 4 -> searchByName();
                case 5 -> {
                    manager.sortBySalary();
                    System.out.println("Sorted by salary (highest first).");
                    manager.displayAll();
                }
                case 6 -> {
                    manager.sortByName();
                    System.out.println("Sorted by name (A to Z).");
                    manager.displayAll();
                }
                case 7 -> deleteEmployee();
                case 8 -> manager.showDeptReport();
                case 9 -> System.out.println("Thank you. Exiting...");
                default -> System.out.println("Invalid choice! Please enter 1 to 9.");
            }
        } while (choice != 9);
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("      EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Add Employee");
        System.out.println("2. Display All Employees");
        System.out.println("3. Search by Employee ID");
        System.out.println("4. Search by Name");
        System.out.println("5. Sort by Salary (High to Low)");
        System.out.println("6. Sort by Name (A to Z)");
        System.out.println("7. Delete Employee");
        System.out.println("8. Department-wise Count");
        System.out.println("9. Exit");
        System.out.println("----------------------------------------");
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static void addEmployee() {
        int id = readInt("Enter Employee ID: ");
        if (id <= 0) {
            System.out.println("Invalid ID! ID must be a positive number.");
            return;
        }
        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine().trim();
        System.out.print("Enter Designation: ");
        String desig = sc.nextLine().trim();
        System.out.print("Enter Salary: ");
        double salary;
        try {
            salary = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException ex) {
            salary = -1;
        }

        if (name.isEmpty()) {
            System.out.println("Invalid name! Name cannot be empty.");
        } else if (dept.isEmpty()) {
            System.out.println("Invalid department! Department cannot be empty.");
        } else if (salary <= 0) {
            System.out.println("Invalid salary! Salary must be greater than 0.");
        } else if (!manager.addEmployee(new Employee(id, name, dept, desig, salary))) {
            System.out.println("Employee ID already exists. Record not added.");
        } else {
            System.out.println("Employee added successfully.");
        }
    }

    private static void searchById() {
        int id = readInt("Enter Employee ID to search: ");
        Employee e = manager.searchById(id);
        System.out.println(e == null ? "Employee not found." : e.toDetail());
    }

    private static void searchByName() {
        System.out.print("Enter name to search: ");
        Employee e = manager.searchByName(sc.nextLine());
        System.out.println(e == null ? "Employee not found." : e.toDetail());
    }

    private static void deleteEmployee() {
        int id = readInt("Enter Employee ID to delete: ");
        System.out.println(manager.deleteEmployee(id)
                ? "Employee deleted successfully." : "Employee not found.");
    }
}