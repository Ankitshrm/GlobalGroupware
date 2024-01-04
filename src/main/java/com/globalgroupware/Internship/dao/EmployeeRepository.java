package com.globalgroupware.Internship.dao;

import com.globalgroupware.Internship.models.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee,Long> {
    Employee findByEmployeeName(String reportsTo);

}
