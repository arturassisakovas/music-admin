package com.mAdmin.controller;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.entity.Employee;
import com.mAdmin.security.EmployeePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class TeacherProfileController {

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    private Employee coach;

    
    public void onLoad() {

        Long coachId = ((EmployeePrincipal) authenticationFacade.getAuthentication().getPrincipal()).getEmployeeId();

        coach = employeeRepository.getOne(coachId);
    }

    
    public Employee getCoach() {
        return coach;
    }

    
    public void setCoach(Employee coach) {
        this.coach = coach;
    }

}
