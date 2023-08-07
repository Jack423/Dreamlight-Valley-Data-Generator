package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.Foraging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ForagingRepository extends JpaRepository<Foraging, Long>, JpaSpecificationExecutor<Foraging> {

}
