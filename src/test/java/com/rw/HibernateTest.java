package com.rw;

import com.rw.entity.Event;
import com.rw.entity.NoDefaultConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HibernateTest {

    private Logger logger;
    private SessionFactory sessionFactory;

    @Before
    public void setup()
    {
        logger = new Logger();
        logger.log("Application Start");

        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            logger.log(e.getMessage());
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @After
    public void teardown()
    {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
        logger.log("Application End");
    }

    @Test
    public void testSaveLoadNoDefaultConstructor()
    {
        // Demonstrates that the object does not need a default constructor.
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new NoDefaultConstructor("Our very first event!"));
        session.save(new NoDefaultConstructor("A follow up event"));
        session.getTransaction().commit();

        session.beginTransaction();
        List<NoDefaultConstructor> items = Arrays.asList(
            session.load(NoDefaultConstructor.class, 1L),
            session.load(NoDefaultConstructor.class, 2L));
        session.getTransaction().commit();
        session.close();

        for (NoDefaultConstructor item: items) {
            logger.log(String.format("%s: %s", item.getId(), item.getTitle()));
        }
    }

    @Test
    public void testStoreEvents()
    {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new Event("Our very first event!", new Date()));
        session.save(new Event("A follow up event", new Date()));
        session.getTransaction().commit();
        session.close();
    }
}
