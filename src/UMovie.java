import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.opencsv.exceptions.CsvValidationException;
import entities.*;
import org.json.JSONArray;
import org.json.JSONException;
import tads.HashT.*;
import tads.LinkedList.MyLinkedListImpl;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
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
        actores = new MyHashMap<>(1000000);
        directores = new MyHashMap<>(50000);

    }

    public void cargarPeliculas() {
        try (CSVReader csvReader = new CSVReader(new FileReader("resources/movies_metadata.csv"))) {
            csvReader.readNext(); // Saltar el header
            System.out.println("Iniciando carga de películas");

            String[] parts;
            while ((parts = csvReader.readNext()) != null) {
                if (parts.length >= 18) {
                    if (!parts[0].trim().equalsIgnoreCase("FALSE")) {
                        continue; // Saltar a la siguiente línea
                    }
                    try {
                        // Parsear datos
                        int id = Integer.parseInt(parts[5].trim());
                        String title = parts[8];
                        String language = parts[7];
                        String collectionGenres = parts[3];
                        MyLinkedListImpl<Genero> listaGeneros = parseGeneros(collectionGenres);
                        double revenue = 0.0;
                        try {
                            revenue = Double.parseDouble(parts[13]);
                        } catch (NumberFormatException e) {
                            revenue = 0.0;
                        }
                        String collectionData = parts[1];
                        Saga objsaga = parseSaga(collectionData);

                        if (objsaga == null) {
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

                    } catch (Exception e) {
                        System.out.println("Error procesando línea: " + String.join(",", parts) + ", mensaje: " + e.getMessage());
                    }
                } else {
                    System.out.println("Línea inválida (menos de 18 columnas): " + String.join(",", parts));
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.out.println("Error cargando movies_metadata.csv: " + e.getMessage());
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
        try (CSVReader csvReader = new CSVReader(new FileReader("resources/ratings_1mm.csv"))) {
            csvReader.readNext(); // Saltar header
            System.out.println("Iniciando carga de ratings");

            String[] parts;
            while ((parts = csvReader.readNext()) != null) {
                if (parts.length == 4) {
                    try {
                        int userId = Integer.parseInt(parts[0].trim());
                        int movieId = Integer.parseInt(parts[1].trim());
                        double rating = Double.parseDouble(parts[2].trim());
                        long timestamp = Long.parseLong(parts[3].trim());

                        Pelicula pelicula = peliculas.get(movieId);
                        if (pelicula == null) {
                            continue;
                        }

                        Calificacion c = new Calificacion(userId, movieId, rating, timestamp);
                        Usuario usuario = usuarios.get(userId);
                        if (usuario == null) {
                            usuario = new Usuario(userId);
                            usuarios.put(userId, usuario);
                        }

                        pelicula.agregarRating(c);
                        usuario.agregarCalificacion(c);


                    } catch (Exception e) {
                        System.out.println("Error procesando rating en línea: " + String.join(",", parts) + ", mensaje: " + e.getMessage());
                    }
                } else {
                    System.out.println("Línea inválida (no tiene 4 columnas): " + String.join(",", parts));
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.out.println("Error leyendo ratings_1mm.csv: " + e.getMessage());
        }
    }



    public void cargarCreditos() {
        try (CSVReader csvReader = new CSVReader(new FileReader("resources/credits.csv"))) {
            csvReader.readNext(); // Saltar el header
            int count = 0;
            System.out.println("Iniciando carga de créditos");

            String[] parts;
            while ((parts = csvReader.readNext()) != null) {
                if (parts.length >= 3) {
                    try {
                        // Parsear el ID de la película (última columna)
                        int movieId = Integer.parseInt(parts[2].trim());
                        if (!peliculas.contains(movieId)) {
                            continue;
                        }
                        // Parsear actores
                        parseActores(parts[0].trim(), movieId);

                        // Parsear el campo 'crew' para obtener el director
                        parseDirector(parts[1].trim(), movieId);

                    } catch (Exception e) {
                        System.out.println("Error procesando línea: " + String.join(",", parts) + ", mensaje: " + e.getMessage());
                    }
                } else {
                    System.out.println("Línea inválida (menos de 3 columnas): " + String.join(",", parts));
                }
            }

            System.out.println(peliculas.size() + " peliculas");
            System.out.println(sagas.size() + " sagas");
            System.out.println(usuarios.size() + " usuarios");
            System.out.println(actores.size() + " actores");
            System.out.println(directores.size() + " directores");

        } catch (IOException | CsvValidationException e) {
            System.out.println("Error cargando credits.csv: " + e.getMessage());
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
                            } catch (NumberFormatException ignored) {
                            }
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
            } catch (Exception ignored) {
            }
        }
    }
    // Función auxiliar para parsear el director
    private void parseDirector(String crewRaw, Integer idMovie) {
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
