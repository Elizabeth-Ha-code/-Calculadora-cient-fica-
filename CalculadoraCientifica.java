import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

/**
 * Calculadora Científica
 * -----------------------
 * Aplicación de escritorio en Java (Swing) que implementa una calculadora
 * científica con operaciones básicas, trigonométricas, logarítmicas,
 * potencias, raíces y más.
 *
 * Autora: (Elizabeth Hernández A)
 */
public class CalculadoraCientifica extends JFrame {
/* Variables de intacias de la calculadora */
    private final JTextField pantalla;
    private final StringBuilder expresion = new StringBuilder();
    private boolean modoGrados = true; // true = grados, false = radianes

    public CalculadoraCientifica() {
        super("Calculadora Científica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        pantalla = new JTextField();
        pantalla.setEditable(false);
        pantalla.setFont(new Font("Consolas", Font.BOLD, 28));
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        pantalla.setPreferredSize(new Dimension(400, 60));
        pantalla.setBackground(Color.WHITE);

        JPanel panelPantalla = new JPanel(new BorderLayout());
        panelPantalla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPantalla.add(pantalla, BorderLayout.CENTER);

        JPanel panelBotones = crearPanelBotones();

        setLayout(new BorderLayout());
        add(panelPantalla, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelBotones() {
        String[] botones = {
            "sin", "cos", "tan", "DEG/RAD",
            "log", "ln", "√", "x²",
            "(", ")", "%", "CE",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "π", "e", "x^y", "C"
        };

        JPanel panel = new JPanel(new GridLayout(8, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String texto : botones) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Arial", Font.PLAIN, 16));
            boton.setFocusPainted(false);
            boton.addActionListener(new BotonListener());
            panel.add(boton);
        }
        return panel;
    }

    private class BotonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = ((JButton) e.getSource()).getText();
            procesarComando(comando);
        }
    }

    private void procesarComando(String comando) {
        switch (comando) {
            case "C":
                expresion.setLength(0);
                pantalla.setText("");
                break;
            case "CE":
                if (expresion.length() > 0) {
                    expresion.deleteCharAt(expresion.length() - 1);
                    pantalla.setText(expresion.toString());
                }
                break;
            case "=":
                calcular();
                break;
            case "DEG/RAD":
                modoGrados = !modoGrados;
                JOptionPane.showMessageDialog(this,
                        "Modo actual: " + (modoGrados ? "Grados" : "Radianes"));
                break;
            case "sin": case "cos": case "tan":
            case "log": case "ln": case "√":
                aplicarFuncion(comando);
                break;
            case "x²":
                aplicarPotenciaCuadrado();
                break;
            case "x^y":
                expresion.append("^");
                pantalla.setText(expresion.toString());
                break;
            case "π":
                expresion.append(Math.PI);
                pantalla.setText(expresion.toString());
                break;
            case "e":
                expresion.append(Math.E);
                pantalla.setText(expresion.toString());
                break;
            default:
                expresion.append(comando);
                pantalla.setText(expresion.toString());
        }
    }

    /** Aplica una función matemática al último número escrito en la expresión */
    private void aplicarFuncion(String funcion) {
        try {
            double valor = Double.parseDouble(expresion.toString());
            double resultado;

            double valorRad = modoGrados ? Math.toRadians(valor) : valor;

            switch (funcion) {
                case "sin": resultado = Math.sin(valorRad); break;
                case "cos": resultado = Math.cos(valorRad); break;
                case "tan": resultado = Math.tan(valorRad); break;
                case "log": resultado = Math.log10(valor); break;
                case "ln":  resultado = Math.log(valor); break;
                case "√":   resultado = Math.sqrt(valor); break;
                default: return;
            }

            expresion.setLength(0);
            expresion.append(formatear(resultado));
            pantalla.setText(expresion.toString());
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    private void aplicarPotenciaCuadrado() {
        try {
            double valor = Double.parseDouble(expresion.toString());
            double resultado = Math.pow(valor, 2);
            expresion.setLength(0);
            expresion.append(formatear(resultado));
            pantalla.setText(expresion.toString());
        } catch (NumberFormatException ex) {
            mostrarError();
        }
    }

    /** Evalúa la expresión aritmética actual respetando precedencia de operadores */
    private void calcular() {
        try {
            double resultado = evaluarExpresion(expresion.toString());
            expresion.setLength(0);
            expresion.append(formatear(resultado));
            pantalla.setText(expresion.toString());
        } catch (Exception ex) {
            mostrarError();
        }
    }

    private void mostrarError() {
        pantalla.setText("Error");
        expresion.setLength(0);
    }

    private String formatear(double valor) {
        if (valor == Math.floor(valor) && !Double.isInfinite(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    // ------------------------------------------------------------------
    // Motor de evaluación de expresiones (shunting-yard + notación postfija)
    // Soporta: + - * / ^ ( )
    // ------------------------------------------------------------------

    private double evaluarExpresion(String expr) {
        String postfija = infijaAPostfija(expr);
        return evaluarPostfija(postfija);
    }

    private int precedencia(char operador) {
        switch (operador) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
            default: return -1;
        }
    }

    private String infijaAPostfija(String expr) {
        StringBuilder salida = new StringBuilder();
        Stack<Character> operadores = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    salida.append(expr.charAt(i));
                    i++;
                }
                salida.append(' ');
                i--;
            } else if (c == '(') {
                operadores.push(c);
            } else if (c == ')') {
                while (!operadores.isEmpty() && operadores.peek() != '(') {
                    salida.append(operadores.pop()).append(' ');
                }
                if (!operadores.isEmpty()) operadores.pop();
            } else if ("+-*/^".indexOf(c) >= 0) {
                while (!operadores.isEmpty() && precedencia(operadores.peek()) >= precedencia(c)) {
                    salida.append(operadores.pop()).append(' ');
                }
                operadores.push(c);
            }
        }

        while (!operadores.isEmpty()) {
            salida.append(operadores.pop()).append(' ');
        }

        return salida.toString();
    }

    private double evaluarPostfija(String postfija) {
        Stack<Double> pila = new Stack<>();
        String[] tokens = postfija.trim().split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (token.length() == 1 && "+-*/^".indexOf(token.charAt(0)) >= 0) {
                double b = pila.pop();
                double a = pila.pop();
                double resultado;
                switch (token.charAt(0)) {
                    case '+': resultado = a + b; break;
                    case '-': resultado = a - b; break;
                    case '*': resultado = a * b; break;
                    case '/': resultado = a / b; break;
                    case '^': resultado = Math.pow(a, b); break;
                    default: throw new IllegalArgumentException("Operador inválido");
                }
                pila.push(resultado);
            } else {
                pila.push(Double.parseDouble(token));
            }
        }
        return pila.pop();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculadoraCientifica calc = new CalculadoraCientifica();
            calc.setVisible(true);
        });
    }
}
