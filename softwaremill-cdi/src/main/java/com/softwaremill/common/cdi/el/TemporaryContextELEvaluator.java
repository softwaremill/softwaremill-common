package com.softwaremill.common.cdi.el;

import com.softwaremill.common.util.ClassUtil;

import javax.el.*;
import javax.enterprise.inject.spi.BeanManager;
import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Adam Warski (adam at warski dot org)
 * @author http://community.jboss.org/thread/148045?tstart=0
 */
public class TemporaryContextELEvaluator extends AbstractELEvaluator {
    private final ExpressionFactory expressionFactory;
    private final ELContext elContext;
    private final ConstantResolver constantResolver;

    public TemporaryContextELEvaluator(BeanManager beanManager) {
        constantResolver = new ConstantResolver();

        ExpressionFactory tempExpressionFactory;
        try {
            tempExpressionFactory = ExpressionFactory.newInstance();
        } catch (RuntimeException e) {
            tempExpressionFactory = ClassUtil.newInstance("com.sun.el.ExpressionFactoryImpl", ExpressionFactory.class);
        }

        expressionFactory = beanManager.wrapExpressionFactory(tempExpressionFactory);

        elContext = createELContext(beanManager);
    }

    public <T> T evaluate(String expression, Class<T> expectedResultType) {
        return ELEvaluatorUtil.evaluate(elContext, expressionFactory, expression, expectedResultType);
    }

    public void setParameter(String name, Object value) {
        constantResolver.addConstant(name, value);
    }

    public void clearParameter(String name) {
        constantResolver.removeConstant(name);
    }

    private ELContext createELContext(BeanManager beanManager) {
        return createELContext(createELResolver(beanManager),
                ClassUtil.newInstance(FUNCTION_MAPPER_IMPL_CLASSNAME, FunctionMapper.class),
                ClassUtil.newInstance(VARIABLE_MAPPER_IMPL_CLASSNAME, VariableMapper.class));
    }

    private ELResolver createELResolver(BeanManager beanManager) {
        CompositeELResolver resolver = new CompositeELResolver();
        resolver.add(beanManager.getELResolver());
        resolver.add(new MapELResolver());
        resolver.add(new ListELResolver());
        resolver.add(new ArrayELResolver());
        resolver.add(new ResourceBundleELResolver());
        resolver.add(new BeanELResolver());
        resolver.add(constantResolver);

        return resolver;
    }

    private ELContext createELContext(final ELResolver resolver, final FunctionMapper functionMapper,
                                      final VariableMapper variableMapper) {
        return new ELContext() {
            @Override
            public ELResolver getELResolver() {
                return resolver;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return functionMapper;
            }

            @Override
            public VariableMapper getVariableMapper() {
                return variableMapper;
            }
        };
    }

    private static class ConstantResolver extends ELResolver {
        private final Map<String, Object> constants = new HashMap<String, Object>();

        public void addConstant(String name, Object value) {
            constants.put(name, value);
        }

        public void removeConstant(String name) {
            constants.remove(name);
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            if (base == null && constants.containsKey(property)) {
                context.setPropertyResolved(true);
                return constants.get(property);
            }

            return null;
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property) {
            if (base == null && constants.containsKey(property)) {
                return constants.get(property).getClass();
            }

            return null;
        }

        @Override
        public void setValue(ELContext context, Object base, Object property, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property) {
            return true;
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
            return null;
        }

        @Override
        public Class<?> getCommonPropertyType(ELContext context, Object base) {
            return null;
        }
    }

    //

    private final static String FUNCTION_MAPPER_IMPL_CLASSNAME;
    private final static String VARIABLE_MAPPER_IMPL_CLASSNAME;

    static {
        FUNCTION_MAPPER_IMPL_CLASSNAME = ClassUtil.findClassPresentInClasspath(
                "com.sun.el.lang.FunctionMapperImpl", "org.apache.el.lang.FunctionMapperImpl");
        VARIABLE_MAPPER_IMPL_CLASSNAME = ClassUtil.findClassPresentInClasspath(
                "com.sun.el.lang.VariableMapperImpl", "org.apache.el.lang.VariableMapperImpl");
    }
}
