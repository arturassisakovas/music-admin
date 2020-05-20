package com.mAdmin.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mAdmin.enumerator.AgreementType;
import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.repository.AgreementRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.entity.Agreement;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.User;


@Service
public class ClientServiceImpl extends UserServiceImpl implements ClientService {

    
    private static final int MAX_CODE = 99999;

    
    private static final int MIN_CODE = 10000;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private AgreementRepository agreementRepository;

    
    @Override
    public List<Client> getAll() {

        return clientRepository.findAll();
    }

    
    @Override
    public Optional<Client> getClientById(Long id) {

        return clientRepository.findById(id);
    }

    
    @Override
    public Optional<Client> getClientByConfirmationToken(String confirmationToken) {

        return clientRepository.findByConfirmationToken(confirmationToken);

    }

    
    @Override
    public String validateAttribute(String attribute) {

        String validAttribute = attribute.trim().replaceAll("\\s+", " ");
        validAttribute = WordUtils.capitalizeFully(validAttribute);

        return validAttribute;
    }

    @Override
    public Client getClientByEmail(String email) {

        return clientRepository.findByEmail(email);
    }

    @Override
    public boolean changePassword(User user, String rawPassword) {

        user.setPassword(passwordEncoder.encode(rawPassword));

        clientRepository.save((Client) user);

        return true;

    }

    @Override
    public Client create(Client client, String password, ClientStatus status) {
        client.setPassword(passwordEncoder.encode(password));
        client.setStatus(status);
        client.setEnabled(true);

        client = clientRepository.save(client);

        return client;
    }

    @Override
    public Client createReserveClient(Client client, String password) {
        client.setConfirmationToken(UUID.randomUUID().toString());
        client.setConfirmationCode(this.generateCode());

        return create(client, password, ClientStatus.RESERVED);
    }

    @Override
    public Client createNewConfirmationCode(Client client) {
        client.setConfirmationToken(UUID.randomUUID().toString());
        client.setConfirmationCode(this.generateCode());
        return client;
    }

    @Override
    public String generateCode() {

        SecureRandom rand = new SecureRandom();
        return Integer.toString(rand.nextInt(MAX_CODE - MIN_CODE + 1) + MIN_CODE);
    }

    @Override
    public void saveClientAgreements(Boolean directMarketing, Boolean dataProcessing, Client client) {
        if (directMarketing) {
            Agreement agreement = agreementRepository.findByType(AgreementType.DIRECT_MARKETING_AGREES);
            client.getAgreements().add(agreement);
        } else {
            Agreement agreement = agreementRepository.findByType(AgreementType.DIRECT_MARKETING_DISAGREES);
            client.getAgreements().add(agreement);
        }

        if (dataProcessing) {
            Agreement agreement = agreementRepository.findByType(AgreementType.DATA_PROCESSING_AGREES);
            client.getAgreements().add(agreement);
        } else {
            Agreement agreement = agreementRepository.findByType(AgreementType.DATA_PROCESSING_DISAGREES);
            client.getAgreements().add(agreement);
        }
        clientRepository.saveAndFlush(client);
    }

}
