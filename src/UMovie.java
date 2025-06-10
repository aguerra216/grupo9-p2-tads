import entities.*;
import tads.HashT.*;
import tads.LinkedList.MyLinkedListImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UMovie {
    private MyHashMap<Integer, Pelicula> peliculas;
    private MyHashMap<Integer, Usuario> usuarios;
    private MyHashMap<Integer, Saga> sagas;
    private MyHashMap<Integer, Actor> actores;
    private MyHashMap<Integer, Director> directores;


    public UMovie() {
        peliculas = new MyHashMap<>(50000);
        usuarios = new MyHashMap<>(1000000);
        sagas = new MyHashMap<>(50000);
        actores = new MyHashMap<>(5000000);
        directores = new MyHashMap<>(5000000);

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
                    if (parts.length >= 18) {
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
                            Saga objsaga = parseSaga(collectionData);

                            if (parseSaga(collectionData) == null) {
                                objsaga = new Saga();
                                objsaga.setId(id);
                                objsaga.setNombre(title);
                                if (!sagas.contains(id)) {
                                    sagas.put(id, objsaga);
                                }

                            }

                            Pelicula objPelicula = new Pelicula();
                            objPelicula.setIdPelicula(id);
                            objPelicula.setTitulo(title);
                            objPelicula.setIdiomaOriginal(language);
                            objPelicula.setListaGeneros(listaGeneros);
                            objPelicula.setRevenue(revenue);
                            objPelicula.setIdColeccion(objsaga.getId());
                            objsaga.agregarPelicula(id);

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
            System.out.println(peliculas.size());
            System.out.println(sagas.size());


        } catch (IOException e) {
            System.out.println("Error loading movies_metadata.csv: " + e.getMessage());
        }
    }

    //Funcion que devuelve la saga y la agrega si no esta al hash
    public Saga parseSaga(String sagaRaw) {
        if (sagaRaw == null || sagaRaw.trim().isEmpty() || sagaRaw.equals("null")) {
            return null;
        }

        sagaRaw = sagaRaw.replace("'", "\"").trim();
        sagaRaw = sagaRaw.replaceAll("^\"|\"$", ""); // Quita comillas externas si las tiene

        try {
            // Elimina las llaves
            sagaRaw = sagaRaw.replace("{", "").replace("}", "");
            String[] campos = sagaRaw.split(",\\s*");

            int id = -1;
            String name = null;

            for (String campo : campos) {
                String[] keyValue = campo.split(":", 2);
                if (keyValue.length != 2) continue;

                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");

                if (key.equals("id")) {
                    id = Integer.parseInt(value);
                } else if (key.equals("name")) {
                    name = value;
                }
            }

            Saga nueva = new Saga();

            if (id != -1 && name != null) {
                nueva.setId(id);
                nueva.setNombre(name);
                if (!sagas.contains(id)) {
                    sagas.put(id, nueva);
                }
            }
            return nueva;


        } catch (Exception e) {
            // Ignorado o logueado si es necesario
        }
        return null;

    }



    //Funcion que nos devuelve solo los nombres de los generos en una lista
    public MyLinkedListImpl<Genero> parseGeneros(String generosRaw) {
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
        try (BufferedReader br = new BufferedReader(new FileReader("resources/ratings_1mm.csv"))) {
            String line;
            br.readLine(); // Saltar header
            int count = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 4) {
                    System.out.println("linea invalida: " + line);
                    continue;
                }

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

                    Pelicula pelicula = peliculas.get(movieId);
                    if (pelicula != null) {
                        pelicula.agregarRating(c);
                    } else {
                        continue;
                    }

                    count++;

                } catch (Exception e) {
                    System.out.println("Error parsing rating: " + line + e.getMessage());
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
            System.out.println("entro  cargar creditos");
            while ((line = br.readLine()) != null) {
                // Acumular líneas hasta que haya al menos 3 columnas

                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (parts.length >= 3) {
                        try {
                            // Parsear el ID de la película (última columna)
                            int movieId = Integer.parseInt(parts[2].trim());
                            System.out.println(movieId + " id");

                            // Parsear actores, crearlos y agregarlos al hash con su pelicula
                            parseActores(parts[0].trim(), movieId);

                            // Parsear el campo 'crew' para obtener el ID del director
                            parseDirector(parts[1].trim(), movieId);

                            //crear cada actor si no existe y asociarlos a pelicula
                            //crear director si no existe y asociarlo a pelicula

                            count++;
                        } catch (Exception e) {
                            System.out.println("Error en línea: " + e.getMessage());
                        }

                    } else {
                        String nextLine = br.readLine();
                        if (nextLine == null) break; // Fin del archivo
                        line += "\n" + nextLine;
                    }

            }

            System.out.println("Créditos cargados: " + count);

        } catch (IOException e) {
            System.out.println("Error loading credits.csv: " + e.getMessage());
        }
    }

    // Función auxiliar para parsear la lista de actores
    private void parseActores(String castRaw, Integer idMovie) {

        if (castRaw == null || castRaw.trim().isEmpty() || castRaw.equals("[]")) {
            return;
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

                int id = -1;
                String name = null;

                for (String campo : campos) {
                    String[] keyValue = campo.split(":\\s*");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");

                        if (key.equals("id")) {
                            try {
                                id = Integer.parseInt(value);
                            } catch (NumberFormatException ignored) {}
                        } else if (key.equals("name")) {
                            name = value;
                        }
                    }
                }

                if (id != -1 && name != null) {
                    Actor actor = new Actor(id, name);
                    actor.agregarPelicula(idMovie);
                    if (!actores.contains(id)) {
                        actores.put(id, actor);
                    }

                }
            } catch (Exception ignored) {}
        }


    }

    // Función auxiliar para parsear el director
    private void parseDirector(String crewRaw, Integer idMovie) {
        int directorId = -1; // Valor por defecto si no se encuentra director

        if (crewRaw == null || crewRaw.trim().isEmpty() || crewRaw.equals("[]")) {
            return;
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

                int id = -1;
                String name = null;

                for (String campo : campos) {
                    String[] keyValue = campo.split(":\\s*");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");

                        if (key.equals("job") && value.equals("Director")) {
                            isDirector = true;
                        } else if (key.equals("id")) {
                            try {
                                id = Integer.parseInt(value);
                            } catch (NumberFormatException ignored) {}
                        } else if (key.equals("name")) {
                            name = value;
                        }
                    }
                }

                if (isDirector && id != -1 && name != null) {
                    Director director = new Director(id, name);
                    director.agregarPelicula(idMovie);
                    if (!directores.contains(id)) {
                        directores.put(id, director);
                    }
                    return;

                }
            } catch (Exception ignored) {}
        }
    }



}
