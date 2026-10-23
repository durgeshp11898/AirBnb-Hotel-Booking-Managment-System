package com.durgesh.project.airBnbApp.strategy;

import com.durgesh.project.airBnbApp.entity.Inventory;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Slf4j
public class BasePricingStrategy implements  PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        log.info("BasePricingStrategy calculatePrice for inventory: {}", inventory.getRoom());
        // Base pricing logic, for example, return the base price of the inventory
        return inventory.getRoom().getBasePrice();
    }

}
