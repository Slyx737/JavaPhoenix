public class ErrorInfo {
    private String errorType;
    private int lineNumber;
    private String description;

    public ErrorInfo(String errorType, int lineNumber, String description) {
        this.errorType = errorType;
        this.lineNumber = lineNumber;
        this.description = description;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
