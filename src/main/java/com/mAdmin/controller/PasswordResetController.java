package com.mAdmin.controller;

import com.mAdmin.repository.ClientPasswordResetTokenRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.EmployeePasswordResetTokenRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.ClientPasswordResetToken;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.EmployeePasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Controller
public class PasswordResetController {

    
    @Autowired
    private EmployeePasswordResetTokenRepository employeeTokenRepository;

    
    @Autowired
    private ClientPasswordResetTokenRepository clientTokenRepository;

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    private String password;

    
    public void passwordReset() throws IOException {

        String updatedPassword = passwordEncoder.encode(password);

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();

        String token = req.getParameter("token");

        EmployeePasswordResetToken employeeToken = employeeTokenRepository.findByToken(token);

        if (employeeToken == null) {

            ClientPasswordResetToken clientToken = clientTokenRepository.findByToken(token);

            if (clientToken == null) {
                ec.redirect("/reset-password?error");
            } else {
                Client client = clientToken.getClient();
                client.setPassword(updatedPassword);
                clientRepository.save(client);
                clientTokenRepository.delete(clientToken);

                ec.redirect("/login?reset-success");
            }

        } else {
            Employee employee = employeeToken.getEmployee();
            employee.setPassword(updatedPassword);
            employee.setEnabled(true);
            employeeRepository.save(employee);
            employeeTokenRepository.delete(employeeToken);

            ec.redirect("/login?resetSuccess");
        }

    }

    
    public String getPassword() {
        return password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

}
