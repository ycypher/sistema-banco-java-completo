package banco.ui;

import banco.model.Usuario;
import banco.service.UsuarioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaCadastroUsuario extends JDialog {

    private final TelaGerenciarUsuarios telaPai;
    private final Usuario usuarioEdicao;
    private JTextField campoNome, campoLogin;
    private JPasswordField campoSenha, campoConfirmar;
    private JComboBox<String> comboPerfil;
    private JLabel lblMensagem;
    private final UsuarioService service = new UsuarioService();

    public TelaCadastroUsuario(TelaGerenciarUsuarios pai, Usuario u) {
        this.telaPai = pai;
        this.usuarioEdicao = u;
        setTitle(u == null ? "Novo Usuario" : "Editar Usuario");
        setSize(430, 430);
        setLocationRelativeTo(pai);
        setModal(true);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho(usuarioEdicao == null ? "Novo Usuario" : "Editar Usuario"), BorderLayout.NORTH);

        JPanel form = ComponenteFactory.painelFormulario();

        campoNome = ComponenteFactory.campo();
        campoLogin = ComponenteFactory.campo();
        campoSenha = ComponenteFactory.campaSenha();
        campoConfirmar = ComponenteFactory.campaSenha();
        comboPerfil = new JComboBox<>(new String[]{"ADMIN", "OPERADOR"});
        comboPerfil.setFont(Cores.NORMAL);
        lblMensagem = ComponenteFactory.labelErro();

        int y = 0;
        form.add(ComponenteFactory.label("Nome Completo:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoNome, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Login:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoLogin, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Senha:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoSenha, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Confirmar Senha:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoConfirmar, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Perfil:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(comboPerfil, ComponenteFactory.gbc(1, y++, 1, true));

        GridBagConstraints gbcMsg = ComponenteFactory.gbc(0, y++, 2, true);
        form.add(lblMensagem, gbcMsg);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(Cores.BRANCO);
        painelBotoes.setBorder(new EmptyBorder(0, 20, 15, 20));

        JButton btnSalvar = ComponenteFactory.botaoPrimario("Salvar");
        JButton btnCancelar = ComponenteFactory.botaoSecundario("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(Cores.BRANCO);
        painelCentro.add(form, BorderLayout.CENTER);
        painelCentro.add(painelBotoes, BorderLayout.SOUTH);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        setContentPane(painelPrincipal);

        if (usuarioEdicao != null) {
            campoNome.setText(usuarioEdicao.getNome());
            campoLogin.setText(usuarioEdicao.getLogin());
            comboPerfil.setSelectedItem(usuarioEdicao.getPerfil());
            lblMensagem.setForeground(Color.GRAY);
            lblMensagem.setText("Deixe a senha em branco para manter a atual.");
        }
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());
        String perfil = (String) comboPerfil.getSelectedItem();

        if (nome.isEmpty() || login.isEmpty()) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText("Nome e login sao obrigatorios.");
            return;
        }

        if (usuarioEdicao == null && senha.isEmpty()) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText("Senha obrigatoria para novo usuario.");
            return;
        }

        if (!senha.isEmpty() && !senha.equals(confirmar)) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText("As senhas nao coincidem.");
            return;
        }

        try {
            if (usuarioEdicao == null) {
                Usuario u = new Usuario(null, login, senha, nome, perfil);
                service.cadastrar(u);
            } else {
                usuarioEdicao.setNome(nome);
                usuarioEdicao.setLogin(login);
                usuarioEdicao.setPerfil(perfil);
                service.atualizar(usuarioEdicao, senha.isEmpty() ? null : senha);
            }
            telaPai.carregarDados();
            dispose();
        } catch (Exception ex) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText(ex.getMessage());
        }
    }
}
