package config;

import model.entity.*;
import init.EnvironmentVariables;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class HibernateUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);
    private static final int TOTAL_ATTEMPTS = 10;
    private static final long SECONDS_BETWEEN_ATTEMPTS = 3;

    private static SessionFactory SESSION_FACTORY;
    private static boolean built = false;

    private static SessionFactory buildSessionFactory() {
        try {
            EnvironmentVariables.loadEnvironmentVariables();

            Properties properties = new Properties();
            properties.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");

            String jdbcURL = String.format("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true",
                    System.getProperty("DB_HOST"),
                    System.getProperty("DB_PORT"),
                    System.getProperty("DB_NAME"));

            properties.put("hibernate.connection.url", jdbcURL);
            properties.put("hibernate.connection.username", System.getProperty("DB_USER"));
            properties.put("hibernate.connection.password", System.getProperty("DB_PASS"));
            properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

            // properties.put("hibernate.hbm2ddl.auto", "create-drop");
            properties.put("hibernate.hbm2ddl.auto", "create-only");
            properties.put("hibernate.show_sql", "true");

            Configuration configuration = new Configuration();
            configuration.setProperties(properties);

            // Entities
            configuration.addAnnotatedClass(ClientSession.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(UserList.class);
            configuration.addAnnotatedClass(Multimedia.class);
            configuration.addAnnotatedClass(MultimediaListItem.class);

            int attempts = 0;
            while(attempts < TOTAL_ATTEMPTS) {
                try {
                    return configuration.buildSessionFactory();
                } catch (Exception e) {
                    attempts++;
                    LOGGER.warn("Waiting... database not ready yet (Attempt {}/10)", attempts);
                    try { Thread.sleep(SECONDS_BETWEEN_ATTEMPTS * 1000); } catch (InterruptedException ignored) {}
                }
            }
            throw new RuntimeException("Could not connect to DB after 10 attempts");
        } catch (HibernateException e) {
            System.err.println("Error inicializando la SessionFactory de Hibernate: " + e);
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        if (!built) {
            SESSION_FACTORY = buildSessionFactory();
            built = true;
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && SESSION_FACTORY.isOpen()) {
            getSessionFactory().close();
        }

    }
}
