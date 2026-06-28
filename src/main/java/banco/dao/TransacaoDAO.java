package banco.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {

    private Connection getConn() {
        return ConexaoDB.getInstance().getConexao();
    }

    public void salvar(Long contaId, String tipoConta, String tipo, String descricao, double valor) throws SQLException {
        String sql = "INSERT INTO transacoes (conta_id, tipo_conta, tipo, descricao, valor, data_hora) VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, contaId);
            ps.setString(2, tipoConta);
            ps.setString(3, tipo);
            ps.setString(4, descricao);
            ps.setDouble(5, valor);
            ps.executeUpdate();
        }
    }

    public List<String[]> buscarPorConta(Long contaId, String tipoConta) throws SQLException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT data_hora, tipo, descricao, valor FROM transacoes " +
                     "WHERE conta_id = ? AND tipo_conta = ? ORDER BY data_hora DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, contaId);
            ps.setString(2, tipoConta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getTimestamp("data_hora").toString(),
                    rs.getString("tipo"),
                    rs.getString("descricao"),
                    String.format("R$ %.2f", rs.getDouble("valor"))
                });
            }
        }
        return lista;
    }
}
