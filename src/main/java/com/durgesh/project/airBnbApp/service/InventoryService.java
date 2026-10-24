package com.durgesh.project.airBnbApp.service;


import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.dto.HotelPriceDto;
import com.durgesh.project.airBnbApp.dto.HotelSearchRequest;
import com.durgesh.project.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void updateRoomAvailability(Long roomId, boolean isAvailable);
    void deleteFutureInventoryForRoom(Room room);
    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
