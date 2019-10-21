package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

/**
 * The concert entity contains the title, image, blurb for the concert as well as the dates of the concerts
 * and performers who perform at the concert.
 */
@Entity
@Table(name = "CONCERTS")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "IMAGE_NAME")
    private String imageName;

    @Column(name = "BLURB", length = 1000)
    private String blurb;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "CONCERT_DATES", joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @Column(name = "DATE")
    private Set<LocalDateTime> dates;

    /**
     * Every concert has at least one performer and performers can perform in any number of concerts
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "CONCERT_PERFORMER",
            joinColumns = @JoinColumn(name = "CONCERT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    @Column(name = "PERFORMER_ID")
    private List<Performer> performers = new ArrayList<>();

    public Concert() {
    }

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
    }

    public Concert(String title, String imageName) {
        this.title = title;
        this.imageName = imageName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public List<Performer> getPerformers() {
        return performers;
    }

    public void setPerformers(List<Performer> performers) {
        this.performers = performers;
    }

    public Set<LocalDateTime> getDates() {
        return dates;
    }

    public void setDates(Set<LocalDateTime> dates) { this.dates = dates; }
}
