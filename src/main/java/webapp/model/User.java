package webapp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Table(name="USER")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "*Please provide your password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    // Jans Änderung
    @OneToMany(mappedBy = "datasetUser")
    private List<Dataset> datasets;

    @OneToMany(mappedBy = "anotatorUser")
    private List<Questions> anotator;
   // @Autowired
   // private BCryptPasswordEncoder bCryptPasswordEncoder;

    protected User(){}

    public User(String email,String password, Role role){
        this.email=email;
        this.password= new BCryptPasswordEncoder().encode(password);
        this.role=role;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
