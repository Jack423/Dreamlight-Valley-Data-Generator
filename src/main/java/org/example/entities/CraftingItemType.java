package org.example.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CraftingItemType {
    @JsonProperty("enchantment")
    ENCHANTMENT,
    @JsonProperty("landscaping")
    LANDSCAPING,
    @JsonProperty("functionalItem")
    FUNCTIONAL_ITEM,
    @JsonProperty("furniture")
    FURNITURE,

}
