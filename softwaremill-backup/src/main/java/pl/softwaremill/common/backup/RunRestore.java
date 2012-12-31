package pl.softwaremill.common.backup;

import com.google.common.base.Charsets;
import com.xerox.amazonws.simpledb.SDBException;
import com.xerox.amazonws.simpledb.SimpleDB;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RunRestore {
    private final SimpleDB simpleDB;
    private final String domainName;
    private final String fileName;

    public RunRestore(String accessKeyId, String secretAccessKey, SimpleDBRegion region, String domainName,
                      String fileName) throws SDBException {
        simpleDB = new SimpleDB(accessKeyId, secretAccessKey, true, region.getAddress());

        this.domainName = domainName;
        this.fileName = fileName;
    }

    public void run() throws SDBException, IOException {
        new DomainRestore(simpleDB.getDomain(domainName), new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), Charsets.UTF_8))).restore();
        System.out.println("Restore complete");
    }

    public static void main(String[] args) throws SDBException, IOException {
        if (args.length != 5) {
            System.out.println("Usage: RunRestore accessKeyId secretAccessKey region domain fileName");
            System.out.println("Region can be one of: US_EAST, US_WEST, EUROPE_WEST, ASIA_SOUTHEAST");
        } else {
            new RunRestore(args[0], args[1], SimpleDBRegion.valueOf(args[2]), args[3], args[4]).run();
        }
    }
}
