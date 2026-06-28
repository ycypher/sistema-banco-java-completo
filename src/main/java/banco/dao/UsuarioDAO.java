package banco.dao;

import banco.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection getConn() {
        return ConexaoDB.getInstance().getConexao();
    }

    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT id, login, senha, nome, perfil FROM usuarios WHERE login = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public Usuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, login, senha, nome, perfil FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }

    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (login, senha, nome, perfil) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getSenha());
            ps.setString(3, u.getNome());
            ps.setString(4, u.getPerfil());
            ps.executeUpdate();
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, perfil = ? WHERE id = ?";
        if (u.getSenha() != null && !u.getSenha().isBlank()) {
            sql = "UPDATE usuarios SET nome = ?, perfil = ?, senha = ? WHERE id = ?";
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setString(1, u.getNome());
                ps.setString(2, u.getPerfil());
                ps.setString(3, u.getSenha());
                ps.setLong(4, u.getId());
                ps.executeUpdate();
                return;
            }
        }
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getPerfil());
            ps.setLong(3, u.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, login, senha, nome, perfil FROM usuarios ORDER BY id";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public boolean loginExiste(String login, Long ignorarId) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE login = ? AND id != ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setLong(2, ignorarId == null ? -1L : ignorarId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("login"),
            rs.getString("senha"),
            rs.getString("nome"),
            rs.getString("perfil")
        );
    }
}
