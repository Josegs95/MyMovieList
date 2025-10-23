package init;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentVariables {

    private static boolean loadDone = false;

    private EnvironmentVariables() {

    }

    public static void loadEnvironmentVariables() {
        if (!loadDone) {
            Dotenv dotenv = Dotenv.configure()
                    .filename(".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );

            loadDone = true;
        }
    }
}
