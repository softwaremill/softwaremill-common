package pl.softwaremill.common.backup;

import com.google.common.collect.ImmutableMap;
import com.xerox.amazonws.simpledb.Domain;
import com.xerox.amazonws.simpledb.SDBException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.softwaremill.common.util.RichString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class StressTestBackupAndRestore extends AbstractBackupAndRestoreTest {
    private Domain lotsOfDataDomain;
    private Map<String, Map<String, Set<String>>> lotsOfData;

    @BeforeClass
    public void prepareData() throws SDBException {
        lotsOfDataDomain = simpleDB.createDomain("backup_and_restore_lots").getResult();
        lotsOfDataDomain.setCacheProvider(null);

        Random random = new Random();

        ImmutableMap.Builder<String, Map<String, Set<String>>> lotsOfDataBuilder = ImmutableMap.builder();
        for (int i=0; i<112; i++) {
            ImmutableMap.Builder<String, Set<String>> attributesBuilder = ImmutableMap.builder();
            int numberOfAttributes = random.nextInt(10) + 1;
            for (int j=0; j<numberOfAttributes; j++) {
                Set<String> values = new HashSet<String>();
                int numberOfValues = random.nextInt(10) + 1;
                for (int k=0; k<numberOfValues; k++) {
                    values.add(RichString.generateRandom(random.nextInt(40) + 10));
                }

                String key = RichString.generateRandom(random.nextInt(10) + 10);
                attributesBuilder = attributesBuilder.put(key, values);
            }

            lotsOfDataBuilder = lotsOfDataBuilder.put(UUID.randomUUID().toString() + RichString.generateRandom(5),
                    attributesBuilder.build());
        }

        lotsOfData = lotsOfDataBuilder.build();

        populateDomainWithData(lotsOfDataDomain, lotsOfData);
    }

    @Test(enabled = false)
    public void stressTestBackupAndRestore() throws IOException, SDBException {
        // Given the data in simpleDataDomain and
        StringWriter writer = new StringWriter();

        // When
        new DomainBackup(lotsOfDataDomain, writer).backup();
        clearDomain(lotsOfDataDomain);
        new DomainRestore(lotsOfDataDomain, new BufferedReader(new StringReader(writer.toString()))).restore();

        // Then
        assertDomainHasData(lotsOfDataDomain, lotsOfData);
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws SDBException {
        simpleDB.deleteDomain(lotsOfDataDomain);
    }
}
