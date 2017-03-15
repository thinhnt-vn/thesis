/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.entity;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;

import java.util.Date;
import java.util.List;

import java.io.*;

/**
 * A basic File Browser. Requires 1.6+ for the Desktop & SwingWorker classes,
 * amongst other minor things.
 *
 * Includes support classes FileTableModel & FileTreeCellRenderer.
 *
 * @TODO Bugs
 * <li>Fix keyboard focus issues - especially when functions like rename/delete
 * etc. are called that update nodes & file lists.
 * <li>Needs more testing in general.
 *
 * @TODO Functionality
 * <li>Double clicking a directory in the table, should update the tree
 * <li>Move progress bar?
 * <li>Add other file display modes (besides table) in CardLayout?
 * <li>Menus + other cruft?
 * <li>Implement history/back
 * <li>Allow multiple selection
 * <li>Add file search
 *
 * @author Andrew Thompson
 * @version 2011-06-08
 * @see http://stackoverflow.com/questions/6182110
 * @license LGPL
 */
public class FileBrowser extends JPanel {

    /**
     * Title of the application
     */
    public static final String APP_TITLE = "";
    /**
     * Used to open/edit/print files.
     */
    private Desktop desktop;
    /**
     * Provides nice icons and names for files.
     */
    private FileSystemView fileSystemView;

    /**
     * currently selected File.
     */
    private File currentFile;

    /**
     * Main GUI container
     */
    private JPanel gui;

    /**
     * File-system tree. Built Lazily
     */
    private JTree tree;
    private DefaultTreeModel treeModel;

    /**
     * Directory listing
     */
    private JTable table;
    private JProgressBar progressBar;
    /**
     * Table model for File[].
     */
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;

    /* File controls. */
    private JButton openFile;
//    private JButton printFile;
    private JButton editFile;
//    private JButton cutFileButton;
//    private JButton copyFileButton;
//    private JButton pasteFileButton;
//    private JButton deleteFileButton;
//    private JButton refreshFileButton;

    /* File details. */
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;

    /* GUI options/containers for new File/Directory creation.  Created lazily. */
    private JPanel newFilePanel;
    private JRadioButton newTypeFile;
    private JTextField name;

    private File[] rootFile;
//    private File copyFile;
//    private File cutFile;

    public FileBrowser() {
    }

    public FileBrowser(File r) {
        rootFile = new File[1];
        rootFile[0] = r;
        setLayout(new BorderLayout(3, 3));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        fileSystemView = FileSystemView.getFileSystemView();
        desktop = Desktop.getDesktop();

        JPanel detailView = new JPanel(new BorderLayout(3, 3));

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int row = table.getSelectionModel().getLeadSelectionIndex();
                setFileDetails(((FileTableModel) table.getModel()).getFile(row));
            }
        };
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        JScrollPane tableScroll = new JScrollPane(table);
        Dimension d = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
        detailView.add(tableScroll, BorderLayout.CENTER);

        // the File tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);

        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                DefaultMutableTreeNode node
                        = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
                showChildren(node);
                setFileDetails((File) node.getUserObject());
            }
        };

        // show the file system roots.
        File[] roots = rootFile;
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add(node);
            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
            //
        }

        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(treeSelectionListener);
        tree.setCellRenderer(new FileTreeCellRenderer());
        tree.expandRow(0);
        JScrollPane treeScroll = new JScrollPane(tree);

        // as per trashgod tip
        tree.setVisibleRowCount(15);

        Dimension preferredSize = treeScroll.getPreferredSize();
        Dimension widePreferred = new Dimension(
                200,
                (int) preferredSize.getHeight());
        treeScroll.setPreferredSize(widePreferred);

        // details for a File
        JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
        fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

        JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
        fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

        JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
        fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

        fileDetailsLabels.add(new JLabel("Tên", JLabel.TRAILING));
        fileName = new JLabel();
        fileDetailsValues.add(fileName);
        fileDetailsLabels.add(new JLabel("Đường dẫn", JLabel.TRAILING));
        path = new JTextField(5);
        path.setEditable(false);
        fileDetailsValues.add(path);
        fileDetailsLabels.add(new JLabel("Thời gian chỉnh sửa", JLabel.TRAILING));
        date = new JLabel();
        fileDetailsValues.add(date);
        fileDetailsLabels.add(new JLabel("Kích thước", JLabel.TRAILING));
        size = new JLabel();
        fileDetailsValues.add(size);
        fileDetailsLabels.add(new JLabel("Kiểu", JLabel.TRAILING));

        JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));

        isDirectory = new JRadioButton("Thư mục");
        flags.add(isDirectory);

        isFile = new JRadioButton("Tập tin");
        flags.add(isFile);
        fileDetailsValues.add(flags);

        JToolBar toolBar = new JToolBar();
        // mnemonics stop working in a floated toolbar
        toolBar.setFloatable(false);

        JButton locateFile = new JButton("Mở thư mục chứa");
        locateFile.setMnemonic('l');

        locateFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    System.out.println("Locate: " + currentFile.getParentFile());
                    desktop.open(currentFile.getParentFile());
                } catch (Throwable t) {
                    showThrowable(t);
                }
                repaint();
            }
        });
        toolBar.add(locateFile);

        openFile = new JButton("Mở");
        openFile.setMnemonic('o');

        openFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    System.out.println("Open: " + currentFile);
                    desktop.open(currentFile);
                } catch (Throwable t) {
                    showThrowable(t);
                }
                repaint();
            }
        });
        toolBar.add(openFile);

        editFile = new JButton("Chỉnh sửa");
        editFile.setMnemonic('e');
        editFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    desktop.edit(currentFile);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        toolBar.add(editFile);

        editFile = new JButton("Làm tươi");
        editFile.setMnemonic('l');
        editFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
