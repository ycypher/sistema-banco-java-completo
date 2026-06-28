package banco.ui;

import banco.dao.ContaCorrenteDAO;
import banco.dao.ContaPoupancaDAO;
import banco.model.ContaBancaria;
import banco.model.ContaCorrente;
import banco.service.BancoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaOperacoes extends JDialog {

    private JTextField campoBusca, campoValorDep, campoValorSaque, campoDestino, campoValorTransf;
    private JLabel lblInfoConta, lblMsgDep, lblMsgSaque, lblMsgTransf, lblLimite;
    private JButton btnAplicarDep, btnAplicarSaque, btnAplicarTransf;
    private ContaBancaria contaAtual;
    private final BancoService bancoService = new BancoService();
    private final ContaCorrenteDAO ccDAO = new ContaCorrenteDAO();
    private final ContaPoupancaDAO cpDAO = new ContaPoupancaDAO();

    public TelaOperacoes(JFrame pai) {
        super(pai, "Operacoes", false);
        setSize(560, 540);
        setLocationRelativeTo(pai);
        construirInterface();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);
        painelPrincipal.add(ComponenteFactory.cabecalho("Operacoes Financeiras"), BorderLayout.NORTH);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        painelBusca.setBackground(Cores.BRANCO);
        painelBusca.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Cores.CINZA_BORDA));
        campoBusca = ComponenteFactory.campo();
        campoBusca.setPreferredSize(new Dimension(200, 32));
        JButton btnBuscar = ComponenteFactory.botaoPrimario("Buscar");
        lblInfoConta = new JLabel("Nenhuma conta selecionada.");
        lblInfoConta.setFont(Cores.NORMAL);
        lblInfoConta.setForeground(Color.GRAY);
        lblLimite = new JLabel("");
        lblLimite.setFont(Cores.NORMAL);
        btnBuscar.addActionListener(e -> buscarConta());
        painelBusca.add(ComponenteFactory.label("Numero da Conta:"));
        painelBusca.add(campoBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(lblInfoConta);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(Cores.SUBTITULO);
        abas.setBackground(Cores.BRANCO);
        abas.addTab("Deposito", construirAbaDeposito());
        abas.addTab("Saque", construirAbaSaque());
        abas.addTab("Transferencia", construirAbaTransferencia());

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(Cores.BRANCO);
        painelCentro.add(painelBusca, BorderLayout.NORTH);
        painelCentro.add(abas, BorderLayout.CENTER);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
        setOperacoesAtivas(false);
    }

    private JPanel construirAbaDeposito() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Cores.BRANCO);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        campoValorDep = ComponenteFactory.campo();
        lblMsgDep = ComponenteFactory.labelErro();
        btnAplicarDep = ComponenteFactory.botaoPrimario("Depositar");
        btnAplicarDep.addActionListener(e -> depositar());
        int y = 0;
        p.add(ComponenteFactory.label("Valor do Deposito (R$):"), ComponenteFactory.gbc(0, y, 1, false));
        p.add(campoValorDep, ComponenteFactory.gbc(1, y++, 1, true));
        p.add(btnAplicarDep, ComponenteFactory.gbc(1, y++, 1, false));
        p.add(lblMsgDep, ComponenteFactory.gbc(0, y, 2, true));
        return p;
    }

    private JPanel construirAbaSaque() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Cores.BRANCO);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        campoValorSaque = ComponenteFactory.campo();
        lblMsgSaque = ComponenteFactory.labelErro();
        lblLimite = new JLabel("");
        lblLimite.setFont(Cores.NORMAL);
        lblLimite.setForeground(Cores.AZUL_MEDIO);
        btnAplicarSaque = ComponenteFactory.botaoPrimario("Sacar");
        btnAplicarSaque.addActionListener(e -> sacar());
        int y = 0;
        p.add(lblLimite, ComponenteFactory.gbc(0, y++, 2, true));
        p.add(ComponenteFactory.label("Valor do Saque (R$):"), ComponenteFactory.gbc(0, y, 1, false));
        p.add(campoValorSaque, ComponenteFactory.gbc(1, y++, 1, true));
        p.add(btnAplicarSaque, ComponenteFactory.gbc(1, y++, 1, false));
        p.add(lblMsgSaque, ComponenteFactory.gbc(0, y, 2, true));
        return p;
    }

    private JPanel construirAbaTransferencia() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Cores.BRANCO);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        campoDestino = ComponenteFactory.campo();
        campoValorTransf = ComponenteFactory.campo();
        lblMsgTransf = ComponenteFactory.labelErro();
        btnAplicarTransf = ComponenteFactory.botaoPrimario("Transferir");
        btnAplicarTransf.addActionListener(e -> transferir());
        int y = 0;
        p.add(ComponenteFactory.label("Conta de Destino:"), ComponenteFactory.gbc(0, y, 1, false));
        p.add(campoDestino, ComponenteFactory.gbc(1, y++, 1, true));
        p.add(ComponenteFactory.label("Valor (R$):"), ComponenteFactory.gbc(0, y, 1, false));
        p.add(campoValorTransf, ComponenteFactory.gbc(1, y++, 1, true));
        p.add(btnAplicarTransf, ComponenteFactory.gbc(1, y++, 1, false));
        p.add(lblMsgTransf, ComponenteFactory.gbc(0, y, 2, true));
        return p;
    }

    private void buscarConta() {
        String numero = campoBusca.getText().trim();
        if (numero.isEmpty()) { lblInfoConta.setText("Informe o numero da conta."); return; }
        try {
            contaAtual = bancoService.buscarConta(numero);
            if (contaAtual == null) {
                lblInfoConta.setText("Conta nao encontrada.");
                setOperacoesAtivas(false);
                return;
            }
            String tipo = contaAtual instanceof ContaCorrente ? "Corrente" : "Poupanca";
            lblInfoConta.setText("<html><b>" + contaAtual.getTitular().getNome() + "</b> | " + tipo + " | Saldo: R$ " + String.format("%.2f", contaAtual.getSaldo()) + "</html>");
            lblInfoConta.setForeground(Cores.AZUL_ESCURO);
            if (contaAtual instanceof ContaCorrente cc) {
                lblLimite.setText("Limite disponivel: R$ " + String.format("%.2f", cc.getLimiteDisponivel()));
            } else {
                lblLimite.setText("");
            }
            setOperacoesAtivas(true);
        } catch (Exception ex) {
            lblInfoConta.setText("Erro: " + ex.getMessage());
        }
    }

    private void depositar() {
        if (contaAtual == null) return;
        try {
            double valor = Double.parseDouble(campoValorDep.getText().trim().replace(",", "."));
            contaAtual.depositar(valor);
            salvarEAtualizar(contaAtual);
            lblMsgDep.setForeground(Cores.VERDE);
            lblMsgDep.setText("Deposito de R$ " + String.format("%.2f", valor) + " realizado com sucesso!");
            campoValorDep.setText("");
            atualizarInfoConta();
        } catch (IllegalArgumentException ex) {
            lblMsgDep.setForeground(Cores.VERMELHO);
            lblMsgDep.setText(ex.getMessage());
        } catch (Exception ex) {
            lblMsgDep.setForeground(Cores.VERMELHO);
            lblMsgDep.setText("Valor invalido.");
        }
    }

    private void sacar() {
        if (contaAtual == null) return;
        try {
            double valor = Double.parseDouble(campoValorSaque.getText().trim().replace(",", "."));
            boolean ok = contaAtual.sacar(valor);
            if (!ok) {
                lblMsgSaque.setForeground(Cores.VERMELHO);
                lblMsgSaque.setText("Saldo insuficiente. Saque bloqueado.");
                return;
            }
            salvarEAtualizar(contaAtual);
            lblMsgSaque.setForeground(Cores.VERDE);
            lblMsgSaque.setText("Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!");
            campoValorSaque.setText("");
            atualizarInfoConta();
        } catch (IllegalArgumentException ex) {
            lblMsgSaque.setForeground(Cores.VERMELHO);
            lblMsgSaque.setText(ex.getMessage());
        } catch (Exception ex) {
            lblMsgSaque.setForeground(Cores.VERMELHO);
            lblMsgSaque.setText("Valor invalido.");
        }
    }

    private void transferir() {
        if (contaAtual == null) return;
        try {
            String numDestino = campoDestino.getText().trim();
            double valor = Double.parseDouble(campoValorTransf.getText().trim().replace(",", "."));
            ContaBancaria destino = bancoService.buscarConta(numDestino);
            if (destino == null) {
                lblMsgTransf.setForeground(Cores.VERMELHO);
                lblMsgTransf.setText("Conta de destino nao encontrada.");
                return;
            }
            if (destino.getNumeroConta().equals(contaAtual.getNumeroConta())) {
                lblMsgTransf.setForeground(Cores.VERMELHO);
                lblMsgTransf.setText("Conta de origem e destino sao iguais.");
                return;
            }
            bancoService.transferir(contaAtual, destino, valor);
            contaAtual = bancoService.buscarConta(contaAtual.getNumeroConta());
            lblMsgTransf.setForeground(Cores.VERDE);
            lblMsgTransf.setText("Transferencia de R$ " + String.format("%.2f", valor) + " realizada com sucesso!");
            campoValorTransf.setText("");
            campoDestino.setText("");
            atualizarInfoConta();
        } catch (IllegalArgumentException ex) {
            lblMsgTransf.setForeground(Cores.VERMELHO);
            lblMsgTransf.setText(ex.getMessage());
        } catch (Exception ex) {
            lblMsgTransf.setForeground(Cores.VERMELHO);
            lblMsgTransf.setText("Valor invalido ou erro: " + ex.getMessage());
        }
    }

    private void salvarEAtualizar(ContaBancaria conta) throws Exception {
        if (conta instanceof banco.model.ContaCorrente cc) {
            ccDAO.atualizar(cc);
        } else if (conta instanceof banco.model.ContaPoupanca cp) {
            cpDAO.atualizar(cp);
        }
    }

    private void atualizarInfoConta() {
        if (contaAtual == null) return;
        String tipo = contaAtual instanceof ContaCorrente ? "Corrente" : "Poupanca";
        lblInfoConta.setText("<html><b>" + contaAtual.getTitular().getNome() + "</b> | " + tipo + " | Saldo: R$ " + String.format("%.2f", contaAtual.getSaldo()) + "</html>");
        if (contaAtual instanceof ContaCorrente cc) {
            lblLimite.setText("Limite disponivel: R$ " + String.format("%.2f", cc.getLimiteDisponivel()));
        }
    }

    private void setOperacoesAtivas(boolean ativo) {
        if (btnAplicarDep != null) btnAplicarDep.setEnabled(ativo);
        if (btnAplicarSaque != null) btnAplicarSaque.setEnabled(ativo);
        if (btnAplicarTransf != null) btnAplicarTransf.setEnabled(ativo);
    }
}
