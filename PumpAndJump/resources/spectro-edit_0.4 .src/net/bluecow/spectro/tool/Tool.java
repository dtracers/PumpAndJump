package net.bluecow.spectro.tool;

import javax.swing.JComponent;
import net.bluecow.spectro.SpectroEditSession;

public abstract interface Tool
{
  public abstract JComponent getSettingsPanel();

  public abstract String getName();

  public abstract void activate(SpectroEditSession paramSpectroEditSession);

  public abstract void deactivate();
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.tool.Tool
 * JD-Core Version:    0.6.1
 */