package com.skeletorsue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Output {
	private Terminal term;
	private TerminalSize size;
	private List<String> Screen = new ArrayList();
	private Integer LastLine = 0;

	public Terminal terminal() throws IOException {

		if (term == null) {
			term = TerminalFacade.createTerminal();
			if (term == null) {
				throw new IOException("Couldn't allocate a terminal!");
			}
			this.setUp();
		}

		return term;
	}

	public void setUp() throws IOException {
		terminal().enterPrivateMode();
		terminal().setCursorVisible(false);
	}

	public void tearDown() throws IOException {
		terminal().clearScreen();
		terminal().exitPrivateMode();
		terminal().setCursorVisible(true);
		for (Integer i = 0; i < this.Screen.size(); i++) {
			if (i < LastLine)
				System.out.println(this.Screen.get(i));
		}
		LastLine = 0;
		this.Screen.clear();
	}

	public Integer print(String message) throws IOException {
		Integer line = LastLine;
		print(0, line, message);

		return line;
	}

	public void print(Integer row, String message) throws IOException {
		print(0, row, message);
	}

	public void print(Integer col, Integer row, String message) throws IOException {
		if (row >= LastLine)
			LastLine = (row + 1);
		StoreOutput(col, row, message);
		while ((message.length() + col) < Width())
			message += " ";
		this.terminal().moveCursor(col, row);
		for (char r : message.toCharArray()) {
			this.terminal().moveCursor(col++, row);
			this.terminal().putCharacter(r);
		}
		this.terminal().moveCursor(this.Width(), this.Height());
	}

	private void StoreOutput(Integer col, Integer row, String message) {
		while (this.Screen.size() < (row + 1)) {
			this.Screen.add("");
		}

		if (col > 0) {
			message = this.Screen.get(row).substring(0, col) + message;
		}

		this.Screen.set(row, message);
	}

	public void ClearScreen() throws IOException {
		tearDown();
		System.out.println("");
		System.out.println("");
		setUp();
	}

	public void TrimScreen(Integer after) throws IOException {
		for (Integer i = (this.Screen.size() - 1); i > after; i--) {
			this.Screen.remove(i);
			print(i, "");
		}

		this.LastLine = (after + 1);
	}

	public TerminalSize Size() throws IOException {

		if (this.size == null) {
			this.size = this.terminal().getTerminalSize();
		}

		return this.size;
	}

	public Integer Width() {
		try {
			return Size().getColumns();
		} catch (IOException e) {
			return 80;
		}
	}

	public Integer Height() {
		try {
			return Size().getRows();
		} catch (IOException e) {
			return 24;
		}
	}

}