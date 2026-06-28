package banco.model;

public class Usuario {
    private Long id;
    private String login;
    private String senha;
    private String nome;
    private String perfil;

    public Usuario() {}

    public Usuario(Long id, String login, String senha, String nome, String perfil) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.perfil = perfil;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public boolean isAdmin() { return "ADMIN".equals(perfil); }

    @Override
    public String toString() { return nome + " (" + login + ")"; }
}
