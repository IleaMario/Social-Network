package com.example.socialnetwork.Repository;


import com.example.socialnetwork.Domain.Entity;
import com.example.socialnetwork.Domain.User;
import com.example.socialnetwork.Repository.Paging.Page;
import com.example.socialnetwork.Repository.Paging.Pageable;

import java.util.Optional;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    Optional<E> findOne(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    Optional<E> save(E entity);

    Optional<E> delete(ID id);

    Optional<E> update(E entity);

    default Page<E> findAll(Pageable pageable, User user, int from){
        return null;
    }

}


