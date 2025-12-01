package com.example.repository;

import com.example.entity.AbstractEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity, ID> extends CrudRepository<T, ID> {

    Optional<T> findFirstByExternalId(String externalId);
}
