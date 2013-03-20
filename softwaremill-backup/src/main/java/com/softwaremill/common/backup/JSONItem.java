package com.softwaremill.common.backup;

import com.xerox.amazonws.simpledb.Item;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class JSONItem {
    private String identifier;
    private Set<JSONAttribute> attributes;

    public JSONItem() {}

    public JSONItem(Item item) {
        identifier = item.getIdentifier();
        attributes = new HashSet<JSONAttribute>();
        for (Map.Entry<String, Set<String>> attributeEntry : item.getAttributes().entrySet()) {
            attributes.add(new JSONAttribute(attributeEntry.getKey(), attributeEntry.getValue()));
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Set<JSONAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<JSONAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSONItem)) return false;

        JSONItem jsonItem = (JSONItem) o;

        if (attributes != null ? !attributes.equals(jsonItem.attributes) : jsonItem.attributes != null) return false;
        if (identifier != null ? !identifier.equals(jsonItem.identifier) : jsonItem.identifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier != null ? identifier.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }
}
