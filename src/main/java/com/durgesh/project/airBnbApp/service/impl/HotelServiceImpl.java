package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.dto.HotelDto;
import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.exception.ResourceNotFoundException;
import com.durgesh.project.airBnbApp.repository.HotelRepository;
import com.durgesh.project.airBnbApp.service.HotelService;
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
       hotelRepository
               .findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found with ID : "+id));
       Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
      hotel =  hotelRepository.save(hotel);
      return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long id) {
       log.info("HotelServiceImpl deleteHotelById : {}",id);
        boolean isExist = hotelRepository.existsById(id);
        if(isExist){
            //delete the future inventories for the hotel so hotel is in archive
            hotelRepository.deleteById(id);
            log.info("HotelServiceImpl hotel is deleted with ID : {}",id);
        }else{
            throw new ResourceNotFoundException("Id is not associated with Hotel : "+id);
        }
    }
}
