package com.mindex.challenge.data;

import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;

@Configurable
public class Compensation {

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    String employeeId;
    int salary;
    Date effectiveDate;

    public Compensation(String employeeId, int salary, Date effectiveDate){
        this.employeeId = employeeId;
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }

}
