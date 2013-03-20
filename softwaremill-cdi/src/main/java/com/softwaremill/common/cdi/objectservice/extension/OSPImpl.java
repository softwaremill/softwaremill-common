package pl.softwaremill.common.cdi.objectservice.extension;

import pl.softwaremill.common.cdi.objectservice.OS;
import pl.softwaremill.common.cdi.objectservice.OSP;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class OSPImpl implements OSP {
    private final ObjectServiceExtension extension;
    private final Class<?> serviceClass;

    public OSPImpl(ObjectServiceExtension extension, Class<?> serviceClass) {
        this.extension = extension;
        this.serviceClass = serviceClass;
    }

    @Override
    public OS f(Object obj) {
        OS objectService = (OS) extension.serviceForObject(obj.getClass(), serviceClass);
        //noinspection unchecked
        objectService.setServiced(obj);
        return objectService;
    }
}
