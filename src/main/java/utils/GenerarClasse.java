package utils;

import entitats.Aeronau;
import entitats.Combat;
import entitats.Dron;
import entitats.Mecanic;
import entitats.Missio;
import entitats.Pilotada;
import entitats.Soldat;
import entitats.Transport;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import main.ClassFactory;
import main.SingleSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Classe auxiliar perquè l'usuari pugui definir qualsevol de les 6 Classes
 * disponibles al joc: Missió, Dron, Combat, Transport, Pilot i Mecànic.
 *
 * @author Txell Llanas: Creació / Implementació
 */
public class GenerarClasse {

    private static Scanner in = new Scanner(System.in);

    private static final Logger logger = LogManager.getLogger(GenerarClasse.class); // Crear Logs per aquesta classe
    private static Session session;
    private static ClassFactory factory = new ClassFactory();

    // Còmputs i Xivatos
    private static int numMissions, aeronausPerMissio, aeronausTotals, numPilots, numMecanics, tipusAeronau, tipusSoldat = 0;
    private static boolean has_aeronaus, has_nausTransport, has_nausCombat, has_drons, has_pilots, has_mecanics = false;

    // Textes detalls objectes creats
    private static String resum_missions, resum_pilots, resum_mecanics, resum_aeronausTransport, resum_aeronausCombat, resum_aeronausDron = "";

    // Instàncies
    private static List<Missio> llistaMissions = null;
    private static List<Aeronau> llistaPilotades = null;
    private static List<Aeronau> llistaAutonomes = null;
    private static List<Mecanic> llistaMecanics = null;

    /**
     * Mètode que personalitza el titular de l'apartat actual segons l'objecte a crear.
     * 
     * @param opcio Valor enter que determina quin objecte crear
     * @author Txell Llanas: Creació / Implementació
     */
    public static void iniciarGeneracions(int opcio) {

        try {

            // Obrir Sessió per iniciar transacció
            session = SingleSession.getInstance().getSessio();

            logger.info("\n" + "------------------------------------------------------------------------" + "\n");

            switch (opcio) {
                case 1:
                    logger.info("GENERADOR D'INSTÀNCIES: << MISSIONS >>\n");
                    crearMissio();
                    break;
                case 2:
                    logger.info("GENERADOR D'INSTÀNCIES: << AERONAU DE TRANSPORT >>\n");
                    tipusAeronau = 2;
                    crearAeronau();
                    break;
                case 3:
                    logger.info("GENERADOR D'INSTÀNCIES: << AERONAU DE COMBAT >>\n");
                    tipusAeronau = 3;
                    crearAeronau();
                    break;
                case 4:
                    logger.info("GENERADOR D'INSTÀNCIES: << AERONAU: DRON >>\n");
                    tipusAeronau = 4;
                    crearAeronau();
                    break;
                case 5:
                    logger.info("GENERADOR D'INSTÀNCIES: << PILOTS >>\n");
                    tipusSoldat = 1;
                    crearSoldat();
                    break;
                case 6:
                    logger.info("GENERADOR D'INSTÀNCIES: << MECÀNICS >>\n");
                    tipusSoldat = 2;
                    crearSoldat();
                    break;
            }

            // Confirmar que tots els objectes s'han generat correctament
            logger.info("\nObjectes generats correctament! :D");

            // Resum instàncies generades
            logger.info("\n" + "------------------------------------------------------------------------" + "\n\n"
                    + "RESUM D'OBJECTES GENERATS: \n\n"
                    + resum_missions);
            if ( has_nausTransport ) {
                logger.info(resum_aeronausTransport);
            }
            if ( has_nausCombat ) {
                logger.info(resum_aeronausCombat);
            }
            if ( has_drons ) {
                logger.info(resum_aeronausDron);
            }
            if ( has_pilots ) {
                logger.info(resum_pilots);
            }
            if ( has_mecanics ) {
                logger.info(resum_mecanics);
            }

            // Resetejar variables
            llistaMissions.clear();
            llistaPilotades.clear();
            llistaAutonomes.clear();
                    
            numMissions = 0;
            aeronausPerMissio = 0;
            aeronausTotals = 0;
            numPilots = 0;
            numMecanics = 0;
            tipusAeronau = 0;
            
            has_aeronaus = false;
            has_nausTransport = false;
            has_nausCombat = false;
            has_drons = false;
            has_pilots = false;
            has_mecanics = false;
            
            resum_aeronausTransport = "";
            resum_aeronausCombat = "";
            resum_aeronausDron = "";
            resum_pilots = "";
            resum_mecanics = "";

        } catch (HibernateException ex) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            logger.error("Excepció d'hibernate: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage());

        } finally {

            //Finalitzem Hibernate
            session.close();
        }

    }

