package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.Fish;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FishService {

    private final FishRepository repository;

    public FishService(FishRepository repository) {
        this.repository = repository;
    }

    public Optional<Fish> get(Long id) {
        return repository.findById(id);
    }

    public Fish update(Fish entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Fish> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Fish> list(Pageable pageable, Specification<Fish> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
