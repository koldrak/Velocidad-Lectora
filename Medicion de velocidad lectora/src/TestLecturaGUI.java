import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TestLecturaGUI extends JFrame {
    private JSONManager db;
    private TextosManager textosManager;
    private JTextArea textoArea;
    private JButton btnIniciar, btnFinalizar;
    private JComboBox<String> comboTextos;
    private JComboBox<Integer> comboTamanoFuente;
    private JComboBox<String> comboFuente; // Nuevo: Selector de tipo de fuente
    private JCheckBox chkContarPuntuacion; // Checkbox para contar puntuación como palabra
    private JLabel labelTemporizador;
    private Timer timer;
    private int segundosTranscurridos;
    private long inicioTiempo;
    private String nombreAlumno;
    private Font pacificoFont; // Fuente manuscrita Pacifico
 // Agregar un límite de tiempo de 60 segundos (1 minuto)
    private static final int TIEMPO_LIMITE_SEGUNDOS = 60;
    private boolean testEnCurso = false; // Controla si el test está en ejecución

    public TestLecturaGUI(JSONManager db, TextosManager textosManager, String nombreAlumno) {
        this.db = db;
        this.textosManager = textosManager;
        this.nombreAlumno = nombreAlumno;
        setTitle("Test de Lectura - " + nombreAlumno);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Cargar la fuente Pacifico
        cargarFuenteManuscrita();

        // Obtener los títulos ordenados por palabras
        ArrayList<String> titulos = new ArrayList<>(textosManager.getTitulos());
        String[] opciones = titulos.isEmpty() ? new String[]{"No hay textos disponibles"} : titulos.toArray(new String[0]);

        comboTextos = new JComboBox<>(opciones);
        comboTextos.addActionListener(e -> {
            String seleccion = (String) comboTextos.getSelectedItem();
            if (!seleccion.equals("No hay textos disponibles")) {
                String tituloSeleccionado = seleccion.replaceAll("\\s*\\(\\d+ palabras\\)", "");
                String contenido = textosManager.getContenidoPorTitulo(tituloSeleccionado);
                textoArea.setText(contenido);
                textoArea.setCaretPosition(0);
            } else {
                textoArea.setText("");
            }
        });

        // ComboBox para seleccionar el tamaño de la fuente
        Integer[] tamanosFuente = {12, 14, 16, 18, 20, 22, 24, 28, 32, 40, 50, 60, 70, 80, 90, 100, 120};
        comboTamanoFuente = new JComboBox<>(tamanosFuente);
        comboTamanoFuente.setSelectedItem(14);
        comboTamanoFuente.addActionListener(e -> actualizarFuente());

        // Nuevo ComboBox para elegir la fuente
        String[] opcionesFuente = {"Imprenta (Arial)", "Manuscrita (Pacifico)"};
        comboFuente = new JComboBox<>(opcionesFuente);
        comboFuente.addActionListener(e -> actualizarFuente());

        // Configuración del JTextArea
        textoArea = new JTextArea();
        textoArea.setLineWrap(true);
        textoArea.setWrapStyleWord(true);
        textoArea.setEditable(false);
        actualizarFuente(); // Aplicar fuente inicial

     // Agregar KeyListener para detectar la barra espaciadora
        textoArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!testEnCurso) {
                        iniciarTest();
                    } else {
                        finalizarTest();
                    }
                }
            }
        });

        
        JScrollPane scrollPane = new JScrollPane(textoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Temporizador
        labelTemporizador = new JLabel("Tiempo: 00:00", SwingConstants.CENTER);
        labelTemporizador.setFont(new Font("Arial", Font.BOLD, 16));

        // Checkbox para contar puntuación
        chkContarPuntuacion = new JCheckBox("Contar '.' y ',' como palabra");

        // Panel superior con opciones
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new GridLayout(1, 4));
        panelSuperior.add(comboTextos);
        panelSuperior.add(comboTamanoFuente);
        panelSuperior.add(comboFuente);
        panelSuperior.add(chkContarPuntuacion);

        // Panel de botones con temporizador
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 3));

        btnIniciar = new JButton("Iniciar Test");
        btnFinalizar = new JButton("Finalizar Test");
        btnFinalizar.setEnabled(false);

        btnIniciar.addActionListener(e -> iniciarTest());
        btnFinalizar.addActionListener(e -> finalizarTest());

        panelBotones.add(btnIniciar);
        panelBotones.add(labelTemporizador);
        panelBotones.add(btnFinalizar);

        // Agregar componentes a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Método para cargar la fuente Pacifico desde un archivo TTF
    private void cargarFuenteManuscrita() {
        try {
            File fontFile = new File("Pacifico-Regular.ttf"); // Debe estar en la carpeta del programa
            if (fontFile.exists()) {
                pacificoFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pacificoFont);
            } else {
                pacificoFont = new Font("Serif", Font.ITALIC, 16); // Alternativa si no se encuentra Pacifico
            }
        } catch (IOException | FontFormatException e) {
            pacificoFont = new Font("Serif", Font.ITALIC, 16);
        }
    }

    // Método para actualizar la fuente y el tamaño según la selección
    private void actualizarFuente() {
        int nuevoTamano = (Integer) comboTamanoFuente.getSelectedItem();
        String tipoFuente = (String) comboFuente.getSelectedItem();

        if (tipoFuente.equals("Manuscrita (Pacifico)") && pacificoFont != null) {
            textoArea.setFont(pacificoFont.deriveFont((float) nuevoTamano));
        } else {
            textoArea.setFont(new Font("Arial", Font.PLAIN, nuevoTamano));
        }
    }

    // Obtener las palabras según la opción del checkbox
    private String[] obtenerPalabras(String texto) {
        if (chkContarPuntuacion.isSelected()) {
            texto = texto.replace(".", " . ").replace(",", " , ");
        }
        texto = texto.trim();
        if (texto.isEmpty()) {
            return new String[0];
        }
        return texto.split("\\s+");
    }

    private void iniciarTest() {
        if (timer != null) {
            timer.stop();
        }

        inicioTiempo = System.currentTimeMillis();
        segundosTranscurridos = 0;
        actualizarTemporizador();
        testEnCurso = true;

        btnIniciar.setEnabled(false);
        btnFinalizar.setEnabled(true);
        textoArea.requestFocus();

        timer = new Timer(1000, e -> {
            segundosTranscurridos++;
            actualizarTemporizador();
            
            if (segundosTranscurridos >= TIEMPO_LIMITE_SEGUNDOS) {
                finalizarTestPorTiempo();
            }
        });
        timer.start();
    }

    private void finalizarTestPorTiempo() {
        if (timer != null) {
            timer.stop();
        }
        
        JOptionPane.showMessageDialog(this, "Se ha agotado el tiempo. Por favor, selecciona hasta qué palabra llegó el alumno.");
        convertirTextoEnBotones();
    }
    
    private void convertirTextoEnBotones() {
        String[] palabras = obtenerPalabras(textoArea.getText());
        int columnas = 5; // Número de columnas para organizar los botones
        int filas = (int) Math.ceil((double) palabras.length / columnas); // Calcular filas dinámicamente

        JPanel panelPalabras = new JPanel(new GridLayout(filas, columnas, 5, 5)); // Diseño en cuadrícula con separación
        
        JDialog dialog = new JDialog(this, "Selecciona la última palabra leída", true);
        dialog.setLayout(new BorderLayout());

        for (int i = 0; i < palabras.length; i++) {
            JButton btnPalabra = new JButton(palabras[i]);
            int index = i + 1; // Número de palabras leídas

            btnPalabra.setFont(new Font("Arial", Font.PLAIN, 14)); // Ajustar tamaño de fuente
            btnPalabra.setPreferredSize(new Dimension(80, 30)); // Ajustar tamaño de botones

            // Cierra el cuadro al seleccionar una palabra
            btnPalabra.addActionListener(e -> {
                calcularPPMPorSeleccion(index);
                dialog.dispose();
                textoArea.requestFocus(); // Restaurar foco para detectar la barra espaciadora
            });

            panelPalabras.add(btnPalabra);
        }

        JScrollPane scrollPane = new JScrollPane(panelPalabras);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void calcularPPMPorSeleccion(int palabrasLeidas) {
        double ppm = (palabrasLeidas / (double) TIEMPO_LIMITE_SEGUNDOS) * 60;
        String resultado = "PPM: " + Math.round(ppm) + " | Tiempo: 60 seg | Palabras: " + palabrasLeidas;

        db.guardarResultadoLectura(nombreAlumno, resultado);
        JOptionPane.showMessageDialog(this, "Resultado guardado.\n" + resultado);

        btnIniciar.setEnabled(true);
        btnFinalizar.setEnabled(false);
        labelTemporizador.setText("Tiempo: 00:00");
    }

    
    private void finalizarTest() {
        if (timer != null) {
            timer.stop();
        }

        long finTiempo = System.currentTimeMillis();
        long tiempoTotal = (finTiempo - inicioTiempo) / 1000;

        if (tiempoTotal < TIEMPO_LIMITE_SEGUNDOS) {
            int palabras = obtenerPalabras(textoArea.getText()).length;
            double ppm = (palabras / (double) tiempoTotal) * 60;
            String resultado = "PPM: " + Math.round(ppm) + " | Tiempo: " + tiempoTotal + " seg | Palabras: " + palabras;

            db.guardarResultadoLectura(nombreAlumno, resultado);
            JOptionPane.showMessageDialog(this, "Resultado guardado.\n" + resultado);
        }

        btnIniciar.setEnabled(true);
        btnFinalizar.setEnabled(false);
        labelTemporizador.setText("Tiempo: 00:00");
        
    }

    private void actualizarTemporizador() {
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        labelTemporizador.setText(String.format("Tiempo: %02d:%02d", minutos, segundos));
    }
}
