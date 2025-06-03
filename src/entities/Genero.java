package entities;
public enum Genero {
    ANIMATION,
    COMEDY,
    FAMILY,
    ADVENTURE,
    FANTASY,
    HORROR,
    ACTION,
    DRAMA,
    ROMANCE,
    THRILLER,
    CRIME,
    MYSTERY,
    SCIENCE_FICTION,
    DOCUMENTARY,
    MUSIC,
    HISTORY,
    WAR,
    WESTERN,
    TV_MOVIE;

    public static Genero fromString(String s) {
        switch (s.toLowerCase()) {
            case "animation": return ANIMATION;
            case "comedy": return COMEDY;
            case "family": return FAMILY;
            case "adventure": return ADVENTURE;
            case "fantasy": return FANTASY;
            case "horror": return HORROR;
            case "action": return ACTION;
            case "drama": return DRAMA;
            case "romance": return ROMANCE;
            case "thriller": return THRILLER;
            case "crime": return CRIME;
            case "mystery": return MYSTERY;
            case "science fiction": return SCIENCE_FICTION;
            case "documentary": return DOCUMENTARY;
            case "music": return MUSIC;
            case "history": return HISTORY;
            case "war": return WAR;
            case "western": return WESTERN;
            case "tv movie": return TV_MOVIE;
            default: return null; // o lanzar excepción
        }
    }
}
