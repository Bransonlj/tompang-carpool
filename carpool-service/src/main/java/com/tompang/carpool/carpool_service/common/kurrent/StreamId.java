package com.tompang.carpool.carpool_service.common.kurrent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StreamId {
    private final String prefix;
    private final String aggId;
    private static final String CONNECTOR_STRING = "_";

    public static StreamId from(String prefix, String aggId) {
        return new StreamId(prefix, aggId);
    }

    @Override
    public String toString() {
        return prefix + CONNECTOR_STRING + aggId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (obj == null || getClass() != obj.getClass()) return false; // Different type

        StreamId streamId = (StreamId) obj;

        return toString().equals(streamId.toString());
    }
}
