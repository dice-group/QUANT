package suggestion.metadata;

public class MetadataSuggestions {
    private String answerType;
    private boolean aggregation;
    private boolean hybrid;
    private boolean onlyDbo;
    private boolean outOfScope;

    public MetadataSuggestions(){
        aggregation = false;
        hybrid = false;
        onlyDbo = false;
        outOfScope = false;

    }
    public boolean isAggregation() {
        return aggregation;
    }

    public void setAggregation(boolean aggregation) {
        this.aggregation = aggregation;
    }

    public boolean isHybrid() {
        return hybrid;
    }

    public void setHybrid(boolean hybrid) {
        this.hybrid = hybrid;
    }

    public boolean isOnlyDbo() {
        return onlyDbo;
    }

    public void setOnlyDbo(boolean onlyDbo) {
        this.onlyDbo = onlyDbo;
    }

    public boolean isOutOfScope() {
        return outOfScope;
    }

    public void setOutOfScope(boolean outOfScope) {
        this.outOfScope = outOfScope;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }
}
