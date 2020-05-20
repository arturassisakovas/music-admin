package com.mAdmin.repository;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.MedicalCertificate;
import com.mAdmin.enumerator.MedicalCertificateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {

    
    List<MedicalCertificate> findByAttendeeOrderByIdDesc(Attendee selectedAttendee);

    
    List<MedicalCertificate> findByValidToBeforeAndStatusNot(LocalDate dateBeforeTwoYears,
            MedicalCertificateStatus medicalCertificateStatus);

    
    Page<MedicalCertificate> findAllByStatusOrderByCreatedAtDesc(Pageable pageable, MedicalCertificateStatus status);

    
    List<MedicalCertificate> findAllByStatus(MedicalCertificateStatus status);

    
    List<MedicalCertificate> findAllByStatusAndValidTo(MedicalCertificateStatus status, LocalDate validTo);

    
    MedicalCertificate findFirstByStatus(MedicalCertificateStatus status);

    
    List<MedicalCertificate> findByStatusAndValidToBeforeAndAttendeeEquals(MedicalCertificateStatus status,
            LocalDate date, Attendee attendee);

    
    List<MedicalCertificate> findAllByValidToEquals(LocalDate now);

    
    MedicalCertificate findFirstByAttendeeAndStatusNotAndStatusNotOrderByIdDesc(Attendee selectedAttendee,
                                                                                      MedicalCertificateStatus absent,
                                                                                      MedicalCertificateStatus expired);

    
    Page<MedicalCertificate> findAllByStatus(Pageable pageable, MedicalCertificateStatus status);

}
