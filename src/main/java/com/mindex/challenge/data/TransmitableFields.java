package com.mindex.challenge.data;

public enum TransmitableFields {
    EMPLOYEE_ID("employeeId"),
    EFFECTIVE_DATE("effectiveDate"),
    SALARY("salary"),
    EMPLOYEE("employee");

    private final String value;

    TransmitableFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

