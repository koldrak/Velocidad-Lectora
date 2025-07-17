import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class Diapositivas extends JFrame {
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private List<String> dias;
    private int index = 0;

    public Diapositivas(String lectura, ImageIcon imagen, Map<String, List<String>> actividades) {
        setTitle("Presentación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // Diapositiva lectura
        JPanel panelLectura = new JPanel(new BorderLayout());
        JTextArea areaTexto = new JTextArea(lectura);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setEditable(false);
        panelLectura.add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        if (imagen != null)
            panelLectura.add(new JLabel(imagen), BorderLayout.EAST);
        panelContenedor.add(panelLectura, "lectura");

        // Diapositivas actividades
        for (String dia : actividades.keySet()) {
            JPanel panelDia = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea(dia + ":\n");
            for (String act : actividades.get(dia)) {
                area.append("- " + act + "\n");
            }
            area.setEditable(false);
            area.setLineWrap(true);
            panelDia.add(new JScrollPane(area), BorderLayout.CENTER);
            panelContenedor.add(panelDia, dia);
        }

        add(panelContenedor);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
                    index++;
                } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    index--;
                }
                if (index < 0) index = 0;
                if (index > actividades.size()) index = actividades.size();
                String clave = (index == 0) ? "lectura" : (String) actividades.keySet().toArray()[index - 1];
                cardLayout.show(panelContenedor, clave);
            }
        });
        setFocusable(true);
        setVisible(true);
    }
}
