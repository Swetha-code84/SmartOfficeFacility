package com.hr.payroll;

public interface IStandardEmployee {
    String getFullName();
    double getYearlySalary();

    // NEW METHOD: Added for the Salary Risk Assessment feature
    String getSalaryAssessment();
}