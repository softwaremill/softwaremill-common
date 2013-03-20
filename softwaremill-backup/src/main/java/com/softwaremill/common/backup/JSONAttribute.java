package pl.softwaremill.common.backup;

import java.util.Set;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class JSONAttribute {
    private String name;
    private Set<String> values;

    public JSONAttribute() {
    }

    public JSONAttribute(String name, Set<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSONAttribute)) return false;

        JSONAttribute that = (JSONAttribute) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
