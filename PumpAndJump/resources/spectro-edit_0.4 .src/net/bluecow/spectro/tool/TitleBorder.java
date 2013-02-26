/*    */ package net.bluecow.spectro.tool;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Insets;
/*    */ import javax.swing.border.Border;
/*    */ 
/*    */ public class TitleBorder
/*    */   implements Border
/*    */ {
/*    */   private String title;
/*    */ 
/*    */   public TitleBorder(String title)
/*    */   {
/* 38 */     this.title = title;
/*    */   }
/*    */ 
/*    */   public void setTitle(String title) {
/* 42 */     this.title = title;
/*    */   }
/*    */ 
/*    */   public String getTitle() {
/* 46 */     return this.title;
/*    */   }
/*    */ 
/*    */   public Insets getBorderInsets(Component c) {
/* 50 */     int height = c.getFontMetrics(getFont(c)).getHeight();
/* 51 */     return new Insets(height, 0, 0, 0);
/*    */   }
/*    */ 
/*    */   public boolean isBorderOpaque() {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
/* 59 */     Graphics2D g2 = (Graphics2D)g;
/* 60 */     Font font = getFont(c);
/* 61 */     FontMetrics fm = c.getFontMetrics(font);
/* 62 */     g.setFont(font);
/* 63 */     g2.drawString(this.title, 0, fm.getAscent());
/* 64 */     g2.drawLine(fm.stringWidth(this.title) + 5, fm.getHeight() / 2, width, fm.getHeight() / 2);
/*    */   }
/*    */ 
/*    */   private Font getFont(Component c) {
/* 68 */     return c.getFont().deriveFont(1);
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.TitleBorder
 * JD-Core Version:    0.6.1
 */