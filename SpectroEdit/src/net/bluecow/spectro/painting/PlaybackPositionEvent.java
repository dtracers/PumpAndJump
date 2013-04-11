package net.bluecow.spectro.painting;

import net.bluecow.spectro.PlayerThread;

/*    */
/*    */ public class PlaybackPositionEvent
/*    */ {
/*    */   private final PlayerThread source;
/*    */   private final long samplePos;
/*    */
/*    */   public PlaybackPositionEvent(PlayerThread source, long samplePos)
/*    */   {
/* 25 */     this.source = source;
/* 26 */     this.samplePos = samplePos;
/*    */   }
/*    */
/*    */   public PlayerThread getSource() {
/* 30 */     return this.source;
/*    */   }
/*    */
/*    */   public long getSamplePos() {
/* 34 */     return this.samplePos;
/*    */   }
/*    */ }