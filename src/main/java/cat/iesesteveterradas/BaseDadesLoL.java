package cat.iesesteveterradas;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.iesesteveterradas.utils.UtilsSQLite;

public final class BaseDadesLoL {

    private static final Logger logger = LoggerFactory.getLogger(BaseDadesLoL.class);

    private BaseDadesLoL() {
    }

    public static String obtenirRuta() {
        return System.getProperty("user.dir") + File.separator + "data" + File.separator + "lol.db";
    }

    public static void inicialitzar(String filePath) throws SQLException {
        new File(filePath).getAbsoluteFile().getParentFile().mkdirs();

        try (Connection conn = UtilsSQLite.connect(filePath)) {
            UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Personatge");
            UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Faccio");
            UtilsSQLite.queryUpdate(conn, "CREATE TABLE Faccio ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " nom VARCHAR(15) NOT NULL,"
                    + " resum VARCHAR(500))");
            UtilsSQLite.queryUpdate(conn, "CREATE TABLE Personatge ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " nom VARCHAR(15) NOT NULL,"
                    + " atac REAL,"
                    + " defensa REAL,"
                    + " idFaccio INTEGER NOT NULL,"
                    + " FOREIGN KEY (idFaccio) REFERENCES Faccio(id))");

            logger.info("Inserint dades inicials...");
            String sqlFaccio = "INSERT INTO Faccio (nom, resum) VALUES (?, ?)";
            UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Demacia", "Regne orgullós amb una gran tradició militar,"
                    + " que posa la justícia, l’honor i el deure per sobre de tot. Desconfia de la màgia i la"
                    + " controla amb duresa dins de les seves fronteres. És el gran rival de Noxus.");
            UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Noxus", "Imperi poderós i expansionista que valora la"
                    + " força i l’ambició. Qualsevol persona hi pot arribar al poder si demostra prou talent,"
                    + " sigui quin sigui el seu origen. Les seves legions han conquerit molts territoris de Runeterra.");
            UtilsSQLite.queryUpdatePS(conn, sqlFaccio, "Ionia", "Arxipèlag on la màgia, la natura i"
                    + " l’espiritualitat conviuen en equilibri. La seva cultura busca l’harmonia en totes les coses,"
                    + " però la invasió de Noxus va obligar molts ionians a agafar les armes per defensar la seva terra.");

            String sqlPersonatge = "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES (?, ?, ?, ?)";
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Galio", 1, 3, 1);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Lux", 2, 2, 1);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Garen", 2, 3, 1);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Darius", 3, 3, 2);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Katarina", 2, 1, 2);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Swain", 1, 2, 2);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Yasuo", 3, 2, 3);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Ahri", 2, 1, 3);
            UtilsSQLite.queryUpdatePS(conn, sqlPersonatge, "Irelia", 3, 2, 3);
            logger.info("Dades inicials inserides correctament.");
        }
    }

    public static void mostrarTaula(Connection conn, String nomTaula) throws SQLException {
        try (ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT * FROM " + nomTaula)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("Taula " + nomTaula + ":");

            StringBuilder columnes = new StringBuilder("   ");
            for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                columnes.append(col == 1 ? " " : ", ").append(rsmd.getColumnLabel(col));
            }
            System.out.println(columnes);

            while (rs.next()) {
                StringBuilder fila = new StringBuilder("   ");
                for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                    fila.append(col == 1 ? " " : ", ").append(rs.getString(col));
                }
                System.out.println(fila);
            }
        }
    }

    public static void mostrarFaccions(Connection conn) throws SQLException {
        System.out.println("Faccions:");
        try (ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT id, nom FROM Faccio ORDER BY id")) {
            while (rs.next()) {
                System.out.println("    " + rs.getInt("id") + ". " + rs.getString("nom"));
            }
        }
    }

    public static void mostrarPersonatgesPerFaccio(Connection conn, int idFaccio) throws SQLException {
        String nomFaccio = obtenirNomFaccio(conn, idFaccio);
        if (nomFaccio == null) {
            System.out.println("No existeix cap facció amb l'id " + idFaccio + ".");
            return;
        }

        System.out.println("Personatges de " + nomFaccio + ":");
        try (ResultSet rs = UtilsSQLite.querySelectPS(conn,
                "SELECT nom, atac, defensa FROM Personatge WHERE idFaccio = ? ORDER BY id", idFaccio)) {
            while (rs.next()) {
                System.out.println("    " + rs.getString("nom") + " (atac: " + rs.getDouble("atac")
                        + ", defensa: " + rs.getDouble("defensa") + ")");
            }
        }
    }


    public static void mostrarMillorAtacant(Connection conn, int idFaccio) throws SQLException {
        String nomFaccio = obtenirNomFaccio(conn, idFaccio);
        if (nomFaccio == null) {
            System.out.println("No existeix cap facció amb l'id " + idFaccio + ".");
            return;
        }

        System.out.println("Millor atacant de " + nomFaccio + ":");
        try (ResultSet rs = UtilsSQLite.querySelectPS(conn,
                "SELECT nom, atac, defensa FROM Personatge WHERE idFaccio = ? ORDER BY atac DESC LIMIT 1",
                idFaccio)) {
            if (rs.next()) {
                System.out.println("    " + rs.getString("nom") + " (atac: " + rs.getDouble("atac")
                        + ", defensa: " + rs.getDouble("defensa") + ")");
            } else {
                System.out.println("    (cap personatge)");
            }
        }
    }


    public static void mostrarMillorDefensor(Connection conn, int idFaccio) throws SQLException {
        String nomFaccio = obtenirNomFaccio(conn, idFaccio);
        if (nomFaccio == null) {
            System.out.println("No existeix cap facció amb l'id " + idFaccio + ".");
            return;
        }

        System.out.println("Millor defensor de " + nomFaccio + ":");
        try (ResultSet rs = UtilsSQLite.querySelectPS(conn,
                "SELECT nom, atac, defensa FROM Personatge WHERE idFaccio = ? ORDER BY defensa DESC LIMIT 1",
                idFaccio)) {
            if (rs.next()) {
                System.out.println("    " + rs.getString("nom") + " (atac: " + rs.getDouble("atac")
                        + ", defensa: " + rs.getDouble("defensa") + ")");
            } else {
                System.out.println("    (cap personatge)");
            }
        }
    }

    private static String obtenirNomFaccio(Connection conn, int idFaccio) throws SQLException {
        try (ResultSet rs = UtilsSQLite.querySelectPS(conn, "SELECT nom FROM Faccio WHERE id = ?", idFaccio)) {
            return rs.next() ? rs.getString("nom") : null;
        }
    }
}
