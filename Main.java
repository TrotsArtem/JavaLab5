import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    static abstract class Ship implements Comparable<Ship> {
        protected String name;
        protected int year;
        protected double displacement; // водотоннажність, т

        Ship(String name, int year, double displacement) {
            this.name = name;
            this.year = year;
            this.displacement = displacement;
        }

        String getName() { return name; }
        int getYear() { return year; }
        double getDisplacement() { return displacement; }

        void Show() { System.out.println(this); }

        abstract String toFileString();

        @Override
        public int compareTo(Ship o) {
            return name.compareToIgnoreCase(o.name);
        }

        @Override
        public String toString() {
            return String.format("%s: назва=%s, рік=%d, водотоннажність=%.1f т",
                    getClass().getSimpleName(), name, year, displacement);
        }
    }

    static class Steamer extends Ship {
        private double enginePower;

        Steamer(String name, int year, double displacement, double enginePower) {
            super(name, year, displacement);
            this.enginePower = enginePower;
        }

        @Override
        public int compareTo(Ship o) {
            if (o instanceof Steamer s) {
                int c = Double.compare(enginePower, s.enginePower);
                if (c != 0) return c;
            }
            return super.compareTo(o);
        }

        @Override
        String toFileString() {
            return String.join(";", name, String.valueOf(year),
                    String.valueOf(displacement), String.valueOf(enginePower));
        }

        @Override
        public String toString() {
            return super.toString() + String.format(", потужність=%.0f к.с.", enginePower);
        }
    }

    static class Sailboat extends Ship {
        private int sails;

        Sailboat(String name, int year, double displacement, int sails) {
            super(name, year, displacement);
            this.sails = sails;
        }

        @Override
        public int compareTo(Ship o) {
            if (o instanceof Sailboat s) {
                int c = Integer.compare(sails, s.sails);
                if (c != 0) return c;
            }
            return super.compareTo(o);
        }

        @Override
        String toFileString() {
            return String.join(";", name, String.valueOf(year),
                    String.valueOf(displacement), String.valueOf(sails));
        }

        @Override
        public String toString() {
            return super.toString() + ", вітрил=" + sails;
        }
    }

    static class Corvette extends Ship {
        private int guns;

        Corvette(String name, int year, double displacement, int guns) {
            super(name, year, displacement);
            this.guns = guns;
        }

        @Override
        public int compareTo(Ship o) {
            if (o instanceof Corvette c) {
                int r = Integer.compare(guns, c.guns);
                if (r != 0) return r;
            }
            return super.compareTo(o);
        }

        @Override
        String toFileString() {
            return String.join(";", name, String.valueOf(year),
                    String.valueOf(displacement), String.valueOf(guns));
        }

        @Override
        public String toString() {
            return super.toString() + ", гармат=" + guns;
        }
    }

    // Comparator: рік > водотоннажність > назва
    static class ShipComparator implements Comparator<Ship> {
        @Override
        public int compare(Ship a, Ship b) {
            int c = Integer.compare(a.getYear(), b.getYear());
            if (c != 0) return c;
            c = Double.compare(a.getDisplacement(), b.getDisplacement());
            if (c != 0) return c;
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }


    static final Scanner sc = new Scanner(System.in);

    static ArrayList<Steamer> readSteamers(String file) throws IOException {
        ArrayList<Steamer> list = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(file))) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            list.add(new Steamer(p[0].trim(), Integer.parseInt(p[1].trim()),
                    Double.parseDouble(p[2].trim()), Double.parseDouble(p[3].trim())));
        }
        return list;
    }

    static ArrayList<Sailboat> readSailboats(String file) throws IOException {
        ArrayList<Sailboat> list = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(file))) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            list.add(new Sailboat(p[0].trim(), Integer.parseInt(p[1].trim()),
                    Double.parseDouble(p[2].trim()), Integer.parseInt(p[3].trim())));
        }
        return list;
    }

    static ArrayList<Corvette> readCorvettes(String file) throws IOException {
        ArrayList<Corvette> list = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(file))) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(";");
            list.add(new Corvette(p[0].trim(), Integer.parseInt(p[1].trim()),
                    Double.parseDouble(p[2].trim()), Integer.parseInt(p[3].trim())));
        }
        return list;
    }

    static void print(String title, List<? extends Ship> list) {
        System.out.println("\n=== " + title + " ===");
        if (list.isEmpty()) System.out.println("(порожньо)");
        for (Ship s : list) s.Show();
    }

    static String[] readTokens(String prompt) {
        while (true) {
            System.out.print(prompt);
            String[] t = sc.nextLine().trim().split("\\s+");
            if (t.length == 4) {
                try {
                    Integer.parseInt(t[1]);
                    Double.parseDouble(t[2]);
                    Double.parseDouble(t[3]);
                    return t;
                } catch (NumberFormatException ignored) { }
            }
            System.out.println("Потрібно 4 значення через пробіл (назва, рік, водотоннажність, поле); числа з крапкою.");
        }
    }


    static void task1() throws IOException {
        ArrayList<Steamer> steamers = readSteamers("steamers.txt");
        ArrayList<Sailboat> sailboats = readSailboats("sailboats.txt");
        ArrayList<Corvette> corvettes = readCorvettes("corvettes.txt");

        System.out.println("\nКРОК 1: дані, прочитані з файлів");
        print("Пароплави", steamers);
        print("Вітрильники", sailboats);
        print("Корвети", corvettes);

        Collections.sort(steamers);
        Collections.sort(sailboats);
        Collections.sort(corvettes);
        System.out.println("\nКРОК 2: після сортування (Comparable)");
        print("Пароплави", steamers);
        print("Вітрильники", sailboats);
        print("Корвети", corvettes);

        System.out.println("\nКРОК 3: введення з клавіатури");
        String[] t = readTokens("Пароплав (назва рік водотоннажність потужність): ");
        steamers.add(new Steamer(t[0], Integer.parseInt(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3])));
        t = readTokens("Вітрильник (назва рік водотоннажність вітрила): ");
        sailboats.add(new Sailboat(t[0], Integer.parseInt(t[1]), Double.parseDouble(t[2]), Integer.parseInt(t[3])));
        t = readTokens("Корвет (назва рік водотоннажність гармати): ");
        corvettes.add(new Corvette(t[0], Integer.parseInt(t[1]), Double.parseDouble(t[2]), Integer.parseInt(t[3])));

        Collections.sort(steamers);
        Collections.sort(sailboats);
        Collections.sort(corvettes);
        System.out.println("\nКРОК 4: після додавання і повторного сортування");
        print("Пароплави", steamers);
        print("Вітрильники", sailboats);
        print("Корвети", corvettes);

        ArrayList<Ship> all = new ArrayList<>();
        all.addAll(steamers);
        all.addAll(sailboats);
        all.addAll(corvettes);
        Collections.sort(all, new ShipComparator());
        System.out.println("\nКРОК 5: єдиний список (Comparator: рік, водотоннажність, назва)");
        print("Усі кораблі", all);

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(
                Paths.get("all_sorted.txt")))) {
            for (Ship s : all) {
                pw.println(s.getClass().getSimpleName() + ";" + s.toFileString());
            }
        }
        System.out.println("\nВідсортований список записано у all_sorted.txt");
    }


    static void task2() throws IOException {
        LinkedList<String> lines = new LinkedList<>(
                Files.readAllLines(Paths.get("input.txt")));
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(
                Paths.get("output.txt")))) {
            Iterator<String> it = lines.descendingIterator();
            while (it.hasNext()) pw.println(it.next());
        }
        System.out.println("Рядків оброблено: " + lines.size() + ". Результат у output.txt (зворотний порядок).");
    }


    public static void main(String[] args) {
        while (true) {
            System.out.println("\n МЕНЮ");
            System.out.println("1. Завдання");
            System.out.println("2. Завдання");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        task1();
                        break;
                    case "2":
                        task2();
                        break;
                    case "0": {
                        System.out.println("До побачення!");
                        return;
                    }
                    default: System.out.println("Невідомий пункт меню.");
                }
            } catch (IOException e) {
                System.out.println("Помилка файлу: " + e.getMessage());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Некоректні дані у файлі: " + e.getMessage());
            }
        }
    }
}