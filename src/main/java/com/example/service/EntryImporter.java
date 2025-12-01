package com.example.service;

import com.example.entity.AbstractEntity;
import com.example.repository.AbstractRepository;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;

public class EntryImporter<E extends AbstractEntity> {

    private final AbstractRepository<E, Long> repository;

    @Setter
    private Clock clock = Clock.systemDefaultZone();

    public EntryImporter(AbstractRepository<E, Long> repository) {
        this.repository = repository;
    }

    public boolean importValid(E entity) {
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        repository.findFirstByExternalId(entity.getExternalId())
            .ifPresentOrElse(
                dbEntity -> {
                    entity.setId(dbEntity.getId());
                    entity.setDateFrom(dbEntity.getDateFrom());
                    entity.setAuthorEmployeeId(dbEntity.getAuthorEmployeeId());
                },
                () -> entity.setDateFrom(currentDateTime)
            );
        entity.setUpdateDate(currentDateTime);
        repository.save(entity);
        return true;
    }
}
