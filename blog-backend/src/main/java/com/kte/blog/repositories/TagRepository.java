package com.kte.blog.repositories;

import com.kte.blog.domain.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.posts")
    List<Tag> findAllwithPostCount();

    Set<Tag> findByNameInIgnoreCase(Set<String> names);

    List<Tag> findByNameIn(Set<String> tagNames);
}
