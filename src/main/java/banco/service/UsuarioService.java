package banco.service;

import banco.dao.UsuarioDAO;
import banco.model.Usuario;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();
    private static final String LOGIN_ADMIN = "victorhugo";

    public static String hashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash: " + e.getMessage());
        }
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        Usuario u = dao.buscarPorLogin(login);
        if (u == null) return null;
        String hash = hashSHA256(senha);
        return hash.equals(u.getSenha()) ? u : null;
    }

    public void cadastrar(Usuario u) throws SQLException {
        if (u.getNome() == null || u.getNome().isBlank()) throw new IllegalArgumentException("Nome obrigatorio.");
        if (u.getLogin() == null || u.getLogin().isBlank()) throw new IllegalArgumentException("Login obrigatorio.");
        if (u.getSenha() == null || u.getSenha().isBlank()) throw new IllegalArgumentException("Senha obrigatoria.");
        if (dao.loginExiste(u.getLogin(), null)) throw new IllegalArgumentException("Login ja existe.");
        u.setSenha(hashSHA256(u.getSenha()));
        dao.salvar(u);
    }

    public void atualizar(Usuario u, String novaSenha) throws SQLException {
        if (u.getNome() == null || u.getNome().isBlank()) throw new IllegalArgumentException("Nome obrigatorio.");
        if (dao.loginExiste(u.getLogin(), u.getId())) throw new IllegalArgumentException("Login ja existe.");
        if (novaSenha != null && !novaSenha.isBlank()) {
            u.setSenha(hashSHA256(novaSenha));
        } else {
            u.setSenha(null);
        }
        dao.atualizar(u);
    }

    public void excluir(Long id) throws SQLException {
        Usuario u = dao.buscarPorId(id);
        if (u != null && LOGIN_ADMIN.equals(u.getLogin())) {
            throw new IllegalArgumentException("O usuario administrador inicial nao pode ser excluido.");
        }
        dao.excluir(id);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }
}
