package com.elitefolk.authservice.models;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EnableJpaAuditing
public class EntityBaseClass implements Serializable {
    @Id
    private UUID id;
    @CreationTimestamp
    private Long createdDate;
    @UpdateTimestamp
    private Long updatedDate;
    private Boolean isDeleted = false;
    @PrePersist
    public void generateId() {
        this.id = UuidCreator.getTimeOrdered();
    }
}
