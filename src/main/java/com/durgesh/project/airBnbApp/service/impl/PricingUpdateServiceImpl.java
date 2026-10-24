package com.durgesh.project.airBnbApp.service.impl;

import com.durgesh.project.airBnbApp.entity.Hotel;
import com.durgesh.project.airBnbApp.entity.HotelMinPrice;
import com.durgesh.project.airBnbApp.entity.Inventory;
import com.durgesh.project.airBnbApp.repository.HotelMinPriceRepository;
import com.durgesh.project.airBnbApp.repository.HotelRepository;
import com.durgesh.project.airBnbApp.repository.InventoryRepository;
import com.durgesh.project.airBnbApp.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PricingUpdateServiceImpl {

    //scheduler to update the pricing of the rooms based on the demand and supply
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

 //   @Scheduled(cron = "*/50 * * * * *")
    @Scheduled(cron = "0 0 * * * *")    //run in every hour
    public void updatePricing() {
        log.info("Updating pricing of the rooms based on the demand and supply");
        //logic to update the pricing of the rooms based on the demand and supply
        int page = 0;
        int batchSize = 100;

        while(true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if(hotelPage.isEmpty()) {
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrices);
            page++;
        }

    }
    private void updateHotelPrices(Hotel hotel) {
        log.info("Updating hotel prices for hotel ID: {}", hotel.getHotelId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);
        log.info("Retrieved {} inventory records for hotel ID: {} between {} and {}", inventoryList.size(), hotel.getHotelId(), startDate, endDate);
        updateInventoryPrices(inventoryList);
        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);
    }


    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // Compute minimum price per day for the hotel
        log.info("Updating hotel minimum prices for hotel ID: {} from {} to {}", hotel.getHotelId(), startDate, endDate);
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        log.info("Prepared {} HotelPrice entities for hotel ID: {} to be saved", hotelPrices.size(), hotel.getHotelId());
        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        log.info("Updating inventory prices for {} records", inventoryList.size());
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }

}
