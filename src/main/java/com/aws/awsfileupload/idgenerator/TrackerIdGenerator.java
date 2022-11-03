package com.aws.awsfileupload.idgenerator;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

public class TrackerIdGenerator extends SequenceStyleGenerator {

    private static final String VALUE_PREFIX_GENERATOR = "valuePrefix";
    private static final String VALUE_PREFIX_DEFAULT = "";
    private String valuePrefix;

    private static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    private static final String NUMBER_FORMAT_DEFAULT = "%d";
    private String numberFormat;


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return super.generate(session, object);
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(type, params, serviceRegistry);
    }
}
