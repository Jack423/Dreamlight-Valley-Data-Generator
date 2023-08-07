package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.Foraging;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ForagingService {

    private final ForagingRepository repository;

    public ForagingService(ForagingRepository repository) {
        this.repository = repository;
    }

    public Optional<Foraging> get(Long id) {
        return repository.findById(id);
    }

    public Foraging update(Foraging entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Foraging> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Foraging> list(Pageable pageable, Specification<Foraging> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
