package com.mAdmin.component;

import com.mAdmin.enumerator.AgreementType;
import com.mAdmin.repository.AgreementRepository;
import com.mAdmin.repository.RoleRepository;
import com.mAdmin.entity.Agreement;
import com.mAdmin.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;


@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {


    private boolean alreadySetup = false;

    private static final String[] ROLES = {
            "ROLE_SADMIN",
            "ROLE_CLIENT",
            "ROLE_TEACHER"
    };

    private static final List<AgreementType> AGREEMENTS = Arrays.asList(AgreementType.values());

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private AgreementRepository agreementRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        for (String role : ROLES) {
            createRoleIfNotFound(role);
        }

        for (AgreementType agreementType : AGREEMENTS) {
            createAgreementTypeIfNotFound(agreementType);
        }

        alreadySetup = true;

    }

    
    @Transactional
    public void createRoleIfNotFound(String name) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }

    }

    
    @Transactional
    public void createAgreementTypeIfNotFound(AgreementType agreementType) {

        Agreement agreement = agreementRepository.findByType(agreementType);

        if (agreement == null) {
            agreement = new Agreement();
            agreement.setType(agreementType);
            agreementRepository.save(agreement);
        }
    }

}
