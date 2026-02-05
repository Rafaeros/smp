package br.rafaeros.smp.modules.client.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.rafaeros.smp.modules.client.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c from Client c WHERE" +
        "(:name IS NULL OR LOWER(CAST(c.name AS string)) LIKE LOWER(CAST(:name AS string)))"
    )
    Page<Client> findByFilters(@Param("name") String name, Pageable Pageable);


    Optional<Client> findByName(String name);
}