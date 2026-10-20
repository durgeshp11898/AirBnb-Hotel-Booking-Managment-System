package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.RoomDto;
import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.entity.Room;
import com.durgesh.project.airBnbApp.exception.ResourceNotFoundException;
import com.durgesh.project.airBnbApp.repository.HotelRepository;
import com.durgesh.project.airBnbApp.repository.RoomRepository;
import com.durgesh.project.airBnbApp.service.InventoryService;
import com.durgesh.project.airBnbApp.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryServiceImpl inventoryServiceImpl;
    private final InventoryService inventoryService;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("RoomServiceImpl createNewRoom : {}",roomDto);
        Room room = modelMapper.map(roomDto, Room.class);
        log.info("RoomServiceImpl saving the room details : {} for hotel with id : {}",room,hotelId);
       //check this hotel id is relly belong to his owner  :: TODO
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel is not found while creating new room"));
        room.setHotel(hotel);
        room = roomRepository.save(room);
        //Create Inventory as soon as room is created if hotel is ACTIVE
        if (hotel.getActive()) {
            log.info("RoomServiceImpl hotel is active, creating inventory for room : {}",room.getRoomId());
            inventoryServiceImpl.initializeRoomForAYear(room);
        }
        roomDto = modelMapper.map(room, RoomDto.class);
        return roomDto;
    }

    @Override
    public List<RoomDto> getAllRoomsByHotelId(Long hotelId) {
        log.info("RoomServiceImpl fetching all rooms by Hotel Id: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with Id : " + hotelId));

        List<RoomDto> roomList = hotel.getRooms()
                .stream()
                .map(r -> modelMapper.map(r, RoomDto.class))
                .collect(Collectors.toList());

        log.info("RoomServiceImpl fetched rooms : {} associated with hotel: {}",roomList,hotelId);
        return roomList;
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("RoomServiceImpl fetching room by Id : {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with Id : " + roomId));
        log.info("RoomServiceImpl room details fetched : {}",room);
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("RoomServiceImpl Deleting room by Id : {}",roomId);
        boolean isExists = roomRepository.existsById(roomId);
        if(isExists){
            Room room = roomRepository
                    .findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with Id : " + roomId));
            inventoryService.deleteFutureInventoryForRoom(room);
          roomRepository.deleteById(roomId); //DUE TO FOREIGN KEY CONSTRAINT, WE ARE NOT DELETING THE ROOM, INSTEAD WE ARE MAKING IT INACTIVE
        }else{
            throw new ResourceNotFoundException("Room not found with Id : " + roomId);
        }
        log.info("RoomServiceImpl roomId deleted successfully : {}",roomId);
    }

    @Override
    public void setRoomActive(Long roomId, boolean active) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with Id : " + roomId));
        room.setActive(active);
        roomRepository.save(room);
    }
}
