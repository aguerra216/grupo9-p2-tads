import com.opencsv.exceptions.CsvValidationException;
import entities.*;
import tads.HashT.*;
import tads.LinkedList.MyLinkedListImpl;
import com.opencsv.CSVReader;
import tads.LinkedList.MyList;
import tads.queue.MyQueueImpl;

import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;


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
                        Saga objsaga = parseSaga(collectionData, id, title);

                        if (!sagas.contains(objsaga.getId())) {
                            sagas.put(objsaga.getId(), objsaga);
                        }
                        objsaga = sagas.get(objsaga.getId());

                        Pelicula objPelicula = new Pelicula(id,title,language,revenue,listaGeneros,objsaga.getId());

                        if (!objsaga.getPeliculas().contains(id)) {
                            objsaga.agregarPelicula(id);
                        }



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

    //Funcion que devuelve la saga del String
    public Saga parseSaga(String sagaRaw, Integer idPelicula, String titulo) {
        if (sagaRaw == null || sagaRaw.trim().isEmpty() || sagaRaw.equals("null")) {
            Saga saga = new Saga();
            saga.setId(idPelicula);
            saga.setNombre(titulo);
            return saga;
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
                return nueva;
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

    public void top5PeliculasPorIdioma(MyHashMap<Integer, Pelicula> peliculas) {
        // Comparator for min-heap based on ratings count
        Comparator<Pelicula> ratingsComparator = (m1, m2) -> {
            int ratingCompare = Integer.compare(m1.getListaRatings().size(), m2.getListaRatings().size());
            return ratingCompare != 0 ? ratingCompare : m1.getTitulo().compareTo(m2.getTitulo()); // Tiebreaker by title
        };

        // Map to store MyQueueImpl for each language
        MyHashMap<String, MyQueueImpl<Pelicula>> queuesByLanguage = new MyHashMap<>(5);
        queuesByLanguage.put("en", new MyQueueImpl<>(ratingsComparator));
        queuesByLanguage.put("es", new MyQueueImpl<>(ratingsComparator));
        queuesByLanguage.put("fr", new MyQueueImpl<>(ratingsComparator));
        queuesByLanguage.put("it", new MyQueueImpl<>(ratingsComparator));
        queuesByLanguage.put("pt", new MyQueueImpl<>(ratingsComparator));

        // Iterate through the hash values using for-each
        MyLinkedListImpl<Pelicula> listaPeliculas = peliculas.values();
        for (Pelicula movie : listaPeliculas) {
            String language = movie.getIdiomaOriginal();
            if (queuesByLanguage.contains(language)) {
                MyQueueImpl<Pelicula> queue = queuesByLanguage.get(language);
                if (queue.getSize() < 5) {
                    queue.enqueueWithPriority(movie);
                } else {
                    // Peek the head (smallest ratings count)
                    Pelicula minMovie = queue.peek();
                    if (movie.getListaRatings().size() > minMovie.getListaRatings().size()) {
                        queue.dequeue(); // Remove the movie with fewest ratings
                        queue.enqueueWithPriority(movie); // Add the new movie
                    }
                }
            }
        }


        // Display top 5 movies for each language
        String[] lenguajesMostrar = {"en", "es", "fr", "it", "pt"};
        for (String language : lenguajesMostrar) {
            System.out.println("Top 5 películas en " + language + ":");
            MyQueueImpl<Pelicula> queue = queuesByLanguage.get(language);
            if (queue == null || queue.isEmpty()) {
                System.out.println("  No hay películas en este idioma.");
            } else {
                int indice = 5;
                while (!queue.isEmpty()) {
                    Pelicula movie = queue.dequeue();
                    System.out.printf("  %d. %d, %s, %d, %s%n",
                            indice , movie.getIdPelicula(), movie.getTitulo(), movie.getListaRatings().size(), movie.getIdiomaOriginal());
                    indice--;

                }
            }
            System.out.println();
        }

    }


    public void top10PeliculasCalificacionMedia(MyHashMap<Integer, Pelicula> peliculas) {
        // Clase interna para almacenar película y su calificación media temporalmente
        class PeliculaConMedia {
            Pelicula pelicula;
            double calificacionMedia;

            PeliculaConMedia(Pelicula pelicula, double calificacionMedia) {
                this.pelicula = pelicula;
                this.calificacionMedia = calificacionMedia;
            }
        }

        // Comparator para el min-heap basado en calificación media
        Comparator<PeliculaConMedia> ratingAvgComparator = (pm1, pm2) -> {
            int avgCompare = Double.compare(pm1.calificacionMedia, pm2.calificacionMedia);
            return avgCompare != 0 ? avgCompare : pm1.pelicula.getTitulo().compareTo(pm2.pelicula.getTitulo());
        };

        // Crear min-heap para las 10 mejores películas
        MyQueueImpl<PeliculaConMedia> top10Queue = new MyQueueImpl<>(ratingAvgComparator);
        MyLinkedListImpl<Pelicula> listaPeliculas = peliculas.values();

        // Iterar sobre las películas
        for (Pelicula movie : listaPeliculas) {
            // Calcular la calificación media solo una vez por película
            double media = obtenerCalificacionMedia(movie.getIdPelicula());
            PeliculaConMedia peliculaConMedia = new PeliculaConMedia(movie, media);

            if (top10Queue.getSize() < 10) {
                top10Queue.enqueueWithPriority(peliculaConMedia);
            } else {
                PeliculaConMedia minPelicula = top10Queue.peek();
                if (media > minPelicula.calificacionMedia) {
                    top10Queue.dequeue();
                    top10Queue.enqueueWithPriority(peliculaConMedia);
                }
            }
        }

        System.out.println("Top 10 películas por calificación media:");
        if (top10Queue.isEmpty()) {
            System.out.println("  No hay películas con calificaciones.");
        } else {
            int indice = 10;
            while(!top10Queue.isEmpty()) {
                PeliculaConMedia movie = top10Queue.dequeue();
                System.out.printf("  %d. %d, %s, %.2f%n",
                        indice, movie.pelicula.getIdPelicula(), movie.pelicula.getTitulo(), movie.calificacionMedia);
                indice--;
            }
        }
        System.out.println();
    }

    private double obtenerCalificacionMedia(Integer id) {
        Pelicula pelicula = peliculas.get(id);
        //Si tiene menos de 100 rating no me interesa la pelicula
        if (pelicula.getListaRatings().size() < 100) {
            return 0;
        }
        double calificacionMedia = 0.0;
        for (Calificacion calificacion : pelicula.getListaRatings()) {
            calificacionMedia+=calificacion.getRating();
        }
        return calificacionMedia / pelicula.getListaRatings().size();
    }

    public void top5ColeccionesIngresos(MyHashMap<Integer, Saga> sagas) {
        // Comparator para el min-heap basado en revenue
        Comparator<Saga> revenueComparator = (s1, s2) -> {
            int revenueCompare = Double.compare(obtenerRevenue(s1.getId()), obtenerRevenue(s2.getId()));
            return revenueCompare != 0 ? revenueCompare : s1.getNombre().compareTo(s2.getNombre());
        };

        // Crear min-heap para las 10 mejores películas
        MyQueueImpl<Saga> top10Sagas = new MyQueueImpl<>(revenueComparator);
        MyLinkedListImpl<Saga> listaSagas = sagas.values();
        for (Saga saga : listaSagas) {
            if (top10Sagas.getSize() < 5) {
                top10Sagas.enqueueWithPriority(saga);
            } else {
                Saga minSaga = top10Sagas.peek();
                if (obtenerRevenue(minSaga.getId()) < obtenerRevenue(saga.getId())) {
                    top10Sagas.dequeue();
                    top10Sagas.enqueueWithPriority(saga);
                }

            }
        }
        System.out.println("Top 10 sagas con más revenue: ");
        if (top10Sagas.isEmpty()) {
            System.out.println("  No hay películas con calificaciones.");
        } else {
            int indice = 5;
            while(!top10Sagas.isEmpty()) {
                Saga saga = top10Sagas.dequeue();
                System.out.printf("  %d. %d, %s, %d, %s, %,.2f%n",
                        indice, saga.getId(), saga.getNombre(),saga.getPeliculas().size(), saga.getPeliculas(), obtenerRevenue(saga.getId()));
                indice--;
            }
        }
        System.out.println();
    }

    private double obtenerRevenue(Integer id) {
        Saga saga = sagas.get(id);
        double revenue = 0.0;
        for (Integer idPelicula : saga.getPeliculas()) {
            Pelicula pelicula = peliculas.get(idPelicula);
            revenue += pelicula.getRevenue();
        }
        return revenue;
    }

    public MyHashMap<Integer, Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(MyHashMap<Integer, Pelicula> peliculas) {
        this.peliculas = peliculas;
    }

    public MyHashMap<Integer, Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(MyHashMap<Integer, Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public MyHashMap<Integer, Saga> getSagas() {
        return sagas;
    }

    public void setSagas(MyHashMap<Integer, Saga> sagas) {
        this.sagas = sagas;
    }

    public MyHashMap<Integer, Actor> getActores() {
        return actores;
    }

    public void setActores(MyHashMap<Integer, Actor> actores) {
        this.actores = actores;
    }

    public MyHashMap<Integer, Director> getDirectores() {
        return directores;
    }

    public void setDirectores(MyHashMap<Integer, Director> directores) {
        this.directores = directores;
    }
}
