package launch;

import data.postgresql.PostgresDAO;
import launch.exceptions.AssertDatabaseInitializationException;
import launch.exceptions.LoadConfigException;
import launch.exceptions.LoadPostgresDAOException;
import launch.exceptions.RunDatabaseMigrationsException;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("main");
        Bootstrap bootstrap = new Bootstrap();
        try {
            Config config = bootstrap.LoadConfig();
            PostgresDAO postgresDAO = bootstrap.LoadPostgresDAO(config);
            bootstrap.AssertDatabaseInitialization(postgresDAO);
            bootstrap.RunDatabaseMigrations(postgresDAO);
            bootstrap.StartProductionServer();
        } catch (LoadConfigException ex) {
            logger.log(Level.SEVERE, " - - - - - - - - - - - LoadConfig Exception - - - - - - - - - -");
            logger.log(Level.INFO, ex.getMessage());
            bootstrap.StartErrorServer();
        } catch (LoadPostgresDAOException ex) {
            logger.log(Level.SEVERE, " - - - - - - - - - - - LoadPostgresDAO Exception - - - - - - - - - -");
            logger.log(Level.INFO, ex.getMessage());
            bootstrap.StartErrorServer();
        } catch (AssertDatabaseInitializationException ex) {
            logger.log(Level.SEVERE, " - - - - - - - - - - - AssertDatabaseInitialization Exception - - - - - - - - - -");
            logger.log(Level.INFO, ex.getMessage());
            bootstrap.StartErrorServer();
        } catch (RunDatabaseMigrationsException ex) {
            logger.log(Level.SEVERE, " - - - - - - - - - - - RunDatabaseMigrations Exception - - - - - - - - - -");
            logger.log(Level.INFO, ex.getMessage());
            bootstrap.StartErrorServer();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, " - - - - - - - - - - - Unknown Exception - - - - - - - - - -");
            logger.log(Level.INFO, ex.getMessage());
            bootstrap.StartErrorServer();
        }
    }
}
