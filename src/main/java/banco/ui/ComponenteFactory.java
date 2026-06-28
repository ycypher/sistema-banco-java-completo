package banco.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ComponenteFactory {

    public static JButton botaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Cores.AZUL_MEDIO);
        btn.setForeground(Cores.BRANCO);
        btn.setFont(Cores.BOTAO);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton botaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Cores.CINZA_CLARO);
        btn.setForeground(Cores.AZUL_ESCURO);
        btn.setFont(Cores.BOTAO);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.CINZA_BORDA),
            BorderFactory.createEmptyBorder(7, 17, 7, 17)
        ));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton botaoDanger(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Cores.VERMELHO);
        btn.setForeground(Cores.BRANCO);
        btn.setFont(Cores.BOTAO);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    public static JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(Cores.NORMAL);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.CINZA_BORDA),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    public static JPasswordField campaSenha() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(Cores.NORMAL);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.CINZA_BORDA),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return pf;
    }

    public static JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Cores.NORMAL);
        lbl.setForeground(Cores.AZUL_ESCURO);
        return lbl;
    }

    public static JLabel labelErro() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(Cores.NORMAL);
        lbl.setForeground(Cores.VERMELHO);
        return lbl;
    }

    public static JLabel labelSucesso() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(Cores.NORMAL);
        lbl.setForeground(Cores.VERDE);
        return lbl;
    }

    public static JPanel painelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Cores.BRANCO);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        return p;
    }

    public static GridBagConstraints gbc(int x, int y, int w, boolean fill) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x; g.gridy = y; g.gridwidth = w;
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = fill ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        g.weightx = fill ? 1.0 : 0;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    public static JScrollPane tabelaScroll(JTable tabela) {
        tabela.setFont(Cores.NORMAL);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setFont(Cores.SUBTITULO);
        tabela.getTableHeader().setBackground(Cores.AZUL_ESCURO);
        tabela.getTableHeader().setForeground(Cores.BRANCO);
        tabela.setSelectionBackground(Cores.AZUL_CLARO);
        tabela.setGridColor(Cores.CINZA_BORDA);
        tabela.setShowGrid(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane sp = new JScrollPane(tabela);
        sp.setBorder(BorderFactory.createLineBorder(Cores.CINZA_BORDA));
        return sp;
    }

    public static JPanel cabecalho(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Cores.AZUL_ESCURO);
        p.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(Cores.TITULO);
        lbl.setForeground(Cores.BRANCO);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }
}
