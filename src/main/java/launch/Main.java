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
            System.out.println("---------------------------------ONE");
            Config config = bootstrap.LoadConfig();
            System.out.println("---------------------------------TWO");
            PostgresDAO postgresDAO = bootstrap.LoadPostgresDAO(config);
            System.out.println("---------------------------------THREE");
            bootstrap.AssertDatabaseInitialization(postgresDAO);
            System.out.println("---------------------------------FOUR");
            bootstrap.RunDatabaseMigrations(postgresDAO);
            System.out.println("---------------------------------FIVE");
            bootstrap.StartProductionServer();
            System.out.println("---------------------------------SIX");
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
