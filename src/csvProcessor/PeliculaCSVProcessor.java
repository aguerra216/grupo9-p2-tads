package csvProcessor;

import entities.Pelicula;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// Clase para procesar el CSV
class PeliculaCsvProcessor {


    public void loadPelicula(){
        try (
            BufferedReader br = new BufferedReader(new FileReader("resources/movies_metadata.csv"))) {
            String line;
            br.readLine(); //Saltearse el header
            while ((line=br.readLine()) != null) {

                String[] parts = line.split(",(?=([^\"]\"[^\"]\")[^\"]$)"); // Handle commas in quoted fields
                int id = Integer.parseInt(parts[5]);
                String title = parts[8];
                String language = parts[7];
                double revenue=0.0;
                try {
                    revenue = Double.parseDouble(parts[13]);
                }catch(Exception e){
                    revenue=0.0;
                }
                String collectionData=parts[1];
                System.out.println(title);
            }

        } catch (IOException e) {
            System.out.println("Error loading movies_metadata.csv: " + e.getMessage());
        }
    }
}