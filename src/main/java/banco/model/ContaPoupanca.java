package banco.model;

public class ContaPoupanca extends ContaBancaria {
    private double taxaRendimentoMensal;

    public ContaPoupanca() {}

    public ContaPoupanca(Long id, String numeroConta, Cliente titular, double saldo, double taxaRendimentoMensal) {
        super(id, numeroConta, titular, saldo);
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        setSaldo(getSaldo() + valor);
        registrarTransacao("Deposito em conta poupanca", valor, "DEPOSITO");
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        if (valor > getSaldo()) return false;
        setSaldo(getSaldo() - valor);
        registrarTransacao("Saque em conta poupanca", valor, "SAQUE");
        return true;
    }

    public double calcularRendimento() {
        return getSaldo() * taxaRendimentoMensal / 100.0;
    }

    public void aplicarRendimento() {
        double rendimento = calcularRendimento();
        setSaldo(getSaldo() + rendimento);
        registrarTransacao("Rendimento mensal aplicado", rendimento, "RENDIMENTO");
    }

    @Override
    public String getTipoConta() { return "POUPANCA"; }

    public double getTaxaRendimentoMensal() { return taxaRendimentoMensal; }
    public void setTaxaRendimentoMensal(double taxaRendimentoMensal) { this.taxaRendimentoMensal = taxaRendimentoMensal; }
}
