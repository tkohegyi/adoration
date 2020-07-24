package org.rockhill.adorApp.database.business.helper.enums;

import org.rockhill.adorApp.database.exception.DatabaseHandlingException;

public enum CoordinatorTypes {
    HOURLY_COORDINATOR_0("Hourly Coordinator: 0", 0),
    HOURLY_COORDINATOR_1("Hourly Coordinator: 1", 1),
    HOURLY_COORDINATOR_2("Hourly Coordinator: 2", 2),
    HOURLY_COORDINATOR_3("Hourly Coordinator: 3", 3),
    HOURLY_COORDINATOR_4("Hourly Coordinator: 4", 4),
    HOURLY_COORDINATOR_5("Hourly Coordinator: 5", 5),
    HOURLY_COORDINATOR_6("Hourly Coordinator: 6", 6),
    HOURLY_COORDINATOR_7("Hourly Coordinator: 7", 7),
    HOURLY_COORDINATOR_8("Hourly Coordinator: 8", 8),
    HOURLY_COORDINATOR_9("Hourly Coordinator: 9", 9),
    HOURLY_COORDINATOR_10("Hourly Coordinator: 10", 10),
    HOURLY_COORDINATOR_11("Hourly Coordinator: 11", 11),
    HOURLY_COORDINATOR_12("Hourly Coordinator: 12", 12),
    HOURLY_COORDINATOR_13("Hourly Coordinator: 13", 13),
    HOURLY_COORDINATOR_14("Hourly Coordinator: 14", 14),
    HOURLY_COORDINATOR_15("Hourly Coordinator: 15", 15),
    HOURLY_COORDINATOR_16("Hourly Coordinator: 16", 16),
    HOURLY_COORDINATOR_17("Hourly Coordinator: 17", 17),
    HOURLY_COORDINATOR_18("Hourly Coordinator: 18", 18),
    HOURLY_COORDINATOR_19("Hourly Coordinator: 19", 19),
    HOURLY_COORDINATOR_20("Hourly Coordinator: 20", 20),
    HOURLY_COORDINATOR_21("Hourly Coordinator: 21", 21),
    HOURLY_COORDINATOR_22("Hourly Coordinator: 22", 22),
    HOURLY_COORDINATOR_23("Hourly Coordinator: 23", 23),
    NIGHT_COORDINATOR("Night Coordinator", 24),
    MORNING_COORDINATOR("Morning Coordinator", 24+6),
    AFTERNOON_COORDINATOR("Afternoon Coordinator", 24+12),
    EVENING_COORDINATOR("Evening Coordinator", 24+18),
    GENERAL_COORDINATOR("General Coordinator", 48),
    SPIRITUAL_COORDINATOR("Spiritual Coordinator", 96);

    private String coordinatorText;
    private Integer coordinatorValue;

    CoordinatorTypes(String coordinatorText, Integer coordinatorValue) {
        this.coordinatorText = coordinatorText;
        this.coordinatorValue = coordinatorValue;
    }

    public static CoordinatorTypes getTypeFromId(Integer coordinatorType) {
        if (HOURLY_COORDINATOR_0.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_0;
        if (HOURLY_COORDINATOR_1.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_1;
        if (HOURLY_COORDINATOR_2.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_2;
        if (HOURLY_COORDINATOR_3.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_3;
        if (HOURLY_COORDINATOR_4.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_4;
        if (HOURLY_COORDINATOR_5.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_5;
        if (HOURLY_COORDINATOR_6.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_6;
        if (HOURLY_COORDINATOR_7.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_7;
        if (HOURLY_COORDINATOR_8.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_8;
        if (HOURLY_COORDINATOR_9.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_9;
        if (HOURLY_COORDINATOR_10.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_10;
        if (HOURLY_COORDINATOR_11.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_11;
        if (HOURLY_COORDINATOR_12.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_12;
        if (HOURLY_COORDINATOR_13.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_13;
        if (HOURLY_COORDINATOR_14.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_14;
        if (HOURLY_COORDINATOR_15.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_15;
        if (HOURLY_COORDINATOR_16.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_16;
        if (HOURLY_COORDINATOR_17.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_17;
        if (HOURLY_COORDINATOR_18.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_18;
        if (HOURLY_COORDINATOR_19.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_19;
        if (HOURLY_COORDINATOR_20.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_20;
        if (HOURLY_COORDINATOR_21.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_21;
        if (HOURLY_COORDINATOR_22.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_22;
        if (HOURLY_COORDINATOR_23.coordinatorValue.equals(coordinatorType)) return HOURLY_COORDINATOR_23;
        if (NIGHT_COORDINATOR.coordinatorValue.equals(coordinatorType)) return NIGHT_COORDINATOR;
        if (MORNING_COORDINATOR.coordinatorValue.equals(coordinatorType)) return MORNING_COORDINATOR;
        if (AFTERNOON_COORDINATOR.coordinatorValue.equals(coordinatorType)) return AFTERNOON_COORDINATOR;
        if (EVENING_COORDINATOR.coordinatorValue.equals(coordinatorType)) return EVENING_COORDINATOR;
        if (GENERAL_COORDINATOR.coordinatorValue.equals(coordinatorType)) return GENERAL_COORDINATOR;
        if (SPIRITUAL_COORDINATOR.coordinatorValue.equals(coordinatorType)) return SPIRITUAL_COORDINATOR;
        throw new DatabaseHandlingException("Invalid CoordinatorType requested: " + coordinatorType);
    }

    public String getCoordinatorText() {
        return coordinatorText;
    }

    public Integer getCoordinatorValue() {
        return coordinatorValue;
    }
}
