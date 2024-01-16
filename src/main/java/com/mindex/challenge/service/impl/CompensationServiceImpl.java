package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;

@Service
public class CompensationServiceImpl implements CompensationService {

    @Autowired
    private CompensationRepository compensationRepository;

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    /*
     * Endpoint to create a new Compensation Report
     */
    @Override
    public Compensation create(String employeeId, int salary, Date effectiveDate){
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());
        Objects.requireNonNull(effectiveDate, NOT_NULL.getValue());

        LOG.debug("Creating compensation report with employeeId [{}]", employeeId);

        //Create the compensation and insert it
        Compensation compensation = new Compensation(employeeId, salary, effectiveDate);
        compensationRepository.insert(compensation);

        //Return the compensation
        return compensation;
    }

    /*
     * Endpoint to read an existing Compensation Report
     */
    public Compensation read(String employeeId) {
        Objects.requireNonNull(employeeId, NOT_NULL.getValue());

        LOG.debug("Reading compensation report with employeeId [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeId(employeeId);
        Objects.requireNonNull(compensation, NOT_NULL.getValue());

        return compensation;
    }
}
