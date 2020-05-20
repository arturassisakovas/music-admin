package com.mAdmin.controller;

import com.mAdmin.repository.EmployeePasswordResetTokenRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.repository.RoleRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Mail;
import com.mAdmin.entity.PasswordResetToken;
import com.mAdmin.entity.Role;
import com.mAdmin.entity.User;
import com.mAdmin.model.EmployeeLazyDataModel;
import com.mAdmin.security.EmployeePrincipal;
import com.mAdmin.security.UserPrincipal;
import com.mAdmin.service.EmailService;
import com.mAdmin.service.PasswordResetTokenService;
import com.mAdmin.util.PrimeFacesWrapper;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;


@Controller
public class EmployeeController {

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private EmployeePasswordResetTokenRepository employeePasswordResetTokenRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private RoleRepository roleRepository;

    
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    
    @Autowired
    private EmailService emailService;

    
    @Autowired
    private SessionRegistry sessionRegistry;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    @Autowired
    private EmployeeLazyDataModel model;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    private Employee employee;

    
    private boolean create;

    
    private List<Role> roles;

    
    private String[] selectedRoles;

    
    private String currentEmail;

    
    private boolean isAdmin;

    
    private boolean initialStatus;

    
    private boolean hideUsedActivity;

    
    private boolean stopSaveMethod;

    
    private boolean selfEditing;

    
    public void add() {
        employee = new Employee();
        selectedRoles = null;
        setCreate(true);
        roles = roleRepository.findByNameNotIn("ROLE_CLIENT");
        isAdmin = false;
    }

    
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();
        PrimeFaces current = primeFacesWrapper.current();
        ResourceBundle label = context.getApplication().getResourceBundle(context, "label");
        Employee employeeWithSameMail = employeeRepository.findByEmail(employee.getEmail());

        if (!stopSaveMethod) {
            if (employeeWithSameMail == null || employeeWithSameMail.getId().equals(employee.getId())) {
                if (!create) {
                    employee = employeeRepository.saveAndFlush(employee);
                    if (initialStatus == employee.getEnabled()) {
                        FacesContext.getCurrentInstance().addMessage(
                                null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!",
                                        MessageFormat.format(
                                                label.getString("employee.edited"), employee.getFullName())));
                        if (!(currentEmail.equals(employee.getEmail()))) {
                            employeePasswordResetTokenRepository.deleteByEmployee(employee);
                            PasswordResetToken userToken =
                                    passwordResetTokenService.formAndSavePasswordResetToken(employee);
                            Mail mail = emailService.formNewUserPasswordMail(employee, userToken);
                            emailService.sendEmailByModel(mail, "mail-templates/new-user-template.html");
                        }
                    } else {
                        changeEmployeeStatus();
                    }
                } else {
                    Random random = new Random();
                    final int oneHundredThousand = 100000;
                    final int oneMillionMinusOne = 999999;
                    int number = oneHundredThousand + random.nextInt(oneMillionMinusOne);
                    String password = Integer.toString(number);
                    employee.setPassword(passwordEncoder.encode(password));
                    employee = employeeRepository.save(employee);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_INFO, "Success!",
                            MessageFormat.format(label.getString("employee.added"), employee.getFullName())));

                    PasswordResetToken userToken = passwordResetTokenService.formAndSavePasswordResetToken(employee);