    /**
     * Mètode per generar una o vàries instàncies de tipus Missió. Genera de
     * forma automàtica i en quantitats majors de zero a escollir per l’usuari,
     * les entitats associades a aquesta.
     *
     * @author Txell Llanas: Creació/ Implementació
     */
    public static void crearMissio() {

        try {

            // DEMANAR Nº MISSIÓ(NS) A PARTICIPAR (Nómés per PILOTS)
            if ( numPilots > 0 ) {
                logger.info(">> A quantes Missions participa un Pilot? [mín.1 - màx. 2]");
                numMissions = utils.ValidadorOpcioMenu.validador(in);
            }
            
            // DEMANAR Nº MISSIÓ(NS)
            if ( numMissions == 0 ) {
                logger.info(">> Quantes Missions vols crear? [mín.1]");
                numMissions = utils.ValidadorOpcioMenu.validador(in);
            }

            session.beginTransaction();

            // Inicialitzar + Generar llista de Missions
            llistaMissions = new ArrayList<>();
            llistaMissions = factory.missionsFactory(numMissions);

            // Persistir dades generades
            llistaMissions.stream().forEach(x -> session.persist(x));

            // Desar a BBDD
            session.getTransaction().commit();

            // Desar detalls Missions generades
            if (llistaMissions.size() > 1) {                                    // Vàries Missions

                resum_missions = numMissions + " Missions creades: " + "\n";

                for (int i = 0; i < llistaMissions.size(); i++) {
                    if (i < llistaMissions.size() - 1) {
                        resum_missions += "- " + llistaMissions.get(i).getAtributString()
                                + " (id:" + llistaMissions.get(i).getCosmicMissionCode() + ")" + ", " + "\n";
                    } else {
                        resum_missions += "- " + llistaMissions.get(i).getAtributString()
                                + " (id:" + llistaMissions.get(i).getCosmicMissionCode() + "). \n";
                    }
                }

            } else {                                                            // 1 sola Missió
                resum_missions = "- Nom Missió: "
                        + llistaMissions.get(0).getAtributString()
                        + " (id:" + llistaMissions.get(0).getCosmicMissionCode() + ").";
            }

            // GENERAR + ASSIGNAR AERONAU(S)
            if (aeronausPerMissio == 0) {                                       // Generar un tipus d'aeronau de forma random

                if (tipusAeronau == 0) {
                    // Tipus: 2 = Transport, 3 = Combat, 4 = Dron,
                    int max = 4;
                    int min = 2;
                    tipusAeronau = (int) (Math.floor(Math.random() * (max - min + 1)) + min);
                }

                crearAeronau();
            }

        } catch (HibernateException ex) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            logger.error("Excepció d'hibernate: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage());

        }

    }

