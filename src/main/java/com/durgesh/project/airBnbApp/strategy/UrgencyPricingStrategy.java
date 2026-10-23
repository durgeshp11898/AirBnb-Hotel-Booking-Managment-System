package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
public class UrgencyPricingStrategy implements  PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        log.info("UrgencyPricingStrategy calculatePrice for inventory: {}", inventory.getRoom());
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        // Implement urgency-based pricing logic here
        LocalDate today = LocalDate.now();

        if (inventory.getDate().isBefore(today.plusDays(3))) {
            price = price.multiply(BigDecimal.valueOf(1.5)); // Increase price by 50% if the booking is within 3 days
        } else if (inventory.getDate().isBefore(today.plusDays(7))) {
            price = price.multiply(BigDecimal.valueOf(1.2)); // Increase price by 20% if the booking is within 7 days
        }
        log.info("UrgencyPricingStrategy finalPrice for inventory: {} is {}", inventory.getRoom(), price);
        return price;
    }
}
