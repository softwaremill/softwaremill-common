package com.softwaremill.common.dbtest;

import com.softwaremill.common.cdi.persistence.EntityWriter;
import com.softwaremill.common.cdi.transaction.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * A manager for the test entity: enables reads (via an injected bean) and writes via entity writer. 
 * @author Adam Warski (adam at warski dot org)
 */
@Transactional
public class TestEntity1Manager {
    @Inject
    private TestEntity1Reader reader;

    @Inject
    private EntityWriter writer;

    public List<TestEntity1> getCurrentData() {
        return reader.getCurrentData();
    }

    public void addEntity(TestEntity1 te) {
        writer.write(te);
    }
}
