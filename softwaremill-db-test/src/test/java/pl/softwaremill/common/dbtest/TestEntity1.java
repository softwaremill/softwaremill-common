package pl.softwaremill.common.dbtest;

import pl.softwaremill.cdiext.persistence.Identifiable;

import javax.persistence.*;

/**
 * A simple test entity.
 * @author Adam Warski (adam at warski dot org)
 */
@Entity
@Table(name = "test_entity_1")
public class TestEntity1 implements Identifiable<Long> {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "data_c")
    private String data;

    public TestEntity1() {
    }

    public TestEntity1(Long id, String data) {
        this.id = id;
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
        if (!(o instanceof TestEntity1)) return false;

        TestEntity1 that = (TestEntity1) o;

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
