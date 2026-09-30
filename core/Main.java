//Tomasz Podlaszewski

package core;

import java.util.Scanner;
import kadra.mapki.pliki.WyjatekSystemuPlikow;

public class Main {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Nie podano sciezki do katalogu na mapki!");
            System.err.println("Uzycie: java -ea core.Main \"sciezka/do/katalogu\" < dane.txt");
            return;
        }

        String mapDirectory = args[0];

        try (Scanner scanner = new Scanner(System.in)) {
            Simulation simulation = DataLoader.load(scanner);
            simulation.run(mapDirectory);

        } catch (WyjatekSystemuPlikow e) {
            System.err.println("\n[!] BLAD SYSTEMU PLIKOW.");
            System.err.println("Sugerowane rozwiazanie: Upewnij sie, ze podana sciezka (" + mapDirectory +
                    ") jest poprawna, istnieje oraz masz do niej odpowiednie uprawnienia zapisu.");
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("\n[!] WYSTAPIL NIEOCZEKIWANY BLAD KRYTYCZNY.");
            System.err.println("Sugerowane rozwiazanie: Prosimy o zgloszenie tego bledu zespolowi deweloperskiemu.");
            e.printStackTrace();
        }
    }
}
