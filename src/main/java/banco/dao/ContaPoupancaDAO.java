package banco.dao;

import banco.model.Cliente;
import banco.model.ContaPoupanca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaPoupancaDAO {

    private Connection getConn() {
        return ConexaoDB.getInstance().getConexao();
    }

    public void salvar(ContaPoupanca c) throws SQLException {
        String sql = "INSERT INTO contas_poupanca (numero_conta, saldo, taxa_rendimento, cliente_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNumeroConta());
            ps.setDouble(2, c.getSaldo());
            ps.setDouble(3, c.getTaxaRendimentoMensal());
            ps.setLong(4, c.getTitular().getId());
            ps.executeUpdate();
        }
    }

    public ContaPoupanca buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT cp.id, cp.numero_conta, cp.saldo, cp.taxa_rendimento, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_poupanca cp JOIN clientes cl ON cp.cliente_id = cl.id " +
                     "WHERE cp.numero_conta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, numero);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public ContaPoupanca buscarPorId(Long id) throws SQLException {
        String sql = "SELECT cp.id, cp.numero_conta, cp.saldo, cp.taxa_rendimento, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_poupanca cp JOIN clientes cl ON cp.cliente_id = cl.id " +
                     "WHERE cp.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public List<ContaPoupanca> listarTodas() throws SQLException {
        List<ContaPoupanca> lista = new ArrayList<>();
        String sql = "SELECT cp.id, cp.numero_conta, cp.saldo, cp.taxa_rendimento, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_poupanca cp JOIN clientes cl ON cp.cliente_id = cl.id " +
                     "ORDER BY cp.numero_conta";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void atualizar(ContaPoupanca c) throws SQLException {
        String sql = "UPDATE contas_poupanca SET saldo = ?, taxa_rendimento = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, c.getSaldo());
            ps.setDouble(2, c.getTaxaRendimentoMensal());
            ps.setLong(3, c.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM contas_poupanca WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean numeroExiste(String numero) throws SQLException {
        String sql = "SELECT id FROM contas_poupanca WHERE numero_conta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, numero);
            return ps.executeQuery().next();
        }
    }

    private ContaPoupanca mapear(ResultSet rs) throws SQLException {
        Cliente cl = new Cliente(rs.getLong("cid"), rs.getString("nome"), rs.getString("cpf"), rs.getString("telefone"));
        return new ContaPoupanca(rs.getLong("id"), rs.getString("numero_conta"), cl, rs.getDouble("saldo"), rs.getDouble("taxa_rendimento"));
    }
}
