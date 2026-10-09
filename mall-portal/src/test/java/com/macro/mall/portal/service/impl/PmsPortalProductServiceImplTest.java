package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsProductExample;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PmsPortalProductServiceImplTest {

    private PmsProductMapper productMapper;
    private PmsPortalProductServiceImpl service;

    @BeforeEach
    void setUp() {
        productMapper = mock(PmsProductMapper.class);
        when(productMapper.selectByExample(any(PmsProductExample.class))).thenReturn(Collections.emptyList());
        service = new PmsPortalProductServiceImpl();
        ReflectionTestUtils.setField(service, "productMapper", productMapper);
    }

    @AfterEach
    void clearPageHelper() {
        PageHelper.clearPage();
    }

    @Test
    void searchesWithoutPriceConditionsWhenBoundsAreOmitted() {
        PmsProductExample example = search(null, null);

        assertTrue(priceConditions(example).isEmpty());
    }

    @Test
    void appliesOnlyMinimumPriceWhenMaximumIsOmitted() {
        PmsProductExample example = search(new BigDecimal("10.00"), null);

        assertEquals(List.of("price >=:10.00"), priceConditions(example));
    }

    @Test
    void appliesOnlyMaximumPriceWhenMinimumIsOmitted() {
        PmsProductExample example = search(null, new BigDecimal("20.00"));

        assertEquals(List.of("price <=:20.00"), priceConditions(example));
    }

    @Test
    void appliesBothInclusivePriceBounds() {
        PmsProductExample example = search(new BigDecimal("10.00"), new BigDecimal("20.00"));

        assertEquals(List.of("price >=:10.00", "price <=:20.00"), priceConditions(example));
    }

    @Test
    void includesPricesEqualToEitherBoundary() {
        PmsProductExample example = search(new BigDecimal("15.00"), new BigDecimal("15.00"));

        assertEquals(List.of("price >=:15.00", "price <=:15.00"), priceConditions(example));
    }

    private PmsProductExample search(BigDecimal minPrice, BigDecimal maxPrice) {
        service.search(null, null, null, minPrice, maxPrice, 0, 10, 0);
        ArgumentCaptor<PmsProductExample> captor = ArgumentCaptor.forClass(PmsProductExample.class);
        verify(productMapper).selectByExample(captor.capture());
        return captor.getValue();
    }

    private List<String> priceConditions(PmsProductExample example) {
        return example.getOredCriteria().get(0).getAllCriteria().stream()
                .filter(criterion -> criterion.getCondition().startsWith("price "))
                .map(criterion -> criterion.getCondition() + ":" + criterion.getValue())
                .collect(Collectors.toList());
    }
}
