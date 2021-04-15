package rs.edu.matgim.zadatak;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {

    String connectionString = "jdbc:sqlite:src\\main\\java\\KompanijaZaPrevoz.db";

    public void printFirma() {
        try (Connection conn = DriverManager.getConnection(connectionString); Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT * FROM Firma");
            while (rs.next()) {
                int IdFil = rs.getInt("IdFir");
                String Naziv = rs.getString("Naziv");
                String Adresa = rs.getString("Adresa");
                String Tel1 = rs.getString("Tel1");
                String Tel2 = rs.getString("Tel2");

                System.out.println(String.format("%d\t%s\t%s\t%s\t%s", IdFil, Naziv, Adresa, Tel1, Tel2));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    public void printUkupnoPopravnki() {
        try ( Connection conn = DriverManager.getConnection(connectionString);  Statement s = conn.createStatement()) {

            ResultSet rs = s.executeQuery("SELECT SUM(BrPopravljanja) BrPopravljanja, Marka\n" +
                                          "FROM Kamion\n" +
                                          "GROUP by Marka\n" +
                                          "ORDER BY BrPopravljanja DESC");
            while (rs.next()) {
                String Marka = rs.getString("Marka");
                int BrojPopravljanja = rs.getInt("BrPopravljanja");

                System.out.println(String.format("%s\t%d", Marka, BrojPopravljanja));
            }

        } catch (SQLException ex) {
            System.out.println("Greska prilikom povezivanja na bazu");
            System.out.println(ex);
        }
    }
    int zadatak(int IdPut) throws SQLException{
        Connection conn = DriverManager.getConnection(connectionString);
        List<Integer> mehanicari = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(connectionString);
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Putovanje \n" +
                                                                "UPDATE Putovanje\n" +
                                                                "SET Status = 'P'\n" +
                                                                "WHERE IDPut = ?;");
            statement.setInt(1, IdPut);
            statement.clearParameters();
            statement.addBatch();

            ResultSet rsIDKam = statement.executeQuery("SELECT IDKam FROM Putovanje WHERE IDPut = ?");
            statement.setInt(1, IdPut); 
            statement.clearParameters();
            int IDKam = -1;
            while (rsIDKam.next()) {
                IDKam = rsIDKam.getInt("IDKam");  
            }
            
            ResultSet rs = statement.executeQuery("SELECT Mehanicar.IDZap\n" +
                                                  "FROM Mehanicar\n" +
                                                  "LEFT JOIN Popravlja ON Mehanicar.IDZap=Popravlja.IDZap\n" +
                                                  "WHERE Popravlja.IDZap IS NULL");
            while (rs.next()) {
                int IDZap = rs.getInt("Mehanicar.IDZap");           
                mehanicari.add(IDZap);
                conn.prepareStatement("INSERT INTO Popravlja (IDZap, IDKam, Dana)\n" +
                                      "VALUES (?, ?, 1);");
                statement.setInt(1, IDZap); 
                statement.setInt(2, IDKam);
                statement.clearParameters();
                statement.addBatch();
            }
            
            conn.prepareStatement("DELETE FROM SePrevozi WHERE IDPut = ?");
            statement.setInt(1, IdPut);    
            statement.clearParameters();
                        
            conn.commit();
            return mehanicari.size();          
        }
        catch (SQLException ex){
                  conn.rollback();
                  System.out.println("Dogodila se greska");
                  System.out.println(ex);
                  return -1;
        }
    }
}