//                DefaultMutableTreeNode node
//                        = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent(); // show the file system roots.
                File[] roots = rootFile;
                DefaultMutableTreeNode oldRoot = root;
                root.removeAllChildren();
                for (File fileSystemRoot : roots) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                    root.add(node);
                    File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                    for (File file : files) {
                        if (file.isDirectory()) {
                            node.add(new DefaultMutableTreeNode(file));
                        }
                    }
                    //
                }
                TreeModelEvent e = new TreeModelEvent(treeModel, new Object[] {root});
                TreeModelListener[] listeners = treeModel.getTreeModelListeners();
                for (TreeModelListener listener : listeners) {
                    listener.treeStructureChanged(e);
                }

//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
//                showChildren(node);
//                setFileDetails((File) node.getUserObject());
            }
        });
        toolBar.add(editFile);
        toolBar.addSeparator();

//        cutFileButton = new JButton("Cut");
//        cutFileButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                String source = (String) fileTableModel.getValueAt(table.getSelectedRow(), 2);
//                cutFile = new File(source);
//                if (copyFile != null){
//                    copyFile = null;
//                }
//            }
//        });
//        Thread cutFileThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    cutFileButton.setEnabled(table.getSelectedRow() > -1);
//                }
//            }
//        });
//        cutFileThread.start();
//        toolBar.add(cutFileButton);
//        
//        copyFileButton = new JButton("Copy");
//        copyFileButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                String source = (String) fileTableModel.getValueAt(table.getSelectedRow(), 2);
//                copyFile = new File(source);
//                if (cutFile != null){
//                    cutFile = null;
//                }
//            }
//        });
//        Thread copyFileThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    copyFileButton.setEnabled(table.getSelectedRow() > -1);
//                }
//            }
//        });
//        copyFileThread.start();
//        toolBar.add(copyFileButton);
//        
//        pasteFileButton = new JButton("Paste");
//        pasteFileButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                
//            }
//        });
//        Thread pasteFileThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    pasteFileButton.setEnabled(cutFile != null || copyFile != null);
//                }
//            }
//        });
//        pasteFileThread.start();
//        toolBar.add(pasteFileButton);
//        
//        deleteFileButton = new JButton("Delete");
//        deleteFileButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                
//            }
//        });
//        Thread deleteFileThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    deleteFileButton.setEnabled(table.getSelectedRow() > -1);
//                }
//            }
//        });
//        deleteFileThread.start();
//        toolBar.add(deleteFileButton);
//        printFile = new JButton("Print");
//        printFile.setMnemonic('p');
//        printFile.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                try {
//                    desktop.print(currentFile);
//                } catch (Throwable t) {
//                    showThrowable(t);
//                }
//            }
//        });
////        toolBar.add(printFile);
        // Check the actions are supported on this platform!
        openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
        editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
