package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.RefinedMaterial;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RefinedMaterialService {

    private final RefinedMaterialRepository repository;

    public RefinedMaterialService(RefinedMaterialRepository repository) {
        this.repository = repository;
    }

    public Optional<RefinedMaterial> get(Long id) {
        return repository.findById(id);
    }

    public RefinedMaterial update(RefinedMaterial entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<RefinedMaterial> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<RefinedMaterial> list(Pageable pageable, Specification<RefinedMaterial> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
