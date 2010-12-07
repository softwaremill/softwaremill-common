package pl.softwaremill.common.sdb.backup;

import com.xerox.amazonws.simpledb.Domain;
import com.xerox.amazonws.simpledb.SDBException;
import com.xerox.amazonws.simpledb.SimpleDB;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import pl.softwaremill.common.util.RichString;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RunBackup {
    private final static DateTimeFormatter dateOnly = new DateTimeFormatterBuilder()
            .appendDayOfMonth(2)
            .appendLiteral('_')
            .appendMonthOfYear(2)
            .appendLiteral('_')
            .appendYear(4, 4)
            .toFormatter();

    private final SimpleDB simpleDB;
    private final List<String> domainNames;

    public RunBackup(String accessKeyId, String secretAccessKey, SimpleDBRegion region, String domainsDescriptor) throws SDBException {
        simpleDB = new SimpleDB(accessKeyId, secretAccessKey, true, region.getAddress());

        if (domainsDescriptor.equals("*")) {
            domainNames = new ArrayList<String>();
            for (Domain domain : simpleDB.listDomains().getItems()) {
                domainNames.add(domain.getName());
            }
        } else {
            domainNames = new RichString(domainsDescriptor).splitByCommaGetNonEmpty();
        }
    }

    public void run() throws SDBException, IOException {
        for (String domainName : domainNames) {
            backupDomain(domainName);
        }
        System.out.println("Backup complete");
    }

    private void backupDomain(String domainName) throws SDBException, IOException {
        String date = dateOnly.print(new DateTime());

        File file = new File("simpledb_backup_" + domainName + "_on_" + date + ".txt");
        Writer writer = new BufferedWriter(new FileWriter(file));
        new DomainBackup(simpleDB.getDomain(domainName), writer).backup();

        System.out.println("Written backup for: '" + domainName + "' into file: " + file.getAbsolutePath());
    }

    public static void main(String[] args) throws SDBException, IOException {
        if (args.length != 4) {
            System.out.println("Usage: RunBackup accessKeyId secretAccessKey region domains");
            System.out.println("Region can be one of: US_EAST, US_WEST, EUROPE_WEST, ASIA_SOUTHEAST");
            System.out.println("Domains can be a comma-separated list of domains or a * (all domains)");
        } else {
            new RunBackup(args[0], args[1], SimpleDBRegion.valueOf(args[2]), args[3]).run();
        }
    }
}
