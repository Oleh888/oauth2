package ua.yaroslav.auth2.entity;

import javax.persistence.*;

@Table(name = "clients")
@Entity
public class Client {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column
    private long id;
    @Column(unique=true)
    private String name;
    @Column
    private String secret;


    public Client() {

    }

    public Client(String name, String secret) {
        this.name = name;
        this.secret = secret;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "\nClient {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}