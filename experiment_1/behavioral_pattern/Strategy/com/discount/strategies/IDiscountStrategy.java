package com.discount.strategies;

// 1. Strategy Interface: Defines the common contract for all discount algorithms
public interface IDiscountStrategy {
    String getName();
    double calculateDiscount(double originalTotal);
}