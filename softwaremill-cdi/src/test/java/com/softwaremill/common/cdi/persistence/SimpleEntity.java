package pl.softwaremill.common.cdi.persistence;

import pl.softwaremill.common.util.persistance.Identifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Entity
public class SimpleEntity implements Identifiable<Long> {
    @Id
    @GeneratedValue
    private Long id;

    private String data;

    public SimpleEntity() {
    }

    public SimpleEntity(String data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEntity)) return false;

        SimpleEntity that = (SimpleEntity) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
