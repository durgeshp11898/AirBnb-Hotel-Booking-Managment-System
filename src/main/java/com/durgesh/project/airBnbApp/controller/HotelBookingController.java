package com.durgesh.project.airBnbApp.controller;

import com.durgesh.project.airBnbApp.dto.BookingDto;
import com.durgesh.project.airBnbApp.dto.BookingRequest;
import com.durgesh.project.airBnbApp.dto.GuestDto;
import com.durgesh.project.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Slf4j
@RequiredArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        log.info("HotelBookingController initialiseBooking with request: {}", bookingRequest);
        BookingDto bookingDto = bookingService.initialiseBooking(bookingRequest);
        log.info("HotelBookingController response of initialiseBooking: {}", bookingDto);
        return ResponseEntity.ok(bookingDto);
    }



    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestDtoList) {
        log.info("HotelBookingController addGuests for bookingId: {} with guests: {}", bookingId, guestDtoList);
        BookingDto booking = bookingService.addGuests(bookingId, guestDtoList);
        log.info("HotelBookingController successfully added guests for bookingId: {}", bookingId);
        return ResponseEntity.ok(booking);
    }

}
