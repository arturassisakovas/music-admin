package com.mAdmin.repository;

import com.mAdmin.entity.Agreement;
import com.mAdmin.enumerator.AgreementType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AgreementRepository extends JpaRepository<Agreement, Long> {

    
    Agreement findByType(AgreementType agreementType);
}
