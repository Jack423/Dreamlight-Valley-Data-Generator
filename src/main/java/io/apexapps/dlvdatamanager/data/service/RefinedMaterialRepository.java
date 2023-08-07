package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.RefinedMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefinedMaterialRepository
        extends
            JpaRepository<RefinedMaterial, Long>,
            JpaSpecificationExecutor<RefinedMaterial> {

}
