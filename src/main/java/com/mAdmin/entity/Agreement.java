package com.mAdmin.entity;

import com.mAdmin.enumerator.AgreementType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;


@Entity
@Table(name = "agreements")
public class Agreement {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AgreementType type;

    
    @ManyToMany (mappedBy = "agreements")
    private Collection<Client> clients;

    
    public long getId() {
        return id;
    }

    
    public void setId(long id) {
        this.id = id;
    }

    
    public Collection<Client> getClients() {
        return clients;
    }

    
    public void setClients(Collection<Client> clients) {
        this.clients = clients;
    }

    
    public AgreementType getType() {
        return type;
    }

    
    public void setType(AgreementType type) {
        this.type = type;
    }
}
