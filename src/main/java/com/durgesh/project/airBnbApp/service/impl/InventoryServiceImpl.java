package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.dto.HotelSearchRequest;
import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.entity.Inventory;
import com.durgesh.project.airBnbApp.entity.Room;
import com.durgesh.project.airBnbApp.repository.InventoryRepository;
import com.durgesh.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private  final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {
        log.debug("InventoryServiceImpl initializeRoomForAYear : {}",room.getRoomId());
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            log.debug("InventoryServiceImpl initializeRoomForAYear : Saving inventory for date : {}", today);
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void updateRoomAvailability(Long roomId, boolean isAvailable) {

    }

    @Override
    public void deleteFutureInventoryForRoom(Room room) {
        log.debug("InventoryServiceImpl deleteFutureInventoryForRoom : {}",room.getRoomId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("InventoryServiceImpl searchHotels : {}",hotelSearchRequest);
        Pageable pageable = Pageable.ofSize(hotelSearchRequest.getSize()).withPage(hotelSearchRequest.getPage());

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                (long) (hotelSearchRequest.getEndDate().toEpochDay() - hotelSearchRequest.getStartDate().toEpochDay() + 1),
                pageable
        );
        Page<HotelDto> hotelDtoPage = hotelPage.map((hotel) -> modelMapper.map(hotel, HotelDto.class));
       log.info("InventoryServiceImpl searchHotels : Found {} hotels for request: {}", hotelDtoPage.getTotalElements(), hotelSearchRequest);
        return hotelDtoPage;
    }
}
