/*     */ package net.bluecow.spectro.tool;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import net.bluecow.spectro.SpectroEditSession;
import net.bluecow.spectro.math.MemoryMonitor;
import net.bluecow.spectro.painting.ClipPanel;
import net.bluecow.spectro.painting.PositionReadout;
import net.bluecow.spectro.painting.ValueColorizer;
/*     */ 
/*     */ public class ToolboxPanel
/*     */ {
/*     */   private final SpectroEditSession session;
/*     */   private final ClipPanel clipPanel;
/*     */   private final JPanel panel;
/*     */   private final JPanel toolButtonPanel;
/*     */   private final JPanel toolSettingsPanel;
/*     */   private final JPanel viewSettingsPanel;
/*  61 */   private final ButtonGroup toolButtonGroup = new ButtonGroup();
/*     */   private Tool currentTool;
/*     */   private final TitleBorder toolSettingsBorder;
/*     */ 
/*     */   public ToolboxPanel(SpectroEditSession session)
/*     */   {
/*  72 */     this.session = session;
/*  73 */     this.clipPanel = session.getClipPanel();
/*     */ 
/*  76 */     GridBagConstraints gbc = new GridBagConstraints();
/*  77 */     gbc.fill = 2;
/*  78 */     gbc.weightx = 1.0D;
/*  79 */     this.viewSettingsPanel = new JPanel(new GridBagLayout());
/*  80 */     this.viewSettingsPanel.setBackground(Color.WHITE);
/*  81 */     this.viewSettingsPanel.setBorder(new TitleBorder("View Settings"));
/*  82 */     gbc.gridx = 0;
/*  83 */     this.viewSettingsPanel.add(this.clipPanel.getColorizer().getSettingsPanel(), gbc);
/*  84 */     this.viewSettingsPanel.add(new PositionReadout(this.clipPanel).getLabel(), gbc);
/*  85 */     MemoryMonitor memoryMonitor = new MemoryMonitor();
/*  86 */     memoryMonitor.start();
/*  87 */     gbc.fill = 1;
/*  88 */     gbc.weighty = 1.0D;
/*  89 */     this.viewSettingsPanel.add(Box.createVerticalGlue(), gbc);
/*  90 */     gbc.fill = 2;
/*  91 */     gbc.weighty = 0.0D;
/*  92 */     this.viewSettingsPanel.add(memoryMonitor.getLabel(), gbc);
/*     */ 
/*  94 */     this.toolSettingsPanel = new JPanel(new BorderLayout());
/*  95 */     this.toolSettingsPanel.setBackground(Color.WHITE);
/*  96 */     this.toolSettingsBorder = new TitleBorder("Tool Settings");
/*  97 */     this.toolSettingsPanel.setBorder(this.toolSettingsBorder);
/*     */ 
/*  99 */     this.toolButtonPanel = new JPanel(new FlowLayout());
/* 100 */     this.toolButtonPanel.setBackground(Color.WHITE);
/* 101 */     this.toolButtonPanel.setBorder(new TitleBorder("Tools"));
/*     */ 
/* 103 */     ActionListener actionHandler = new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 105 */         if (ToolboxPanel.this.currentTool != null) {
/* 106 */           ToolboxPanel.this.currentTool.deactivate();
/* 107 */           ToolboxPanel.this.toolSettingsPanel.remove(ToolboxPanel.this.currentTool.getSettingsPanel());
/*     */         }
/* 109 */         ToolboxPanel.this.currentTool = ((ToolButton)e.getSource()).getTool();
/* 110 */         ToolboxPanel.this.currentTool.activate(ToolboxPanel.this.session);
/* 111 */         ToolboxPanel.this.toolSettingsPanel.add(ToolboxPanel.this.currentTool.getSettingsPanel(), "Center");
/* 112 */         ToolboxPanel.this.toolSettingsBorder.setTitle("Tool Settings: " + ToolboxPanel.this.currentTool.getName());
/* 113 */         ToolboxPanel.this.panel.revalidate();
/* 114 */         ToolboxPanel.this.panel.repaint();
/*     */       }
/*     */     };
/* 118 */     JRadioButton paintbrushToolButton = new ToolButton(new PaintbrushTool(), "paintbrush", this.toolButtonGroup);
/* 119 */     this.toolButtonPanel.add(paintbrushToolButton);
/* 120 */     paintbrushToolButton.addActionListener(actionHandler);
/*     */ 
/* 122 */     JRadioButton regionScaleToolButton = new ToolButton(new RegionScaleTool(), "contrast_change", this.toolButtonGroup);
/* 123 */     this.toolButtonPanel.add(regionScaleToolButton);
/* 124 */     regionScaleToolButton.addActionListener(actionHandler);
/*     */ 
/* 126 */     JRadioButton regionThresholdToolButton = new ToolButton(new RegionThresholdTool(), "threshold", this.toolButtonGroup);
/* 127 */     this.toolButtonPanel.add(regionThresholdToolButton);
/* 128 */     regionThresholdToolButton.addActionListener(actionHandler);
/*     */ 
/* 130 */     JRadioButton flipToolButton = new ToolButton(new RegionFlipTool(), "shape_flip_vertical", this.toolButtonGroup);
/* 131 */     this.toolButtonPanel.add(flipToolButton);
/* 132 */     flipToolButton.addActionListener(actionHandler);
/*     */ 
/* 134 */     this.panel = new JPanel(new GridLayout(3, 1));
/* 135 */     this.panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
/* 136 */     this.panel.setBackground(Color.WHITE);
/* 137 */     this.panel.add(this.toolButtonPanel);
/* 138 */     this.panel.add(this.toolSettingsPanel);
/* 139 */     this.panel.add(this.viewSettingsPanel);
/*     */ 
/* 142 */     paintbrushToolButton.doClick();
/*     */   }
/*     */ 
/*     */   public JPanel getPanel() {
/* 146 */     return this.panel;
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.ToolboxPanel
 * JD-Core Version:    0.6.1
 */