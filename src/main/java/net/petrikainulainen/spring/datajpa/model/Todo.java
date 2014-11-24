package net.petrikainulainen.spring.datajpa.model;

import javax.persistence.*;

/**
 * @author Petri Kainulainen
 */
@Entity
@Table(name="todos")
public class Todo {

    public static final int MAX_LENGTH_DESCRIPTION = 500;
    public static final int MAX_LENGTH_TITLE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description", nullable = true, length = MAX_LENGTH_DESCRIPTION)
    private String description;

    @Column(name = "title", nullable = false, length = MAX_LENGTH_TITLE)
    private String title;

    @Version
    private long version;

    protected Todo(){

    }
    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
