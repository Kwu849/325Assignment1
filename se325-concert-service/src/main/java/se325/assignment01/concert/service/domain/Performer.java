package se325.assignment01.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se325.assignment01.concert.common.types.Genre;

import javax.persistence.*;

/**
 * Performer entity contains the performer's name, image, genre and blurb
 */
@Entity
@Table(name = "PERFORMERS")
public class Performer implements Comparable<Performer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "IMAGE_NAME")
    private String imageName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "GENRE")
    private Genre genre;

    @Column(name = "BLURB", length = 1000)
    private String blurb;

    public Performer() {
    }

    public Performer(Long id, String name, String imageName, Genre genre, String blurb) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.genre = genre;
        this.blurb = blurb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Performer that = (Performer) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(imageName, that.imageName)
                .append(genre, that.genre)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(imageName)
                .append(genre)
                .toHashCode();
    }

    @Override
    public int compareTo(Performer other) {
        return other.getName().compareTo(getName());
    }

}
