package webapp.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Date;
import java.util.Set;


@Entity
@Table(name="QUESTIONS")
public class Questions implements Serializable{


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "DATASET_FID", nullable =false)
    private Dataset datasetQuestion;

    private long questionSetId;
    private String answertype;
    private boolean aggregation;
    private boolean onlydb;
    private boolean hybrid;
    private int version;
    private boolean original;
    private boolean removed;
    private boolean anotated;
    private boolean activeVersion;
    private boolean outOfScope;
    private Timestamp timestamp;
    @Lob
    private String sparqlQuery;

    @ElementCollection
    @Lob
    private Set<String> answer;
    private String originalId;



    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "ANOTATOR_ID", nullable =false)
    private User anotatorUser;

    @OneToMany (mappedBy = "qid")
    private List<Translations> translationsList;

    public Questions() {}

    //Constructor for uploaded datasets and newly created questions
    public Questions(Dataset datasetQuestion, String answertype, boolean aggregation, boolean onlydb, boolean hybrid, boolean original,
                     boolean activeVersion, boolean anotated, User user, int version, boolean outOfScope, String sparqlQuery, Set answer, String originalId)
    {
        this.datasetQuestion = datasetQuestion;
        this.answertype = answertype;
        this.original = original;
        this.activeVersion = activeVersion;
        this.anotated = anotated;
        this.anotatorUser = user;
        Date date = new Date();
        this.timestamp = new Timestamp((date.getTime()));
        this.aggregation = aggregation;
        this.hybrid =hybrid;
        this.onlydb = onlydb;
        this.removed = false;
        this.version = version;
        this.outOfScope = outOfScope;
        this.questionSetId = this.id;
        this.sparqlQuery = sparqlQuery;
        this.answer = answer;
        this.originalId = originalId;


    }

    public Questions(Dataset datasetQuestion, String answertype, boolean aggregation, boolean onlydb, boolean hybrid, boolean original,
                     boolean activeVersion, boolean anotated, User user, int version, boolean outOfScope, long questionSetId, String sparqlQuery, Set answer)
    {
        this.datasetQuestion = datasetQuestion;
        this.answertype = answertype;
        this.original = original;
        this.activeVersion = activeVersion;
        this.anotated = anotated;
        this.anotatorUser = user;
        Date date = new Date();
        this.timestamp = new Timestamp((date.getTime()));
        this.aggregation = aggregation;
        this.hybrid =hybrid;
        this.onlydb = onlydb;
        this.removed = false;
        this.version = version;
        this.outOfScope = outOfScope;
        this.questionSetId = questionSetId;
        this.sparqlQuery = sparqlQuery;
        this.answer = answer;
        this.originalId = "0";

        }

    public long getNext(List<Questions> liste){
        int index = liste.indexOf(this);
        if (index < 0 || index +1 ==liste.size()) return -1;

        return liste.get(index+1).getId();
    }

    public String getAnswerAsString(){
        String answerString = String.join("\n", this.answer);
        return answerString;
    }
    public String getAnswertype() {
        return answertype;
    }

    public void setAnswertype(String answertype) {
        this.answertype = answertype;
    }

    public boolean isAggregation() {
        return aggregation;
    }

    public void setAggregation(boolean aggregation) {
        this.aggregation = aggregation;
    }

    public boolean isOnlydb() {
        return onlydb;
    }

    public void setOnlydb(boolean onlydb) {
        this.onlydb = onlydb;
    }

    public boolean isHybrid() {
        return hybrid;
    }

    public void setHybrid(boolean hybrid) {
        this.hybrid = hybrid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public void setSparqlQuery(String sparqlQuery) {
        this.sparqlQuery = sparqlQuery;
    }

    public Set getAnswer() {
        return answer;
    }

//    public void setAnswer(Set answer) {
//        this.answer = answer;
//    }
    public void setAnswer(Set<String> answer) {
    this.answer = answer;
}

    public User getAnotatorUser() {
        return anotatorUser;
    }

    public void setAnotatorUser(User anotatorUser) {
        this.anotatorUser = anotatorUser;
    }

    public List<Translations> getTranslationsList() {
        return translationsList;
    }

    public void setTranslationsList(List<Translations> translationsList) {
        this.translationsList = translationsList;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public boolean isActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(boolean activeVersion) {
        this.activeVersion = activeVersion;
    }

    public long getQuestionSetId() {
        return questionSetId;
    }

    public void setQuestionSetId(long questionSetId) {
        this.questionSetId = questionSetId;
    }

    public Dataset getDatasetQuestion() {
        return datasetQuestion;
    }

    public void setDatasetQuestion(Dataset datasetQuestion) {
        this.datasetQuestion = datasetQuestion;
    }

    public boolean isOutOfScope() {
        return outOfScope;
    }

    public void setOutOfScope(boolean outOfScope) {
        this.outOfScope = outOfScope;
    }

    public boolean isAnotated() {
        return anotated;
    }

    public void setAnotated(boolean anotated) {
        this.anotated = anotated;
    }


}
