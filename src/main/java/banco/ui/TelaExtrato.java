package banco.ui;

import banco.dao.ContaCorrenteDAO;
import banco.dao.ContaPoupancaDAO;
import banco.model.ContaBancaria;
import banco.model.ContaCorrente;
import banco.model.ContaPoupanca;
import banco.service.BancoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaExtrato extends JDialog {

    private JTextField campoBusca;
    private JLabel lblInfo, lblExtra, lblMsg;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnRendimento;
    private ContaBancaria contaAtual;
    private final BancoService bancoService = new BancoService();
    private final ContaCorrenteDAO ccDAO = new ContaCorrenteDAO();
    private final ContaPoupancaDAO cpDAO = new ContaPoupancaDAO();

    public TelaExtrato(JFrame pai) {
        super(pai, "Extrato da Conta", false);
        setSize(680, 560);
        setLocationRelativeTo(pai);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho("Extrato da Conta"), BorderLayout.NORTH);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        painelBusca.setBackground(Cores.BRANCO);
        painelBusca.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Cores.CINZA_BORDA));
        campoBusca = ComponenteFactory.campo();
        campoBusca.setPreferredSize(new Dimension(180, 32));
        JButton btnBuscar = ComponenteFactory.botaoPrimario("Buscar");
        btnBuscar.addActionListener(e -> buscarConta());
        painelBusca.add(ComponenteFactory.label("Numero da Conta:"));
        painelBusca.add(campoBusca);
        painelBusca.add(btnBuscar);

        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setBackground(Cores.AZUL_CLARO);
        painelInfo.setBorder(new EmptyBorder(10, 15, 10, 15));
        lblInfo = new JLabel("Busque uma conta para ver o extrato.");
        lblInfo.setFont(Cores.SUBTITULO);
        lblInfo.setForeground(Cores.AZUL_ESCURO);
        lblExtra = new JLabel(" ");
        lblExtra.setFont(Cores.NORMAL);
        lblExtra.setForeground(Cores.AZUL_MEDIO);
        painelInfo.add(lblInfo);
        painelInfo.add(Box.createVerticalStrut(4));
        painelInfo.add(lblExtra);

        String[] colunas = {"Data/Hora", "Tipo", "Descricao", "Valor"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Cores.BRANCO);
        painelTabela.setBorder(new EmptyBorder(10, 15, 10, 15));
        painelTabela.add(ComponenteFactory.tabelaScroll(tabela), BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        painelRodape.setBackground(Cores.BRANCO);
        painelRodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Cores.CINZA_BORDA));
        btnRendimento = ComponenteFactory.botaoPrimario("Aplicar Rendimento");
        btnRendimento.setVisible(false);
        lblMsg = ComponenteFactory.labelErro();
        btnRendimento.addActionListener(e -> aplicarRendimento());
        painelRodape.add(btnRendimento);
        painelRodape.add(lblMsg);

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(Cores.BRANCO);
        painelCentro.add(painelBusca, BorderLayout.NORTH);
        painelCentro.add(painelInfo, BorderLayout.CENTER);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.setBackground(Cores.BRANCO);
        painelSul.add(painelTabela, BorderLayout.CENTER);
        painelSul.add(painelRodape, BorderLayout.SOUTH);

        painelPrincipal.add(painelCentro, BorderLayout.NORTH);
        painelPrincipal.add(painelSul, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
    }

    private void buscarConta() {
        String numero = campoBusca.getText().trim();
        if (numero.isEmpty()) return;
        try {
            contaAtual = bancoService.buscarConta(numero);
            if (contaAtual == null) {
                lblInfo.setText("Conta nao encontrada.");
                lblExtra.setText(" ");
                modeloTabela.setRowCount(0);
                btnRendimento.setVisible(false);
                return;
            }
            atualizarInfo();
            carregarTransacoes();
            btnRendimento.setVisible(contaAtual instanceof ContaPoupanca);
        } catch (Exception ex) {
            lblInfo.setText("Erro: " + ex.getMessage());
        }
    }

    private void atualizarInfo() {
        if (contaAtual == null) return;
        String tipo = contaAtual instanceof ContaCorrente ? "Corrente" : "Poupanca";
        lblInfo.setText(contaAtual.getTitular().getNome() + " | Conta " + tipo + " " + contaAtual.getNumeroConta() + " | Saldo: R$ " + String.format("%.2f", contaAtual.getSaldo()));
        if (contaAtual instanceof ContaCorrente cc) {
            lblExtra.setText("Limite Cheque Especial: R$ " + String.format("%.2f", cc.getLimiteChequEspecial()) + " | Limite Disponivel: R$ " + String.format("%.2f", cc.getLimiteDisponivel()));
        } else if (contaAtual instanceof ContaPoupanca cp) {
            lblExtra.setText("Taxa de Rendimento: " + cp.getTaxaRendimentoMensal() + "% | Rendimento Estimado Proximo Mes: R$ " + String.format("%.2f", cp.calcularRendimento()));
        }
    }

    private void carregarTransacoes() {
        modeloTabela.setRowCount(0);
        List<String[]> extrato = contaAtual.gerarExtrato();
        for (String[] linha : extrato) {
            modeloTabela.addRow(linha);
        }
    }

    private void aplicarRendimento() {
        if (!(contaAtual instanceof ContaPoupanca cp)) return;
        try {
            cp.aplicarRendimento();
            cpDAO.atualizar(cp);
            lblMsg.setForeground(Cores.VERDE);
            lblMsg.setText("Rendimento aplicado! Novo saldo: R$ " + String.format("%.2f", cp.getSaldo()));
            atualizarInfo();
            carregarTransacoes();
        } catch (Exception ex) {
            lblMsg.setForeground(Cores.VERMELHO);
            lblMsg.setText("Erro: " + ex.getMessage());
        }
    }
}
