package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        Objects.requireNonNull(employee, NOT_NULL.getValue());

        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String employeeId) {
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());

        LOG.debug("Creating employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        Objects.requireNonNull(employee, NOT_NULL.getValue());

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        Objects.requireNonNull(employee, NOT_NULL.getValue());

        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /*
     * Helper method that checks if the database contains this Employee. Swallows the error, so useful for checks.
     */
    @Override
    public boolean contains(String employeeId) {
        try{
            return read(employeeId) != null;
        }
        catch (Exception e) {
            //The employee isn't present in the database..
            return false;
        }
    }

    /*
     * Helper method that checks if the database contains this Employee (based on the employeeId) before proceeding.
     */
    @Override
    public void containsOrThrowError(String employeeId){
        //Check to see if the employee is in the database
        if(!contains(employeeId))
            throw new RuntimeException("No employee found in the database with employeeId" + employeeId);
    }
}
