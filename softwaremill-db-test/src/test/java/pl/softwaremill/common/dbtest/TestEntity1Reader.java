package pl.softwaremill.common.dbtest;

import pl.softwaremill.cdiext.persistence.ReadOnly;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * A helper bean for reading test entities.
 * @author Adam Warski (adam at warski dot org)
 */
@RequestScoped
public class TestEntity1Reader {
    @Inject
    @ReadOnly
    private EntityManager em;

    @SuppressWarnings({"unchecked"})
    public List<TestEntity1> getCurrentData() {
        return em.createQuery("select t from TestEntity1 t order by t.id").getResultList();
    }
}
