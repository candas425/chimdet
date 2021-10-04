package ro.utcn.backend.model;

import javax.persistence.*;

/**
 * User
 *
 * Created by Lucian on 5/11/2017.
 */

@Entity
@Table(name = "user")
public class User {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "parola";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PAROLA", nullable = false)
    private String parola;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
}
