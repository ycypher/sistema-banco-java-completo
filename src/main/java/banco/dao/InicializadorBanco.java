package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InicializadorBanco {

    public static void inicializar() {
        criarTabelas();
        inserirUsuarioInicial();
    }

    private static void criarTabelas() {
        Connection conn = ConexaoDB.getInstance().getConexao();
        try (Statement st = conn.createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id SERIAL PRIMARY KEY," +
                "login VARCHAR(100) UNIQUE NOT NULL," +
                "senha VARCHAR(64) NOT NULL," +
                "nome VARCHAR(200) NOT NULL," +
                "perfil VARCHAR(20) NOT NULL" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS clientes (" +
                "id SERIAL PRIMARY KEY," +
                "nome VARCHAR(200) NOT NULL," +
                "cpf VARCHAR(14) UNIQUE NOT NULL," +
                "telefone VARCHAR(20)" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS contas_correntes (" +
                "id SERIAL PRIMARY KEY," +
                "numero_conta VARCHAR(20) UNIQUE NOT NULL," +
                "saldo NUMERIC(15,2) NOT NULL DEFAULT 0," +
                "limite_cheque NUMERIC(15,2) NOT NULL DEFAULT 0," +
                "cliente_id INTEGER REFERENCES clientes(id)" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS contas_poupanca (" +
                "id SERIAL PRIMARY KEY," +
                "numero_conta VARCHAR(20) UNIQUE NOT NULL," +
                "saldo NUMERIC(15,2) NOT NULL DEFAULT 0," +
                "taxa_rendimento NUMERIC(10,4) NOT NULL DEFAULT 0," +
                "cliente_id INTEGER REFERENCES clientes(id)" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS transacoes (" +
                "id SERIAL PRIMARY KEY," +
                "conta_id INTEGER NOT NULL," +
                "tipo_conta VARCHAR(10) NOT NULL," +
                "tipo VARCHAR(20) NOT NULL," +
                "descricao VARCHAR(255)," +
                "valor NUMERIC(15,2) NOT NULL," +
                "data_hora TIMESTAMP NOT NULL DEFAULT NOW()" +
                ")"
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar tabelas: " + e.getMessage(), e);
        }
    }

    private static void inserirUsuarioInicial() {
        Connection conn = ConexaoDB.getInstance().getConexao();
        String senhaHash = banco.service.UsuarioService.hashSHA256("20252021847");
        String sql = "INSERT INTO usuarios (login, senha, nome, perfil) " +
                     "VALUES (?, ?, ?, 'ADMIN') ON CONFLICT (login) DO NOTHING";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "victorhugo");
            ps.setString(2, senhaHash);
            ps.setString(3, "Victor Hugo Rodrigues Silverio");
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir usuario inicial: " + e.getMessage(), e);
        }
    }
}
