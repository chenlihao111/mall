package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.api.ResultCode;
import com.macro.mall.portal.service.PmsPortalProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class PmsPortalProductControllerTest {

    private PmsPortalProductService productService;
    private PmsPortalProductController controller;

    @BeforeEach
    void setUp() {
        productService = mock(PmsPortalProductService.class);
        controller = new PmsPortalProductController();
        ReflectionTestUtils.setField(controller, "portalProductService", productService);
    }

    @Test
    void rejectsNegativeMinimumPrice() {
        CommonResult<?> result = controller.search(null, null, null,
                new BigDecimal("-1.00"), null, 0, 5, 0);

        assertInvalidPriceRange(result);
    }

    @Test
    void rejectsNegativeMaximumPrice() {
        CommonResult<?> result = controller.search(null, null, null,
                null, new BigDecimal("-1.00"), 0, 5, 0);

        assertInvalidPriceRange(result);
    }

    @Test
    void rejectsMinimumPriceAboveMaximumPrice() {
        CommonResult<?> result = controller.search(null, null, null,
                new BigDecimal("20.00"), new BigDecimal("10.00"), 0, 5, 0);

        assertInvalidPriceRange(result);
    }

    private void assertInvalidPriceRange(CommonResult<?> result) {
        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), result.getCode());
        verifyNoInteractions(productService);
    }
}
