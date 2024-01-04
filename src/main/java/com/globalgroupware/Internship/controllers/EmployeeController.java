package com.globalgroupware.Internship.controllers;

import com.globalgroupware.Internship.models.Employee;
import com.globalgroupware.Internship.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Creating User

    @PostMapping
    public ResponseEntity registerUser(@RequestParam("employeeName") String employeeName,
                                       @RequestParam("phoneNumber") String phoneNumber,
                                       @RequestParam("email") String email,
                                       @RequestParam("reportsTo") String reportsTo,
                                       @RequestParam("profileImage") MultipartFile profileImage){
        try{
            Employee savedUser = employeeService.register(employeeName, phoneNumber, email,reportsTo,profileImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        }

        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // List all users

    @GetMapping
    public ResponseEntity <List<Employee>> getAllEmployee(){

        List<Employee> list ;
        try{
            list =this.employeeService.getAllUsers();
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Sorting employee using name ascending order

    @GetMapping("/sort/name")
    public ResponseEntity <List<Employee>> getAllEmployeeSortByName(){
        List<Employee> list ;
        try{
            list =this.employeeService.getAllUsers();
            list.sort(Comparator.comparing(Employee::getEmployeeName));
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Sorting employee using name descending order

    @GetMapping("/sort/name/desc")
    public ResponseEntity <List<Employee>> getAllEmployeeSortByNameDesc(){
        List<Employee> list ;
        try{
            list =this.employeeService.getAllUsers();
            list.sort(Comparator.comparing(Employee::getEmployeeName).reversed());
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Sorting employee using email ascending order
    @GetMapping("/sort/email")
    public ResponseEntity <List<Employee>> getAllEmployeeSortByEmails(){
        List<Employee> list ;
        try{
            list =this.employeeService.getAllUsers();
            list.sort(Comparator.comparing(Employee::getEmail));
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Sorting employee using email descending order
    @GetMapping("/sort/email/desc")
    public ResponseEntity <List<Employee>> getAllEmployeeSortByEmailsDesc(){
        List<Employee> list ;
        try{
            list =this.employeeService.getAllUsers();
            list.sort(Comparator.comparing(Employee::getEmail).reversed());
            return ResponseEntity.ok().body(list);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Delete employee

    @DeleteMapping("/{empId}")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable("empId") Long empId) {
        try{
            Employee e =this.employeeService.deleteEmp(empId);
            return ResponseEntity.ok().body(e);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Updating the profile

    @PutMapping("/{empId}")
    public ResponseEntity<Employee> updateMyProfile(@PathVariable Long id, @RequestBody Employee updatingEmp) {
        try{
            Employee u =this.employeeService.updateProfile(id,updatingEmp);
            return ResponseEntity.ok().body(u);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    // Get nth level manager

    @GetMapping("/{empid}/manager/{level}")
    public ResponseEntity<Employee> getNthLevelManager(@PathVariable("empid") Long empid, @PathVariable("level") int level) {
        try{
            Employee u =employeeService.findNthLevelManager(empid, level);
            return ResponseEntity.ok().body(u);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }
    }

    //Pagination

    @GetMapping("/pagination")
    public ResponseEntity<List<Employee>> getEmployeesPage(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<Employee> productPage = employeeService.getPaginatedProducts(page, size);
        List<Employee> products = productPage.getContent();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    // send mail function will work only when new user is created


//    @PostMapping("/send")
//    public ResponseEntity<Employee> sendMail(String to) {
//        try{
//            Employee emp =employeeService.send(to);
//            return ResponseEntity.ok().build();
//        }catch (Exception e){
//            e.printStackTrace();
//            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
//        }
//    }

}
