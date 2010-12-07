package pl.softwaremill.common.sdb.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xerox.amazonws.simpledb.Domain;
import com.xerox.amazonws.simpledb.Item;
import com.xerox.amazonws.simpledb.SDBException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class DomainRestore {
    private final Domain domain;
    private final BufferedReader reader;

    private final Gson gson;

    private final List<Item> itemsQueue;

    public DomainRestore(Domain domain, BufferedReader reader) {
        this.domain = domain;
        this.reader = reader;

        gson = new GsonBuilder().create();
        itemsQueue = new ArrayList<Item>();
    }

    public void restore() throws IOException, SDBException {
        String line;
        while ((line = reader.readLine()) != null) {
            addItemFromLine(line);
        }

        flushItemsQueue();
    }

    private void addItemFromLine(String line) throws SDBException {
        JSONItem jsonItem = gson.fromJson(line, JSONItem.class);
        itemsQueue.add(jsonItemToSimpleDBItem(jsonItem));

        // Batch put can have up to 25 items
        if (itemsQueue.size() > 24) {
            flushItemsQueue();
        }
    }

    private void flushItemsQueue() throws SDBException {
        if (itemsQueue.size() > 0) {
            domain.batchPutAttributes(itemsQueue);
            itemsQueue.clear();
        }
    }

    private Item jsonItemToSimpleDBItem(JSONItem jsonItem) {
        Item item = domain.createItem(jsonItem.getIdentifier());
        for (JSONAttribute jsonAttribute : jsonItem.getAttributes()) {
            item.getAttributes().put(jsonAttribute.getName(), jsonAttribute.getValues());
        }

        return item;
    }
}
