package banco.ui;

import banco.dao.ClienteDAO;
import banco.model.Cliente;
import banco.model.ContaPoupanca;
import banco.service.BancoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TelaCadastroContaPoupanca extends JDialog {

    private JTextField campoNumero, campoSaldo, campoTaxa;
    private JComboBox<Cliente> comboCliente;
    private JLabel lblMensagem;
    private final BancoService bancoService = new BancoService();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public TelaCadastroContaPoupanca(JFrame pai) {
        super(pai, "Cadastrar Conta Poupanca", true);
        setSize(430, 360);
        setLocationRelativeTo(pai);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho("Cadastrar Conta Poupanca"), BorderLayout.NORTH);

        JPanel form = ComponenteFactory.painelFormulario();

        comboCliente = new JComboBox<>();
        comboCliente.setFont(Cores.NORMAL);
        campoNumero = ComponenteFactory.campo();
        campoSaldo = ComponenteFactory.campo();
        campoTaxa = ComponenteFactory.campo();
        lblMensagem = ComponenteFactory.labelErro();

        carregarClientes();

        int y = 0;
        form.add(ComponenteFactory.label("Cliente:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(comboCliente, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Numero da Conta:"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoNumero, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Saldo Inicial (R$):"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoSaldo, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(ComponenteFactory.label("Taxa de Rendimento Mensal (%):"), ComponenteFactory.gbc(0, y, 1, false));
        form.add(campoTaxa, ComponenteFactory.gbc(1, y++, 1, true));
        form.add(lblMensagem, ComponenteFactory.gbc(0, y, 2, true));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(Cores.BRANCO);
        painelBotoes.setBorder(new EmptyBorder(0, 20, 15, 20));

        JButton btnCadastrar = ComponenteFactory.botaoPrimario("Cadastrar");
        JButton btnCancelar = ComponenteFactory.botaoSecundario("Cancelar");
        btnCadastrar.addActionListener(e -> cadastrar());
        btnCancelar.addActionListener(e -> dispose());
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnCadastrar);

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(Cores.BRANCO);
        painelCentro.add(form, BorderLayout.CENTER);
        painelCentro.add(painelBotoes, BorderLayout.SOUTH);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
    }

    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.buscarTodos();
            comboCliente.removeAllItems();
            for (Cliente c : clientes) comboCliente.addItem(c);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void cadastrar() {
        Cliente cliente = (Cliente) comboCliente.getSelectedItem();
        String numero = campoNumero.getText().trim();
        String saldoStr = campoSaldo.getText().trim().replace(",", ".");
        String taxaStr = campoTaxa.getText().trim().replace(",", ".");

        if (cliente == null) { lblMensagem.setText("Selecione um cliente."); return; }
        if (numero.isEmpty()) { lblMensagem.setText("Numero da conta obrigatorio."); return; }

        double saldo, taxa;
        try {
            saldo = saldoStr.isEmpty() ? 0 : Double.parseDouble(saldoStr);
            taxa = taxaStr.isEmpty() ? 0 : Double.parseDouble(taxaStr);
        } catch (NumberFormatException e) {
            lblMensagem.setText("Valores monetarios invalidos.");
            return;
        }

        try {
            ContaPoupanca cp = new ContaPoupanca(null, numero, cliente, saldo, taxa);
            bancoService.cadastrarContaPoupanca(cp);
            lblMensagem.setForeground(Cores.VERDE);
            lblMensagem.setText("Conta poupanca cadastrada com sucesso!");
            campoNumero.setText("");
            campoSaldo.setText("");
            campoTaxa.setText("");
        } catch (Exception ex) {
            lblMensagem.setForeground(Cores.VERMELHO);
            lblMensagem.setText(ex.getMessage());
        }
    }
}
