package com.discount.strategies;
public interface IDiscountStrategy {
    String getName();
    double calculateDiscount(double originalTotal);
}
