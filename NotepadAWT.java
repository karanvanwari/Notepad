import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;  // Import for Clipboard and StringSelection
import java.io.*;
import javax.swing.*;

public class NotepadAWT extends Frame implements ActionListener {

    // Components of the Notepad
    TextArea textArea;
    MenuBar menuBar;
    Menu fileMenu, editMenu, formatMenu;
    MenuItem newItem, openItem, saveItem, exitItem;
    MenuItem cutItem, copyItem, pasteItem, undoItem, redoItem, findItem, replaceItem;
    MenuItem fontItem;

    // For undo/redo
    String lastText = "";
    String previousText = "";

    // Constructor
    public NotepadAWT() {
        // Set the Frame layout and title
        setTitle("Java AWT Notepad with Extended Features");
        setSize(800, 600);

        // Create the TextArea where the text will be typed
        textArea = new TextArea();
        add(textArea);

        // Create the MenuBar and add it to the Frame
        menuBar = new MenuBar();

        // Create the "File" Menu and its MenuItems
        fileMenu = new Menu("File");
        newItem = new MenuItem("New");
        openItem = new MenuItem("Open");
        saveItem = new MenuItem("Save");
        exitItem = new MenuItem("Exit");

        // Add MenuItems to the "File" Menu
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        // Create the "Edit" Menu and its MenuItems
        editMenu = new Menu("Edit");
        cutItem = new MenuItem("Cut");
        copyItem = new MenuItem("Copy");
        pasteItem = new MenuItem("Paste");
        undoItem = new MenuItem("Undo");
        redoItem = new MenuItem("Redo");
        findItem = new MenuItem("Find");
        replaceItem = new MenuItem("Replace");

        // Add MenuItems to the "Edit" Menu
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(findItem);
        editMenu.add(replaceItem);

        // Create the "Format" Menu for changing font
        formatMenu = new Menu("Format");
        fontItem = new MenuItem("Font");
        formatMenu.add(fontItem);

        // Add ActionListeners to the MenuItems
        newItem.addActionListener(this);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        cutItem.addActionListener(this);
        copyItem.addActionListener(this);
        pasteItem.addActionListener(this);
        undoItem.addActionListener(this);
        redoItem.addActionListener(this);
        findItem.addActionListener(this);
        replaceItem.addActionListener(this);

        fontItem.addActionListener(this);

        // Add the File, Edit, and Format Menus to the MenuBar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);

        // Add the MenuBar to the Frame
        setMenuBar(menuBar);

        // Set the Frame to close when "Exit" is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    // Method to handle menu item clicks
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("New")) {
            lastText = textArea.getText(); // Save last text for undo
            textArea.setText("");
        } else if (command.equals("Open")) {
            openFile();
        } else if (command.equals("Save")) {
            saveFile();
        } else if (command.equals("Exit")) {
            System.exit(0);
        } else if (command.equals("Cut")) {
            cutText();
        } else if (command.equals("Copy")) {
            copyText();
        } else if (command.equals("Paste")) {
            pasteText();
        } else if (command.equals("Undo")) {
            undoAction();
        } else if (command.equals("Redo")) {
            redoAction();
        } else if (command.equals("Find")) {
            findText();
        } else if (command.equals("Replace")) {
            replaceText();
        } else if (command.equals("Font")) {
            changeFont();
        }
    }

    private void cutText() {
        lastText = textArea.getText(); // Save current text for undo
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(selectedText);
            clipboard.setContents(selection, selection);
            textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
        }
    }

    private void copyText() {
        String selectedText = textArea.getSelectedText();
        if (selectedText != null) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(selectedText);
            clipboard.setContents(selection, selection);
        }
    }

    private void pasteText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            String pastedText = (String) clipboard.getData(DataFlavor.stringFlavor);
            int caretPosition = textArea.getCaretPosition();
            textArea.insert(pastedText, caretPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to open a file
    private void openFile() {
        FileDialog fileDialog = new FileDialog(this, "Open File", FileDialog.LOAD);
        fileDialog.setVisible(true);

        String filePath = fileDialog.getDirectory() + fileDialog.getFile();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            lastText = textArea.getText(); // Save last text for undo
            textArea.setText(content.toString());
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }

    // Method to save a file
    private void saveFile() {
        FileDialog fileDialog = new FileDialog(this, "Save File", FileDialog.SAVE);
        fileDialog.setVisible(true);

        String filePath = fileDialog.getDirectory() + fileDialog.getFile();
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(textArea.getText());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Undo the last change
    private void undoAction() {
        previousText = lastText;
        lastText = textArea.getText();
        textArea.setText(previousText);
    }

    // Redo the undone change
    private void redoAction() {
        textArea.setText(lastText);
    }

    // Find text in the document
    private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Enter text to find:");
        if (searchText != null) {
            String content = textArea.getText();
            int index = content.indexOf(searchText);
            if (index != -1) {
                textArea.select(index, index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found");
            }
        }
    }

    // Replace text in the document
    private void replaceText() {
        String findText = JOptionPane.showInputDialog(this, "Enter text to replace:");
        String replaceText = JOptionPane.showInputDialog(this, "Enter replacement text:");
        if (findText != null && replaceText != null) {
            String content = textArea.getText();
            content = content.replace(findText, replaceText);
            textArea.setText(content);
        }
    }

    // Change font of the TextArea
    private void changeFont() {
        Font currentFont = textArea.getFont();
        String fontName = JOptionPane.showInputDialog(this, "Enter Font Name (e.g., Arial):");
        String fontSizeStr = JOptionPane.showInputDialog(this, "Enter Font Size:");
        try {
            int fontSize = Integer.parseInt(fontSizeStr);
            textArea.setFont(new Font(fontName, Font.PLAIN, fontSize));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid font size");
        }
    }

    // Main method to run the Notepad application
    public static void main(String[] args) {
        NotepadAWT notepad = new NotepadAWT();
        notepad.setVisible(true);
    }
}
