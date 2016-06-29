package com.xy.gears.basicweb.sample.handler;

import com.xy.gears.valve.dispatcher.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Saurabh on 26-06-2016.
 */

public class ObjectDefHandler {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ObjectDefHandler.class);

    @JsonService(mapping = "/save/objectDef")
    public void saveObjectDef() {
        LOGGER.info("started processing request" + new Date());

        LOGGER.info("finished processing request" + new Date());
    }

    @JsonService(mapping = "/save/employee")
    public Employee saveEmployee(Employee employee) {
        LOGGER.info("started processing request for saveEmployee" + new Date());
        LOGGER.info("Emp: " + employee);
        employee.setSalary(Math.random() * 100000);
        LOGGER.info("finished processing request for saveEmployee" + new Date());
        return employee;
    }
}
