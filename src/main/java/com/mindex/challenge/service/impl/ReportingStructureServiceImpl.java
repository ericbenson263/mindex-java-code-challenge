package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Map<String, Employee> EMPLOYEE_CACHE = new HashMap<>();

    @Override
    public ReportingStructure create(String employeeId){
        return new ReportingStructure(obtainEmployeeAndAddToCache(employeeId), calculateNumberOfReports(employeeId));
    }

    /*
     * Helper method that recursively runs and generates the information we will be populating the ReportingStructure with. 
     */
    private int calculateNumberOfReports(String employeeId){
        int numberOfReports = 0;

        //Check the cache to see if we've already obtained this employee before. This will speed up future calls.
        Employee employee = EMPLOYEE_CACHE.containsKey(employeeId)? EMPLOYEE_CACHE.get(employeeId) : obtainEmployeeAndAddToCache(employeeId);

        Objects.requireNonNull(employee, NOT_NULL.getValue());
        if(employee.getDirectReports() != null){
            for(Employee directReportEmployee : employee.getDirectReports()){
                numberOfReports++;
                numberOfReports += calculateNumberOfReports(directReportEmployee.getEmployeeId());
            }
        }
        return numberOfReports;
    }

    /*
     * Helper method that obtains the employee by the employeeId and then store sit in the cache of known employees
     */
    private Employee obtainEmployeeAndAddToCache(String employeeId){
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());
        Employee retval = employeeRepository.findByEmployeeId(employeeId);
        EMPLOYEE_CACHE.put(employeeId, retval);

        return retval;
    }
}
