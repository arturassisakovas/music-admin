package com.mAdmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mAdmin.entity.ClientPasswordResetToken;


@Repository
public interface ClientPasswordResetTokenRepository extends JpaRepository<ClientPasswordResetToken, Long> {

    
    ClientPasswordResetToken findByToken(String token);

}
