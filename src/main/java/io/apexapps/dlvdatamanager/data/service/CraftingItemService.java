package io.apexapps.dlvdatamanager.data.service;

import io.apexapps.dlvdatamanager.data.entity.CraftingItem;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CraftingItemService {

    private final CraftingItemRepository repository;

    public CraftingItemService(CraftingItemRepository repository) {
        this.repository = repository;
    }

    public Optional<CraftingItem> get(Long id) {
        return repository.findById(id);
    }

    public CraftingItem update(CraftingItem entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<CraftingItem> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<CraftingItem> list(Pageable pageable, Specification<CraftingItem> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
