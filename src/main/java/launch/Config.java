package launch;

import java.util.logging.Level;

public class Config extends Abstract {

    public final String DATA_POSTGRES_HOST;
    public final String DATA_POSTGRES_NAME;
    public final String DATA_POSTGRES_USER;
    public final String DATA_POSTGRES_PASS;
    public final String WEB_APP;

    public Config() throws Exception {
        this.DATA_POSTGRES_HOST = getEnv("DATA_POSTGRES_HOST", true);
        this.DATA_POSTGRES_NAME = getEnv("DATA_POSTGRES_NAME", true);
        this.DATA_POSTGRES_USER = getEnv("DATA_POSTGRES_USER", true);
        this.DATA_POSTGRES_PASS = getEnv("DATA_POSTGRES_PASS", true);
        this.WEB_APP = getEnv("WEB_APP", true);
    }

    private String getEnv(String name, boolean required) throws Exception {
        String value = System.getenv(name);
        if (required == true) {
            if (value == null) {
                throw new Exception("Missing required environment variable: " + name);
            }
            if (value.length() == 0) {
                this.SYS_LOG.log(Level.WARNING, "Empty required environment variable: " + name);
            }
        }
        return value;
    }
}
