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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "tracks")

public class Track {

  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  
  @Column(name = "number")
  private Integer number;

  
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pool_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Pool pool;

  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "track")
  private Set<TrackPeriod> trackPeriods = new HashSet<>();

  
  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  
  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  
  public Long getId() {
    return id;
  }

  
  public void setId(Long id) {
    this.id = id;
  }

  
  public Integer getNumber() {
    return number;
  }

  
  public void setNumber(Integer number) {
    this.number = number;
  }

  
  public Pool getPool() {
    return pool;
  }

  
  public void setPool(Pool pool) {
    this.pool = pool;
  }

  
  public Set<TrackPeriod> getTrackPeriods() {
    return trackPeriods;
  }

  
  public void setTrackPeriods(Set<TrackPeriod> trackPeriods) {
    this.trackPeriods = trackPeriods;
  }

  
  public Track(Pool pool, Set<Group> groups) {
    super();
    this.pool = pool;
  }

  
  public Track(Long id, Pool pool, Set<Group> groups, Set<TrackPeriod> trackPeriods) {
    super();
    this.id = id;
    this.pool = pool;
    this.trackPeriods = trackPeriods;
  }

  
  public Track(int number, Pool pool) {
    super();
    this.number = number;
    this.pool = pool;
  }

  
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  
  public Track() {
    super();
  }

}
