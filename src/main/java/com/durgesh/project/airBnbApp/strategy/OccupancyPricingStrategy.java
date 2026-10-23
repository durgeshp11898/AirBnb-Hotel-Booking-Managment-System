package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Slf4j
@RequiredArgsConstructor
public class OccupancyPricingStrategy  implements   PricingStrategy{

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        log.info("OccupancyPricingStrategy calculatePrice for inventory: {}", inventory.getRoom());
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        // Implement occupancy-based pricing logic here

        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        if (occupancyRate > 0.8) {
            price = price.multiply(BigDecimal.valueOf(1.2)); // Increase price by 20% if occupancy is above 80%
        } else if (occupancyRate < 0.6) {
            price = price.multiply(BigDecimal.valueOf(0.9)); // Decrease price by 10% if occupancy is below 60%
        }
        log.info("OccupancyPricingStrategy finalPrice for inventory: {} is {}", inventory.getRoom(), price);
        return price;
    }
}
