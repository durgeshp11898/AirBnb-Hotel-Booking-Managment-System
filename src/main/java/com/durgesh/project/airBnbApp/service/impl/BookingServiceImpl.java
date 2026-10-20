package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.BookingDto;
import com.durgesh.project.airBnbApp.dto.BookingRequest;
import com.durgesh.project.airBnbApp.dto.GuestDto;
import com.durgesh.project.airBnbApp.entity.*;
import com.durgesh.project.airBnbApp.entity.enums.BookingStatus;
import com.durgesh.project.airBnbApp.exception.ResourceNotFoundException;
import com.durgesh.project.airBnbApp.repository.*;
import com.durgesh.project.airBnbApp.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl  implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("BookingServiceImpl initialiseBooking with request: {}", bookingRequest);
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID : " + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found with ID : " + bookingRequest.getRoomId()));


        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getRoomId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        log.info("BookingServiceImpl retrieved inventoryList: {}", inventoryList);
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if (inventoryList.size() != daysCount) {
            log.error("BookingServiceImpl Room is not available for the requested dates. Expected days: {}, Available days: {}", daysCount, inventoryList.size());
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories
        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }
        log.info("BookingServiceImpl inventoryList  is done: {}", inventoryList);
        inventoryRepository.saveAll(inventoryList);

        log.debug("BookingServiceImpl Room is available for the requested dates. Proceeding to create booking.");
        //create the booking entity and save it to the database
        // TODO: calculate dynamic amount

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(room.getBasePrice())
                .build();
        log.info("BookingServiceImpl saving booking: {}", booking);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("BookingServiceImpl addGuests for bookingId: {} with guests: {}", bookingId, guestDtoList);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found with ID : " + bookingId));
        log.debug("BookingServiceImpl retrieved booking: {}", booking);
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }
        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }
        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        log.info("BookingServiceImpl successfully added guests for booking: {}", booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    public User getCurrentUser() {
        User user = new User();
        user.setUserId(1L); // TODO: REMOVE DUMMY USER
        return user;
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

}
