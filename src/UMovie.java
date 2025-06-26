import com.opencsv.exceptions.CsvValidationException;
import entities.*;
import tads.HashT.*;
import tads.LinkedList.MyLinkedListImpl;
import com.opencsv.CSVReader;
import tads.queue.MyQueueImpl;

import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        usuarios = new MyHashMap<>(500000);
        sagas = new MyHashMap<>(50000);
        actores = new MyHashMap<>(500000);
        directores = new MyHashMap<>(40000);

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
                    if (!actores.contains(id)) {
                        Actor actor = new Actor(id, name);
                        actor.agregarPelicula(idMovie);
                        actores.put(id, actor);
                    } else {
                        actores.get(id).agregarPelicula(idMovie);
                    }
                    peliculas.get(idMovie).agregarActor(id);

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
                    if (!directores.contains(id)) {
                        Director director = new Director(id, name);
                        director.agregarPelicula(idMovie);
                        directores.put(id, director);
                    } else {
                        directores.get(id).agregarPelicula(idMovie);
                    }
                    return;

                }
            } catch (Exception ignored) {}
        }
    }

    public void top5PeliculasPorIdioma() {
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


    public void top10PeliculasCalificacionMedia() {
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

    public void top5ColeccionesIngresos() {
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

    public void top10DirectoresMejorCalificacion() {
        class DirectorConMediana{
            Director director;
            double mediana;

            public DirectorConMediana(Director director, double mediana) {
                this.director = director;
                this.mediana = mediana;
            }
        }
        Comparator<DirectorConMediana> medianaComparator = (a, b) -> {
            int comparacionMediana = Double.compare(a.mediana, b.mediana);
            if (comparacionMediana != 0) {
                return comparacionMediana;
            }
            // En caso de empate, comparar por cantidad de peliculas (menor es prioritario)
            return Integer.compare(
                    a.director.getListaPeliculas().size(),
                    b.director.getListaPeliculas().size()
            );
        };

        MyQueueImpl<DirectorConMediana> top10Queue = new MyQueueImpl<>(medianaComparator);


        for(Director director : directores.values()) {
            if (director.getListaPeliculas().size() <= 1) {
                continue;
            }
            MyLinkedListImpl<Double> listaCalificaciones = new MyLinkedListImpl<>();
            for (Integer idPelicula : director.getListaPeliculas()) {
                Pelicula pelicula = peliculas.get(idPelicula);
                for (Calificacion calificacion : pelicula.getListaRatings()) {
                    listaCalificaciones.add(calificacion.getRating());
                }
            }
            if (listaCalificaciones.size() < 100) {
                continue;
            }
            double mediana = calcularMediana(listaCalificaciones);
            DirectorConMediana dir = new DirectorConMediana(director, mediana);
            if (top10Queue.getSize() < 10) {
                top10Queue.enqueueWithPriority(dir);
            } else {
                DirectorConMediana minDirector = top10Queue.peek();
                if (mediana > minDirector.mediana) {
                    top10Queue.dequeue();
                    top10Queue.enqueueWithPriority(dir);
                }
            }

        }

        System.out.println("Top 10 actores con mejor calificación: ");
        if (top10Queue.isEmpty()) {
            System.out.println("  No hay directores para mostrar");
        } else {
            int indice = 10;
            while(!top10Queue.isEmpty()) {
                DirectorConMediana dir = top10Queue.dequeue();
                System.out.printf("  %s. %s, %d, %s%n",
                        indice, dir.director.getNombre(), dir.director.getListaPeliculas().size(), dir.mediana);
                indice--;
            }
        }
        System.out.println();

    }

    // Función auxiliar para calcular la mediana
    private static double calcularMediana(MyLinkedListImpl<Double> calificaciones) {
        // Ordenar la lista usando Merge Sort
        MyLinkedListImpl<Double> sortedCalificaciones = mergeSort(calificaciones);

        // Encontrar la mediana
        int size = sortedCalificaciones.size();
        int mid = size / 2;
        Iterator<Double> iterator = sortedCalificaciones.iterator();
        double left = 0.0, right = 0.0;
        for (int i = 0; i <= mid && iterator.hasNext(); i++) {
            double value = iterator.next();
            if (i == mid - 1 && size % 2 == 0) {
                left = value;
            }
            if (i == mid) {
                right = value;
            }
        }

        if (size % 2 == 0) {
            return (left + right) / 2.0; // Promedio de los dos valores centrales
        } else {
            return right; // Valor central
        }
    }

    // Implementación de Merge Sort para MyLinkedListImpl
    private static MyLinkedListImpl<Double> mergeSort(MyLinkedListImpl<Double> list) {
        if (list.size() <= 1) {
            return list;
        }

        // Dividir la lista en dos mitades
        int mid = list.size() / 2;
        MyLinkedListImpl<Double> left = new MyLinkedListImpl<>();
        MyLinkedListImpl<Double> right = new MyLinkedListImpl<>();
        Iterator<Double> iterator = list.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Double value = iterator.next();
            if (i < mid) {
                left.add(value);
            } else {
                right.add(value);
            }
        }

        // Ordenar recursivamente las mitades
        left = mergeSort(left);
        right = mergeSort(right);

        // Combinar las mitades ordenadas
        return merge(left, right);
    }

    // Combinar dos listas ordenadas
    private static MyLinkedListImpl<Double> merge(MyLinkedListImpl<Double> left, MyLinkedListImpl<Double> right) {
        MyLinkedListImpl<Double> result = new MyLinkedListImpl<>();
        Iterator<Double> leftIterator = left.iterator();
        Iterator<Double> rightIterator = right.iterator();
        Double leftValue = leftIterator.hasNext() ? leftIterator.next() : null;
        Double rightValue = rightIterator.hasNext() ? rightIterator.next() : null;

        while (leftValue != null && rightValue != null) {
            if (leftValue <= rightValue) {
                result.add(leftValue);
                leftValue = leftIterator.hasNext() ? leftIterator.next() : null;
            } else {
                result.add(rightValue);
                rightValue = rightIterator.hasNext() ? rightIterator.next() : null;
            }
        }

        // Agregar los elementos restantes de left
        if (leftValue != null) {
            result.add(leftValue);
            while (leftIterator.hasNext()) {
                result.add(leftIterator.next());
            }
        }

        // Agregar los elementos restantes de right
        if (rightValue != null) {
            result.add(rightValue);
            while (rightIterator.hasNext()) {
                result.add(rightIterator.next());
            }
        }

        return result;
    }

    public void actorMasCalificacionPorMes () {

        class EstadisticasActor {
            String nombre;
            int cantidadPeliculas;
            int cantidadCalificaciones;

            EstadisticasActor(String nombre) {
                this.nombre = nombre;
                this.cantidadPeliculas = 0;
                this.cantidadCalificaciones = 0;
            }
        }

        //mes - (idActor - estadísticas)
        MyHashMap<String, MyHashMap<Integer, EstadisticasActor>> estadisticasPorMes = new MyHashMap<>(12);
        MyHashMap<String, MyHashMap<Integer, Integer>> peliculasVistasPorMes = new MyHashMap<>(12);
        //los 12 meses
        for (int mes = 1; mes <= 12; mes++) {
            String mesString = String.format("%02d", mes);
            estadisticasPorMes.put(mesString, new MyHashMap<>(300000));
            peliculasVistasPorMes.put(mesString, new MyHashMap<>(300000));
        }
        for (Pelicula pelicula : peliculas.values()) {
            MyLinkedListImpl<Integer> actoresPelicula = pelicula.getListaActors();
            for (Calificacion calificacion : pelicula.getListaRatings()) {
                long timestamp = calificacion.getTimestamp(); // segundos
                Instant instant = Instant.ofEpochSecond(timestamp);
                ZonedDateTime fecha = instant.atZone(ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM"); //solo mes
                String mes = fecha.format(formatter);

                MyHashMap<Integer, EstadisticasActor> actoresMes = estadisticasPorMes.get(mes);
                for (Integer idActor : actoresPelicula) {
                    Actor actor = actores.get(idActor);
                    if (!actoresMes.contains(idActor)) {
                        EstadisticasActor estadisticasActor = new EstadisticasActor(actor.getNombre());
                        actoresMes.put(idActor, estadisticasActor);
                    }
                    EstadisticasActor objEstadisticasActor = actoresMes.get(idActor);
                    objEstadisticasActor.cantidadCalificaciones++;
                    if (!peliculasVistasPorMes.get(mes).contains(pelicula.getIdPelicula())) {
                        peliculasVistasPorMes.get(mes).put(pelicula.getIdPelicula(),pelicula.getIdPelicula());
                        objEstadisticasActor.cantidadPeliculas++;
                    }
                }
            }
        }

        //imprimir por mes
        for (int mes = 1; mes <= 12; mes++) {
            String mesString = String.format("%02d", mes); // <-- esto está bien
            MyHashMap<Integer, EstadisticasActor> actoresMes = estadisticasPorMes.get(mesString);
            EstadisticasActor actorMasCalificado = null;
            int maxCalificaciones = -1;

            // Encontrar actor con más calificaciones
            for (EstadisticasActor stats : actoresMes.values()) {
                if (stats.cantidadCalificaciones > maxCalificaciones) {
                    maxCalificaciones = stats.cantidadCalificaciones;
                    actorMasCalificado = stats;
                }
            }

            // Imprimir resultado
            if (actorMasCalificado != null) {
                System.out.printf("Mes: %d, Actor: %s, Películas: %d, Calificaciones: %d%n",
                        mes,
                        actorMasCalificado.nombre,
                        actorMasCalificado.cantidadPeliculas,
                        actorMasCalificado.cantidadCalificaciones);
            } else {
                System.out.printf("Mes: %d, Sin datos%n", mes);
            }
        }

    }


    public void usuariosMasCalificacionesPorGenero () {

        class EstadisticasGenero {
            String genero;
            int contadorRatings;

            public EstadisticasGenero(String genero, int contadorRatings) {
                this.genero = genero;
                this.contadorRatings = contadorRatings;
            }

        }

        class EstadisticasUsuario {
            Integer usuarioId;
            int contadorRatings;

            public EstadisticasUsuario(Integer usuario, int contadorRatings) {
                this.usuarioId = usuario;
                this.contadorRatings = contadorRatings;
            }
        }

        MyHashMap<String, Integer> generosRatingContador = new MyHashMap<>(25);
        MyHashMap<String, MyHashMap<Integer, Integer>> usuariosGenerosContador = new MyHashMap<>(25);

        for (Usuario usuario : usuarios.values()){
            for (Calificacion c: usuario.getCalificaciones()){
                Pelicula pelicula = peliculas.get(c.getMovieId());
                for (Genero genero: pelicula.getListaGeneros()){
                    if (!generosRatingContador.contains(genero.name())){
                        generosRatingContador.put(genero.name(), 1);
                    } else {
                        generosRatingContador.put(genero.name(), generosRatingContador.get(genero.name()) + 1);
                    }
                    if (!usuariosGenerosContador.contains(genero.name())){
                        usuariosGenerosContador.put(genero.name(), new MyHashMap<>(200000));
                    }
                    if (!usuariosGenerosContador.get(genero.name()).contains(usuario.getId())){
                        usuariosGenerosContador.get(genero.name()).put(usuario.getId(),1);
                    } else {
                        usuariosGenerosContador.get(genero.name()).put(usuario.getId(),usuariosGenerosContador.get(genero.name()).get(usuario.getId()) + 1);
                    }
                }
            }
        }

        MyLinkedListImpl<EstadisticasGenero> listaGen = new MyLinkedListImpl<>();
        for (String genero : generosRatingContador.keys()) {
            listaGen.add(new EstadisticasGenero(genero, generosRatingContador.get(genero)));
        }

        //bubble sort para ordenar
        for (int i = 0; i < listaGen.size() - 1; i++) {
            for (int j = i + 1; j < listaGen.size(); j++) {
                EstadisticasGenero gi = listaGen.get(i);
                EstadisticasGenero gj = listaGen.get(j);
                if (gi.contadorRatings < gj.contadorRatings) {
                    listaGen.set(i, gj);
                    listaGen.set(j, gi);
                }
            }
        }

        // tomo solo 10 del top
        int topGenerosLimite = 10;
        MyLinkedListImpl<EstadisticasGenero> topGeneros = new MyLinkedListImpl<>();
        for (int i = 0; i < topGenerosLimite; i++) {
            topGeneros.add(listaGen.get(i));
        }

        // por cada genero, encontrar el usuario con mas cal
        for (EstadisticasGenero genreStats : topGeneros) {
            MyHashMap<Integer, Integer> contadorUsuario = usuariosGenerosContador.get(genreStats.genero);
            EstadisticasUsuario topUsuario = null;
            for (Integer userId : contadorUsuario.keys()) {
                int count = contadorUsuario.get(userId);
                if (topUsuario == null || count > topUsuario.contadorRatings) {
                    topUsuario = new EstadisticasUsuario(userId, count);
                }
            }

            // Mostrar resultado
            if (topUsuario != null) {
                System.out.printf("%d, %s, %d\n", topUsuario.usuarioId, genreStats.genero, topUsuario.contadorRatings);
            }
        }

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