    /**
     * Mètode per generar una o vàries instàncies dels 3 tipus d'aeronaus: 
     * Transport, Combat o Dron. 
     * Genera de forma automàtica i en quantitats majors de zero a escollir per 
     * l’usuari, les entitats associades a aquesta.
     *
     * @author Txell Llanas: Creació/Implementació
     * @author Izan Jimenez: Implementació
     *
     */
    public static void crearAeronau() {

        // DEMANAR Nº D'AERONAU(S)
        if (aeronausPerMissio == 0 && numPilots == 0) {
            logger.info(">> Quantes Aeronaus vols assignar a una Missió? [Mín. 1 - Màx. 8]");
            aeronausPerMissio = utils.ValidadorOpcioMenu.numAeronausMissio(in);
        }

        if (numMissions == 0) {
            crearMissio();
        }

        if ( !has_aeronaus ) {

            try {
                if (aeronausTotals == 0) {
                    // Calcular les naus necessàries a generar (respectant cardinalitat de 2 Missions màx.)
                    if ((numMissions > 1) && (aeronausPerMissio >= 2)) {
                        aeronausTotals = aeronausPerMissio + aeronausPerMissio * (int) Math.floor((numMissions - 1) / 2);
                    } else {
                        aeronausTotals = aeronausPerMissio * numMissions;
                    }
                } else if (numPilots > 0) {
                    logger.info("\n( Generades "+ aeronausTotals +" aeronaus, 1 per pilot )");
                    aeronausPerMissio = aeronausTotals;
                }

                // Iniciar Transacció
                session.beginTransaction();

                // Inicialitzar llistat d'Aeronaus
                llistaPilotades = new ArrayList<>();
                llistaAutonomes = new ArrayList<>();

                // Generar Aeronaus Totals
                for (int i = 0; i < aeronausTotals; i++) {

                    switch (tipusAeronau) {
                        case 2:
                            llistaPilotades.add(factory.aeronauFactory(Transport.class));
                            has_pilots = true;
                            break;
                        case 3:
                            llistaPilotades.add(factory.aeronauFactory(Combat.class));
                            has_pilots = true;
                            break;
                        case 4:
                            llistaAutonomes.add(factory.aeronauFactory(Dron.class));
                            break;
                    }
                }

                // Persistir dades generades
                if (!llistaPilotades.isEmpty()) {

                    llistaPilotades.stream().forEach(x -> session.persist(x));

                } else {

                    llistaAutonomes.stream().forEach(x -> session.persist(x));

                }

                // Desar a BBDD
                session.getTransaction().commit();

                // ASSIGNAR: /AERONAUS <--> MISSIONS/ + /MECÀNIC(S) <--> AERONAUS/
                assignacions();

                // Desar detalls Aeronaus generades
                switch (tipusAeronau) {
                    case 2:
                        resum_aeronausTransport = "- Generades " + (aeronausTotals)
                                + " aeronaus de Transport per cobrir " + numMissions
                                + " Missió(ns), [1 aeronau pot participar a 2 Missions com a màxim]";
                        resum_pilots = "- Generats " + aeronausTotals + " pilot(s), 1 pilot per aeronau Pilotada.";
                        resum_mecanics = "- Generats " + numMecanics * aeronausTotals + " mecànic(s), " + numMecanics + " mecànic(s) per aeronau Pilotada.";
                        has_aeronaus = true;
                        has_nausTransport = true;
                        break;
                    case 3:
                        resum_aeronausCombat = "- Generades " + (aeronausTotals)
                                + " aeronaus de Combat per cobrir " + numMissions
                                + " Missió(ns), [1 aeronau pot participar a 2 Missions com a màxim]";
                        resum_pilots = "- Generats " + aeronausTotals + " pilot(s), 1 pilot per aeronau Pilotada.";
                        resum_mecanics = "- Generats " + numMecanics * aeronausTotals + " mecànic(s), " + numMecanics + " mecànic(s) per aeronau Pilotada.";
                        has_aeronaus = true;
                        has_nausCombat = true;
                        break;
                    case 4:
                        resum_aeronausDron = "- Generats " + (aeronausTotals)
                                + " Drons per cobrir " + numMissions
                                + " Missió(ns), [1 aeronau pot participar a 2 Missions com a màxim]";
                        resum_mecanics = "- Generats " + numMecanics * aeronausTotals + " mecànic(s), " + numMecanics + " mecànic(s) per aeronau Pilotada.";
                        has_aeronaus = true;
                        has_drons = true;
                        break;
                }

            } catch (HibernateException ex) {

                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                logger.error("Excepció d'hibernate: " + ex.getMessage());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    /**
     * Mètode per assignar una o vàries instàncies de tipus Aeronau i Mecànic a
     * una Missió.
     *
     * @author Txell Llanas: Creació/ Implementació
     * @author Izan Jimenez: Implementació
     * @author Pablo Morante: Implementació
     * @author Víctor García: Implementació
     */
    public static void assignacions() {

        int i = 0, indexAutonoma = 0, indexPilotada = 0;

        if (!has_mecanics && tipusAeronau != 4) {
            logger.info(">> Quants Mecànics vols assignar per Aeronau? [Mín. 1 - Màx. 2]");
            numMecanics = utils.ValidadorOpcioMenu.numAeronausMissio(in);
            has_mecanics = true;
        }

        while (i < llistaMissions.size()) {

            session.beginTransaction();

            llistaMissions.stream().forEach(x -> session.persist(x));

            List<Aeronau> tempNau = new ArrayList<>();

            // Seleccionar aeronaus x cada Missió
            for (int j = 0; j < aeronausPerMissio; j++) {

                if (!llistaPilotades.isEmpty() && indexPilotada < llistaPilotades.size()) {  // Evitar desbordament de l'índex  // Afegir si hi ha Pilotades (Transport/Combat)...                    
                    tempNau.add(llistaPilotades.get(indexPilotada));
                    indexPilotada++;                                            // Evitar assignar índexs repetits: avanço al següent del total d'elements de la llista                         
                }
                if (!llistaAutonomes.isEmpty() && indexAutonoma < llistaAutonomes.size()) {  // Evitar desbordament de l'índex  // Afegir si hi ha Drons...
                    tempNau.add(llistaAutonomes.get(indexAutonoma));
                    indexAutonoma++;                                            // Evitar assignar índexs repetits: avanço al següent del total d'elements de la llista                            
                }
            }

            // Assignar Aeronaus a una Missió (màx.8)          
            try {
                
                if(aeronausPerMissio > 1) {                                     // Si vàries Missions...
                    
                    if (i % 2 == 0) {                                           // si l'índex és PARELL
                        factory.addAeronausToMissio(tempNau, llistaMissions.get(i));
                        if (i + 1 < llistaMissions.size()) {
                            factory.addAeronausToMissio(tempNau, llistaMissions.get(i + 1));
                        }
                    } else {                                                    // si l'índex és SENAR
                        factory.addAeronausToMissio(tempNau, llistaMissions.get(i - 1));
                        factory.addAeronausToMissio(tempNau, llistaMissions.get(i));
                    }
                } else {                                                        // Si una Missió...
                    factory.addAeronausToMissio(tempNau, llistaMissions.get(i));
                    i--;
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            // Assignar Mecanics a Aeronau
            llistaMecanics = new ArrayList<>();
            List<Soldat> soldat = new ArrayList<>();
            
            for (Aeronau aeronau : tempNau) {
                
                if( tipusAeronau != 4 ) {
                    soldat = factory.mecanicsToAeronauFactory(numMecanics, (Pilotada) aeronau);
                }
                
                for (Soldat mecanic : soldat) {
                    llistaMecanics.add((Mecanic) mecanic);
                }
            }

            // Persistir dades generades
            llistaMecanics.stream().forEach(x -> session.persist(x));
            
            session.getTransaction().commit();
            i += 2;
        }
    }

    /**
     * Mètode per generar una o vàries instàncies de tipus Soldat (Pilot, Mecànic).
     * Assigna de forma random el valor pel tipus d'aeronau Pilotada (Transport, Combat)
     * i redirigeix al mètode per crear l'aeronau.
     * 
     * @author Txell Llanas: Creació/ Implementació
     */
    public static void crearSoldat() {

        if ( numPilots == 0 && numMecanics == 0 ) {
            
            if ( tipusSoldat == 1 ) {
                logger.info(">> Quants Pilots vols crear?");
                numPilots = utils.ValidadorOpcioMenu.validador(in);
            } else {
                logger.info(">> Quants Mecànics vols assignar per Aeronau? [Mín. 1 - Màx. 2]");
                numMecanics = utils.ValidadorOpcioMenu.numMecanicAeronau(in);
                has_mecanics = true;
            }  
            
            aeronausTotals = numPilots;
            tipusAeronau = (int) (Math.floor(Math.random() * (3 - 2 + 1)) + 2);
            crearAeronau();            
        }
    }

}
