package com.va4815.springresourcebackend.repository;

import com.va4815.springresourcebackend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByMenuId(Long menuId);
    Optional<MenuItem> findByIdAndMenuId(Long itemId, Long menuId);
}
