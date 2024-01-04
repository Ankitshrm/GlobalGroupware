package com.globalgroupware.Internship.services;

import com.globalgroupware.Internship.dao.EmployeeRepository;
import com.globalgroupware.Internship.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Value("${spring.mail.username")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private S3Client s3Client;
    private static final String S3_BUCKET_NAME = "displaypic";
    private static final String S3_BASE_URL = "https://displaypic.s3.ap-south-1.amazonaws.com/";


    private void send(String employeeName, String phoneNumber, String email) {
        try {
            String to=("ankitsharma97194@gmail.com");
            String subject="New Employee assigned";
            String body= employeeName+" will now work under you. Mobile number is "+phoneNumber+" and email is "+email;
            MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper =new MimeMessageHelper(mimeMailMessage,true );
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body);
            javaMailSender.send(mimeMailMessage);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Employee register(String employeeName, String phoneNumber, String email, String reportsTo, MultipartFile profileImage) throws IOException {

        String imagePath = uploadImageToS3(profileImage);
        // Create new user object
        Employee user = new Employee();
        long uniqueLongID = generateUniqueLongID();
        user.setId(uniqueLongID);
        user.setEmployeeName(employeeName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setReportsTo(reportsTo);
        user.setProfileImage(imagePath);

        // Save user to the database

        send(employeeName,phoneNumber,email);

        return employeeRepository.save(user);
    }




    private String uploadImageToS3(MultipartFile imageFile) throws IOException {
        try {
            // Generate a unique key for the image file
            String key = generateUniqueKey(imageFile.getOriginalFilename());

            // Upload the file to S3 bucket
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(key)
                    .build(), RequestBody.fromBytes(imageFile.getBytes()));

            // Return the S3 image URL
            return S3_BASE_URL + key;
        } catch (SdkException e) {
            // Handle exception
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage());
        }
    }

    public static long generateUniqueLongID() {
        UUID uuid = UUID.randomUUID();
        return uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
    }

    private String generateUniqueKey(String fileName) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();

    }

    public List<Employee> getAllUsers() {
        return employeeRepository.findAll();
    }

    public Employee deleteEmp(Long empId) {
        Employee emp =employeeRepository.findById(empId).orElse(null);
        employeeRepository.delete(emp);
        return emp;
    }

    public Employee updateProfile(Long id, Employee updatingEmp) {
        Employee user = this.employeeRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEmployeeName(updatingEmp.getEmployeeName());
            user.setPhoneNumber(updatingEmp.getPhoneNumber());
            user.setEmail(updatingEmp.getEmail());
            user.setReportsTo(updatingEmp.getReportsTo());
            user.setProfileImage(updatingEmp.getProfileImage());
        }
        assert user != null;
        employeeRepository.save(user);
        return user;
    }

    public Employee findNthLevelManager(Long employeeId, int level) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }
        for(int i=1;i<=level;i++)
        {
            String reportsTo = employee.getReportsTo();
            Employee emp = employeeRepository.findByEmployeeName(reportsTo);
            System.out.println(emp.getEmployeeName());
            employee =emp;
        }

        return employee;
    }


    public Page<Employee> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    }
}
