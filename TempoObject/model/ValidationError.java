package TempoObject.model;

public class ValidationError {
    private final String fileName;
    private final int lineNumber;
    private final String message;
    private final ErrorType errorType;

    public enum ErrorType {
        TRANSACTION_TOTAL_MISMATCH,
        DUPLICATE_ENTRY,
        INVALID_FORMAT,
        CALCULATION_ERROR
    }

    public ValidationError(String fileName, int lineNumber, String message, ErrorType errorType) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.message = message;
        this.errorType = errorType;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return String.format("Error in %s at line %d: %s (Type: %s)", 
            fileName, lineNumber, message, errorType);
    }
}
