package TempoObject.model;

public enum TransactionType {
    NORMAL(""),
    ROKOK("R"),
    WALLET("W");

    private final String suffix;

    TransactionType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public static TransactionType fromSuffix(String value) {
        if (value == null || value.isEmpty()) {
            return NORMAL;
        }
        for (TransactionType type : values()) {
            if (value.endsWith(type.getSuffix())) {
                return type;
            }
        }
        return NORMAL;
    }
}
