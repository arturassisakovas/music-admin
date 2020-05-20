package com.mAdmin.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "client_password_reset_tokens")
public class ClientPasswordResetToken extends PasswordResetToken {

    
    @OneToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "client_id")
    private Client client;

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }
}
