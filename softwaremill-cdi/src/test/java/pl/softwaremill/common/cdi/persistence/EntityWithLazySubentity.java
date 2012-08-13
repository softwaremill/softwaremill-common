package pl.softwaremill.common.cdi.persistence;

import pl.softwaremill.common.util.persistance.Identifiable;

import javax.persistence.*;

/**
 * @author Pawel Stawicki
 * @since 8/13/12 5:30 PM
 */
@Entity
public class EntityWithLazySubentity implements Identifiable<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SimpleEntity subentity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleEntity getSubentity() {
        return subentity;
    }

    public void setSubentity(SimpleEntity subentity) {
        this.subentity = subentity;
    }

}
