package com.durgesh.project.airBnbApp.service;

import com.durgesh.project.airBnbApp.dto.RoomDto;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);
    List<RoomDto> getAllRoomsByHotelId(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
    void setRoomActive(Long roomId, boolean active);;
}
