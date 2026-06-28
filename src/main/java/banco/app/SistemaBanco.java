package banco.app;

import banco.dao.InicializadorBanco;
import banco.ui.TelaLogin;
import javax.swing.*;

public class SistemaBanco {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                InicializadorBanco.inicializar();
                new TelaLogin().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar o sistema:\n" + e.getMessage() +
                    "\n\nVerifique o arquivo src/main/resources/db.properties",
                    "Erro de Inicializacao",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
