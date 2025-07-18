import java.util.Scanner;

public class LecturaTest {
    public static void iniciarTest(String nombreAlumno, String texto, JSONManager db) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Presiona ENTER para comenzar la lectura...");
        scanner.nextLine();
        long inicioTiempo = System.currentTimeMillis();

        System.out.println("Lee el siguiente texto en voz alta y presiona ENTER cuando termines:");
        System.out.println(texto);
        scanner.nextLine();
        long finTiempo = System.currentTimeMillis();

        long tiempoTotal = (finTiempo - inicioTiempo) / 1000; // Convertir a segundos
        int palabras = contarPalabras(texto);
        double ppm = (palabras / (double) tiempoTotal) * 60;
        String resultado = "PPM: " + Math.round(ppm) + " | Tiempo: " + tiempoTotal + " seg | Palabras: " + palabras;

        System.out.println("Tiempo total: " + tiempoTotal + " segundos.");
        System.out.println("Palabras por minuto: " + Math.round(ppm));

        // Guardar resultado en JSON
        db.guardarResultadoLectura(nombreAlumno, resultado);
    }

    private static int contarPalabras(String texto) {
        String[] palabras = texto.split("\\s+");
        return palabras.length;
    }
}
