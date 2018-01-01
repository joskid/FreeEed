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

import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import org.freeeed.main.FreeEedMain;
import org.freeeed.main.ParameterProcessing;
import org.freeeed.main.Version;
import org.freeeed.services.Mode;
import org.freeeed.services.Project;
import org.freeeed.services.Review;
import org.freeeed.services.Settings;
import org.freeeed.services.Services;
import org.freeeed.services.Util;
import org.freeeed.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mark
 */
public class FreeEedUI extends javax.swing.JFrame {

    private static final Logger logger = LoggerFactory.getLogger(FreeEedUI.class);
    private static FreeEedUI instance;

    public static FreeEedUI getInstance() {
        return instance;
    }

    /**
     * Creates new form Main
     */
    public FreeEedUI() {
        logger.info("Starting {}", Version.getVersionAndBuild());
        logger.info("System check:");

        String systemCheckErrors = OsUtil.systemCheck();
        if (!systemCheckErrors.isEmpty()) {
            SystemCheckUI ui = new SystemCheckUI(this, true);
            ui.setSystemErrorsText(systemCheckErrors);
            ui.setVisible(true);
        }
        List<String> status = OsUtil.getSystemSummary();
        for (String stat : status) {
            logger.info(stat);
        }
        try {
            Mode.load();
            Settings.load();
        } catch (Exception e) {
            logger.error("Problem initializing internal db");
        }
        initComponents();
        showHistory();
    }

