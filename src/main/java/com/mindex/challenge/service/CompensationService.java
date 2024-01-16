package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

import java.util.Date;

public interface CompensationService {
    Compensation create(String employeeId, int salary, Date effectiveDate);
    Compensation read(String employeeId);
}
