package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.entity.Room;
import com.durgesh.project.airBnbApp.exception.AlreadyExistsException;
import com.durgesh.project.airBnbApp.exception.ResourceNotFoundException;
import com.durgesh.project.airBnbApp.repository.HotelRepository;
import com.durgesh.project.airBnbApp.service.HotelService;
import com.durgesh.project.airBnbApp.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("HotelServiceImpl createNewHotel : {}",hotelDto);
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel=hotelRepository.save(hotel);
        log.info("HotelServiceImpl Created new hotel with id : {}",hotel.getHotelId());
        return  modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("HotelServiceImpl getHotelById : {}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID : "+id));
        return  modelMapper.map(hotel, HotelDto.class);    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
       log.info("HotelServiceImpl updateHotelById : {}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID : " + id));
       modelMapper.map(hotelDto, Hotel.class);
       hotel.setHotelId(id);
      hotel =  hotelRepository.save(hotel);
      return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
       log.info("HotelServiceImpl deleteHotelById : {}",id);
        boolean isExist = hotelRepository.existsById(id);
        if(isExist){
            //delete the future inventories for the hotel so hotel is in archive
            Hotel hotel = hotelRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID : " + id));
            hotelRepository.deleteById(id);
            log.info("HotelServiceImpl hotel is deleted with ID : {}",id);
            for(Room room: hotel.getRooms()) {
                inventoryService.deleteFutureInventoryForRoom(room);
            }

        }else{
            throw new ResourceNotFoundException("Id is not associated with Hotel : "+id);
        }
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("HotelServiceImpl  activateHotel by Id: {}",hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with Id: " + hotelId));
        if(hotel.getActive()){
            log.info("HotelServiceImpl hotel is already active with ID : {}",hotelId);
            throw new AlreadyExistsException("Hotel is already active with ID : " + hotelId);
        }
        hotel.setActive(true);
        hotelRepository.save(hotel);
        //create inventory for all rooms of this hotel for next 1 year
        hotel.getRooms().forEach(room -> {
            log.info("HotelServiceImpl activating inventory for room id : {}",room.getRoomId());
            inventoryService.initializeRoomForAYear(room);
        });
        log.info("HotelServiceImpl hotel is activated with ID : {}",hotelId);

    }
}
