package banco.ui;

import banco.dao.ClienteDAO;
import banco.model.Cliente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class TelaCadastroCliente extends JDialog {

    private JTextField campoNome;
    private JFormattedTextField campoCpf, campoTelefone;
    private JLabel lblMensagem;
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public TelaCadastroCliente(JFrame pai) {
        super(pai, "Cadastrar Cliente", true);
        setSize(420, 360);
        setLocationRelativeTo(pai);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho("Cadastrar Cliente"), BorderLayout.NORTH);

        JPanel form = ComponenteFactory.painelFormulario();

        campoNome = ComponenteFactory.campo();
        lblMensagem = ComponenteFactory.labelErro();

        try {
            MaskFormatter maskCpf = new MaskFormatter("###.###.###-##");
            maskCpf.setPlaceholderCharacter('_');
            campoCpf = new JFormattedTextField(maskCpf);
            campoCpf.setFont(Cores.NORMAL);
            campoCpf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Cores.CINZA_BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

            MaskFormatter maskTel = new MaskFormatter("(##) #####-####");
            maskTel.setPlaceholderCharacter('_');
            campoTelefone = new JFormattedTextField(maskTel);
            campoTelefone.setFont(Cores.NORMAL);
            campoTelefone.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Cores.CINZA_BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        } catch (Exception e) {
            campoCpf = new JFormattedTextField();
            campoTelefone = new JFormattedTextField();
        }

        int y = 0;
        form.add(ComponenteFactory.label("Nome:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoNome, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("CPF:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoCpf, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Telefone:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoTelefone, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(lblMensagem, ComponenteFactory.gbc(0, y, 2, true));

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
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().replaceAll("[^0-9]", "");
        String telefone = campoTelefone.getText().trim();

        if (nome.isEmpty()) { lblMensagem.setText("Nome obrigatorio."); return; }
        if (cpf.length() != 11) { lblMensagem.setText("CPF invalido."); return; }
        if (!validarCpf(cpf)) { lblMensagem.setText("CPF invalido."); return; }

        try {
            if (clienteDAO.buscarPorCpf(campoCpf.getText().trim()) != null) {
                lblMensagem.setText("CPF ja cadastrado.");
                return;
            }
            Cliente c = new Cliente(null, nome, campoCpf.getText().trim(), telefone);
            clienteDAO.salvar(c);
            lblMensagem.setForeground(Cores.VERDE);
            lblMensagem.setText("Cliente cadastrado com sucesso!");
            campoNome.setText("");
            campoCpf.setValue(null);
            campoTelefone.setValue(null);
        } catch (Exception ex) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText("Erro: " + ex.getMessage());
        }
    }

    private boolean validarCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false;
        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
        int r1 = soma % 11;
        int d1 = r1 < 2 ? 0 : 11 - r1;
        if (d1 != (cpf.charAt(9) - '0')) return false;
        soma = 0;
        for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
        int r2 = soma % 11;
        int d2 = r2 < 2 ? 0 : 11 - r2;
        return d2 == (cpf.charAt(10) - '0');
    }
}
