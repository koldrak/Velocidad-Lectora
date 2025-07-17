public class Main {
    public static void main(String[] args) throws Exception {
        // Paso 1: Extraer RAR
        String rarPath = "ruta/a/archivo.rar";
        String outputDir = "ruta/destino/";
        RarExtractor.extraer(rarPath, outputDir);

        // Paso 2: Leer archivo txt y cargar imagen
        File txt = new File(outputDir + "morado de momia.txt");
        File img = new File(outputDir + "imagen.jpg"); // asumiendo nombre
        Contenido contenido = Contenido.cargarDesdeTXT(txt);
        ImageIcon icon = img.exists() ? new ImageIcon(img.getAbsolutePath()) : null;

        // Paso 3: Mostrar presentación
        new Diapositivas(contenido.lectura, icon, contenido.actividades);
    }
}
