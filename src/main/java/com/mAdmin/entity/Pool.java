package com.mAdmin.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "pools")
public class Pool {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "city")
    private String city;

    
    @Column(name = "name")
    private String name;

    
    @Column(name = "abbreviation")
    private String abbreviation;

    
    @Column(name = "address")
    private String address;

    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "pool")
    private Set<Track> tracks = new HashSet<>();

    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE,
            mappedBy = "pool")
    private Set<PoolNonWorkday> poolNonWorkdays = new HashSet<>();

    
    @ManyToMany
    @JoinTable(name = "season_pool", joinColumns = {@JoinColumn(name = "pool_id")}, inverseJoinColumns = {
            @JoinColumn(name = "season_id")})
    private Set<Season> seasons = new HashSet<>();

    
    public Long getId() {
        return this.id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getCity() {
        return city;
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public String getName() {
        return this.name;
    }

    
    public void setName(String name) {
        this.name = name.trim();
    }

    
    public String getAddress() {
        return this.address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }

    
    public Set<Track> getTracks() {
        return tracks;
    }

    
    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
    public Set<PoolNonWorkday> getPoolNonWorkdays() {
        return poolNonWorkdays;
    }

    
    public void setPoolNonWorkdays(Set<PoolNonWorkday> poolNonWorkdays) {
        this.poolNonWorkdays = poolNonWorkdays;
    }

    
    public Set<Season> getSeasons() {
        return seasons;
    }

    
    public void setSeasons(Set<Season> seasons) {
        this.seasons = seasons;
    }

    
    public String getAbbreviation() {
        return abbreviation;
    }

    
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    
    public Pool() {
    }

    
    public Pool(String name, String address, String city) {
        setName(name);
        this.address = address;
        this.city = city;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d]", getClass().getSimpleName(), getId());
    }

    @Override
    public boolean equals(Object other) {
        if ((other instanceof Pool) && (id != null)) {
            return id.equals(((Pool) other).id);
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return (this.getClass().hashCode() + id.hashCode());
        }
        return super.hashCode();
    }

}
