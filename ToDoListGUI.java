import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ToDoListGUI {

    private static DefaultListModel<String> taskListModel = new DefaultListModel<>();
    private static JList<String> taskList = new JList<>(taskListModel);
    private static File file = new File("tasks.txt");

    public static void main(String[] args) {
        // Load tasks from file
        loadTasks();

        // --- Main Window ---
        JFrame frame = new JFrame("My To-Do List App");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // --- Task List with custom renderer ---
        taskList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scrollPane = new JScrollPane(taskList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Input field and Add button ---
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        JTextField taskField = new JTextField();
        JButton addButton = new JButton("Add Task");
        inputPanel.add(taskField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        // --- Action buttons ---
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        JButton markDoneButton = new JButton("Mark Done");
        JButton deleteButton = new JButton("Delete Task");
        actionPanel.add(markDoneButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);

        // --- Button Actions ---

        // Add Task
        addButton.addActionListener(e -> {
            String task = taskField.getText().trim();
            if (!task.isEmpty()) {
                taskListModel.addElement(formatTask(task, false)); // new task with cross
                taskField.setText("");
                updateListNumbers();
                saveTasks();
            }
        });

        // Mark Done
        markDoneButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                String task = taskListModel.getElementAt(selectedIndex);
                taskListModel.set(selectedIndex, formatTask(task, true)); // add tick
                updateListNumbers();
                saveTasks();
            }
        });

        // Delete Task
        deleteButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.remove(selectedIndex);
                updateListNumbers();
                saveTasks();
            }
        });
    }

    // --- Helper method to format tasks ---
    private static String formatTask(String task, boolean done) {
        task = task.replaceAll("\\[✔\\]|\\[✘\\]", "").trim(); // remove old symbols
        if (done) {
            return task + " [✔]";
        } else {
            return task + " [✘]";
        }
    }

    // --- Update numbering ---
    private static void updateListNumbers() {
        for (int i = 0; i < taskListModel.getSize(); i++) {
            String task = taskListModel.getElementAt(i);
            task = task.replaceFirst("^\\d+\\. ", ""); // remove existing number
            taskListModel.set(i, (i + 1) + ". " + task);
        }
    }

    // --- Save tasks to file ---
    private static void saveTasks() {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < taskListModel.getSize(); i++) {
                String task = taskListModel.getElementAt(i);
                task = task.replaceFirst("^\\d+\\. ", ""); // remove numbers
                pw.println(task);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error saving tasks: " + e.getMessage());
        }
    }

    // --- Load tasks from file ---
    private static void loadTasks() {
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    taskListModel.addElement(line);
                }
                updateListNumbers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error loading tasks: " + e.getMessage());
            }
        }
    }

    // --- Custom cell renderer for coloring tick/cross ---
    static class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String task = value.toString();
            if (task.contains("[✔]")) {
                // Green tick at end
                int tickIndex = task.indexOf("[✔]");
                String text = "<html>" + task.substring(0, tickIndex) + " <font color='green'>" + task.substring(tickIndex) + "</font></html>";
                label.setText(text);
            } else if (task.contains("[✘]")) {
                // Red cross at end
                int crossIndex = task.indexOf("[✘]");
                String text = "<html>" + task.substring(0, crossIndex) + " <font color='red'>" + task.substring(crossIndex) + "</font></html>";
                label.setText(text);
            }
            return label;
        }
    }
}

