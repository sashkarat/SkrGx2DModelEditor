package org.skr.PhysModelEditor.ScriptSourceEditor;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.skr.PhysModelEditor.ApplicationSettings;
import org.skr.SkrScript.Builder;
import org.skr.SkrScript.Dumper;
import org.skr.gdx.scene.PhysScene;
import org.skr.gdx.script.PhysScript;
import org.skr.gdx.script.PhysScriptProvider;
import org.skr.gdx.script.PhysScriptSource;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DialogPhysScriptSourceEditor extends JDialog {

    public interface SaveAllRequestListener {
        public void save();
    }

    public class GuiStream extends OutputStream {

        boolean errStream = false;

        public GuiStream(boolean errStream) {
            this.errStream = errStream;
        }

        StringBuilder bc = new StringBuilder();

        @Override
        public void write(int i) throws IOException {
            char c = (char) i;
            bc.append( c );
            if ( c == '\n' ) {
                logOutDataModel.addElement( bc.toString() );
                bc = new StringBuilder();

                listOutput.revalidate();
                int h = (int) listOutput.getPreferredSize().getHeight();
                listOutput.scrollRectToVisible( new Rectangle( 0, h, 10, 10) );
            }
        }
    }

    private JPanel contentPane;
    private JButton btnOk;
    private JButton buttonCancel;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea taSourceText;
    private JButton btnUpdate;
    private RTextScrollPane tsp;
    private JComboBox comboVerboseLevel;
    private JList listOutput;
    private JScrollPane scrollListOut;
    private JLabel lblCaretPos;
    private JButton btnClearLog;
    private JButton btnSaveLog;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnSaveAll;
    private JButton btnDump;
    protected boolean accept = false;

    protected PhysScriptSource source;
    protected PhysScriptProvider provider;

    protected DefaultListModel<String> logOutDataModel = new DefaultListModel<String>();

    SaveAllRequestListener saveAllRequestListener;

    public DialogPhysScriptSourceEditor() {

        taSourceText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        taSourceText.setAntiAliasingEnabled( true );

        listOutput.setModel(logOutDataModel);
        listOutput.setFont( new Font("", Font.ITALIC, 11) );

        DefaultCompletionProvider completionProvider = new DefaultCompletionProvider();
        for ( String keyword :  Builder.getKeywords() )
            completionProvider.addCompletion( new BasicCompletion( completionProvider, keyword) );
        for ( String keyword :  Builder.getDataTypeSpec().keySet() )
            completionProvider.addCompletion( new BasicCompletion( completionProvider, keyword) );
        for ( String keyword :  Builder.getProperties().keySet() )
            completionProvider.addCompletion( new BasicCompletion( completionProvider, keyword) );
        for ( String  keyword : Builder.getDefines().keySet() )
            completionProvider.addCompletion( new BasicCompletion( completionProvider, keyword) );
        for ( int id : Builder.getBfuncMap().keySet() ) {
            Builder.FunctionDesc fd = Builder.getBfuncMap().get( id );
            completionProvider.addCompletion( new BasicCompletion( completionProvider, fd.name) );
        }

        AutoCompletion ac = new AutoCompletion( completionProvider );
        ac.install( taSourceText );

        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/policyScript", ScriptSourceTokenMaker.class.getName());
        taSourceText.setSyntaxEditingStyle("text/policyScript");

        SyntaxScheme ss = taSourceText.getSyntaxScheme();
        ss.getStyle(Token.RESERVED_WORD).foreground = new Color(38, 57, 104);

        ss.getStyle(Token.MARKUP_COMMENT).foreground = Color.gray;

        ss.getStyle(Token.FUNCTION).font = new Font("", Font.ITALIC, 12 );
        ss.getStyle(Token.FUNCTION).foreground = Color.black;

        ss.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).font = new Font("",
                Font.ITALIC | Font.BOLD, 12 );
        ss.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(60, 135, 49);
        taSourceText.revalidate();


        taSourceText.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent caretEvent) {
                int caretPos = taSourceText.getCaretPosition();
                try {
                    int lineNum = taSourceText.getLineOfOffset( caretPos );
                    int column = caretPos - taSourceText.getLineStartOffset( lineNum );
                    lineNum += 1;

                    lblCaretPos.setText( lineNum + ":" + column + " " );

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnOk);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateScript();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAll();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateScript();
            }
        });

        comboVerboseLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVerboseLevel();
            }
        });
        btnClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clearOutputLog();
            }
        });
        btnSaveLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveOutputLog();
            }
        });
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                exportSource();
            }
        });
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                importSource();
            }
        });
        btnSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveAll();
            }
        });
        btnDump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dumpScript();
            }
        });
    }

    private void dumpScript() {
        PhysScript script = provider.findScript(source.getId());
        if ( script == null )
            return;
        Dumper.dump( script );
    }

    private void saveAll() {
        source.setSourceText( taSourceText.getText() );

        if ( saveAllRequestListener != null ) {
            saveAllRequestListener.save();
        }
    }

    private static FileNameExtensionFilter ff2 = new FileNameExtensionFilter("Source text", "script");

    private void importSource() {
        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
        fch.setFileFilter( ff2 );

        res = fch.showOpenDialog(this);

        if ( res != JFileChooser.APPROVE_OPTION )
            return;

        File fl = fch.getSelectedFile();

        try {
            String txt = new String( Files.readAllBytes(Paths.get(fl.getAbsolutePath())) );
            taSourceText.setText( txt );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taSourceText.revalidate();
    }

    private void exportSource() {
        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
        fch.setFileFilter( ff2 );

        res = fch.showSaveDialog(this);

        if ( res != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File fl = fch.getSelectedFile();

        String apth = fl.getAbsolutePath();

        if ( !apth.toLowerCase().endsWith( "." + ff2.getExtensions()[0]) ) {
            apth += ("." + ff2.getExtensions()[0]);
            fl = new File(apth);
        }

        try {
            FileWriter fw = new FileWriter( fl );
            fw.write( taSourceText.getText() );
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ApplicationSettings.get().setLastDirectory( fl.getParent() );
        }

    }

    private static FileNameExtensionFilter ff = new FileNameExtensionFilter("Log or text files:", "log", "txt");

    private void saveOutputLog() {
        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
        fch.setFileFilter( ff );

        res = fch.showSaveDialog(this);

        if ( res != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File fl = fch.getSelectedFile();

        String apth = fl.getAbsolutePath();

        if ( !apth.toLowerCase().endsWith( "." + ff.getExtensions()[0]) ) {
            apth += ("." + ff.getExtensions()[0]);
            fl = new File(apth);
        }

        try {
            FileWriter fw = new FileWriter( fl );
            for ( int i = 0; i < logOutDataModel.getSize(); i++ )
                fw.write(logOutDataModel.getElementAt(i));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clearOutputLog() {
        logOutDataModel.clear();
        listOutput.updateUI();
    }

    private void setVerboseLevel() {
        Integer l = Integer.valueOf( comboVerboseLevel.getSelectedItem().toString() );
        Builder.setVerboseLevel( l );
    }

    private void onOK() {
        accept = true;
        dispose();
    }

    private void onCancel() {
        //TODO: add yes/no dialog
        accept = false;
        dispose();
    }

    protected void updateScript() {
        source.setSourceText( taSourceText.getText() );
        if ( provider.updateScript( source ) ) {
            System.out.println("Script building successfully done.");
        } else {
            System.out.println("Script building failed.");
        }
    }

    public boolean execute(PhysScriptSource source, PhysScriptProvider provider ) {
        this.source = source;
        this.provider = provider;
        taSourceText.setText(source.getSourceText());
        pack();
        setSize(400, 400);

        PrintStream stdOut = System.out;
        PrintStream stdErr = System.err;

        PrintStream guiOut = new PrintStream( new GuiStream( false ) );
        PrintStream guiErr = new PrintStream( new GuiStream( true ) );

        System.setOut( guiOut );
        System.setErr(guiErr);

        setVisible(true);
        if ( accept )
            updateScript();

        System.setOut( stdOut );
        System.setErr( stdErr );

        return accept;
    }

    public SaveAllRequestListener getSaveAllRequestListener() {
        return saveAllRequestListener;
    }

    public void setSaveAllRequestListener(SaveAllRequestListener saveAllRequestListener) {
        this.saveAllRequestListener = saveAllRequestListener;
    }
}
