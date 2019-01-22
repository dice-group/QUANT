package webapp.model;


import org.apache.jena.tdb.store.Hash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="TRANSLATIONS")
public class Translations implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String lang;



    @ElementCollection
    private List<String> keywords;

    private String questionString;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTIONS_FID", nullable =false)
    private Questions qid;

    protected Translations(){}

    public Translations(Questions qid, String lang, List keywords, String questionString)
    {
        this.qid = qid;
        this.lang = lang;
        this.keywords = keywords;
        this.questionString = questionString;
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
    public List getKeywords() {
        return keywords;
    }

    public void setKeywords(List keywords) {
        this.keywords = keywords;
    }
}