    public void setInstance(FreeEedUI aInstance) {
        instance = aInstance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainMenu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        menuItemProjects = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        menuItemProjectOptions = new javax.swing.JMenuItem();
        processMenu = new javax.swing.JMenu();
        stageMenuItem = new javax.swing.JMenuItem();
        processMenuItem = new javax.swing.JMenuItem();
        processSeparator = new javax.swing.JPopupMenu.Separator();
        ecProcessMenuItem = new javax.swing.JMenuItem();
        historyMenuItem = new javax.swing.JMenuItem();
        reviewMenu = new javax.swing.JMenu();
        menuItemOutputFolder = new javax.swing.JMenuItem();
        menuItemOpenSearchUI = new javax.swing.JMenuItem();
        menuItemOpenRawSolr = new javax.swing.JMenuItem();
        analyticsMenu = new javax.swing.JMenu();
        wordCloudMenuItem = new javax.swing.JMenuItem();
        settingsMenu = new javax.swing.JMenu();
        modeMenuItem = new javax.swing.JMenuItem();
        programSettingsMenuItem = new javax.swing.JMenuItem();
        s3SetupMenuItem = new javax.swing.JMenuItem();
        ec2SetupMenuItem = new javax.swing.JMenuItem();
        clusterMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FreeEed - Graphical User Interface");

        fileMenu.setText("File");

        menuItemProjects.setText("Projects");
        menuItemProjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemProjectsActionPerformed(evt);
            }
        });
        fileMenu.add(menuItemProjects);

        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        fileMenu.add(menuItemExit);

        mainMenu.add(fileMenu);

        editMenu.setText("Edit");

        menuItemProjectOptions.setText("Project options");
        menuItemProjectOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemProjectOptionsActionPerformed(evt);
            }
        });
        editMenu.add(menuItemProjectOptions);

        mainMenu.add(editMenu);

        processMenu.setText("Process");

        stageMenuItem.setText("Stage");
        stageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stageMenuItemActionPerformed(evt);
            }
        });
        processMenu.add(stageMenuItem);

        processMenuItem.setText("Process locally");
        processMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                processMenuItemActionPerformed(evt);
            }
        });
        processMenu.add(processMenuItem);
        processMenu.add(processSeparator);

        ecProcessMenuItem.setText("Process on Amazon");
        ecProcessMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ecProcessMenuItemActionPerformed(evt);
            }
        });
        processMenu.add(ecProcessMenuItem);

        historyMenuItem.setText("History");
        historyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyMenuItemActionPerformed(evt);
            }
        });
        processMenu.add(historyMenuItem);

        mainMenu.add(processMenu);

        reviewMenu.setText("Review");

        menuItemOutputFolder.setText("See output files");
        menuItemOutputFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOutputFolderActionPerformed(evt);
            }
        });
        reviewMenu.add(menuItemOutputFolder);

        menuItemOpenSearchUI.setText("Go to review");
        menuItemOpenSearchUI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenSearchUIActionPerformed(evt);
            }
        });
        reviewMenu.add(menuItemOpenSearchUI);

        menuItemOpenRawSolr.setText("Open SOLR index");
        menuItemOpenRawSolr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenRawSolrActionPerformed(evt);
            }
        });
        reviewMenu.add(menuItemOpenRawSolr);

        mainMenu.add(reviewMenu);

        analyticsMenu.setText("Analytics");

        wordCloudMenuItem.setText("Word Cloud");
        wordCloudMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordCloudMenuItemActionPerformed(evt);
            }
        });
        analyticsMenu.add(wordCloudMenuItem);

        mainMenu.add(analyticsMenu);

        settingsMenu.setText("Settings");

        modeMenuItem.setText("Run mode");
        modeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(modeMenuItem);

        programSettingsMenuItem.setText("Program settings");
        programSettingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programSettingsMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(programSettingsMenuItem);

        s3SetupMenuItem.setText("S3 settings");
        s3SetupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                s3SetupMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(s3SetupMenuItem);

        ec2SetupMenuItem.setText("EC2 settings");
        ec2SetupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ec2SetupMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(ec2SetupMenuItem);

        clusterMenuItem.setText("EC2 cluster control");
        clusterMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clusterMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(clusterMenuItem);

        mainMenu.add(settingsMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        mainMenu.add(helpMenu);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        //new AboutDialog(this, true).setVisible(true);
        new AboutGUI(this).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void menuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemExitActionPerformed
        try {
            exitApp();
        } catch (Exception e) {
            logger.error("Error saving project", e);
            JOptionPane.showMessageDialog(this, "Application error " + e.getMessage());
        }
    }//GEN-LAST:event_menuItemExitActionPerformed

    private void menuItemProjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemProjectsActionPerformed
        openProject();
    }//GEN-LAST:event_menuItemProjectsActionPerformed

	private void stageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stageMenuItemActionPerformed
            stageProject();
	}//GEN-LAST:event_stageMenuItemActionPerformed

	private void processMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_processMenuItemActionPerformed
            processProject();
	}//GEN-LAST:event_processMenuItemActionPerformed

	private void historyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyMenuItemActionPerformed
            showHistory();
	}//GEN-LAST:event_historyMenuItemActionPerformed

	private void menuItemOutputFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOutputFolderActionPerformed
            try {
                openOutputFolder();
            } catch (IOException e) {
                logger.error("Could not open folder", e);
                JOptionPane.showMessageDialog(this, "Somthing is wrong with the OS, please open the output folder manually");
            }
	}//GEN-LAST:event_menuItemOutputFolderActionPerformed

    private void s3SetupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_s3SetupMenuItemActionPerformed
        S3SetupUI ui = new S3SetupUI(this, true);
        ui.setVisible(true);
    }//GEN-LAST:event_s3SetupMenuItemActionPerformed

    private void ec2SetupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ec2SetupMenuItemActionPerformed
        EC2SetupUI ui = new EC2SetupUI(this, true);
        ui.setVisible(true);
    }//GEN-LAST:event_ec2SetupMenuItemActionPerformed

    private void clusterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clusterMenuItemActionPerformed
        ClusterControlUI ui = new ClusterControlUI(this, false);
        ui.setVisible(true);
    }//GEN-LAST:event_clusterMenuItemActionPerformed

    private void ecProcessMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ecProcessMenuItemActionPerformed
        if (Project.getCurrentProject().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create or open a project first");
            return;
        }
        EC2ProcessUI ui = new EC2ProcessUI(this, false);
        ui.setVisible(true);
    }//GEN-LAST:event_ecProcessMenuItemActionPerformed

    private void menuItemProjectOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemProjectOptionsActionPerformed
        showProcessingOptions();
    }//GEN-LAST:event_menuItemProjectOptionsActionPerformed

    private void menuItemOpenSearchUIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenSearchUIActionPerformed
        openReviewUI();
    }//GEN-LAST:event_menuItemOpenSearchUIActionPerformed

    private void programSettingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programSettingsMenuItemActionPerformed
        openProgramSettings();
    }//GEN-LAST:event_programSettingsMenuItemActionPerformed

    private void menuItemOpenRawSolrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenRawSolrActionPerformed
        openSolr();
    }//GEN-LAST:event_menuItemOpenRawSolrActionPerformed

    private void modeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeMenuItemActionPerformed
        openModeUI();
    }//GEN-LAST:event_modeMenuItemActionPerformed

    private void wordCloudMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordCloudMenuItemActionPerformed
        openWordCloudUI();
    }//GEN-LAST:event_wordCloudMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            FreeEedUI ui = new FreeEedUI();
            ui.setInstance(ui);
            Services.start();
            ui.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu analyticsMenu;
    private javax.swing.JMenuItem clusterMenuItem;
    private javax.swing.JMenuItem ec2SetupMenuItem;
    private javax.swing.JMenuItem ecProcessMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem historyMenuItem;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemOpenRawSolr;
    private javax.swing.JMenuItem menuItemOpenSearchUI;
    private javax.swing.JMenuItem menuItemOutputFolder;
    private javax.swing.JMenuItem menuItemProjectOptions;
    private javax.swing.JMenuItem menuItemProjects;
    private javax.swing.JMenuItem modeMenuItem;
    private javax.swing.JMenu processMenu;
    private javax.swing.JMenuItem processMenuItem;
    private javax.swing.JPopupMenu.Separator processSeparator;
    private javax.swing.JMenuItem programSettingsMenuItem;
    private javax.swing.JMenu reviewMenu;
    private javax.swing.JMenuItem s3SetupMenuItem;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JMenuItem stageMenuItem;
    private javax.swing.JMenuItem wordCloudMenuItem;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setVisible(boolean b) {
        if (b) {
            myInitComponents();
        }
        super.setVisible(b);
    }

    private void myInitComponents() {
        addWindowListener(new FrameListener());
        setBounds(64, 40, 640, 400);
        setLocationRelativeTo(null);
        setTitle(ParameterProcessing.APP_NAME + ParameterProcessing.TM + " - e-Discovery, Search and Analytics Platform");
    }

    private void exitApp() throws Exception {
        if (!isExitAllowed()) {
            return;
        }
        Settings.getSettings().save();
        setVisible(false);
        System.exit(0);
    }

    private boolean isExitAllowed() {
        return true;
    }

    private void openProject() {
        ProjectsUI dialog = new ProjectsUI(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void openProject(File selectedFile) {
        Project project = Project.loadFromFile(selectedFile);
        project.setProjectFilePath(selectedFile.getPath());
        updateTitle(project.getProjectCode() + " " + project.getProjectName());
        logger.trace("Opened project file: " + selectedFile.getPath());
        Settings settings = Settings.getSettings();
        settings.addRecentProject(selectedFile.getPath());
        showProcessingOptions();
    }

    private class ProjectFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".project") || file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Project files";
        }
    }

    public void updateTitle(String title) {
        if (title != null) {
            setTitle(ParameterProcessing.APP_NAME + ParameterProcessing.TM + " - " + title);
        } else {
            setTitle(ParameterProcessing.APP_NAME + ParameterProcessing.TM);
        }
    }

    public void showProcessingOptions() {
        if (Project.getCurrentProject().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create or open a project first");
            return;
        }
        ProjectUI dialog = new ProjectUI(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void stageProject() {
        Project project = Project.getCurrentProject();
        if (project.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create or open a project first");
            return;
        }
        // check for empty input directories
        String[] dirs = project.getInputs();
        if (dirs.length == 0) {
            JOptionPane.showMessageDialog(rootPane, "You selected no data to stage");
            return;
        }
        for (String dir : dirs) {
            File file = new File(dir);
            if (file.isDirectory() && file.list().length == 0) {
                JOptionPane.showMessageDialog(rootPane, "Some of the directories you are trying to stage are empty. "
                        + "\\It does not make sense to stage them and may lead to confusion."
                        + "\\Please check the project directories");
                return;
            }
        }
        try {
            FreeEedMain.getInstance().runStagePackageInput();
        } catch (Exception e) {
            logger.error("Error staging project", e);
        }
    }

    private void runProcessing() throws IllegalStateException {
        Project project = Project.getCurrentProject();
        if (project.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please create or open a project first");
            return;
        }
        project.setEnvironment(Project.ENV_LOCAL);
        FreeEedMain mainInstance = FreeEedMain.getInstance();
        if (new File(project.getResultsDir()).exists()) {
            // in most cases, it won't already exist, but just in case
            try {
                Util.deleteDirectory(new File(project.getResultsDir()));
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        String processWhere = project.getProcessWhere();
        if (processWhere != null) {
            mainInstance.runProcessing(processWhere);
        } else {
            throw new IllegalStateException("No processing option selected.");
        }
    }

    private void showHistory() {
        HistoryUI ui = new HistoryUI();
        ui.setVisible(true);
    }

    private boolean areResultsPresent() {

        Project project = Project.getCurrentProject();
        if (project == null || project.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please open a project first");
            return false;
        }
        try {
            boolean success = Review.deliverFiles();
            if (!success) {
                JOptionPane.showMessageDialog(this, "No results yet");
                return false;
            }
        } catch (IOException e) {
            logger.warn("Problem while checking for results", e);
            return false;
        }
        return true;
    }

    private void openOutputFolder() throws IOException {
        if (!areResultsPresent()) {
            return;
        }
        String resultsFolder = Project.getCurrentProject().getResultsDir();
        try {
            // Desktop should work, but it stopped lately in Ubuntu
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(resultsFolder));
            } else if (OsUtil.isLinux()) {
                String command = "nautilus " + resultsFolder;
                OsUtil.runCommand(command);
            } else if (OsUtil.isMac()) {
                String command = "open " + resultsFolder;
                OsUtil.runCommand(command);
            }
        } catch (IOException e) {
            if (OsUtil.isLinux()) {
                String command = "nautilus " + resultsFolder;
                OsUtil.runCommand(command);
            } else if (OsUtil.isMac()) {
                String command = "open " + resultsFolder;
                OsUtil.runCommand(command);
            }
        }
    }

    class FrameListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            try {
                Settings.getSettings().save();
            } catch (Exception ex) {
                logger.error("Error saving project", ex);
                JOptionPane.showMessageDialog(null, "Application error " + ex.getMessage());
            }

        }
    }

    private void openWiki() {
        Settings settings = Settings.getSettings();
        String url = settings.getManualPage();
        UtilUI.openBrowser(this, url);
    }

    private void openProgramSettings() {
        ProgramSettingsUI programSettingsUI = new ProgramSettingsUI(this, true);
        programSettingsUI.setVisible(true);
    }

    private void openSolr() {
        Settings settings = Settings.getSettings();
        String url = settings.getSolrEndpoint() + "/solr/admin";
        UtilUI.openBrowser(this, url);
    }

    private void openReviewUI() {
        Settings settings = Settings.getSettings();
        String url = settings.getReviewEndpoint();
        UtilUI.openBrowser(this, url);
    }

    public void processProject() {
        try {
            runProcessing();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, "There were some problems with processing, \""
                    + e.getMessage() + "\n"
                    + "please check console output");
        }
    }

    private void openModeUI() {
        RunModeUI ui = new RunModeUI(this, true);
        ui.setVisible(true);
    }

    private void openWordCloudUI() {
        Project project = Project.getCurrentProject();
        if (project.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Please open a project first");
            return;
        }
        WordCloudUI ui = new WordCloudUI(this, true);
        ui.setVisible(true);
    }
}
