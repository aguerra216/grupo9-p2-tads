import csvProcessor.*;
import entities.Genero;
import entities.Pelicula;
import tads.LinkedList.MyLinkedListImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("Seleccione la opción que desee:");
            System.out.println("1. Carga de datos");
            System.out.println("2. Ejecutar consultas");
            System.out.println("3. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    long inicio = System.currentTimeMillis();
                    try (
                            BufferedReader br = new BufferedReader(new FileReader("resources/movies_metadata.csv"))) {
                        String line;
                        br.readLine(); // Saltarse el header
                        int count = 0;

                        while ((line = br.readLine()) != null) {
                            // Acumular líneas hasta que haya al menos 14 columnas
                            while (true) {
                                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                                if (parts.length >= 14) {
                                    // Parsear datos
                                    try {
                                        int id = Integer.parseInt(parts[5].trim());
                                        String title = parts[8];
                                        String language = parts[7];
                                        String collectionGenres = parts[3];
                                        MyLinkedListImpl<Genero> listaGeneros = parseGeneros(collectionGenres);
                                        double revenue = 0.0;
                                        try {
                                            revenue = Double.parseDouble(parts[13]);
                                        } catch (Exception e) {
                                            revenue = 0.0;
                                        }
                                        String collectionData = parts[1];
                                        Pelicula objPelicula = new Pelicula();
                                        objPelicula.setIdPelicula(id);
                                        objPelicula.setTitulo(title);
                                        objPelicula.setIdiomaOriginal(language);
                                        objPelicula.setListaGeneros(listaGeneros);
                                        objPelicula.setRevenue(revenue);
                                        System.out.println(objPelicula.getIdPelicula());
                                        count++;
                                    } catch (Exception e) {
                                        System.out.println("Error en línea: " + e.getMessage());
                                    }
                                    break;
                                } else {
                                    String nextLine = br.readLine();
                                    if (nextLine == null) break; // fin del archivo
                                    line += "\n" + nextLine;
                                }
                            }
                        }

                        System.out.println("Películas cargadas: " + count);

                    } catch (IOException e) {
                        System.out.println("Error loading movies_metadata.csv: " + e.getMessage());
                    }
                    long fin = System.currentTimeMillis();
                    System.out.println("Carga de datos exitosa, tiempo de ejecución de la carga: " + (fin - inicio) + " ms\n");
                    break;

                case 2:
                    ejecutarConsultas(scanner);
                    break;

                case 3:
                    salir = true;
                    System.out.println("Saliendo del sistema.");
                    break;

                default:
                    System.out.println("Opción inválida.\n");
            }
        }

        scanner.close();
    }

    public static void ejecutarConsultas(Scanner scanner) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\nSeleccione una consulta:");
            System.out.println("1. Top 5 de las películas que más calificaciones por idioma.");
            System.out.println("2. Top 10 de las películas que mejor calificación media tienen por parte de los usuarios.");
            System.out.println("3. Top 5 de las colecciones que más ingresos generaron.");
            System.out.println("4. Top 10 de los directores que mejor calificación tienen.");
            System.out.println("5. Actor con más calificaciones recibidas en cada mes del año.");
            System.out.println("6. Usuarios con más calificaciones por género.");
            System.out.println("7. Salir");

            int opcionConsulta = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (opcionConsulta) {
                case 1:
                    // TODO: Implementar lógica real
                    System.out.println("Mostrando Top 5 de las películas que más calificaciones por idioma...");
                    break;
                case 2:
                    System.out.println("Mostrando Top 10 de películas con mejor calificación media...");
                    break;
                case 3:
                    System.out.println("Mostrando Top 5 de colecciones con más ingresos...");
                    break;
                case 4:
                    System.out.println("Mostrando Top 10 de directores con mejor calificación...");
                    break;
                case 5:
                    System.out.println("Mostrando actor con más calificaciones por mes...");
                    break;
                case 6:
                    System.out.println("Mostrando usuarios con más calificaciones por género...");
                    break;
                case 7:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    public static MyLinkedListImpl<Genero> parseGeneros(String generosRaw) {
        MyLinkedListImpl<Genero> lista = new MyLinkedListImpl<>();

        if (generosRaw == null || generosRaw.trim().isEmpty() || generosRaw.equals("[]")) {
            return lista;
        }

        generosRaw = generosRaw.replace("'", "\"");

        String[] objetos = generosRaw.split("\\},\\s*\\{");

        for (String obj : objetos) {
            try {
                obj = obj.replace("[", "").replace("]", "").replace("{", "").replace("}", "").trim();
                String[] campos = obj.split(",\\s*");

                for (String campo : campos) {
                    if (campo.contains("\"name\":")) {
                        String[] keyValue = campo.split(":");
                        if (keyValue.length == 2) {
                            String nombre = keyValue[1].trim().replace("\"", "");
                            Genero genero = Genero.fromString(nombre);
                            if (genero != null) {
                                lista.add(genero);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return lista;
    }


}
