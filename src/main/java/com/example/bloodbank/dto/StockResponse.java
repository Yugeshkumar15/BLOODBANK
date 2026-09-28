package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import java.util.Map;

public class StockResponse {

    private Map<BloodGroup, Integer> stock;
    private int totalAvailableUnits;

    public StockResponse() {
    }

    public StockResponse(Map<BloodGroup, Integer> stock, int totalAvailableUnits) {
        this.stock = stock;
        this.totalAvailableUnits = totalAvailableUnits;
    }

    public Map<BloodGroup, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<BloodGroup, Integer> stock) {
        this.stock = stock;
    }

    public int getTotalAvailableUnits() {
        return totalAvailableUnits;
    }

    public void setTotalAvailableUnits(int totalAvailableUnits) {
        this.totalAvailableUnits = totalAvailableUnits;
    }
}
