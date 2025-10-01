package com.hr.payroll;

public class LegacyToStandardAdapter implements IStandardEmployee {
    private LegacyEmployeeRecord adaptee;

    public LegacyToStandardAdapter(LegacyEmployeeRecord adaptee) {
        this.adaptee = adaptee;
    }

    /**
     * Helper method to convert a string to Title Case (first letter of each word capitalized).
     * This is part of the Name Cleaning innovation.
     */
    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder converted = new StringBuilder();
        boolean convertNext = true;

        // Iterate through characters to apply Title Case logic
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }
        return converted.toString();
    }

    /**
     * Helper method to check if a name component contains any digits.
     */
    private boolean containsDigit(String name) {
        return name.matches(".*\\d+.*");
    }

    @Override
    public String getFullName() {
        String raw = adaptee.getRawData();
        String namePart = raw.split("\\|")[0].trim();

        // Second split to separate LastName from FirstName (using ', ')
        String[] nameComponents = namePart.split(", ");

        // 1. FORMAT VALIDATION
        if (nameComponents.length < 2) {
            System.err.println("[ADAPTER ERROR] Name is not in 'LastName, FirstName' format. Returning raw name.");
            return namePart;
        }

        String lastName = nameComponents[0].trim();
        String firstName = nameComponents[1].trim();

        // 2. CONTENT VALIDATION (Digits)
        if (containsDigit(firstName) || containsDigit(lastName)) {
            System.err.println("[ADAPTER ERROR] Invalid name content: First or last name contains numbers/digits.");
            return "INVALID NAME: CONTAINS DIGITS";
        }

        // 3. SUCCESSFUL REORDERING & INNOVATION (Title Case)
        return toTitleCase(firstName) + " " + toTitleCase(lastName);
    }

    @Override
    public double getYearlySalary() {
        String raw = adaptee.getRawData();
        String salaryPart = "0.0";

        try {
            String[] rawParts = raw.split("\\|");

            if (rawParts.length > 1) {
                salaryPart = rawParts[1].trim();
            } else {
                throw new IllegalArgumentException("Missing salary data after '|'");
            }

            return Double.parseDouble(salaryPart);

        } catch (NumberFormatException e) {
            System.err.println("[ADAPTER ERROR] Invalid salary format: '" + salaryPart + "'. Defaulting to 0.0.");
            return 0.0;
        } catch (IllegalArgumentException e) {
            System.err.println("[ADAPTER ERROR] " + e.getMessage() + ". Defaulting to 0.0.");
            return 0.0;
        }
    }

    @Override
    public String getSalaryAssessment() {
        double salary = getYearlySalary(); // Reuse the existing method to get the value

        // INNOVATION: Define a simple business rule for data quality/risk assessment
        if (salary == 0.0) {
            return "CRITICAL: SALARY DATA MISSING/INVALID";
        } else if (salary > 1000000.0) {
            return "HIGH SALARY RISK (EXCEEDS $1M THRESHOLD)";
        } else {
            return "OK";
        }
    }
}