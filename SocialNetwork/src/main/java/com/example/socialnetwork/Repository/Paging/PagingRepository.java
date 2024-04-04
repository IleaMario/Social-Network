package com.example.socialnetwork.Repository.Paging;


import com.example.socialnetwork.Domain.Entity;
import com.example.socialnetwork.Repository.Repository;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
