package com.tompang.carpool.carpool_service.query.geocode.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.geocode.enums.GeocodeEntityField;

public class ReverseGeocodeCompletedEvent {

    public final boolean success;
    public final String address;
    public final GeocodeEntity entity;
    public final String entityId;
    public final GeocodeEntityField field;

    @JsonCreator
    public ReverseGeocodeCompletedEvent(
        @JsonProperty("success") boolean success,
        @JsonProperty("address") String address,
        @JsonProperty("entity") GeocodeEntity entity,
        @JsonProperty("entityId") String entityId,
        @JsonProperty("field") GeocodeEntityField field
    ) {
        this.success = success;
        this.address = address;
        this.entity = entity;
        this.entityId = entityId;
        this.field = field;
    }

    @Override
    public String toString() {
        return "GeocodeReverseCompletedEvent{" +
            "success=" + success +
            ", address='" + address + '\'' +
            ", entity=" + entity +
            ", entityId='" + entityId + '\'' +
            ", field=" + field +
            '}';
    }
}
