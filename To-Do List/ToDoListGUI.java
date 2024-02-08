import java.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ToDoListGUI extends JFrame {
    private JTextField taskInputField;
    private JButton addButton;
    private JButton deleteButton; // Novo botão para deletar
    private JList<String> taskList;
    private DefaultListModel<String> listModel;
    private Connection connection;

    public ToDoListGUI() {
        setTitle("To-Do List");
        setSize(300, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        taskInputField = new JTextField();
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete"); // Novo botão "Delete"
        taskList = new JList<>();
        listModel = new DefaultListModel<>();
        taskList.setModel(listModel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        inputPanel.add(deleteButton, BorderLayout.WEST); // Adicionando o botão "Delete" à esquerda

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskInputField.getText().trim();
                if (!task.isEmpty()) {
                    if (showConfirmationDialog("Are you sure you want to add this task?")) {
                        listModel.addElement(task);
                        taskInputField.setText("");
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    if (showConfirmationDialog("Are you sure you want to delete this task?")) {
                        listModel.remove(selectedIndex);
                    }
                }
            }
        });

        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JList<String> list = (JList<String>) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        if (showConfirmationDialog("Are you sure you want to delete this task?")) {
                            listModel.remove(index);
                        }
                    }
                }
            }
        });

        taskList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        if (showConfirmationDialog("Are you sure you want to delete this task?")) {
                            listModel.remove(selectedIndex);
                        }
                    }
                }
            }
        });

        // Adiciona um ActionListener para o campo de entrada para capturar a tecla
        // "Enter"
        taskInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskInputField.getText().trim();
                if (!task.isEmpty()) {
                    if (showConfirmationDialog("Are you sure you want to add this task?")) {
                        listModel.addElement(task);
                        taskInputField.setText("");
                    }
                }
            }
        });

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/seu_banco_de_dados", "seu_usuario",
                    "sua_senha");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar uma tarefa ao banco de dados
    private void addTaskToDatabase(String task) {
        String sql = "INSERT INTO tasks (task_description) VALUES (?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, task);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para remover uma tarefa do banco de dados
    private void deleteTaskFromDatabase(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, taskId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para carregar as tarefas do banco de dados para a lista
    private void loadTasksFromDatabase() {
        String sql = "SELECT * FROM tasks";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                listModel.addElement(resultSet.getString("task_description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para exibir uma caixa de diálogo de confirmação
    private boolean showConfirmationDialog(String message) {
        int option = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ToDoListGUI().setVisible(true);
            }
        });
    }
}
