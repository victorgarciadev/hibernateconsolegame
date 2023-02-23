package main;

import com.github.javafaker.Faker;
import entitats.Combat;
import entitats.Mecanic;
import entitats.Pilot;
import entitats.Pilotada;
import java.sql.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import main.SingleSession;

/**
 * JavaFX App
 */
public class App {

    private static SessionFactory factory;
    private static Session session;

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        SingleSession singleton = SingleSession.getInstance();

        menu();

        try {

            factory = new Configuration().configure("hibernate/hibernate.cfg.xml").buildSessionFactory();

            logger.trace("Iniciem sessió...");
            session = factory.openSession();

            logger.trace("Iniciem transaccio...");
            session.getTransaction().begin();

            Faker faker = new Faker();

            // Test relacions Pilot-Pilotada (nau de combat)
            logger.trace("Creem objectes");
            Combat nauCombat1 = new Combat();
            nauCombat1.setNuclearPower(true);
            nauCombat1.setTotalAmmunition(12000);
            nauCombat1.setHasEjectoSeat(true);
            nauCombat1.setShellCapacity(12.7f);
            //nauCombat1.setFabricationNumber(123456);
            nauCombat1.setCorporation("Funny Bunny");
            nauCombat1.setEngineTorque(12.67f);
//            nauCombat1.setAutodestructionDate(date);
            nauCombat1.setHasDeathLaser(Boolean.TRUE);

            Pilot pilot1 = new Pilot();
            pilot1.setNickname("chimichurri");
            pilot1.setHealingSpeed(5.0f);
            //pilot1.setLastDrugTestDate(date);
            pilot1.setIsOtaku(false);
            pilot1.setMaxGForce(7.0f);

            // FK
            pilot1.setPilotada(nauCombat1);
            nauCombat1.setPilotAeronau(pilot1);
            System.out.println("Pilot: " + pilot1.toString());

            logger.trace("Persistim l'estat dels objectes");

            logger.info("Persistint Nau de Combat...");
            session.persist(nauCombat1);

            logger.info("Persistint Pilot...");
            session.persist(pilot1);

            Combat c1 = new Combat(true, 0, true, 0, "C1", 0, new Date(2000, 2, 25), Boolean.FALSE, pilot1);
            session.persist(c1);
            Mecanic m1 = new Mecanic("avions", 10f, c1, "sonic", 0, null, true);
            Mecanic m2 = new Mecanic("alas", 10f, c1, "mario", 0, null, true);
            Mecanic m3 = new Mecanic("aceite", 10f, c1, "liugi", 0, null, true);
            Mecanic m4 = new Mecanic("aa", 10f, c1, "aa", 0, null, true);

            session.persist(m1);
            session.persist(m2);
            session.persist(m3);
            session.persist(m4);

            for (Mecanic mecanic : c1.getMecanics()) {
                System.out.println(mecanic.getPilotada());
            }

            //session.find(Combat.class, c1);
            // fi test
//            for (int i = 0; i < 1000; i++) {
//
//                session.save(new Dron(faker.code().ean8(), faker.harryPotter().character()));
//            }
            logger.info("Finalitzem transacció i desem a BBDD...");
            session.getTransaction().commit();

//            
        } catch (ConstraintViolationException ex) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }

        } catch (HibernateException ex) {

            logger.info("HibernateException..." + ex.getMessage());
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }

        } finally {

            //Tanquem la sessió
            session.close();

            //Finalitzem Hibernate
            factory.close();
        }

    }

    public static void menu() {

        Scanner in = new Scanner(System.in);
        int opcio = 0;

        do {
            logger.info("\n" + "------------------------------------------------------------------------" + "\n"
                    + " __  __ ______ _   _ _    _ \n"
                    + "|  \\/  |  ____| \\ | | |  | |\n"
                    + "| \\  / | |__  |  \\| | |  | |\n"
                    + "| |\\/| |  __| | . ` | |  | |\n"
                    + "| |  | | |____| |\\  | |__| |\n"
                    + "|_|  |_|______|_| \\_|\\____/ \n"
                    + "                             " + "\n"
                    + "1. Generar classe" + "\n"
                    + "2. Llistar classe" + "\n"
                    + "3. Eliminar classe" + "\n"
                    + "4. Sortir" + "\n\n"
                    + ">> Escull una de les opcions anteriors:");

            opcio = (utils.ValidadorOpcioMenu.validador(in));

            logger.info("------------------------------------------------------------------------" + "\n");

            switch (opcio) {
                case 1:
                    menuGenerarClasse(in);
                    break;
                case 2:
                    menuLlistarClasse(in);
                    break;
                case 3:
                    menuEliminarClasse(in);
                    break;
                case 4:
                    logger.info("Gràcies per utilitzar el nostre programa. Fins aviat!" + "\n");
                    System.exit(0);
                default:
                    logger.info("Número introduït no vàlid!!" + "\n"
                            + "Introdueix un dels números del menú");
            }

        } while (opcio != 4);
    }

    public static void menuGenerarClasse(Scanner in) {

        int opcioMenuGenerarClasse = 0;

        do {
            logger.info("\n" + "------------------------------------------------------------------------" + "\n"
                    + "\n" + "GENERAR CLASSE" + "\n\n" 
                    + "Quina classe vols generar?");
            utils.LlistatMenuClasses.retornaClasses();

            opcioMenuGenerarClasse = utils.ValidadorOpcioMenu.validador(in);

            logger.info("------------------------------------------------------------------------" + "\n");

            switch (opcioMenuGenerarClasse) {
                case 1:
                    logger.info("Classe combat generada!");
                    break;
                case 2:
                    logger.info("Classe dron generada!");
                    break;
                case 3:
                    logger.info("Classe mecànic generada!");
                    break;
                case 4:
                    logger.info("Classe missió generada!");
                    break;
                case 5:
                    logger.info("Classe pilot generada!");
                    break;
                case 6:
                    logger.info("Classe transport generada!");
                    break;
                case 7:
                    break;
                default:
                    logger.info("Número introduït no vàlid!!" + "\n"
                            + "Introdueix un dels números del menú");
            }

        } while (opcioMenuGenerarClasse != 7);
    }

    public static void menuLlistarClasse(Scanner in) {
        int opcioMenuLlistarClasse = 0;

        do {
            logger.info("\n" + "------------------------------------------------------------------------" + "\n"
                    + "\n" + "LLISTAR CLASSE" + "\n\n" 
                    + "Quina classe vols llistar?");
            utils.LlistatMenuClasses.retornaClasses();

            opcioMenuLlistarClasse = utils.ValidadorOpcioMenu.validador(in);

            logger.info("------------------------------------------------------------------------" + "\n");

            switch (opcioMenuLlistarClasse) {
                case 1:
                    logger.info("Classe combat llistada!");
                    break;
                case 2:
                    logger.info("Classe dron llistada!");
                    break;
                case 3:
                    logger.info("Classe mecànic llistada!");
                    break;
                case 4:
                    logger.info("Classe missió llistada!");
                    break;
                case 5:
                    logger.info("Classe pilot llistada!");
                    break;
                case 6:
                    logger.info("Classe transport llistada!");
                    break;
                case 7:
                    break;
                default:
                    logger.info("Número introduït no vàlid!!" + "\n"
                            + "Introdueix un dels números del menú");
            }

        } while (opcioMenuLlistarClasse != 7);
    }

    public static void menuEliminarClasse(Scanner in) {
        int opcioMenuEliminarClasse = 0;

        do {
            logger.info("\n" + "------------------------------------------------------------------------" + "\n"
                    + "\n" + "ELIMINAR CLASSE" + "\n\n" 
                    + "Quina classe vols eliminar?");
            utils.LlistatMenuClasses.retornaClasses();

            opcioMenuEliminarClasse = utils.ValidadorOpcioMenu.validador(in);

            logger.info("------------------------------------------------------------------------" + "\n");

            switch (opcioMenuEliminarClasse) {
                case 1:
                    logger.info("Classe combat eliminada!");
                    break;
                case 2:
                    logger.info("Classe dron eliminada!");
                    break;
                case 3:
                    logger.info("Classe mecànic eliminada!");
                    break;
                case 4:
                    logger.info("Classe missió eliminada!");
                    break;
                case 5:
                    logger.info("Classe pilot eliminada!");
                    break;
                case 6:
                    logger.info("Classe transport eliminada!");
                    break;
                case 7:
                    break;
                default:
                    logger.info("Número introduït no vàlid!!" + "\n"
                            + "Introdueix un dels números del menú");
            }

        } while (opcioMenuEliminarClasse != 7);
    }
}
