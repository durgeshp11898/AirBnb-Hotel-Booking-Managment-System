package com.durgesh.project.airBnbApp.controller;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@Slf4j
@RequiredArgsConstructor
public class HotelAdminController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto) {
        log.info("HotelAdminController createNewHotel with name : {}", hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        log.info("HotelAdminController hotel created successfully : {}", hotel.getName());
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelWithId(@PathVariable Long hotelId) {
        log.info("HotelAdminController fetching ");
        HotelDto hotel = hotelService.getHotelById(hotelId);
        log.info("HotelAdminController hotel fetched successfully : {}", hotel.getName());
        return ResponseEntity.ok(hotel);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelDetails(@PathVariable Long hotelId,@RequestBody HotelDto hotelDto) {
         log.info("HotelAdminController updating hotel details :");
         HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
         return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        log.info("HotelAdminController deleting hotel id : {}", hotelId);
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable  Long hotelId) {
        log.info("HotelAdminController activating hotel id : {}", hotelId);
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
