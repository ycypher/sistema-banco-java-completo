package banco.dao;

import banco.model.Cliente;
import banco.model.ContaCorrente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaCorrenteDAO {

    private Connection getConn() {
        return ConexaoDB.getInstance().getConexao();
    }

    public void salvar(ContaCorrente c) throws SQLException {
        String sql = "INSERT INTO contas_correntes (numero_conta, saldo, limite_cheque, cliente_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getNumeroConta());
            ps.setDouble(2, c.getSaldo());
            ps.setDouble(3, c.getLimiteChequEspecial());
            ps.setLong(4, c.getTitular().getId());
            ps.executeUpdate();
        }
    }

    public ContaCorrente buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT cc.id, cc.numero_conta, cc.saldo, cc.limite_cheque, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_correntes cc JOIN clientes cl ON cc.cliente_id = cl.id " +
                     "WHERE cc.numero_conta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, numero);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public ContaCorrente buscarPorId(Long id) throws SQLException {
        String sql = "SELECT cc.id, cc.numero_conta, cc.saldo, cc.limite_cheque, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_correntes cc JOIN clientes cl ON cc.cliente_id = cl.id " +
                     "WHERE cc.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public List<ContaCorrente> listarTodas() throws SQLException {
        List<ContaCorrente> lista = new ArrayList<>();
        String sql = "SELECT cc.id, cc.numero_conta, cc.saldo, cc.limite_cheque, " +
                     "cl.id as cid, cl.nome, cl.cpf, cl.telefone " +
                     "FROM contas_correntes cc JOIN clientes cl ON cc.cliente_id = cl.id " +
                     "ORDER BY cc.numero_conta";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void atualizar(ContaCorrente c) throws SQLException {
        String sql = "UPDATE contas_correntes SET saldo = ?, limite_cheque = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDouble(1, c.getSaldo());
            ps.setDouble(2, c.getLimiteChequEspecial());
            ps.setLong(3, c.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM contas_correntes WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean numeroExiste(String numero) throws SQLException {
        String sql = "SELECT id FROM contas_correntes WHERE numero_conta = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, numero);
            return ps.executeQuery().next();
        }
    }

    private ContaCorrente mapear(ResultSet rs) throws SQLException {
        Cliente cl = new Cliente(rs.getLong("cid"), rs.getString("nome"), rs.getString("cpf"), rs.getString("telefone"));
        return new ContaCorrente(rs.getLong("id"), rs.getString("numero_conta"), cl, rs.getDouble("saldo"), rs.getDouble("limite_cheque"));
    }
}
