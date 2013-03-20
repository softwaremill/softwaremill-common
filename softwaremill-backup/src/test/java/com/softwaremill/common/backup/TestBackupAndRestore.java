package pl.softwaremill.common.backup;

import com.google.common.collect.ImmutableMap;
import com.xerox.amazonws.simpledb.Domain;
import com.xerox.amazonws.simpledb.SDBException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.ImmutableSet.*;
import static org.joda.time.format.ISODateTimeFormat.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TestBackupAndRestore extends AbstractBackupAndRestoreTest {
    private Domain simpleDataDomain;
    private Domain dashedDomain;
    private Map<String, Map<String, Set<String>>> simpleData;

    @BeforeClass
    public void prepareData() throws SDBException {
        simpleDataDomain = simpleDB.createDomain("backup_and_restore_simple").getResult();
        simpleDataDomain.setCacheProvider(null);

        ImmutableMap.Builder<String, Map<String, Set<String>>> simpleDataBuilder = ImmutableMap.builder();
        simpleData = simpleDataBuilder
                .put("id1", ImmutableMap.<String, Set<String>>of(
                        "attr1", of("val1", "val2", "val3"),
                        "attr2", of("1234", "5678"),
                        "attr3", of("xxx", "yyy"),
                        "attr4", of("val1", "val2", "val3")))
                .put(UUID.randomUUID().toString(), ImmutableMap.<String, Set<String>>of(
                        "date", of(dateTimeNoMillis().print(System.currentTimeMillis()), dateTimeNoMillis().print(System.currentTimeMillis() - 1234568L)),
                        "long", of("00000000012"),
                        "multilinetext", of("01abc\ndef\nxyz", "02asdahslkdhaslkdhaslkjdhaslkjhdalksjhdalksjhdal")))
                .put(UUID.randomUUID().toString(), ImmutableMap.<String, Set<String>>of(
                        "locals", of("ąęśćżźćłó")))
                .build();

        populateDomainWithData(simpleDataDomain, simpleData);
        makeConsistent(simpleDataDomain);

        // --

        dashedDomain = simpleDB.createDomain("backup-and-restore-simple").getResult();
        dashedDomain.setCacheProvider(null);
    }

    @Test
    public void testBackupAndRestoreSimple() throws IOException, SDBException {
        // Given the data in simpleDataDomain and
        StringWriter writer = new StringWriter();

        // When
        new DomainBackup(simpleDataDomain, writer).backup();
        clearDomain(simpleDataDomain);
        new DomainRestore(simpleDataDomain, new BufferedReader(new StringReader(writer.toString()))).restore();

        // Then
        assertDomainHasData(simpleDataDomain, simpleData);
    }

    @Test
    public void testBackupAndRestoreDashed() throws IOException, SDBException {
        // Given the data in simpleDataDomain and
        StringWriter writer = new StringWriter();

        // When
        new DomainBackup(dashedDomain, writer).backup();
        new DomainRestore(dashedDomain, new BufferedReader(new StringReader(writer.toString()))).restore();

        // Then
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws SDBException {
        simpleDB.deleteDomain(simpleDataDomain);
        simpleDB.deleteDomain(dashedDomain);
    }
}
