package com.issa.poc.repository;

import com.issa.poc.model.Teller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TellerRepository extends JpaRepository<Teller, Long> {
}
