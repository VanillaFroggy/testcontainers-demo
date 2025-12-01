package com.example.repository;

import com.example.entity.TestAbstractEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAbstractRepository extends AbstractRepository<TestAbstractEntity, Long> {
}
