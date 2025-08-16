package com.tompang.carpool.carpool_service.query.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EventualAddress {
    @Enumerated(EnumType.STRING)
    private EventualAddressStatus status;
    private String addressString;

    /**
     * Constructs and returns a default EventualAddress with PENDING status and null addressString.
     * @return
     */
    public static EventualAddress get() {
        EventualAddress a = new EventualAddress();
        a.setStatus(EventualAddressStatus.PENDING);
        return a;
    }

    @Override
    public String toString() {
        if (status.equals(EventualAddressStatus.RESOLVED)) {
            return addressString;
        }

        if (status.equals(EventualAddressStatus.FAILED)) {
            return "Failed to get address";
        }

        return status.toString();
    }
}
