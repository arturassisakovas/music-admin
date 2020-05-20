package com.mAdmin.controller;

import java.util.Optional;

import com.mAdmin.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mAdmin.entity.Client;
import com.mAdmin.service.ClientService;


@Controller
public class ConfirmationController {

    
    @Autowired
    private ClientService clientService;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @GetMapping("/confirm")
    public String showConfirmationPage(@RequestParam String token) {

        Client client;
        Optional<Client> optionalClient = clientService.getClientByConfirmationToken(token);

        if (optionalClient.isPresent()) {
            client = optionalClient.get();
            client.setEnabled(true);
            client.setConfirmationToken("");
            clientRepository.save(client);
            return "/confirm.xhtml";
        } else {
            return "/confirm-failed.xhtml";
        }

    }

}
