/*
 *
 * Copyright SHMsoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freeeed.ui;

import jiconfont.icons.google_material_design_icons.GoogleMaterialDesignIcons;
import jiconfont.swing.IconFontSwing;
import org.freeeed.Entity.Project;
import org.freeeed.ServiceDao.ProjectService;
import org.freeeed.export.NativeCreator;
import org.freeeed.helpers.FreeEedUIHelper;
import org.freeeed.listner.FreeEedClosing;
import org.freeeed.listner.SetActiveCase;
import org.freeeed.main.*;
import org.freeeed.menu.analytic.OpenSmokingGun;
import org.freeeed.menu.analytic.OpenWordCloud;
import org.freeeed.menu.file.ExitApplication;
import org.freeeed.menu.file.OpenNewCase;
import org.freeeed.menu.file.OpenSetting;
import org.freeeed.menu.help.OpenAbout;
import org.freeeed.menu.help.OpenHelp;
import org.freeeed.menu.help.OpenHistory;
import org.freeeed.menu.review.OpenElasticSearch;
import org.freeeed.menu.review.OpenOutputFile;
import org.freeeed.menu.review.OpenReview;
import org.freeeed.staging.Staging;
import org.freeeed.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author mark
 */
public class FreeEedUI extends JFrame implements FreeEedUIHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeEedUI.class);
    private static FreeEedUI instance;
    private JLabel projectName;
    private final JTable caseTable;
    private JButton deleteButton, editButton, stageButton, processButton;
    private JProgressBar progressBar, secondProgressBar;
    private JLabel progressLabel, progressSizeLabel;
    NumberFormat nf = NumberFormat.getInstance();
    private final JScrollPane caseScrollPane = new JScrollPane();

    public static FreeEedUI getInstance() {
        return instance;
    }

    /**
     * Creates new form Main
     */
    public FreeEedUI() {
        setLayout(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        LOGGER.info("Starting {}", Version.getVersionAndBuild());
        List<String> status = OsUtil.getSystemSummary();
        status.forEach(LOGGER::info);
        /*
        try {
            Settings.load();
        } catch (Exception e) {
            LOGGER.error("Problem initializing internal db");
        }
*/

        getContentPane().setBackground(Color.white);


        caseTable = new JTable();

        IconFontSwing.register(GoogleMaterialDesignIcons.getIconFont());
        initTopMenu();
        initCaseList();
        initActionButton();
        initProgressBar();
        initProgressSizeLabel();
        initStatusBar();
        initProgressLabel();
    }

    private void initProgressBar() {

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 365, 800, 20);
        progressBar.setStringPainted(true);

        secondProgressBar = new JProgressBar();
        secondProgressBar.setBounds(10, 395, 800, 20);
        secondProgressBar.setStringPainted(true);

        getContentPane().add(progressBar);
        getContentPane().add(secondProgressBar);
    }

    private void initProgressLabel() {
        progressLabel = new JLabel();
        progressLabel.setBounds(10, 340, 800, 30);
        getContentPane().add(progressLabel);
    }

    private void initProgressSizeLabel() {
        progressSizeLabel = new JLabel();
        progressSizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        progressSizeLabel.setBounds(509, 340, 300, 30);
        getContentPane().add(progressSizeLabel);
    }

    @Override
    public void setProgressedSize(String label) {
        progressSizeLabel.setText(label);
    }

    public void setProgressIndeterminate(boolean status) {
        progressBar.setIndeterminate(status);
        progressBar.setStringPainted(!status);
    }

    @Override
    public void setSecondProgressBarMaximum(int max) {
        secondProgressBar.setMaximum(max);
    }

    @Override
    public void setSecondProgressBarValue(int prg) {
        secondProgressBar.setValue(prg);
    }

    public void setProgressLabel(String label) {
        progressLabel.setText(label);
    }

    public void setProgressDone() {
        progressLabel.setText("Done");
        releaseLock();
    }

    private void LockDown() {
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        stageButton.setEnabled(false);
        processButton.setEnabled(false);
        caseTable.setEnabled(false);
    }

    private void releaseLock() {
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
        stageButton.setEnabled(true);
        processButton.setEnabled(true);
        caseTable.setEnabled(true);
    }

    private void initActionButton() {
        Icon icon;

        int buttonWidth = 100, buttonHeight = 25;
        int buttonY = 320;


        /* Delete Button Config */
        deleteButton = new JButton("Delete");
        deleteButton.setBounds(710, buttonY, buttonWidth, buttonHeight);
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DELETE, 16, Color.RED);
        deleteButton.setIcon(icon);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            try {
                deleteProject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        getContentPane().add(deleteButton);


        /* Edit Button Config */
        editButton = new JButton("Edit");
        editButton.setBounds(600, buttonY, buttonWidth, buttonHeight);
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.EDIT, 16, Color.BLUE);
        editButton.setIcon(icon);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editProject());
        getContentPane().add(editButton);


        /* Staging Button Config */
        stageButton = new JButton("Stage");
        stageButton.setBounds(10, buttonY, buttonWidth + 25, buttonHeight);
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SYNC, 16, new Color(252, 143, 53));
        stageButton.setIcon(icon);
        stageButton.setEnabled(false);

        stageButton.addActionListener(e -> {
            Project project = Project.getActiveProject();
            if (stageDataIsValid(project)) {
                try {
                    int mode = 1;
                    /*
                    if (new File(project.getStagingDir()).exists()) {
                        String[] options = new String[3];
                        options[0] = "Merge Stage";
                        options[1] = "Clean Stage";
                        options[2] = "Cancel";
                        mode = JOptionPane.showOptionDialog(null, "How do you want to Stage your project?", "Select an Option...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
                        System.out.println(mode);
                    }
                    */
                    stageProcess(mode);
                } catch (Exception ex) {
                    LOGGER.error("Error staging project", ex);
                }
            }
        });

        getContentPane().add(stageButton);

        /* Proccess Button Config */
        processButton = new JButton("Proccess");
        processButton.setBounds(150, buttonY, buttonWidth + 25, buttonHeight);
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.DONE, 16, new Color(42, 219, 56));
        processButton.setIcon(icon);
        processButton.setEnabled(false);
        processButton.addActionListener(e -> runProcessing());
        getContentPane().add(processButton);

    }

    private void stageProcess(int mode) {
        if (mode != 2) {
            LockDown();
            Thread t = new Thread(new Staging(this, mode));
            t.start();
        }
    }

    public void setProgressBarMaximum(int max) {
        progressBar.setValue(0);
        progressBar.setMaximum(max);
        //System.out.println(max);
    }

    public void setProgressBarValue(int prg) {
        progressBar.setValue(prg);
    }

    private void setInstance(FreeEedUI aInstance) {
        instance = aInstance;
    }

    private void initTopMenu() {


        JMenuBar mainMenu = new JMenuBar();
        mainMenu.setBorderPainted(false);
        Icon icon;
        JMenu fileMenu = new JMenu(Language_English.MENU_FILE);

        JMenuItem menuItemNewCase = new JMenuItem(Language_English.NEW_CASE);
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.INSERT_DRIVE_FILE, 16);
        menuItemNewCase.addActionListener(new OpenNewCase());
        menuItemNewCase.setIcon(icon);

        JMenuItem menuItemExit = new JMenuItem(Language_English.EXIT);
        menuItemExit.addActionListener(new ExitApplication());
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.CLOSE, 16);
        menuItemExit.setIcon(icon);

        JMenuItem programSettingsMenuItem = new JMenuItem("Settings");
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.SETTINGS, 16);
        programSettingsMenuItem.addActionListener(new OpenSetting());
        programSettingsMenuItem.setIcon(icon);

        fileMenu.add(menuItemNewCase);
        fileMenu.add(programSettingsMenuItem);
        fileMenu.add(menuItemExit);
        mainMenu.add(fileMenu);


        JMenu analyticsMenu;
        JMenu reviewMenu;
        JMenuItem wordCloudMenuItem;
        JMenuItem simDocMenuItem;
        JMenuItem menuItemOutputFolder;
        JMenuItem menuItemOpenSearchUI;
        JMenuItem menuItemOpenRawES;


        reviewMenu = new JMenu();
        menuItemOutputFolder = new JMenuItem();
        menuItemOpenSearchUI = new JMenuItem();
        menuItemOpenRawES = new JMenuItem();
        analyticsMenu = new JMenu();
        wordCloudMenuItem = new JMenuItem();
        simDocMenuItem = new JMenuItem();


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("FreeEed - Graphical User Interface");


        reviewMenu.setText("Review");

        menuItemOutputFolder.setText("See output files");
        menuItemOutputFolder.addActionListener(new OpenOutputFile());
        reviewMenu.add(menuItemOutputFolder);

        menuItemOpenSearchUI.setText("Go to review");
        menuItemOpenSearchUI.addActionListener(new OpenReview());
        reviewMenu.add(menuItemOpenSearchUI);

        menuItemOpenRawES.setText("Open ElasticSearch index");
        menuItemOpenRawES.addActionListener(new OpenElasticSearch());
        reviewMenu.add(menuItemOpenRawES);

        mainMenu.add(reviewMenu);


        analyticsMenu.setText("Analytics");

        wordCloudMenuItem.setText("Word Cloud");
        wordCloudMenuItem.addActionListener(new OpenWordCloud());
        analyticsMenu.add(wordCloudMenuItem);

        simDocMenuItem.setText("Smoking Gun");
        simDocMenuItem.addActionListener(new OpenSmokingGun());
        analyticsMenu.add(simDocMenuItem);

        mainMenu.add(analyticsMenu);


        JMenu helpMenu = new JMenu(Language_English.MENU_HELP);
        JMenuItem aboutMenuItem = new JMenuItem(Language_English.MENU_ABOUT);
        JMenuItem historyMenuItem = new JMenuItem(Language_English.MENU_HISTORY);
        JMenuItem helpMenuItem = new JMenuItem(Language_English.MENU_HELP);


        aboutMenuItem.addActionListener(new OpenAbout());
        historyMenuItem.addActionListener(new OpenHistory());
        helpMenuItem.addActionListener(new OpenHelp());
        icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.LIVE_HELP, 16);
        helpMenuItem.setIcon(icon);

        helpMenu.add(helpMenuItem);
        helpMenu.add(aboutMenuItem);
        helpMenu.add(historyMenuItem);
        mainMenu.add(helpMenu);


        setJMenuBar(mainMenu);
        pack();
    }

    private void initStatusBar() {
        projectName = new JLabel("Test Project");
        projectName.setBounds(10, 420, 200, 25);

        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 420, 820, 25);

        getContentPane().add(projectName);
        getContentPane().add(mainPanel);
    }

    public void setStatusBarProjectName(String name) {
        projectName.setText(name);
    }

    private void deleteProject() throws Exception {
        int row = caseTable.getSelectedRow();
        if (row >= 0) {
            int projectId = (Integer) caseTable.getValueAt(row, 0);
            String projectName = caseTable.getValueAt(row, 2).toString().trim();
            int retStatus = JOptionPane.showConfirmDialog(this, "Delete project " + projectName + "?");
            if (retStatus == JOptionPane.OK_OPTION) {
                LOGGER.debug("Deleted project {} ID {}", projectName, projectId);
                ProjectService.getInstance().deleteProject(projectId);
                initCaseList();
            }
        }
    }

    private void editProject() {

        ProjectUI dialog = new ProjectUI(FreeEedUI.getInstance(), false);
        dialog.setLocationRelativeTo(FreeEedUI.getInstance());
        dialog.setVisible(true);


    }

    void initCaseList() {
        caseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseScrollPane.setBounds(10, 10, 800, 300);
        PopulateCaseList.Populate(caseTable);
        caseTable.setRowHeight(30);
        caseTable.getColumnModel().getColumn(0).setMaxWidth(60);
        caseTable.getColumnModel().getColumn(1).setMaxWidth(60);
        caseTable.getSelectionModel().addListSelectionListener(new SetActiveCase(caseTable));
        caseTable.getSelectionModel().addListSelectionListener(e -> {
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
            stageButton.setEnabled(true);
            processButton.setEnabled(true);
        });
        caseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() % 2 == 0 && caseTable.getSelectedRow() != -1) {
                    editProject();
                }
            }
        });
        caseScrollPane.setViewportView(caseTable);
        getContentPane().add(caseScrollPane);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FreeEedUI ui = new FreeEedUI();
            ui.setInstance(ui);
            ui.setVisible(true);
        });
        NativeCreator.getInstance().packNative();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            myInitComponents();
        }
        super.setVisible(b);
    }

    private void myInitComponents() {
        addWindowListener(new FreeEedClosing());
        setBounds(64, 40, 835, 500);
        setResizable(false);

        getRootPane().setBorder(BorderFactory.createEmptyBorder());

        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        setIconImage(icon.getImage());


        setLocationRelativeTo(null);
        setTitle(ParameterProcessing.APP_NAME + ParameterProcessing.TM + " - eDiscovery, Search and Analytics Platform");
    }

    private boolean stageDataIsValid(Project project) {

        System.out.println(Project.getActiveProject().getName());


        /*
        // check for empty input directories
        String[] dirs = project.getInputs();
        if (dirs.length == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(rootPane, "There are no dataset in the project");
            return false;
        }
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists() || (file.isDirectory() && file.list().length == 0)) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(rootPane, "Some of the directories you are trying to stage are empty. "
                        + "\n It does not make sense to stage them and may lead to confusion."
                        + "\n Please check the project directories");
                return false;
            }
        }
        */

        return true;


    }

    private void runProcessing() throws IllegalStateException {
        LockDown();

       /*
        if (new File(project.getResultsDir()).exists()) {
            try {
                Util.deleteDirectory(new File(project.getResultsDir()));
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        */
        Thread th = new Thread(new ActionProcessing(this));
        th.start();
    }

}
