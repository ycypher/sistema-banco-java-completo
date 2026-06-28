package banco.ui;

import banco.service.BancoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaRelatorio extends JDialog {

    private JLabel lblTotalCC, lblTotalCP, lblPatrimonio;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final BancoService bancoService = new BancoService();

    public TelaRelatorio(JFrame pai) {
        super(pai, "Relatorio Geral do Banco", false);
        setSize(700, 540);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarDados();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho("Relatorio Geral do Banco"), BorderLayout.NORTH);

        JPanel painelResumo = new JPanel(new GridLayout(1, 3, 10, 0));
        painelResumo.setBackground(Cores.CINZA_CLARO);
        painelResumo.setBorder(new EmptyBorder(12, 15, 12, 15));

        lblTotalCC = new JLabel("Contas Correntes: -");
        lblTotalCC.setFont(Cores.SUBTITULO);
        lblTotalCC.setForeground(Cores.AZUL_ESCURO);
        lblTotalCC.setHorizontalAlignment(SwingConstants.CENTER);

        lblTotalCP = new JLabel("Contas Poupanca: -");
        lblTotalCP.setFont(Cores.SUBTITULO);
        lblTotalCP.setForeground(Cores.AZUL_ESCURO);
        lblTotalCP.setHorizontalAlignment(SwingConstants.CENTER);

        lblPatrimonio = new JLabel("Patrimonio Total: R$ -");
        lblPatrimonio.setFont(Cores.SUBTITULO);
        lblPatrimonio.setForeground(Cores.VERDE);
        lblPatrimonio.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cardCC = criarCard(lblTotalCC);
        JPanel cardCP = criarCard(lblTotalCP);
        JPanel cardPat = criarCard(lblPatrimonio);

        painelResumo.add(cardCC);
        painelResumo.add(cardCP);
        painelResumo.add(cardPat);

        String[] colunas = {"Numero", "Titular", "Tipo", "Saldo (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setDefaultRenderer(Object.class, new RendererDestaque());

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBackground(Cores.BRANCO);
        painelTabela.setBorder(new EmptyBorder(0, 15, 10, 15));
        painelTabela.add(ComponenteFactory.tabelaScroll(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(Cores.CINZA_CLARO);
        painelBotoes.setBorder(new EmptyBorder(5, 15, 10, 15));
        JButton btnAtualizar = ComponenteFactory.botaoPrimario("Atualizar");
        btnAtualizar.addActionListener(e -> carregarDados());
        painelBotoes.add(btnAtualizar);

        painelPrincipal.add(painelResumo, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        setContentPane(painelPrincipal);
    }

    private JPanel criarCard(JLabel conteudo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Cores.BRANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Cores.CINZA_BORDA),
            new EmptyBorder(12, 10, 12, 10)
        ));
        card.add(conteudo, BorderLayout.CENTER);
        return card;
    }

    private void carregarDados() {
        try {
            modeloTabela.setRowCount(0);
            long totalCC = bancoService.totalContasCorrentes();
            long totalCP = bancoService.totalContasPoupanca();
            double patrimonio = bancoService.calcularPatrimonioTotal();
            lblTotalCC.setText("Contas Correntes: " + totalCC);
            lblTotalCP.setText("Contas Poupanca: " + totalCP);
            lblPatrimonio.setText("Patrimonio: R$ " + String.format("%.2f", patrimonio));

            List<Object[]> dados = bancoService.exibirRelatorioGeral();
            double maior = Double.MIN_VALUE, menor = Double.MAX_VALUE;
            for (Object[] d : dados) {
                double saldo = (Double) d[3];
                if (saldo > maior) maior = saldo;
                if (saldo < menor) menor = saldo;
            }
            for (Object[] d : dados) {
                modeloTabela.addRow(new Object[]{d[0], d[1], d[2], String.format("%.2f", (Double) d[3])});
            }
            ((RendererDestaque) tabela.getDefaultRenderer(Object.class)).setMaiorMenor(maior, menor, dados);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar relatorio: " + ex.getMessage());
        }
    }

    private static class RendererDestaque extends DefaultTableCellRenderer {
        private double maior = Double.MIN_VALUE;
        private double menor = Double.MAX_VALUE;
        private final java.util.List<Object[]> dados = new java.util.ArrayList<>();

        public void setMaiorMenor(double maior, double menor, List<Object[]> dados) {
            this.maior = maior;
            this.menor = menor;
            this.dados.clear();
            this.dados.addAll(dados);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected && row < dados.size()) {
                double saldo = (Double) dados.get(row)[3];
                if (saldo == maior) {
                    c.setBackground(Cores.VERDE_DESTAQUE);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (saldo == menor) {
                    c.setBackground(Cores.AMARELO_DESTAQUE);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(Cores.BRANCO);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            return c;
        }
    }
}
