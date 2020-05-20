package com.mAdmin.service;

import java.util.List;
import java.util.Optional;

import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.entity.Client;


public interface ClientService extends UserService {

    
    List<Client> getAll();

    
    Optional<Client> getClientById(Long id);

    
    Optional<Client> getClientByConfirmationToken(String confirmationToken);

    
    String validateAttribute(String attribute);

    
    Client getClientByEmail(String email);

    
    Client create(Client client, String password, ClientStatus clientStatus);

    
    Client createReserveClient(Client client, String password);

    
    Client createNewConfirmationCode(Client client);

    
    String generateCode();

    
    void saveClientAgreements(Boolean directMarketing, Boolean dataProcessing, Client client);
}
