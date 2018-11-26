package webapp.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="DATASET")
public class Dataset implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable =false)
    private User datasetUser;

    private String name;
    private String meta;

//   @OneToMany(mappedBy = "datasetQuestion")
  //  private List<Questions> questionsList;

    protected Dataset() {}

    public Dataset(User user, String name, String meta) {
        this.datasetUser = user;
        this.name = name;
         this.meta = meta;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getDatasetUser() {
        return datasetUser;
    }

    public void setDatasetUser(User datasetUser) {
        this.datasetUser = datasetUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
