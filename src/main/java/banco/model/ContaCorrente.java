package banco.model;

public class ContaCorrente extends ContaBancaria {
    private double limiteChequEspecial;

    public ContaCorrente() {}

    public ContaCorrente(Long id, String numeroConta, Cliente titular, double saldo, double limiteChequEspecial) {
        super(id, numeroConta, titular, saldo);
        this.limiteChequEspecial = limiteChequEspecial;
    }

    @Override
    public void depositar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        setSaldo(getSaldo() + valor);
        registrarTransacao("Deposito em conta corrente", valor, "DEPOSITO");
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        double limiteDisponivel = getSaldo() + limiteChequEspecial;
        if (valor > limiteDisponivel) return false;
        setSaldo(getSaldo() - valor);
        registrarTransacao("Saque em conta corrente", valor, "SAQUE");
        return true;
    }

    public double getLimiteDisponivel() {
        return getSaldo() + limiteChequEspecial;
    }

    @Override
    public String getTipoConta() { return "CORRENTE"; }

    public double getLimiteChequEspecial() { return limiteChequEspecial; }
    public void setLimiteChequEspecial(double limiteChequEspecial) { this.limiteChequEspecial = limiteChequEspecial; }
}
