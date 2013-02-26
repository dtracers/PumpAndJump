/*    */ package net.bluecow.spectro.tool;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics;
/*    */ import java.net.URL;
/*    */ import javax.swing.ButtonGroup;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JRadioButton;
/*    */ 
/*    */ public class ToolButton extends JRadioButton
/*    */ {
/*    */   private final Tool tool;
/*    */ 
/*    */   public ToolButton(Tool tool, String iconName, ButtonGroup group)
/*    */   {
/* 39 */     super(loadIcon(iconName));
/* 40 */     this.tool = tool;
/* 41 */     group.add(this);
/*    */   }
/*    */ 
/*    */   private static Icon loadIcon(String name) {
/* 45 */     URL resourceUrl = ToolboxPanel.class.getResource("/icons/" + name + ".png");
/* 46 */     if (resourceUrl == null) {
/* 47 */       throw new RuntimeException("Missing icon resource: " + name);
/*    */     }
/* 49 */     return new ImageIcon(resourceUrl);
/*    */   }
/*    */ 
/*    */   public Tool getTool() {
/* 53 */     return this.tool;
/*    */   }
/*    */ 
/*    */   protected void paintComponent(Graphics g)
/*    */   {
/* 58 */     if (isSelected()) {
/* 59 */       g.setColor(Color.RED);
/* 60 */       g.fillRect(0, 0, getWidth(), getHeight());
/*    */     }
/* 62 */     getIcon().paintIcon(this, g, getWidth() / 2 - getIcon().getIconWidth() / 2, getHeight() / 2 - getIcon().getIconHeight() / 2);
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.ToolButton
 * JD-Core Version:    0.6.1
 */