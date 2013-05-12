package com.musicgame.PumpAndJump.Util;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.esotericsoftware.tablelayout.Cell;

public class TableUtil
{
	/**
	 * puts the widget into the table in the center with the specified number of total columns
	 * @param table
	 * @param wid
	 * @param numColums
	 * @return returns the Cell of the addedWidget
	 */
	public static Cell addToCenter(Table table,Widget wid,int numColums)
	{
		int halfSize = numColums/2;
		for(int k =0 ;k<halfSize;k++)
		{
			table.add().expand().fill();
		}
			Cell c = table.add(wid);
		for(int k =0 ;k<halfSize;k++)
		{
			table.add().expand().fill();
		}
		return c;
	}
	public static void addEmptyRow(Table table,int numColums)
	{
		for(int k =0 ;k<numColums;k++)
		{
			table.add().expand().fill();
		}
	}
	public static Cell addToCenter(Table table, Button button,int numColums)
	{
		int halfSize = numColums/2;
		for(int k =0 ;k<halfSize;k++)
		{
			table.add().expand().fill();
		}
			Cell c = table.add(button);
		for(int k =0 ;k<halfSize;k++)
		{
			table.add().expand().fill();
		}
		return c;
	}
}
