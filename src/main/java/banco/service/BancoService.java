package banco.service;

import banco.dao.ContaCorrenteDAO;
import banco.dao.ContaPoupancaDAO;
import banco.dao.TransacaoDAO;
import banco.model.ContaBancaria;
import banco.model.ContaCorrente;
import banco.model.ContaPoupanca;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BancoService {

    private final ContaCorrenteDAO ccDAO = new ContaCorrenteDAO();
    private final ContaPoupancaDAO cpDAO = new ContaPoupancaDAO();

    public void cadastrarContaCorrente(ContaCorrente c) throws SQLException {
        if (ccDAO.numeroExiste(c.getNumeroConta()) || cpDAO.numeroExiste(c.getNumeroConta())) {
            throw new IllegalArgumentException("Numero de conta ja existe.");
        }
        ccDAO.salvar(c);
    }

    public void cadastrarContaPoupanca(ContaPoupanca c) throws SQLException {
        if (ccDAO.numeroExiste(c.getNumeroConta()) || cpDAO.numeroExiste(c.getNumeroConta())) {
            throw new IllegalArgumentException("Numero de conta ja existe.");
        }
        cpDAO.salvar(c);
    }

    public ContaBancaria buscarConta(String numero) throws SQLException {
        ContaCorrente cc = ccDAO.buscarPorNumero(numero);
        if (cc != null) return cc;
        return cpDAO.buscarPorNumero(numero);
    }

    public void persistirConta(ContaBancaria conta) throws SQLException {
        if (conta instanceof ContaCorrente cc) {
            ccDAO.atualizar(cc);
        } else if (conta instanceof ContaPoupanca cp) {
            cpDAO.atualizar(cp);
        }
    }

    public void transferir(ContaBancaria origem, ContaBancaria destino, double valor) throws SQLException {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero.");
        boolean sacou;
        if (origem instanceof ContaCorrente cc) {
            sacou = cc.sacar(valor);
        } else {
            sacou = origem.sacar(valor);
        }
        if (!sacou) throw new IllegalArgumentException("Saldo insuficiente para transferencia.");
        destino.depositar(valor);
        new TransacaoDAO().salvar(origem.getId(), origem.getTipoConta(), "TRANSFERENCIA",
                "Transferencia para conta " + destino.getNumeroConta(), valor);
        new TransacaoDAO().salvar(destino.getId(), destino.getTipoConta(), "TRANSFERENCIA",
                "Transferencia recebida de conta " + origem.getNumeroConta(), valor);
        persistirConta(origem);
        persistirConta(destino);
    }

    public double calcularPatrimonioTotal() throws SQLException {
        double total = 0;
        for (ContaCorrente cc : ccDAO.listarTodas()) total += cc.getSaldo();
        for (ContaPoupanca cp : cpDAO.listarTodas()) total += cp.getSaldo();
        return total;
    }

    public List<Object[]> exibirRelatorioGeral() throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        for (ContaCorrente cc : ccDAO.listarTodas()) {
            lista.add(new Object[]{cc.getNumeroConta(), cc.getTitular().getNome(), "Corrente", cc.getSaldo()});
        }
        for (ContaPoupanca cp : cpDAO.listarTodas()) {
            lista.add(new Object[]{cp.getNumeroConta(), cp.getTitular().getNome(), "Poupanca", cp.getSaldo()});
        }
        return lista;
    }

    public long totalContasCorrentes() throws SQLException { return ccDAO.listarTodas().size(); }
    public long totalContasPoupanca() throws SQLException { return cpDAO.listarTodas().size(); }
}
