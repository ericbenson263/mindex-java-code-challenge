package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.TransmitableFields;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mindex.challenge.data.ErrorMessages.NOT_NULL;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Ensures all test methods are encapsulated
public class CompensationImplTest {


    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompensationService compensationService;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    public CompensationImplTest() {
    }

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = compensationUrl + "/{employeeId}";
    }

    /*
     * COMPENSATION SERVICE TESTS
     */
    @Test
    public void testServiceCreate(){
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        Compensation compensation = compensationService.create(employee.getEmployeeId(), salary,effectiveDate);

        //CHECK IT IS NOT NULL
        Assert.assertNotNull(compensation);

        //CHECK THE DATA ISN'T NULL
        Assert.assertNotNull(compensation.getEmployeeId());
        Assert.assertNotNull(compensation.getEffectiveDate());

        //CHECK IT HAS CORRECT DATA
        Assert.assertEquals(compensation.getEmployeeId(), employee.getEmployeeId());
        Assert.assertEquals(compensation.getSalary(), salary);
        Assert.assertEquals(compensation.getEffectiveDate().toString(), effectiveDate.toString());
    }

    @Test
    public void testServiceCreateNullEmployee(){
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        try{
            compensationService.create(null, salary, effectiveDate);
            Assert.fail();
        }
        catch (NullPointerException e){
            Assert.assertNotNull(e);
            Assert.assertNotNull(e.getMessage());
            Assert.assertEquals(NOT_NULL.getValue(), e.getMessage());//Assert we hit our null check
        }
        catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testServiceCreateNullEffectiveDate(){
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        try{
            compensationService.create(employee.getEmployeeId(), salary, null);
            Assert.fail();
        }
        catch (NullPointerException e){
            Assert.assertNotNull(e);
            Assert.assertNotNull(e.getMessage());
            Assert.assertEquals(NOT_NULL.getValue(), e.getMessage());//Assert we hit our null check
        }
        catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void testServiceRead(){
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        compensationService.create(employee.getEmployeeId(), salary,effectiveDate);
        Compensation compensation = compensationService.read(employee.getEmployeeId());

        //CHECK IT IS NOT NULL
        Assert.assertNotNull(compensation);

        //CHECK THE DATA ISN'T NULL
        Assert.assertNotNull(compensation.getEmployeeId());
        Assert.assertNotNull(compensation.getEffectiveDate());

        //CHECK IT HAS CORRECT DATA
        Assert.assertEquals(compensation.getEmployeeId(), employee.getEmployeeId());
        Assert.assertEquals(compensation.getSalary(), salary);
        Assert.assertEquals(compensation.getEffectiveDate().toString(), effectiveDate.toString());
    }

    /*
     * COMPENSATION ENDPOINT TESTS
     */
    @Test
    public void testCreateEndpoint(){
        //CREATE THE TEST DATA
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        //CREATE THE REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TransmitableFields.EMPLOYEE.getValue(), employee);
        requestBody.put(TransmitableFields.SALARY.getValue(), salary);
        requestBody.put(TransmitableFields.EFFECTIVE_DATE.getValue(), effectiveDate);

        //CREATE THE REQUEST HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        //HIT THE ENDPOINT
        ResponseEntity<Compensation> createEntity = restTemplate.postForEntity(compensationUrl, requestEntity, Compensation.class);

        //CHECK IT IS NOT NULL
        Assert.assertNotNull(createEntity);
        Assert.assertNotNull(createEntity.getBody());
        Assert.assertEquals("200 OK", createEntity.getStatusCode().toString());

        //CHECK THE DATA ISN'T NULL
        Compensation compensation = createEntity.getBody();
        Assert.assertNotNull(compensation.getEmployeeId());
        Assert.assertNotNull(compensation.getEffectiveDate());

        //CHECK IT HAS CORRECT DATA
        Assert.assertEquals(compensation.getEmployeeId(), employee.getEmployeeId());
        Assert.assertEquals(compensation.getSalary(), salary);
        Assert.assertEquals(compensation.getEffectiveDate().toString(), effectiveDate.toString());
    }

    @Test
    public void testReadEndpoint(){
        //CREATE THE TEST DATA
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employee = employeeService.create(employee);

        //CREATE THE REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TransmitableFields.EMPLOYEE.getValue(), employee);
        requestBody.put(TransmitableFields.SALARY.getValue(), salary);
        requestBody.put(TransmitableFields.EFFECTIVE_DATE.getValue(), effectiveDate);

        //CREATE THE REQUEST HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        //HIT THE POST ENDPOINT
        restTemplate.postForEntity(compensationUrl, requestEntity, Compensation.class);

        //HIT THE READ ENDPOINT
        String employeeID = employee.getEmployeeId();
        ResponseEntity<Compensation> readEntity = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employeeID);

        //CHECK IT IS NOT NULL
        Assert.assertNotNull(readEntity);
        Assert.assertNotNull(readEntity.getBody());
        Assert.assertEquals("200 OK", readEntity.getStatusCode().toString());

        //CHECK THE DATA ISN'T NULL
        Compensation compensation = readEntity.getBody();
        Assert.assertNotNull(compensation.getEmployeeId());
        Assert.assertNotNull(compensation.getEffectiveDate());

        //CHECK IT HAS CORRECT DATA
        Assert.assertEquals(compensation.getEmployeeId(), employee.getEmployeeId());
        Assert.assertEquals(compensation.getSalary(), salary);
        Assert.assertEquals(compensation.getEffectiveDate().toString(), effectiveDate.toString());
    }

    @Test
    public void testCreateEndpointNullEmployee() {
        //CREATE THE TEST DATA
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        //CREATE THE REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TransmitableFields.EMPLOYEE.getValue(), null);//WILL TRIGGER A FAILURE
        requestBody.put(TransmitableFields.SALARY.getValue(), salary);
        requestBody.put(TransmitableFields.EFFECTIVE_DATE.getValue(), effectiveDate);

        //CREATE THE REQUEST HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        //HIT THE ENDPOINT
        ResponseEntity<Compensation> postEntity = restTemplate.postForEntity(compensationUrl, requestEntity, Compensation.class);

        //ASSERTS
        Assert.assertNotNull(postEntity);
        Assert.assertNotNull(postEntity.getStatusCode());
        Assert.assertNotNull(postEntity.getStatusCode().toString());
        Assert.assertEquals(INTERNAL_SERVER_ERROR, postEntity.getStatusCode());
    }

    @Test
    public void testCreateEndpointNullDate() {
        //CREATE THE TEST DATA
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employeeService.create(employee);

        //CREATE THE REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TransmitableFields.EMPLOYEE.getValue(), employee);
        requestBody.put(TransmitableFields.SALARY.getValue(), salary);
        requestBody.put(TransmitableFields.EFFECTIVE_DATE.getValue(), null);//WILL TRIGGER A FAILURE

        //CREATE THE REQUEST HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        //HIT THE ENDPOINT
        ResponseEntity<Compensation> postEntity = restTemplate.postForEntity(compensationUrl, requestEntity, Compensation.class);

        //ASSERTS
        Assert.assertNotNull(postEntity);
        Assert.assertNotNull(postEntity.getStatusCode());
        Assert.assertNotNull(postEntity.getStatusCode().toString());
        Assert.assertEquals(INTERNAL_SERVER_ERROR, postEntity.getStatusCode());
    }

    @Test
    public void testReadEndpointNullRead() {
        //CREATE THE TEST DATA
        Employee employee = new Employee("123", "Don", "Lemon");
        int salary = 5000;
        Date effectiveDate = new Date();
        employee = employeeService.create(employee);

        //CREATE THE REQUEST BODY
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TransmitableFields.EMPLOYEE.getValue(), employee);
        requestBody.put(TransmitableFields.SALARY.getValue(), salary);
        requestBody.put(TransmitableFields.EFFECTIVE_DATE.getValue(), effectiveDate);

        //CREATE THE REQUEST HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        //HIT THE POST ENDPOINT
        restTemplate.postForEntity(compensationUrl, requestEntity, Compensation.class);
        String employeeID = null;//WILL TRIGGER A FAILURE

        //HIT THE READ ENDPOINT
        ResponseEntity<Compensation> readEntity = restTemplate.getForEntity(compensationIdUrl, Compensation.class, employeeID);

        //ASSERTS
        Assert.assertNotNull(readEntity);
        Assert.assertNotNull(readEntity.getStatusCode());
        Assert.assertNotNull(readEntity.getStatusCode().toString());
        Assert.assertEquals(METHOD_NOT_ALLOWED, readEntity.getStatusCode());
    }
}
