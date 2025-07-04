package org.acmsl.bytehot.infrastructure.production;

public class OperationContext {
    private final String operationType;
    private final boolean isCritical;
    private final int retryCount;
    private final boolean hasUserImpact;
    
    public OperationContext(String operationType, boolean isCritical, int retryCount, boolean hasUserImpact) {
        this.operationType = operationType;
        this.isCritical = isCritical;
        this.retryCount = retryCount;
        this.hasUserImpact = hasUserImpact;
    }
    
    public static Builder builder() { return new Builder(); }
    
    public String getOperationType() { return operationType; }
    public boolean isCriticalOperation() { return isCritical; }
    public int getRetryCount() { return retryCount; }
    public boolean hasUserImpact() { return hasUserImpact; }
    
    public static class Builder {
        private String operationType;
        private boolean isCritical;
        private int retryCount;
        private boolean hasUserImpact;
        
        public Builder operationType(String operationType) { this.operationType = operationType; return this; }
        public Builder isCritical(boolean isCritical) { this.isCritical = isCritical; return this; }
        public Builder retryCount(int retryCount) { this.retryCount = retryCount; return this; }
        public Builder hasUserImpact(boolean hasUserImpact) { this.hasUserImpact = hasUserImpact; return this; }
        
        public OperationContext build() {
            return new OperationContext(operationType, isCritical, retryCount, hasUserImpact);
        }
    }
}