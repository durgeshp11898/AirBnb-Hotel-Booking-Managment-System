package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        log.info("Calculating dynamic pricing for inventory: {}", inventory.getRoom());

        // Create the base pricing strategy
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        // Wrap the base pricing strategy with holiday pricing strategy
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        log.info("Final pricing strategy chain created for inventory: {}", inventory.getRoom());
        return pricingStrategy.calculatePrice(inventory);
    }
}
