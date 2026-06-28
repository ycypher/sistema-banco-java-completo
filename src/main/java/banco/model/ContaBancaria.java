package banco.model;

import banco.dao.TransacaoDAO;
import banco.interfaces.Operavel;
import javax.swing.JLabel;
import java.util.List;

public abstract class ContaBancaria implements Operavel {
    private Long id;
    private String numeroConta;
    private Cliente titular;
    private double saldo;

    public ContaBancaria() {}

    public ContaBancaria(Long id, String numeroConta, Cliente titular, double saldo) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.saldo = saldo;
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        saldo += valor;
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        if (saldo < valor) return false;
        saldo -= valor;
        return true;
    }

    @Override
    public void exibirSaldo(JLabel label) {
        label.setText(String.format("Saldo: R$ %.2f", saldo));
    }

    protected void registrarTransacao(String descricao, double valor, String tipo) {
        try {
            TransacaoDAO dao = new TransacaoDAO();
            dao.salvar(id, getTipoConta(), tipo, descricao, valor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String[]> gerarExtrato() {
        try {
            TransacaoDAO dao = new TransacaoDAO();
            return dao.buscarPorConta(id, getTipoConta());
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public abstract String getTipoConta();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public Cliente getTitular() { return titular; }
    public void setTitular(Cliente titular) { this.titular = titular; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
