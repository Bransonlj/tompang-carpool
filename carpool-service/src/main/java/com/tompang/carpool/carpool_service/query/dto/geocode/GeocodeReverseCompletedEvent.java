package com.tompang.carpool.carpool_service.query.dto.geocode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeResponse.GeocodeInfo;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntity;
import com.tompang.carpool.carpool_service.query.dto.geocode.enums.GeocodeEntityField;

public class GeocodeReverseCompletedEvent {

    public final boolean success;
    public final String address;
    public final GeocodeEntity entity;
    public final String entityId;
    public final GeocodeEntityField field;

    @JsonCreator
    public GeocodeReverseCompletedEvent(
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
    
    public static GeocodeReverseCompletedEvent from(GeocodeReverseJobDto dto, GeocodeResponse response) {
        if (response == null || response.getGeocodeInfo().isEmpty()) {
            return new GeocodeReverseCompletedEvent(false, null, dto.entity, dto.entityId, dto.field);
        }

        // we will use the first address (closest)
        GeocodeInfo firstInfo = response.getGeocodeInfo().get(0);
        StringBuilder builder = new StringBuilder();

        if (!firstInfo.getBuildingName().equals(GeocodeResponse.NIL)) {
            builder.append(firstInfo.getBuildingName());
            builder.append(" ");
        }

        if (!firstInfo.getBlock().equals(GeocodeResponse.NIL)) {
            builder.append(firstInfo.getBlock());
            builder.append(" ");
        }

        if (!firstInfo.getRoad().equals(GeocodeResponse.NIL)) {
            builder.append(firstInfo.getRoad());
            builder.append(" ");
        }

        if (!firstInfo.getPostalCode().equals(GeocodeResponse.NIL)) {
            builder.append(firstInfo.getPostalCode());
            builder.append(" ");
        }

        if (builder.isEmpty()) {
            // no result found (NIL for all fields)
            return new GeocodeReverseCompletedEvent(false, null, dto.entity, dto.entityId, dto.field);
        }

        return new GeocodeReverseCompletedEvent(true, builder.toString(), dto.entity, dto.entityId, dto.field);
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
