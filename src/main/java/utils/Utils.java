package utils;

import models.Employee;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Employee> getEmployeeDummyData(){
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Ahmed", "Ali", "single"));
        employees.add(new Employee(2L, "Uzair", "Syed", "married"));
        employees.add(new Employee(3L, "Asim", "Aslam", "married"));
        employees.add(new Employee(4L, "Danish", "hamid", "married"));
        employees.add(new Employee(5L, "Anas", "Muhammad", "single"));
        return employees;
    }
}
