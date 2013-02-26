/*     */ package net.bluecow.spectro;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.prefs.BackingStoreException;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ import net.bluecow.spectro.action.PlayPauseAction;
/*     */ import net.bluecow.spectro.action.RewindAction;
/*     */ import net.bluecow.spectro.action.SaveAction;
/*     */ import net.bluecow.spectro.action.UndoRedoAction;
/*     */ import net.bluecow.spectro.tool.ToolboxPanel;
/*     */ 
/*     */ public class SpectroEditSession
/*     */ {
/*  58 */   private static final Logger logger = Logger.getLogger(SpectroEditSession.class.getName());
/*     */ 
/*  63 */   private static final Preferences prefs = Preferences.userNodeForPackage(SpectroEditSession.class);
/*     */ 
/*  69 */   private final UndoManager undoManager = new UndoManager();
/*     */   private final PlayerThread playerThread;
/*     */   private final ClipPanel clipPanel;
/*     */ 
/*     */   protected SpectroEditSession(Clip c)
/*     */     throws LineUnavailableException
/*     */   {
/*  76 */     this.playerThread = new PlayerThread(c);
/*  77 */     this.playerThread.start();
/*     */ 
/*  79 */     this.clipPanel = ClipPanel.newInstance(c, this.playerThread);
/*  80 */     this.clipPanel.addUndoableEditListener(this.undoManager);
/*     */ 
/*  82 */     final JFrame f = new JFrame("Spectro-Edit " + Version.VERSION);
/*  83 */     f.setDefaultCloseOperation(3);
/*  84 */     f.setLayout(new BorderLayout());
/*     */ 
/*  86 */     JSplitPane splitPane = new JSplitPane(1);
/*  87 */     splitPane.setTopComponent(new ToolboxPanel(this).getPanel());
/*  88 */     splitPane.setBottomComponent(new JScrollPane(this.clipPanel));
/*  89 */     f.add(splitPane, "Center");
/*     */ 
/*  91 */     JToolBar toolbar = new JToolBar();
/*  92 */     toolbar.add(new SaveAction(c, f));
/*  93 */     toolbar.add(UndoRedoAction.createUndoInstance(this.undoManager));
/*  94 */     toolbar.add(UndoRedoAction.createRedoInstance(this.undoManager));
/*  95 */     toolbar.addSeparator();
/*  96 */     toolbar.add(new PlayPauseAction(this.playerThread));
/*  97 */     toolbar.add(new RewindAction(this.playerThread));
/*  98 */     f.add(toolbar, "North");
/*     */ 
/* 100 */     if (prefs.get("frameBounds", null) != null) {
/* 101 */       String[] frameBounds = prefs.get("frameBounds", null).split(",");
/* 102 */       if (frameBounds.length == 4) {
/* 103 */         f.setBounds(Integer.parseInt(frameBounds[0]), Integer.parseInt(frameBounds[1]), Integer.parseInt(frameBounds[2]), Integer.parseInt(frameBounds[3]));
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 110 */       f.pack();
/* 111 */       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/* 112 */       f.setSize(Math.min(screenSize.width - 50, f.getWidth()), Math.min(screenSize.height - 50, f.getHeight()));
/*     */ 
/* 115 */       f.setLocationRelativeTo(null);
/*     */     }
/* 117 */     f.addWindowListener(new WindowAdapter()
/*     */     {
/*     */       public void windowClosing(WindowEvent e) {
/*     */         try {
/* 121 */           SpectroEditSession.prefs.put("frameBounds", String.format("%d,%d,%d,%d", new Object[] { Integer.valueOf(f.getX()), Integer.valueOf(f.getY()), Integer.valueOf(f.getWidth()), Integer.valueOf(f.getHeight()) }));
/* 122 */           SpectroEditSession.prefs.flush();
/*     */         } catch (BackingStoreException ex) {
/* 124 */           SpectroEditSession.logger.log(Level.WARNING, "Failed to flush preferences", ex);
/*     */         }
/*     */       }
/*     */     });
/* 128 */     f.setVisible(true);
/*     */   }
/*     */ 
/*     */   public static SpectroEditSession createSession(File wavFile)
/*     */     throws UnsupportedAudioFileException, IOException, LineUnavailableException
/*     */   {
/* 143 */     Clip c = new Clip(wavFile);
/* 144 */     SpectroEditSession session = new SpectroEditSession(c);
/* 145 */     c.addUndoableEditListener(session.undoManager);
/* 146 */     return session;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 157 */     System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spectro-Edit");
/* 158 */     System.setProperty("apple.laf.useScreenMenuBar", "true");
/* 159 */     LogManager.getLogManager().readConfiguration(SpectroEditSession.class.getResourceAsStream("LogManager.properties"));
/* 160 */     JFrame f = new JFrame("Dummy frame for owning dialogs");
/* 161 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/*     */         try {
/* 164 */           FileDialog fd = new FileDialog(this.val$f, "Choose a 16-bit WAV or AIFF file");
/* 165 */           fd.setVisible(true);
/* 166 */           String dir = fd.getDirectory();
/* 167 */           String file = fd.getFile();
/* 168 */           if ((dir == null) || (file == null)) {
/* 169 */             JOptionPane.showMessageDialog(this.val$f, "Ok, maybe next time");
/* 170 */             System.exit(0);
/*     */           }
/* 172 */           File wavFile = new File(dir, file);
/* 173 */           SpectroEditSession.createSession(wavFile);
/*     */         } catch (Exception e) {
/* 175 */           e.printStackTrace();
/* 176 */           JOptionPane.showMessageDialog(this.val$f, "Sorry, couldn't read your sample:\n" + e.getMessage() + "\nBe sure your file is 16-bit WAV or AIFF!");
/*     */ 
/* 180 */           System.exit(0);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void undo() {
/* 187 */     this.undoManager.undo();
/*     */   }
/*     */ 
/*     */   public void redo() {
/* 191 */     this.undoManager.redo();
/*     */   }
/*     */ 
/*     */   public ClipPanel getClipPanel() {
/* 195 */     return this.clipPanel;
/*     */   }
/*     */ 
/*     */   public UndoManager getUndoManager() {
/* 199 */     return this.undoManager;
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.SpectroEditSession
 * JD-Core Version:    0.6.1
 */