package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.impl.ReportingStructureServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;

@RestController
public class ReportingStructureController {

    @Autowired
    private ReportingStructureServiceImpl reportingStructureService;
    @Autowired
    private EmployeeService employeeService;
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);

    @PostMapping("reportingstructure/{employeeId}")
    public ReportingStructure create(@PathVariable String employeeId) {
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());
        employeeService.containsOrThrowError(employeeId);

        LOG.debug("Received reporting structure create request for employeeId [{}]", employeeId);

        return reportingStructureService.create(employeeId);
    }
}
