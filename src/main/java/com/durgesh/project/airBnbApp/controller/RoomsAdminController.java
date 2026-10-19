package com.durgesh.project.airBnbApp.controller;

import com.durgesh.project.airBnbApp.dto.RoomDto;
import com.durgesh.project.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomsAdminController {

    private final RoomService roomService;

    @PostMapping()
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId ,
                                                 @RequestBody RoomDto roomDto){
        log.info("RoomsAdminController createNewRoom  for hotel : {}",hotelId);
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        log.info("RoomsAdminController room created with Id: {}",room.getId());
        return new ResponseEntity<>(room,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId) {
        log.info("RoomsAdminController get all room by Hotel Id: {}",hotelId);
        return ResponseEntity.ok(roomService.getAllRoomsByHotelId(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        log.info("RoomsAdminController get  room by room Id: {}",roomId);
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDto> deleteRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        log.info("RoomsAdminController delete room by room Id: {}",roomId);
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

}
