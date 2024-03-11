package com.partytobxpdrops.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Fonts
{
	REGULAR("RS Regular"),
	ARIAL("Arial"),
	CAMBRIA("Cambria"),
	ROCKWELL("Rockwell"),
	SEGOE_UI("Segoe Ui"),
	TIMES_NEW_ROMAN("Times New Roman"),
	VERDANA("Verdana"),
	DIALOG("DIALOG"),
	RUNESCAPE("RuneScape");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}