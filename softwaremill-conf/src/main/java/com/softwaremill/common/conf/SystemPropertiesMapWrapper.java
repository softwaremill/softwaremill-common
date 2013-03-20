package pl.softwaremill.common.conf;

import java.util.Map;

/**
 * Map Wrapper returning System Property if value is null
 */
public class SystemPropertiesMapWrapper extends MapWrapper {

    public SystemPropertiesMapWrapper(Map<String, String> props) {
        super(props);
    }

    @Override
    public String get(Object key) {
        final String fromMap = delegate.get(key);
        return fromMap != null ? fromMap : System.getProperty(key.toString());
    }
}
