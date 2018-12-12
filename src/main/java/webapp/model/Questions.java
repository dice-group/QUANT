package webapp.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="QUESTIONS")
public class Questions implements Serializable{


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID", nullable =false)
    private Dataset datasetQuestion;

    private String answertype;
    private boolean aggregation;
    private boolean onlydb;
    private boolean hybrid;
    private int version;
    private boolean original;
    private boolean removed;
    private Timestamp timestamp;
    private String sparqlQuery;
    private String answer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "ANOTATOR_ID", nullable =false)
    private User anotatorUser;

   // @OneToMany (mappedBy = "qid")
    //private List<Translations> translationsList;

    protected Questions() {}

    public Dataset getDatasetQuestion() {
        return datasetQuestion;
    }

    public void setDatasetQuestion(Dataset datasetQuestion) {
        this.datasetQuestion = datasetQuestion;
    }

    public Questions(Dataset datasetQuestion, String answertype, boolean aggregation, boolean onlydb, boolean hybrid, boolean original, User user, int version)
    {
        this.datasetQuestion = datasetQuestion;
        this.answertype = answertype;
        this.original = original;
        this.anotatorUser = user;
        Date date = new Date();
        this.timestamp = new Timestamp((date.getTime()));
        this.aggregation = aggregation;
        this.hybrid =hybrid;
        this.onlydb = onlydb;
        this.removed = false;
        this.version = version;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public User getAnotatorUser() {
        return anotatorUser;
    }

    public void setAnotatorUser(User anotatorUser) {
        this.anotatorUser = anotatorUser;
    }

    //public List<Translations> getTranslationsList() {
        //return translationsList;
    //}

    //public void setTranslationsList(List<Translations> translationsList) {
    //    this.translationsList = translationsList;
    //}

}
