package com.macro.mall.portal.service;

import com.macro.mall.model.PmsProduct;

import java.util.List;

/**
 * Represents a pre-existing consumer that still uses the original search API.
 */
public class LegacySearchConsumer {
    public List<PmsProduct> search(PmsPortalProductService service) {
        return service.search(null, null, null, 0, 5, 0);
    }
}
