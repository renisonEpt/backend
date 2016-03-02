package com.renison.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.renison.model.Test;

@RepositoryRestResource
public interface TestRepository extends CrudRepository<Test, Long> {
}
