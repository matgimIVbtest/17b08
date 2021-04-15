package rs.edu.matgim.zadatak;

import java.sql.SQLException;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        DB _db = new DB();
        //_db.printFirma();
        _db.printUkupnoPopravnki();
        Scanner sc= new Scanner (System.in);
        int i=sc.nextInt();
        int k;
        try {
            k=_db.zadatak(i);
            System.out.println("Uspešna realizacija");
        } catch (SQLException e) {
            System.out.println("Dogodila se greska");
        }
    }

}
