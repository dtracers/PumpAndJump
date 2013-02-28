package net.bluecow.spectro.painting;

import javax.swing.JComponent;

public abstract interface ValueColorizer
{
  public abstract int colorFor(double paramDouble);

  public abstract JComponent getSettingsPanel();
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.ValueColorizer
 * JD-Core Version:    0.6.1
 */