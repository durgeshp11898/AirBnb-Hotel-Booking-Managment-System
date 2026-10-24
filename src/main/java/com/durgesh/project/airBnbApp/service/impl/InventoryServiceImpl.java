package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.dto.HotelPriceDto;
import com.durgesh.project.airBnbApp.dto.HotelSearchRequest;
import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.entity.Inventory;
import com.durgesh.project.airBnbApp.entity.Room;
import com.durgesh.project.airBnbApp.repository.HotelMinPriceRepository;
import com.durgesh.project.airBnbApp.repository.InventoryRepository;
import com.durgesh.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private  final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

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
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("InventoryServiceImpl searchHotels : {}",hotelSearchRequest);

        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        // business logic - 90 days
        Page<HotelPriceDto> hotelPage =
                hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                        hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(),
                        dateCount, pageable);
        log.info("InventoryServiceImpl searchHotels : Found {} hotels for request: {}", hotelPage.getTotalElements(), hotelSearchRequest);

        return hotelPage;
    }
}

