package com.durgesh.project.airBnbApp.service;

import com.durgesh.project.airBnbApp.dto.BookingDto;
import com.durgesh.project.airBnbApp.dto.BookingRequest;
import com.durgesh.project.airBnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
