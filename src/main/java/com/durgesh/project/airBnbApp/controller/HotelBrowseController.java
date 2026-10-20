package com.durgesh.project.airBnbApp.controller;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.dto.HotelInfoDto;
import com.durgesh.project.airBnbApp.dto.HotelSearchRequest;
import com.durgesh.project.airBnbApp.service.HotelService;
import com.durgesh.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels")
@Slf4j
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>>  searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
        log.info("HotelBrowseController Searching hotels with request: {}", hotelSearchRequest);
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        log.debug("HotelBrowseController response of search: {}", page);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        log.info("HotelBrowseController getHotelInfo for hotelId: {}", hotelId);
        HotelInfoDto hotelInfo = hotelService.getHotelInfoById(hotelId);
        log.info("HotelBrowseController retrieved info for hotelInfo: {}", hotelInfo);
        return ResponseEntity.ok(hotelInfo);
    }


}