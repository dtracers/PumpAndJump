/*    */ package net.bluecow.spectro;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class Version
/*    */ {
/*    */   public static final String VERSION;
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 29 */       Properties props = new Properties();
/* 30 */       props.load(Version.class.getResourceAsStream("version.properties"));
/* 31 */       VERSION = props.getProperty("net.bluecow.spectro.VERSION");
/*    */     } catch (Exception e) {
/* 33 */       throw new RuntimeException("Failed to read version from classpath resource", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.Version
 * JD-Core Version:    0.6.1
 */