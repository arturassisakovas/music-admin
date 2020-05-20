package com.mAdmin.controller;

import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.User;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.security.EmployeePrincipal;
import com.mAdmin.service.ClientService;
import com.mAdmin.service.EmployeeService;
import com.mAdmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;


@Controller
@Scope(value = "view")
public class ChangePasswordController {

    
    private String currentPassword;

    
    private String newPassword;

    
    private String newPasswordConfirm;

    
    private boolean redisplay = false;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private EmployeeService employeeService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private ClientService clientService;

    
    public void changePassword(String encodedPassword, User user, UserService userService) {

        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

        if (userService.checkCurrentPassword(currentPassword, encodedPassword)) {
            redisplay = false;
            if (userService.changePassword(user, newPassword)) {
                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO,
                        "user.changepassword.success", context);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
                        msg.getString("facesmessage.severity.fatal"), msg.getString("something.went.wrong")));
            }

        } else {
            redisplay = true;
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg.getString("facesmessage.severity.error"),
                            msg.getString("user.changepassword.currentpassword.wrong")));
        }

    }

    
    public void changeEmployeePassword() {
        EmployeePrincipal employeePrincipal = (EmployeePrincipal) authenticationFacade.getAuthentication()
                .getPrincipal();
        String encodedPassword = employeePrincipal.getPassword();
        Employee employee = employeePrincipal.getEmployee();
        changePassword(encodedPassword, employee, employeeService);
    }

    
    public void changeClientPassword() {
        ClientPrincipal clientPrincipal = (ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        String encodedPassword = clientPrincipal.getPassword();
        Client client = clientPrincipal.getClient();
        changePassword(encodedPassword, client, clientService);
    }

    
    public String getCurrentPassword() {
        return currentPassword;
    }

    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    
    public String getNewPassword() {
        return newPassword;
    }

    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    
    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    
    public boolean isRedisplay() {
        return redisplay;
    }

    
    public void setRedisplay(boolean redisplay) {
        this.redisplay = redisplay;
    }

}
