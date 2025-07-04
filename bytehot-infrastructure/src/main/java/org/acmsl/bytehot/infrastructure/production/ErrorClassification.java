package org.acmsl.bytehot.infrastructure.production;

public class ErrorClassification {
    private final ErrorType errorType;
    private final ErrorSeverity severity;
    private final Recoverability recoverability;
    private final boolean requiresIncidentReport;
    private final Throwable error;
    private final int previousRetries;
    
    public ErrorClassification(ErrorType errorType, ErrorSeverity severity, Recoverability recoverability, 
                              boolean requiresIncidentReport, Throwable error, int previousRetries) {
        this.errorType = errorType;
        this.severity = severity;
        this.recoverability = recoverability;
        this.requiresIncidentReport = requiresIncidentReport;
        this.error = error;
        this.previousRetries = previousRetries;
    }
    
    public static Builder builder() { return new Builder(); }
    public Builder toBuilder() { return new Builder(this); }
    
    public ErrorType getErrorType() { return errorType; }
    public ErrorSeverity getSeverity() { return severity; }
    public Recoverability getRecoverability() { return recoverability; }
    public boolean requiresIncidentReport() { return requiresIncidentReport; }
    public Throwable getError() { return error; }
    public int getPreviousRetries() { return previousRetries; }
    
    public static ErrorClassification unknown(Throwable error) {
        return builder().errorType(ErrorType.UNKNOWN).severity(ErrorSeverity.MEDIUM)
            .recoverability(Recoverability.UNKNOWN).requiresIncidentReport(true).error(error).build();
    }
    
    public static class Builder {
        private ErrorType errorType;
        private ErrorSeverity severity;
        private Recoverability recoverability;
        private boolean requiresIncidentReport;
        private Throwable error;
        private int previousRetries;
        
        public Builder() {}
        public Builder(ErrorClassification source) {
            this.errorType = source.errorType;
            this.severity = source.severity;
            this.recoverability = source.recoverability;
            this.requiresIncidentReport = source.requiresIncidentReport;
            this.error = source.error;
            this.previousRetries = source.previousRetries;
        }
        
        public Builder errorType(ErrorType errorType) { this.errorType = errorType; return this; }
        public Builder severity(ErrorSeverity severity) { this.severity = severity; return this; }
        public Builder recoverability(Recoverability recoverability) { this.recoverability = recoverability; return this; }
        public Builder requiresIncidentReport(boolean requiresIncidentReport) { this.requiresIncidentReport = requiresIncidentReport; return this; }
        public Builder error(Throwable error) { this.error = error; return this; }
        public Builder previousRetries(int previousRetries) { this.previousRetries = previousRetries; return this; }
        
        public ErrorClassification build() {
            return new ErrorClassification(errorType, severity, recoverability, requiresIncidentReport, error, previousRetries);
        }
    }
}