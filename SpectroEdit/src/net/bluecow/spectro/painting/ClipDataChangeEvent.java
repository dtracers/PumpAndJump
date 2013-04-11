/*    */ package net.bluecow.spectro.painting;
/*    */ 
/*    */ import java.awt.Rectangle;

import net.bluecow.spectro.clipAndFrame.Clip;
/*    */ 
/*    */ public class ClipDataChangeEvent
/*    */ {
/*    */   private final Clip source;
/*    */   private final Rectangle region;
/*    */ 
/*    */   public ClipDataChangeEvent(Clip source, Rectangle region)
/*    */   {
/* 35 */     this.source = source;
/* 36 */     this.region = region;
/* 37 */     if ((region.width == 0) || (region.height == 0))
/* 38 */       throw new IllegalArgumentException("Region has 0 area (width=" + region.width + ", height=" + region.height + ")");
/*    */   }
/*    */ 
/*    */   public Clip getSource()
/*    */   {
/* 45 */     return this.source;
/*    */   }
/*    */ 
/*    */   public Rectangle getRegion()
/*    */   {
/* 55 */     return new Rectangle(this.region);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 60 */     return "Clip Data Change @ " + this.region;
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.ClipDataChangeEvent
 * JD-Core Version:    0.6.1
 */