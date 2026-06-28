package banco.ui;

import banco.model.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaMenuPrincipal extends JFrame {

    private final Usuario usuarioLogado;

    public TelaMenuPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Sistema Bancario - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Cores.AZUL_ESCURO);
        cabecalho.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblBemVindo = new JLabel("Bem-vindo, " + usuarioLogado.getNome());
        lblBemVindo.setFont(Cores.TITULO);
        lblBemVindo.setForeground(Cores.BRANCO);

        JLabel lblPerfil = new JLabel("Perfil: " + usuarioLogado.getPerfil());
        lblPerfil.setFont(Cores.NORMAL);
        lblPerfil.setForeground(new Color(180, 200, 230));

        JPanel painelTextos = new JPanel();
        painelTextos.setLayout(new BoxLayout(painelTextos, BoxLayout.Y_AXIS));
        painelTextos.setOpaque(false);
        painelTextos.add(lblBemVindo);
        painelTextos.add(Box.createVerticalStrut(4));
        painelTextos.add(lblPerfil);

        JButton btnSair = ComponenteFactory.botaoSecundario("Sair");
        btnSair.addActionListener(e -> sair());

        cabecalho.add(painelTextos, BorderLayout.WEST);
        cabecalho.add(btnSair, BorderLayout.EAST);

        JPanel painelBotoes = new JPanel(new GridLayout(4, 2, 14, 14));
        painelBotoes.setBackground(Cores.CINZA_CLARO);
        painelBotoes.setBorder(new EmptyBorder(30, 40, 30, 40));

        if (usuarioLogado.isAdmin()) {
            JButton btnUsuarios = criarBotaoMenu("Gerenciar Usuarios", "\uD83D\uDC64");
            btnUsuarios.addActionListener(e -> new TelaGerenciarUsuarios(usuarioLogado).setVisible(true));
            painelBotoes.add(btnUsuarios);
        } else {
            painelBotoes.add(new JLabel());
        }

        JButton btnCliente = criarBotaoMenu("Cadastrar Cliente", "\uD83D\uDC68");
        btnCliente.addActionListener(e -> new TelaCadastroCliente(this).setVisible(true));

        JButton btnContaCorrente = criarBotaoMenu("Conta Corrente", "\uD83C\uDFE6");
        btnContaCorrente.addActionListener(e -> new TelaCadastroContaCorrente(this).setVisible(true));

        JButton btnContaPoupanca = criarBotaoMenu("Conta Poupanca", "\uD83D\uDCB0");
        btnContaPoupanca.addActionListener(e -> new TelaCadastroContaPoupanca(this).setVisible(true));

        JButton btnOperacoes = criarBotaoMenu("Operacoes", "\uD83D\uDCB3");
        btnOperacoes.addActionListener(e -> new TelaOperacoes(this).setVisible(true));

        JButton btnExtrato = criarBotaoMenu("Extrato da Conta", "\uD83D\uDCCB");
        btnExtrato.addActionListener(e -> new TelaExtrato(this).setVisible(true));

        JButton btnRelatorio = criarBotaoMenu("Relatorio Geral", "\uD83D\uDCCA");
        btnRelatorio.addActionListener(e -> new TelaRelatorio(this).setVisible(true));

        painelBotoes.add(btnCliente);
        painelBotoes.add(btnContaCorrente);
        painelBotoes.add(btnContaPoupanca);
        painelBotoes.add(btnOperacoes);
        painelBotoes.add(btnExtrato);
        painelBotoes.add(btnRelatorio);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);

        setContentPane(painelPrincipal);
    }

    private JButton criarBotaoMenu(String texto, String icone) {
        JButton btn = new JButton("<html><center>" + icone + "<br><b>" + texto + "</b></center></html>");
        btn.setFont(Cores.NORMAL);
        btn.setBackground(Cores.BRANCO);
        btn.setForeground(Cores.AZUL_ESCURO);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.CINZA_BORDA),
            BorderFactory.createEmptyBorder(18, 10, 18, 10)
        ));
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(Cores.AZUL_CLARO); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(Cores.BRANCO); }
        });
        return btn;
    }

    private void sair() {
        int op = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            new TelaLogin().setVisible(true);
            dispose();
        }
    }
}
