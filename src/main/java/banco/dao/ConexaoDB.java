package banco.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexaoDB {
    private static ConexaoDB instancia;
    private Connection conexao;

    private ConexaoDB() {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (is == null) throw new RuntimeException("Arquivo db.properties nao encontrado.");
            props.load(is);
            String url = props.getProperty("db.url");
            String usuario = props.getProperty("db.usuario");
            String senha = props.getProperty("db.senha");
            conexao = DriverManager.getConnection(url, usuario, senha);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static ConexaoDB getInstance() {
        if (instancia == null) {
            instancia = new ConexaoDB();
        }
        return instancia;
    }

    public Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                instancia = new ConexaoDB();
                return instancia.conexao;
            }
        } catch (Exception e) {
            instancia = new ConexaoDB();
            return instancia.conexao;
        }
        return conexao;
    }
}
