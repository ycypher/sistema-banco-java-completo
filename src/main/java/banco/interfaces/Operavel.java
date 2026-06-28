package banco.interfaces;

public interface Operavel {
    void depositar(double valor);
    boolean sacar(double valor);
    void exibirSaldo(javax.swing.JLabel label);
}
