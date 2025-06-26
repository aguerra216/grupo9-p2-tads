import java.util.Scanner;

public class Main {

    static UMovie objUMovie = new UMovie();

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
                    objUMovie.cargarPeliculas();
                    objUMovie.cargarRatings();
                    objUMovie.cargarCreditos();
                    long fin = System.currentTimeMillis();
                    System.out.println("Carga de datos exitosa, tiempo de ejecución de la carga: " + (fin - inicio));
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
                    System.out.println("Mostrando Top 5 de las películas que más calificaciones por idioma...");
                    long inicio = System.currentTimeMillis();
                    objUMovie.top5PeliculasPorIdioma();
                    long fin = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin-inicio) + " ms");
                    break;
                case 2:
                    System.out.println("Mostrando Top 10 de películas con mejor calificación media...");
                    long inicio2 = System.currentTimeMillis();
                    objUMovie.top10PeliculasCalificacionMedia();
                    long fin2 = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin2-inicio2) + " ms");
                    break;
                case 3:
                    System.out.println("Mostrando Top 5 de colecciones con más ingresos...");
                    long inicio3 = System.currentTimeMillis();
                    objUMovie.top5ColeccionesIngresos();
                    long fin3 = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin3-inicio3) + " ms");
                    break;
                case 4:
                    System.out.println("Mostrando Top 10 de directores con mejor calificación...");
                    long inicio4 = System.currentTimeMillis();
                    objUMovie.top10DirectoresMejorCalificacion();
                    long fin4 = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin4-inicio4) + " ms");
                    break;
                case 5:
                    System.out.println("Mostrando actor con más calificaciones por mes...");
                    long inicio5 = System.currentTimeMillis();
                    objUMovie.actorMasCalificacionPorMes();
                    long fin5 = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin5-inicio5) + " ms");
                    break;
                case 6:
                    System.out.println("Mostrando usuarios con más calificaciones por género...");
                    long inicio6 = System.currentTimeMillis();
                    objUMovie.usuariosMasCalificacionesPorGenero();
                    long fin6 = System.currentTimeMillis();
                    System.out.println("Tiempo de ejecucción de la consulta: " + (fin6-inicio6) + " ms");
                    break;
                case 7:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }
}
