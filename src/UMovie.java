import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.opencsv.exceptions.CsvValidationException;
import entities.*;
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

                    Usuario usuario = usuarios.get(userId);
                    Pelicula pelicula = peliculas.get(movieId);
                    if (pelicula != null) {
                        pelicula.agregarRating(c);
                        count++;
                    }
                    if (usuario != null) {
                        usuario.agregarCalificacion(c);
                    }


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
                            System.out.println("La pelicula no existe: " + movieId);
                            continue;
                        }
                        // Parsear actores
                        parseActores(parts[0].trim(), movieId);

                        // Parsear el campo 'crew' para obtener el director
                        parseDirector(parts[1].trim(), movieId);

                        count++;

                    } catch (NumberFormatException e) {
                        System.out.println("Error parseando movieId en línea: " + String.join(",", parts) + ", mensaje: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error procesando línea: " + String.join(",", parts) + ", mensaje: " + e.getMessage());
                    }
                } else {
                    System.out.println("Línea inválida (menos de 3 columnas): " + String.join(",", parts));
                }
            }

            System.out.println("Créditos cargados: " + count);
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

        try {
            Gson gson = new Gson();
            JsonArray actorsArray = gson.fromJson(castRaw, JsonArray.class);
            int actorCount = 0;

            for (int i = 0; i < actorsArray.size(); i++) {
                try {
                    JsonObject actorObj = actorsArray.get(i).getAsJsonObject();
                    int id = actorObj.has("id") ? actorObj.get("id").getAsInt() : -1;
                    String name = actorObj.has("name") ? actorObj.get("name").getAsString() : null;

                    if (id != -1 && name != null) {
                        Actor actor = new Actor(id, name);
                        actor.agregarPelicula(idMovie);
                        if(!actores.contains(id)) {
                            actores.put(id, actor);
                        }
                        actorCount++;
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando actor en índice " + i + " de movieId " + idMovie + ": " + e.getMessage());
                }
            }

            if (actorCount % 100 == 0) {
                System.out.println("Procesados " + actorCount + " actores para movieId " + idMovie);
            }
        } catch (JsonParseException e) {
            System.out.println("Error parseando JSON para movieId " + idMovie + ": " + e.getMessage());
        }
    }

    private void parseDirector(String crewRaw, Integer idMovie) {
        if (crewRaw == null || crewRaw.trim().isEmpty() || crewRaw.equals("[]")) {
            return;
        }

        try {
            Gson gson = new Gson();
            JsonArray crewArray = gson.fromJson(crewRaw, JsonArray.class);

            for (int i = 0; i < crewArray.size(); i++) {
                try {
                    JsonObject crewObj = crewArray.get(i).getAsJsonObject();
                    if (crewObj.has("job") && crewObj.get("job").getAsString().equals("Director")) {
                        int id = crewObj.has("id") ? crewObj.get("id").getAsInt() : -1;
                        String name = crewObj.has("name") ? crewObj.get("name").getAsString() : null;

                        if (id != -1 && name != null) {
                            Director director = new Director(id, name);
                            director.agregarPelicula(idMovie);
                            if(!directores.contains(id)) {
                                directores.put(id, director);
                            }
                            return; // Salir tras encontrar el primer director
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando miembro del crew en índice " + i + " de movieId " + idMovie + ": " + e.getMessage());
                }
            }
        } catch (JsonParseException e) {
            System.out.println("Error parseando JSON de crew para movieId " + idMovie + ": " + e.getMessage());
        }
    }



}
