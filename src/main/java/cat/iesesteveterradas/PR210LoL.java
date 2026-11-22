package cat.iesesteveterradas;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.iesesteveterradas.utils.UtilsSQLite;


public class PR210LoL {

    private static final Logger logger = LoggerFactory.getLogger(PR210LoL.class);

    public static void main(String[] args) {
        String filePath = BaseDadesLoL.obtenirRuta();

        try {
            if (!new File(filePath).exists()) {
                BaseDadesLoL.inicialitzar(filePath);
            }

            //si ho faig amb try resources es desconecta automaticament i no fa falta fer una funcio de disconnect
            try (Connection conn = UtilsSQLite.connect(filePath);
                 Scanner scanner = new Scanner(System.in)) {

                boolean exit = false;
                while (!exit) {
                    showMenu();
                    System.out.println("Tria una opció:");
                    while (!scanner.hasNextInt()) {
                        scanner.nextLine();
                        System.out.println("Cal escriure un número. Tria una opció:");
                    }
                    int opcio = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcio) {
                        case 1: mostrarTaula(conn, scanner); break;
                        case 2: BaseDadesLoL.mostrarPersonatgesPerFaccio(conn, triarFaccio(conn, scanner)); break;
                        case 3: BaseDadesLoL.mostrarMillorAtacant(conn, triarFaccio(conn, scanner)); break;
                        case 4: BaseDadesLoL.mostrarMillorDefensor(conn, triarFaccio(conn, scanner)); break;
                        case 0: exit = true; break;
                        default : System.out.println("Opció no vàlida.");
                    }
                }
                System.out.println("Sortint del programa....");
            }

        } catch (SQLException e) {
            logger.error("S'ha produit un error en operar amb la base de dades.", e);
        }
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("=== League of Legends ===");
        System.out.println("1. Mostrar una taula");
        System.out.println("2. Mostrar personatges per facció");
        System.out.println("3. Mostrar el millor atacant per facció");
        System.out.println("4. Mostrar el millor defensor per facció");
        System.out.println("0. Sortir");
    }

    private static void mostrarTaula(Connection conn, Scanner scanner) throws SQLException {
        List<String> taules = UtilsSQLite.listTables(conn);
        System.out.println("Taules:");
        for (int i = 0; i < taules.size(); i++) {
            System.out.println("    " + (i + 1) + ". " + taules.get(i));
        }

        System.out.println("Tria una taula:");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.println("Cal escriure un número. Tria una taula:");
        }
        int numero = scanner.nextInt();
        scanner.nextLine();

        if (numero < 1 || numero > taules.size()) {
            System.out.println("Taula no vàlida.");
        } else {
            BaseDadesLoL.mostrarTaula(conn, taules.get(numero - 1));
        }
    }

    private static int triarFaccio(Connection conn, Scanner scanner) throws SQLException {
        BaseDadesLoL.mostrarFaccions(conn);
        System.out.println("Tria una facció:");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.println("Cal escriure un número. Tria una facció:");
        }
        int idFaccio = scanner.nextInt();
        scanner.nextLine();
        return idFaccio;
    }
}
