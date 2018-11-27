package webapp.model;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="TRANSLATIONS")
public class Translations implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String lang;
    private String keywords;
    private String questionString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSLATIONS_ID", nullable =false)
    private Questions qid;

    protected Translations(){}

    public Translations(Questions qid, String lang, String keywords, String frage)
    {
        this.qid = qid;
        this.lang = lang;
        this.keywords = keywords;
        this.questionString = frage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public Questions getQid() {
        return qid;
    }

    public void setQid(Questions qid) {
        this.qid = qid;
    }
}
