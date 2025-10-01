package com.hr.payroll;

import java.util.Scanner; // Import the Scanner class for input
import java.text.NumberFormat; // Import for currency formatting
import java.util.Locale; // Import for locale-specific formatting

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String userInput;

        // Initialize a formatter for USD based on US locale (a standard for professional reports)
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        System.out.println("--- HR System Integration Demo with User Input & Formatted Output ---");

        // Loop to enforce a minimum validation requirement (must contain '|')
        do {
            System.out.println("\nPlease enter the employee data in the legacy format:");
            // NOTE: I've updated the prompt to reflect the *correct* Legacy format: LastName, FirstName
            System.out.println("Format: LastName, FirstName|Salary (e.g., Smith, Jane|75000.00)");
            System.out.print("Enter Legacy Data: ");

            userInput = scanner.nextLine();

            if (!userInput.contains("|")) {
                System.err.println("\n[ERROR] Input is invalid. Missing the required '|' separator. Please try again.");
            }

        } while (!userInput.contains("|")); // Repeat until a '|' is found

        scanner.close(); // Close the scanner

        // 1. Create the incompatible legacy data (Adaptee) using validated input
        LegacyEmployeeRecord legacyRecord = new LegacyEmployeeRecord(userInput);
        System.out.println("\nLegacy Data Access: Retrieved raw data \"" + legacyRecord.getRawData() + "\"");

        // 2. Instantiate the Adapter, wrapping the legacy object
        IStandardEmployee standardizedEmployee = new LegacyToStandardAdapter(legacyRecord);

        // 3. The new HR system (Client) calls the standard methods on the Adapter
        System.out.println("\nNew HR System Accessing STANDARDIZED & ENRICHED Data:");

        // Output now uses Title Case from the Adapter
        System.out.println("Employee Full Name (Standardized/Cleaned): " + standardizedEmployee.getFullName());

        // Retrieve the raw salary value (double) from the Adapter
        double rawSalary = standardizedEmployee.getYearlySalary();

        // Apply professional formatting to the raw double using the NumberFormat instance
        String formattedSalary = currencyFormatter.format(rawSalary);

        // Display the professionally formatted output
        System.out.println("Annual Salary (Standardized/Formatted): " + formattedSalary);

        // Output the new data enrichment tag (The innovative feature)
        System.out.println("Data Quality Assessment: " + standardizedEmployee.getSalaryAssessment());

        System.out.println("\nProcess finished with exit code 0");
    }
}