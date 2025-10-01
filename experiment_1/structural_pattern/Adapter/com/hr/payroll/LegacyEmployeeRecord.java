package com.hr.payroll;

public class LegacyEmployeeRecord {
    private String rawData;

    public LegacyEmployeeRecord(String rawData) {
        this.rawData = rawData;
    }

    public String getRawData() {
        return rawData;
    }
}