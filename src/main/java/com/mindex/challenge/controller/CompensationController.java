package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.TransmitableFields;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.impl.CompensationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;

@RestController
public class CompensationController {
    @Autowired
    private CompensationServiceImpl compensationService;
    @Autowired
    private EmployeeService employeeService;
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Map<String, Object> requestMap) throws ParseException {
        String employeeId = readEmployeeId(requestMap);
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());
        employeeService.containsOrThrowError(employeeId);

        LOG.debug("Received compensation create request for employeeId [{}]", employeeId);

        return compensationService.create(readEmployeeId(requestMap), readSalary(requestMap), readDate(requestMap));
    }

    @GetMapping("/compensation/{employeeId}")
    public Compensation read(@PathVariable String employeeId) {
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());
        employeeService.containsOrThrowError(employeeId);

        LOG.debug("Received compensation read request for employeeId [{}]", employeeId);

        return compensationService.read(employeeId);
    }

    //TODO could add an update request here

    //TODO could add a delete request here


    //Helper Methods for obtaining fields from the request body
    private String readEmployeeId(Map<String, Object> requestMap) {
        return Optional.ofNullable(requestMap)
                .map(map -> map.get(TransmitableFields.EMPLOYEE.getValue()))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(employeeMap -> employeeMap.get(TransmitableFields.EMPLOYEE_ID.getValue()))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse(null);
    }

    private int readSalary(Map<String, Object> requestMap) {
        return Optional.ofNullable(requestMap)
                .map(map -> map.get(TransmitableFields.SALARY.getValue()))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(0);
    }

    private Date readDate(Map<String, Object> requestMap) {
        return Optional.ofNullable(requestMap)
                .map(map -> map.get(TransmitableFields.EFFECTIVE_DATE.getValue()))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(this::parseDate)
                .orElse(null);
    }

    private Date parseDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        }
        catch (ParseException e) {
            return null;
        }
    }
}