//        printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

        flags.add(new JLabel("::  Quyền"));
        readable = new JCheckBox("Đọc  ");
        readable.setMnemonic('a');
        flags.add(readable);

        writable = new JCheckBox("Ghi  ");
        writable.setMnemonic('w');
        flags.add(writable);

        executable = new JCheckBox("Thực thi");
        executable.setMnemonic('x');
        flags.add(executable);

        int count = fileDetailsLabels.getComponentCount();
        for (int ii = 0; ii < count; ii++) {
            fileDetailsLabels.getComponent(ii).setEnabled(false);
        }

        count = flags.getComponentCount();
        for (int ii = 0; ii < count; ii++) {
            flags.getComponent(ii).setEnabled(false);
        }

        JPanel fileView = new JPanel(new BorderLayout(3, 3));

        fileView.add(toolBar, BorderLayout.NORTH);
        fileView.add(fileMainDetails, BorderLayout.CENTER);

        detailView.add(fileView, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treeScroll,
                detailView);
        add(splitPane, BorderLayout.CENTER);

        JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
        progressBar = new JProgressBar();
        simpleOutput.add(progressBar, BorderLayout.EAST);
        progressBar.setVisible(false);

        add(simpleOutput, BorderLayout.SOUTH);
    }

    public JPanel getGui() {
        if (gui == null) {

        }
        return gui;
    }

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0, 0);
    }

    private TreePath findTreePath(File find) {
        for (int ii = 0; ii < tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
            File nodeFile = (File) node.getUserObject();

            if (nodeFile == find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                errorTitle,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                t.toString(),
                t.getMessage(),
                JOptionPane.ERROR_MESSAGE
        );
        repaint();
    }

    /**
     * Update the table on the EDT
     */
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel == null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon;
                    if (files.length > 0) {
                        icon = fileSystemView.getSystemIcon(files[0]);
                        // size adjustment to better account for icons
                        table.setRowHeight(icon.getIconHeight() + rowIconPadding);
                    }

                    setColumnWidth(0, -1);
                    setColumnWidth(3, 60);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4, -1);
                    setColumnWidth(5, -1);
                    setColumnWidth(6, -1);
                    setColumnWidth(7, -1);
                    setColumnWidth(8, -1);
                    setColumnWidth(9, -1);

                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width < 0) {
            // use the preferred width of the header..
            JLabel label = new JLabel((String) tableColumn.getHeaderValue());
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int) preferred.getWidth() + 14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /**
     * Add the files that are contained within the directory of this node.
     * Thanks to Hovercraft Full Of Eels for the SwingWorker fix.
     */
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    /**
     * Update the File details view with the details of this File.
     */
    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame) getTopLevelAncestor();
        if (f != null) {
            f.setTitle(
                    APP_TITLE
                    + ""
                    + fileSystemView.getSystemDisplayName(file));
        }

        repaint();
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    // Significantly improves the look of the output in
//                    // terms of the file names returned by FileSystemView!
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch (Exception weTried) {
//                }
//                JFrame f = new JFrame(APP_TITLE);
//                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//                FileBrowser FileBrowser = new FileBrowser();
//                f.setContentPane(FileBrowser.getGui());
//
//                try {
//                    URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
//                    URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
//                    ArrayList<Image> images = new ArrayList<Image>();
//                    images.add(ImageIO.read(urlBig));
//                    images.add(ImageIO.read(urlSmall));
//                    f.setIconImages(images);
//                } catch (Exception weTried) {
//                }
//
//                f.pack();
//                f.setLocationByPlatform(true);
//                f.setMinimumSize(f.getSize());
//                f.setVisible(true);
//
//                FileBrowser.showRootFile();
//            }
//        });
//    }
}

/**
 * A TableModel to hold File[].
 */
class FileTableModel extends AbstractTableModel {

    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Biểu tượng",
        "Tên",
        "Đường dẫn",
        "Kích thước",
        "Thời gian chỉnh sửa",
        "R",
        "W",
        "E",
        "D",
        "F",};

    FileTableModel() {
        this(new File[0]);
    }

    FileTableModel(File[] files) {
        this.files = files;
    }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}

/**
 * A TreeCellRenderer for a File.
 */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private FileSystemView fileSystemView;

    private JLabel label;

    FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        File file = (File) node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
//        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}
