package com.durgesh.project.airBnbApp.service;


import com.durgesh.project.airBnbApp.entity.Room;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void updateRoomAvailability(Long roomId, boolean isAvailable);
    void deleteFutureInventoryForRoom(Room room);
}
