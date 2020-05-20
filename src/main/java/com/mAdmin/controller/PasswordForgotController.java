package com.mAdmin.controller;

import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Mail;
import com.mAdmin.entity.PasswordResetToken;
import com.mAdmin.service.EmailService;
import com.mAdmin.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ResourceBundle;


@Controller
@Scope(value = "request")
public class PasswordForgotController {

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    
    private String email;

    
    @Autowired
    private EmailService emailService;

    
    public void processForgotPasswordForm() throws IOException {

        Employee employee = employeeRepository.findByEmail(email);

        if (employee == null) {

            Client client = clientRepository.findByEmail(email);

            if (client == null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/forgot-password?error=true");
            } else {

                PasswordResetToken clientToken = passwordResetTokenService.formAndSavePasswordResetToken(client);

                Mail mail = emailService.formPasswordRecoveryMail(client, clientToken);
                emailService.sendEmailByModel(mail, "mail-templates/password-recovery-template.html");

                FacesContext.getCurrentInstance().getExternalContext().redirect("/forgot-password?success");
            }

        } else {

            if (employee.getEnabled()) {
                PasswordResetToken employeeToken = passwordResetTokenService.formAndSavePasswordResetToken(employee);
                Mail mail = emailService.formPasswordRecoveryMail(employee, employeeToken);
                emailService.sendEmailByModel(mail, "mail-templates/password-recovery-template.html");

                FacesContext.getCurrentInstance().getExternalContext().redirect("/forgot-password?success");
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error!",
                        "incorrect.disabled.user.password"));
            }

        }

    }

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

}
