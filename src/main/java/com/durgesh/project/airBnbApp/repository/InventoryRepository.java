package com.durgesh.project.airBnbApp.repository;

import com.durgesh.project.airBnbApp.entity.Inventory;
import com.durgesh.project.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByRoom(Room room);
}
