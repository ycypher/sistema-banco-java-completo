package banco.ui;

import banco.model.Usuario;
import banco.service.UsuarioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TelaLogin extends JFrame {

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;
    private int tentativasFalhas = 0;
    private Timer timerBloqueio;
    private int segundosRestantes;

    public TelaLogin() {
        setTitle("Sistema Bancario - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(Cores.AZUL_ESCURO);
        painelTopo.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblBanco = new JLabel("Sistema Bancario");
        lblBanco.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblBanco.setForeground(Cores.BRANCO);
        lblBanco.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSub = new JLabel("CEFET-MG - Campo Belo");
        lblSub.setFont(Cores.NORMAL);
        lblSub.setForeground(new Color(180, 200, 230));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel painelTextos = new JPanel();
        painelTextos.setLayout(new BoxLayout(painelTextos, BoxLayout.Y_AXIS));
        painelTextos.setOpaque(false);
        lblBanco.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTextos.add(lblBanco);
        painelTextos.add(Box.createVerticalStrut(6));
        painelTextos.add(lblSub);
        painelTopo.add(painelTextos, BorderLayout.CENTER);

        JPanel painelForm = new JPanel();
        painelForm.setLayout(new BoxLayout(painelForm, BoxLayout.Y_AXIS));
        painelForm.setBackground(Cores.BRANCO);
        painelForm.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblLogin = ComponenteFactory.label("Login");
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoLogin = ComponenteFactory.campo();
        campoLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campoLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSenha = ComponenteFactory.label("Senha");
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoSenha = ComponenteFactory.campaSenha();
        campoSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campoSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnEntrar = ComponenteFactory.botaoPrimario("Entrar");
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        lblMensagem = ComponenteFactory.labelErro();
        lblMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelForm.add(lblLogin);
        painelForm.add(Box.createVerticalStrut(5));
        painelForm.add(campoLogin);
        painelForm.add(Box.createVerticalStrut(15));
        painelForm.add(lblSenha);
        painelForm.add(Box.createVerticalStrut(5));
        painelForm.add(campoSenha);
        painelForm.add(Box.createVerticalStrut(20));
        painelForm.add(btnEntrar);
        painelForm.add(Box.createVerticalStrut(12));
        painelForm.add(lblMensagem);

        JPanel painelRodape = new JPanel();
        painelRodape.setBackground(Cores.CINZA_CLARO);
        painelRodape.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel lblRodape = new JLabel("POO - Programacao Orientada a Objetos - 2 Modulo");
        lblRodape.setFont(Cores.PEQUENA);
        lblRodape.setForeground(Color.GRAY);
        painelRodape.add(lblRodape);

        painelPrincipal.add(painelTopo, BorderLayout.NORTH);
        painelPrincipal.add(painelForm, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);

        btnEntrar.addActionListener((ActionEvent e) -> realizarLogin());
        campoSenha.addActionListener((ActionEvent e) -> realizarLogin());
        campoLogin.addActionListener((ActionEvent e) -> campoSenha.requestFocus());
    }

    private void realizarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Preencha login e senha.");
            return;
        }

        try {
            UsuarioService service = new UsuarioService();
            Usuario usuario = service.autenticar(login, senha);
            if (usuario != null) {
                tentativasFalhas = 0;
                new TelaMenuPrincipal(usuario).setVisible(true);
                dispose();
            } else {
                tentativasFalhas++;
                if (tentativasFalhas >= 3) {
                    bloquearBotao();
                } else {
                    lblMensagem.setText("Login ou senha incorretos. Tentativa " + tentativasFalhas + " de 3.");
                }
            }
        } catch (Exception ex) {
            lblMensagem.setText("Erro ao conectar: " + ex.getMessage());
        }
    }

    private void bloquearBotao() {
        btnEntrar.setEnabled(false);
        segundosRestantes = 30;
        timerBloqueio = new Timer(1000, e -> {
            segundosRestantes--;
            if (segundosRestantes <= 0) {
                timerBloqueio.stop();
                btnEntrar.setEnabled(true);
                tentativasFalhas = 0;
                lblMensagem.setText("Voce pode tentar novamente.");
                btnEntrar.setText("Entrar");
            } else {
                btnEntrar.setText("Aguarde " + segundosRestantes + "s");
                lblMensagem.setText("Muitas tentativas falhas. Aguarde " + segundosRestantes + " segundos.");
            }
        });
        timerBloqueio.start();
    }
}
