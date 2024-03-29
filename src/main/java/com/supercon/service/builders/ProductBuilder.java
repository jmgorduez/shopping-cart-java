package com.supercon.service.builders;

import com.supercon.model.Product;
import com.supercon.model.ProductPackage;
import com.supercon.service.builders.abstractions.IProductBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.supercon.utils.Constants.EMPTY_STRING;
import static com.supercon.utils.Constants._0;

@Service
public class ProductBuilder implements IProductBuilder {

    private String productCode;
    private String name;
    private double price;
    private Function<Double, Double> discountStrategy;
    private Function<Double, Integer> loyaltyPointsStrategy;
    private final List<Product> elements;

    public ProductBuilder() {
        this.productCode = EMPTY_STRING;
        this.name = EMPTY_STRING;
        this.price = _0;
        this.discountStrategy = Double::doubleValue;
        this.loyaltyPointsStrategy = Double::intValue;
        elements = new ArrayList<>();
    }

    public ProductBuilder(Product product) {
        this.productCode = product.getProductCode();
        this.name = product.getName();
        this.price = product.getPrice();
        this.discountStrategy = Double::doubleValue;
        this.loyaltyPointsStrategy = Double::intValue;
        elements = new ArrayList<>();
    }

    @Override
    public IProductBuilder instance(Product product) {
        return new ProductBuilder(product);
    }

    @Override
    public IProductBuilder setProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    @Override
    public IProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public IProductBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    public IProductBuilder setLoyaltyPointsStrategy(Function<Double, Integer> loyaltyPointsStrategy) {
        this.loyaltyPointsStrategy = loyaltyPointsStrategy;
        return this;
    }

    @Override
    public IProductBuilder setDiscountStrategy(Function<Double, Double> discountStrategy) {
        this.discountStrategy = discountStrategy;
        return this;
    }

    @Override
    public IProductBuilder addElement(Product product) {
        this.elements.add(product);
        return this;
    }

    @Override
    public Double finalPrice() {
        price += elements.stream()
                .mapToDouble(Product::getPrice)
                .sum();
        return discountStrategy.apply(price);
    }

    @Override
    public Integer loyaltyPointsEarned() {
        return loyaltyPointsStrategy.apply(price);
    }

    @Override
    public Product build() {
        if (elements.isEmpty()) {
            return new Product(finalPrice(), productCode, name,
                    loyaltyPointsEarned());
        }
        return new ProductPackage(finalPrice(), productCode, name,
                loyaltyPointsEarned(), elements);
    }
}