import entities.Calificacion;
import entities.Genero;
import entities.Pelicula;
import entities.Usuario;
import tads.HashT.*;
import tads.LinkedList.MyLinkedListImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UMovie {
    private MyHashMap<Integer, Pelicula> peliculas;
    private MyHashMap<Integer, Usuario> usuarios;


    public UMovie() {
        peliculas = new MyHashMap<>(50000);
        usuarios = new MyHashMap<>(1000000);
    }

    public void cargarPeliculas() {
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

                            peliculas.put(id, objPelicula);

                            count++; //contador de prueba
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

            System.out.println("Películas cargadas: " + count); //prueba

        } catch (IOException e) {
            System.out.println("Error loading movies_metadata.csv: " + e.getMessage());
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

    public void cargarRatings() {
        try (BufferedReader br = new BufferedReader(new FileReader("resources/ratings.csv"))) {
            String line;
            br.readLine(); // Saltar header
            int count = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                try {
                    int userId = Integer.parseInt(parts[0]);
                    int movieId = Integer.parseInt(parts[1]);
                    double rating = Double.parseDouble(parts[2]);
                    long timestamp = Long.parseLong(parts[3]);


                    if (!usuarios.contains(userId)) {
                        Usuario nuevoUsuario = new Usuario(userId);
                        usuarios.put(userId, nuevoUsuario);
                    }
                    Calificacion c = new Calificacion(userId, movieId, rating, timestamp);

                    //terminar de ver donde tengo list/hash guardada


                    count++;
                } catch (Exception e) {
                    // Línea inválida
                }
            }

            System.out.println("Ratings cargados: " + count);
        } catch (IOException e) {
            System.out.println("Error leyendo ratings.csv: " + e.getMessage());
        }

    }

    public void cargarCreditos() {
        try (BufferedReader br = new BufferedReader(new FileReader("resources/credits.csv"))) {
            String line;
            br.readLine(); // Saltarse el header
            int count = 0;

            while ((line = br.readLine()) != null) {
                // Acumular líneas hasta que haya al menos 3 columnas
                while (true) {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (parts.length >= 3) {
                        try {
                            // Parsear el ID de la película (última columna)
                            int movieId = Integer.parseInt(parts[2].trim());

                            // Parsear el campo 'cast' para obtener IDs de actores
                            MyLinkedListImpl<Integer> actorIds = parseActores(parts[0].trim());

                            // Parsear el campo 'crew' para obtener el ID del director
                            int directorId = parseDirector(parts[1].trim());

                            //crear cada actor si no existe y asociarlos a pelicula
                            //crear director si no existe y asociarlo a pelicula

                            count++;
                        } catch (Exception e) {
                            System.out.println("Error en línea: " + e.getMessage());
                        }
                        break;
                    } else {
                        String nextLine = br.readLine();
                        if (nextLine == null) break; // Fin del archivo
                        line += "\n" + nextLine;
                    }
                }
            }

            System.out.println("Créditos cargados: " + count);

        } catch (IOException e) {
            System.out.println("Error loading credits.csv: " + e.getMessage());
        }
    }

    // Función auxiliar para parsear la lista de actores
    private MyLinkedListImpl<Integer> parseActores(String castRaw) {
        MyLinkedListImpl<Integer> actorIds = new MyLinkedListImpl<>();

        if (castRaw == null || castRaw.trim().isEmpty() || castRaw.equals("[]")) {
            return actorIds;
        }

        // Reemplazar comillas simples por dobles para consistencia
        castRaw = castRaw.replace("'", "\"");

        // Dividir en objetos individuales
        String[] objetos = castRaw.split("\\},\\s*\\{");

        for (String obj : objetos) {
            try {
                // Limpiar el objeto
                obj = obj.replace("[", "").replace("]", "").replace("{", "").replace("}", "").trim();
                String[] campos = obj.split(",\\s*");

                for (String campo : campos) {
                    if (campo.contains("\"id\":")) {
                        String[] keyValue = campo.split(":\\s*");
                        if (keyValue.length == 2) {
                            String idStr = keyValue[1].trim().replace("\"", "");
                            try {
                                int actorId = Integer.parseInt(idStr);
                                actorIds.add(actorId);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return actorIds;
    }

    // Función auxiliar para parsear el director
    private int parseDirector(String crewRaw) {
        int directorId = -1; // Valor por defecto si no se encuentra director

        if (crewRaw == null || crewRaw.trim().isEmpty() || crewRaw.equals("[]")) {
            return directorId;
        }

        // Reemplazar comillas simples por dobles para consistencia
        crewRaw = crewRaw.replace("'", "\"");

        // Dividir en objetos individuales
        String[] objetos = crewRaw.split("\\},\\s*\\{");

        for (String obj : objetos) {
            try {
                // Limpiar el objeto
                obj = obj.replace("[", "").replace("]", "").replace("{", "").replace("}", "").trim();
                String[] campos = obj.split(",\\s*");

                boolean isDirector = false;
                String idStr = null;

                for (String campo : campos) {
                    if (campo.contains("\"job\":")) {
                        String[] keyValue = campo.split(":\\s*");
                        if (keyValue.length == 2 && keyValue[1].trim().replace("\"", "").equals("Director")) {
                            isDirector = true;
                        }
                    }
                    if (campo.contains("\"id\":")) {
                        String[] keyValue = campo.split(":\\s*");
                        if (keyValue.length == 2) {
                            idStr = keyValue[1].trim().replace("\"", "");
                        }
                    }
                }

                if (isDirector && idStr != null) {
                    try {
                        directorId = Integer.parseInt(idStr);
                        break; // Solo necesitamos el primer director
                    } catch (NumberFormatException ignored) {}
                }
            } catch (Exception ignored) {}
        }

        return directorId;
    }



}
