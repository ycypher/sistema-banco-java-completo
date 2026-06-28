package banco.ui;

import banco.model.Usuario;
import banco.service.UsuarioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaGerenciarUsuarios extends JDialog {

    private final Usuario usuarioLogado;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final UsuarioService service = new UsuarioService();

    public TelaGerenciarUsuarios(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setTitle("Gerenciar Usuarios");
        setSize(700, 460);
        setLocationRelativeTo(null);
        setModal(false);
        construirInterface();
        carregarDados();
    }

    private void construirInterface() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Cores.CINZA_CLARO);

        painelPrincipal.add(ComponenteFactory.cabecalho("Gerenciar Usuarios"), BorderLayout.NORTH);

        String[] colunas = {"ID", "Login", "Nome", "Perfil"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelBotoes.setBackground(Cores.CINZA_CLARO);

        JButton btnNovo = ComponenteFactory.botaoPrimario("Novo Usuario");
        JButton btnEditar = ComponenteFactory.botaoSecundario("Editar");
        JButton btnExcluir = ComponenteFactory.botaoDanger("Excluir");

        btnNovo.addActionListener(e -> {
            TelaCadastroUsuario tela = new TelaCadastroUsuario(this, null);
            tela.setVisible(true);
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuario."); return; }
            Long id = (Long) modeloTabela.getValueAt(linha, 0);
            try {
                List<Usuario> lista = service.listarTodos();
                Usuario u = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
                if (u != null) new TelaCadastroUsuario(this, u).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione um usuario."); return; }
            Long id = (Long) modeloTabela.getValueAt(linha, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try {
                    service.excluir(id);
                    carregarDados();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        JPanel painelCentro = new JPanel(new BorderLayout());
        painelCentro.setBackground(Cores.CINZA_CLARO);
        painelCentro.setBorder(new EmptyBorder(10, 15, 10, 15));
        painelCentro.add(ComponenteFactory.tabelaScroll(tabela), BorderLayout.CENTER);
        painelCentro.add(painelBotoes, BorderLayout.SOUTH);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        setContentPane(painelPrincipal);
    }

    public void carregarDados() {
        try {
            modeloTabela.setRowCount(0);
            for (Usuario u : service.listarTodos()) {
                modeloTabela.addRow(new Object[]{u.getId(), u.getLogin(), u.getNome(), u.getPerfil()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar usuarios: " + e.getMessage());
        }
    }
}
