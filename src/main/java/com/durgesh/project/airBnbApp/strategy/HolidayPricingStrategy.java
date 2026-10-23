package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HolidayPricingStrategy  implements  PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        log.info("HolidayPricingStrategy calculatePrice for inventory: {}", inventory.getRoom());
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        // Implement holiday-based pricing logic here
        if (isHoliday()) {
            price = price.multiply(BigDecimal.valueOf(1.3)); // Increase price by 30% if it's a holiday
        }
        log.info("HolidayPricingStrategy finalPrice for inventory: {} is {}", inventory.getRoom(), price);
        return price;
    }

    private boolean isHoliday() {
        //call an API to check if today is a holiday or not. For now, we will return true for testing purpose.
        List<String> holidays = List.of("2026-10-23", "2026-10-27"); // Example holiday dates
        String today = LocalDate.now().toString();
        if (holidays.contains(today)) {
            log.info("Today {} is a holiday.", today);
            return true;
        }
        return false;
    }
}
