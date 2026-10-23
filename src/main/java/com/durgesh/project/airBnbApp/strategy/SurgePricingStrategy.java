package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class SurgePricingStrategy  implements  PricingStrategy {

    private final PricingStrategy pricingStrategy;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        log.info("SurgePricingStrategy calculatePrice for inventory: {}", inventory.getRoom());
        BigDecimal price = pricingStrategy.calculatePrice(inventory);
        BigDecimal finalPrice =  price.multiply(inventory.getSurgeFactor());
        log.info("SurgePricingStrategy finalPrice for inventory: {} is {}", inventory.getRoom(), finalPrice);
        return finalPrice;
    }
}
