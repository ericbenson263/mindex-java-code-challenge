package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Ensures all test methods are encapsulated
public class ReportingStructureImplTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    private String reportingStructureUrl;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:" + port + "/reportingstructure/{id}";
    }


    @Test
    public void testControllerReporting() {
        //Root Employee
        Employee rootEmployee = new Employee();
        rootEmployee.setFirstName("John");
        rootEmployee.setLastName("Doe");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer II");

        //Tier 1 Employees
        Employee tier1Employee1 = new Employee();
        rootEmployee.setFirstName("Jack");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer");

        Employee tier1Employee2 = new Employee();
        rootEmployee.setFirstName("John");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer");

        //Tier 2 Employees
        Employee tier2Employee1 = new Employee();
        rootEmployee.setFirstName("Little Jimmy");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer Intern");

        Employee tier2Employee2 = new Employee();
        rootEmployee.setFirstName("Little Timmy");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer Intern");

        Employee tier2Employee3 = new Employee();
        rootEmployee.setFirstName("Little Kimmy");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer Intern");

        Employee tier2Employee4 = new Employee();
        rootEmployee.setFirstName("Little Milly");
        rootEmployee.setLastName("Daniels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer Intern");

        Employee tier2Employee5 = new Employee(); //NOT INVOLVED IN THIS TEST! CONTROL GROUP EMPLOYEE
        rootEmployee.setFirstName("Little Billy");
        rootEmployee.setLastName("Ganiels");
        rootEmployee.setDepartment("Engineering");
        rootEmployee.setPosition("Developer Intern");

        //Set the direct reports
        tier1Employee1.setDirectReports(Arrays.asList(tier2Employee1, tier2Employee2, tier2Employee3));
        tier1Employee2.setDirectReports(Arrays.asList(tier2Employee4));
        rootEmployee.setDirectReports(Arrays.asList(tier1Employee1, tier1Employee2));
        List<Employee> subordinatesList = Arrays.asList(tier1Employee1, tier1Employee2, tier2Employee1, tier2Employee2, tier2Employee3, tier2Employee4);

        //Push the employees to the database
        employeeService.create(tier2Employee1);
        employeeService.create(tier2Employee2);
        employeeService.create(tier2Employee3);
        employeeService.create(tier2Employee4);
        employeeService.create(tier2Employee5);
        employeeService.create(tier1Employee1);
        employeeService.create(tier1Employee2);
        employeeService.create(rootEmployee);

        //Hit the endpoint to generate the reporting structure
        String userId = rootEmployee.getEmployeeId();
        ReportingStructure reportingStructure = restTemplate.postForEntity(reportingStructureUrl, "", ReportingStructure.class, userId).getBody();

        //ASSERT NOT NULL
        Assert.assertNotNull(reportingStructure);
        Assert.assertNotNull(reportingStructure.getEmployee());
        //ASSERT CORRECT VALUES
        Assert.assertNotEquals( 0, reportingStructure.getNumberOfReports());
        Assert.assertNotEquals( 8, reportingStructure.getNumberOfReports());//To ensure it doesn't include the root employee and the extra employee
        Assert.assertEquals( 6, reportingStructure.getNumberOfReports());
        Assert.assertEquals(subordinatesList.size(), reportingStructure.getNumberOfReports());//Second check
        Assert.assertEquals(rootEmployee.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
    }

    @Test
    public void testControllerReportingNullRoot() {
        String userId = null;//WILL TRIGGER A FAILURE

        //HIT THE ENDPOINT
        ResponseEntity<ReportingStructure> postEntity = restTemplate.postForEntity(reportingStructureUrl, "", ReportingStructure.class, userId);

        //ASSERTS
        Assert.assertNotNull(postEntity);
        Assert.assertNotNull(postEntity.getStatusCode());
        Assert.assertNotNull(postEntity.getStatusCode().toString());
        Assert.assertEquals(NOT_FOUND, postEntity.getStatusCode());
    }

    @Test
    public void testControllerReportingUnknownRoot() {
        String userId = "thiswontbefound";//WILL TRIGGER A FAILURE

        //HIT THE ENDPOINT
        ResponseEntity<ReportingStructure> postEntity = restTemplate.postForEntity(reportingStructureUrl, "", ReportingStructure.class, userId);

        //ASSERTS
        Assert.assertNotNull(postEntity);
        Assert.assertNotNull(postEntity.getStatusCode());
        Assert.assertNotNull(postEntity.getStatusCode().toString());
        Assert.assertEquals(INTERNAL_SERVER_ERROR, postEntity.getStatusCode());
    }
}
