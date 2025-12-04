package config;

import init.EnvironmentVariables;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateUtil {

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

            properties.put("hibernate.hbm2ddl.auto", "create-only");
            properties.put("hibernate.show_sql", "true");

            Configuration configuration = new Configuration();
            configuration.setProperties(properties);

            // Entities
            configuration.addAnnotatedClass(model.entity.User.class);
            configuration.addAnnotatedClass(model.entity.UserList.class);
            configuration.addAnnotatedClass(model.entity.Multimedia.class);
            configuration.addAnnotatedClass(model.entity.MultimediaListItem.class);

            return configuration.buildSessionFactory();
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
