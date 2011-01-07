package pl.softwaremill.common.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xerox.amazonws.simpledb.Domain;
import com.xerox.amazonws.simpledb.Item;
import com.xerox.amazonws.simpledb.SDBException;
import com.xerox.amazonws.simpledb.SDBListResult;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class DomainBackup {
    private final Domain domain;
    private final Writer writer;

    private final Gson gson;

    public DomainBackup(Domain domain, Writer writer) {
        this.domain = domain;
        this.writer = writer;

        gson = new GsonBuilder().create();
    }

    public void backup() throws SDBException, IOException {
        String nextToken = null;
        do {
            SDBListResult<Item> result = selectNextDataPortion(nextToken);
            nextToken = result.getNextToken();

            appendItemsToWriter(result.getItems());
        } while (nextToken != null);
    }

    private SDBListResult<Item> selectNextDataPortion(String nextToken) throws SDBException {
        return domain.selectItems("select * from `" + domain.getName() + "`", nextToken, false);
    }

    private void appendItemsToWriter(List<Item> items) throws IOException {
        for (Item item : items) {
            JSONItem jsonItem = new JSONItem(item);
            String jsonOutput = gson.toJson(jsonItem);
            writer.append(jsonOutput).append("\n");
        }

        writer.flush();
    }
}