                    Mail mail = emailService.formNewUserPasswordMail(employee, userToken);
                    emailService.sendEmailByModel(mail, "mail-templates/new-user-template.html");
                }

                Collection<Role> userRoles = new HashSet<>(employee.getRoles());
                employee.getRoles().clear();
                Long currentUserId =
                        ((UserPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getUser().getId();
                Employee editor = employeeRepository.findById(currentUserId).get();
                Role adminRole = roleRepository.findByName("ROLE_SADMIN");

                if (editor.getRoles().contains(adminRole)) {
                    for (String singleRole : selectedRoles) {
                        Long roleId = Long.valueOf(singleRole);
                        Role role = roleRepository.getOne(roleId);
                        employee.getRoles().add(role);
                    }
                    if (isAdmin) {
                        employee.getRoles().add(adminRole);
                    }
                } else {
                    for (String singleRole : selectedRoles) {
                        Long roleId = Long.valueOf(singleRole);
                        Role role = roleRepository.getOne(roleId);
                        employee.getRoles().add(role);
                    }
                    for (Role role : userRoles) {
                        if (role.getName().equals(adminRole.getName())) {
                            employee.getRoles().add(adminRole);
                        }
                    }
                }
                employeeRepository.saveAndFlush(employee);
                current.executeScript("PF('userDialog').hide();");
            } else {
                FacesContext.getCurrentInstance().addMessage(
                        null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Mail Error",
                                MessageFormat.format(label.getString("employee.email.is.used"), employee.getEmail())));
            }
        }
    }

    
    public void edit(User employee) {
        this.employee = employeeRepository.findById(employee.getId()).get();
        currentEmail = this.employee.getEmail();
        setCreate(false);
        roles = roleRepository.findByNameNotIn("ROLE_CLIENT");
        isAdmin = false;
        initialStatus = employee.getEnabled();
        Role adminRole = roleRepository.findByName("ROLE_SADMIN");


        int i = 0;
        Long currentUserId = ((UserPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getUser()
                .getId();

        if (employee.getId().equals(currentUserId)) {
            String[] userRoles = new String[this.employee.getRoles().size()];
            for (Role userRole : this.employee.getRoles()) {
                if (userRole != adminRole) {
                    userRoles[i] = userRole.getId().toString();
                    i++;
                } else {
                    isAdmin = true;
                }
            }
            selectedRoles = userRoles;
            roles.remove(adminRole);
        } else {
            String[] userRoles = new String[this.employee.getRoles().size()];
            for (Role userRole : this.employee.getRoles()) {
                userRoles[i] = userRole.getId().toString();
                i++;
            }
            selectedRoles = userRoles;
        }
    }

    
    private void changeEmployeeStatus() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle label = context.getApplication().getResourceBundle(context, "label");
        employeePasswordResetTokenRepository.deleteByEmployee(employee);
        expireEmployeeSessions(employee);

        if (!initialStatus) {
            PasswordResetToken userToken = passwordResetTokenService.formAndSavePasswordResetToken(employee);

            Mail mail = emailService.formPasswordRecoveryMail(employee, userToken);
            emailService.sendEmailByModel(mail, "mail-templates/password-recovery-template.html");

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success!", MessageFormat.format(
                            label.getString("employee.activation.email"), employee.getEmail())));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success!", MessageFormat.format(label.getString("employee.deleted"), employee.getName())));
        }

        employee.setEnabled(false);
        employeeRepository.save(employee);
    }

    
    public void expireEmployeeSessions(Employee employee) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (principal instanceof EmployeePrincipal) {
                UserDetails userDetails = (UserDetails) principal;
                if (userDetails.getUsername().equals(employee.getEmail())) {
                    for (SessionInformation sessionInformation : sessionRegistry.getAllSessions(userDetails, true)) {
                        sessionInformation.expireNow();
                    }
                }
            }
        }
    }

    
    public boolean checkIfCanSetAdmin() {
        Employee user = ((EmployeePrincipal) authenticationFacade.getAuthentication().getPrincipal()).getEmployee();
        Set<String> authorities = new HashSet<>();
        Set<String> targetAuthorities = new HashSet<>();

        user.getRoles().forEach(r -> authorities.add(r.getName()));
        employee.getRoles().forEach(r -> targetAuthorities.add(r.getName()));
        setHideUsedActivity(false);
        selfEditing = false;
        if (!authorities.contains("ROLE_SADMIN")
                && targetAuthorities.contains("ROLE_SADMIN")
                && !user.getId().equals(employee.getId())) {

            setHideUsedActivity(true);
        }
        if (Objects.equals(user.getId(), employee.getId())) {
            selfEditing = true;
        }
        return authorities.contains("ROLE_SADMIN");
    }

    
    public void validateRolesList() {
        FacesContext context = FacesContext.getCurrentInstance();
        stopSaveMethod = false;
        if (selectedRoles != null) {
            if (!create && selectedRoles.length == 0) {
                    if (checkIfCanSetAdmin()) {
                        if (!selfEditing) {
                            messageBundleComponent.generateMessage(
                                    FacesMessage.SEVERITY_ERROR, "user.wrong.input.roles", context);
                            stopSaveMethod = true;
                        }
                    } else if (!hideUsedActivity || selfEditing) {
                            messageBundleComponent.generateMessage(
                                    FacesMessage.SEVERITY_ERROR, "user.wrong.input.roles", context);
                            stopSaveMethod = true;
                    }
            } else if (selectedRoles.length == 0) {
                    messageBundleComponent.generateMessage(
                            FacesMessage.SEVERITY_ERROR, "user.wrong.input.roles", context);
                    stopSaveMethod = true;
            }
        }
    }

    
    public Employee getEmployee() {
        return employee;
    }

    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public List<Role> getRoles() {
        return roles;
    }

    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    
    public String[] getSelectedRoles() {
        return selectedRoles;
    }

    
    public void setSelectedRoles(String[] selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    
    public EmployeeLazyDataModel getModel(boolean enabled) {
        model.setEnabled(enabled);
        return model;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }
    
    public boolean isHideUsedActivity() {
        return hideUsedActivity;
    }

    
    public void setHideUsedActivity(boolean hideUsedActivity) {
        this.hideUsedActivity = hideUsedActivity;
    }

    
    public boolean isSelfEditing() {
        return selfEditing;
    }
}
