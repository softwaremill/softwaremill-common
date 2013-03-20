package pl.softwaremill.common.cdi.autofactory.extension.parameter.converter;

import pl.softwaremill.common.cdi.autofactory.extension.parameter.ParameterValue;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public interface ConstructorToParameterValuesConverter {
    ParameterValue[] convert();
}
