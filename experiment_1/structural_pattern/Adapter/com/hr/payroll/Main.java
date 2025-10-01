package com.hr.payroll;
import java.util.Scanner; 
import java.text.NumberFormat; 
import java.util.Locale; 
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        System.out.println("--- HR System Integration Demo with User Input & Formatted Output ---");
        do {
            System.out.println("\nPlease enter the employee data in the legacy format:");
            System.out.println("Format: LastName, FirstName|Salary (e.g., Smith, Jane|75000.00)");
            System.out.print("Enter Legacy Data: ");
            userInput = scanner.nextLine();
            if (!userInput.contains("|")) {
                System.err.println("\n[ERROR] Input is invalid. Missing the required '|' separator. Please try again.");
            }

        } while (!userInput.contains("|")); 
        scanner.close(); 
        LegacyEmployeeRecord legacyRecord = new LegacyEmployeeRecord(userInput);
        System.out.println("\nLegacy Data Access: Retrieved raw data \"" + legacyRecord.getRawData() + "\"");
        IStandardEmployee standardizedEmployee = new LegacyToStandardAdapter(legacyRecord);
        System.out.println("\nNew HR System Accessing STANDARDIZED & ENRICHED Data:");
        System.out.println("Employee Full Name (Standardized/Cleaned): " + standardizedEmployee.getFullName());
        double rawSalary = standardizedEmployee.getYearlySalary();
        String formattedSalary = currencyFormatter.format(rawSalary);
        System.out.println("Annual Salary (Standardized/Formatted): " + formattedSalary);
        System.out.println("Data Quality Assessment: " + standardizedEmployee.getSalaryAssessment());
        System.out.println("\nProcess finished with exit code 0");
    }
}
