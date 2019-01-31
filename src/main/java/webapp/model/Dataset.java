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
    @JoinColumn(name = "USER_FID", nullable =false)
    private User datasetUser;
    private String name;
    private String endpoint;


//   @OneToMany(mappedBy = "datasetQuestion")
  //  private List<Questions> questionsList;

    protected Dataset() {}

    public Dataset(User user, String name, String endpoint) {
        this.datasetUser = user;
        this.name = name;
        this.endpoint = endpoint;
    }

    public Dataset(User user, String name) {
        this.datasetUser = user;
        this.name = name;
        this.endpoint = "";


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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
