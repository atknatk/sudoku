/* 
 * Copyright (C) 2009 Roman Masek
 * 
 * This file is part of AoskSudoku.
 * 
 * AoskSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * AoskSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with AoskSudoku.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.everva.aosksudoku.gui.inputmethod;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.everva.aosksudoku.game.Cell;
import com.everva.aosksudoku.game.CellCollection;
import com.everva.aosksudoku.game.CellNote;
import com.everva.aosksudoku.game.SudokuGame;
import com.everva.aosksudoku.gui.HintsQueue;
import com.everva.aosksudoku.gui.SudokuBoardView;

import org.everva.aosksudoku.R;

public class IMNumpad extends InputMethod {

	private boolean moveCellSelectionOnPress = true;
	private boolean mHighlightCompletedValues = true;
	private boolean mShowNumberTotals = false;

	private static final int MODE_EDIT_VALUE = 0;
	private static final int MODE_EDIT_NOTE = 1;

	private Cell mSelectedCell;
	private ImageButton mSwitchNumNoteButton;

	private int mEditMode = MODE_EDIT_VALUE;

	private Map<Integer, Button> mNumberButtons;

	public boolean isMoveCellSelectionOnPress() {
		return moveCellSelectionOnPress;
	}

	public void setMoveCellSelectionOnPress(boolean moveCellSelectionOnPress) {
		this.moveCellSelectionOnPress = moveCellSelectionOnPress;
	}

	public boolean getHighlightCompletedValues() {
		return mHighlightCompletedValues;
	}

	/**
	 * If set to true, buttons for numbers, which occur in {@link CellCollection}
	 * more than {@link CellCollection#SUDOKU_SIZE}-times, will be highlighted.
	 *
	 * @param highlightCompletedValues
	 */
	public void setHighlightCompletedValues(boolean highlightCompletedValues) {
		mHighlightCompletedValues = highlightCompletedValues;
	}

	public boolean getShowNumberTotals() {
		return mShowNumberTotals;
	}

	public void setShowNumberTotals(boolean showNumberTotals) {
		mShowNumberTotals = showNumberTotals;
	}

	@Override
	protected void initialize(Context context, IMControlPanel controlPanel,
							  SudokuGame game, SudokuBoardView board, HintsQueue hintsQueue) {
		super.initialize(context, controlPanel, game, board, hintsQueue);

		game.getCells().addOnChangeListener(mOnCellsChangeListener);
	}

	@Override
	protected View createControlPanelView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View controlPanel = inflater.inflate(R.layout.im_numpad, null);

		Typeface plain = Typeface.createFromAsset(mContext.getAssets(), "font/o_symb.ttf");

		mNumberButtons = new HashMap<Integer, Button>();
		mNumberButtons.put(1, (Button) controlPanel.findViewById(R.id.button_1));
		mNumberButtons.put(2, (Button) controlPanel.findViewById(R.id.button_2));
		mNumberButtons.put(3, (Button) controlPanel.findViewById(R.id.button_3));
		mNumberButtons.put(4, (Button) controlPanel.findViewById(R.id.button_4));
		mNumberButtons.put(5, (Button) controlPanel.findViewById(R.id.button_5));
		mNumberButtons.put(6, (Button) controlPanel.findViewById(R.id.button_6));
		mNumberButtons.put(7, (Button) controlPanel.findViewById(R.id.button_7));
		mNumberButtons.put(8, (Button) controlPanel.findViewById(R.id.button_8));
		mNumberButtons.put(9, (Button) controlPanel.findViewById(R.id.button_9));
		mNumberButtons.put(0, (Button) controlPanel.findViewById(R.id.button_clear));

		for (Integer num : mNumberButtons.keySet()) {
			Button b = mNumberButtons.get(num);
			if(num != 0)
				b.setTypeface(plain);
			b.setTag(num);
			b.setOnClickListener(mNumberButtonClick);
		}

		mSwitchNumNoteButton = (ImageButton) controlPanel.findViewById(R.id.switch_num_note);
		mSwitchNumNoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditMode = mEditMode == MODE_EDIT_VALUE ? MODE_EDIT_NOTE : MODE_EDIT_VALUE;
				update();
			}

		});

		return controlPanel;

	}

	@Override
	public int getNameResID() {
		return R.string.numpad;
	}

	@Override
	public int getHelpResID() {
		return R.string.im_numpad_hint;
	}

	@Override
	public String getAbbrName() {
		return mContext.getString(R.string.numpad_abbr);
	}

	@Override
	protected void onActivated() {
		update();

		mSelectedCell = mBoard.getSelectedCell();
	}

	@Override
	protected void onCellSelected(Cell cell) {
		mSelectedCell = cell;
	}

	private OnClickListener mNumberButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int selNumber = (Integer) v.getTag();
			Cell selCell = mSelectedCell;

			if (selCell != null) {
				switch (mEditMode) {
					case MODE_EDIT_NOTE:
						if (selNumber == 0) {
							mGame.setCellNote(selCell, CellNote.EMPTY);
						} else if (selNumber > 0 && selNumber <= 9) {
							mGame.setCellNote(selCell, selCell.getNote().toggleNumber(selNumber));
						}
						break;
					case MODE_EDIT_VALUE:
						if (selNumber >= 0 && selNumber <= 9) {
							mGame.setCellValue(selCell, selNumber);
							if (isMoveCellSelectionOnPress()) {
								mBoard.moveCellSelectionRight();
							}
						}
						break;
				}
			}
		}

	};

	private CellCollection.OnChangeListener mOnCellsChangeListener = new CellCollection.OnChangeListener() {

		@Override
		public void onChange() {
			if (mActive) {
				update();
			}
		}
	};


	private void update() {
		switch (mEditMode) {
			case MODE_EDIT_NOTE:
				mSwitchNumNoteButton.setImageResource(R.drawable.ic_edit_white);
				break;
			case MODE_EDIT_VALUE:
				mSwitchNumNoteButton.setImageResource(R.drawable.ic_edit_grey);
				break;
		}

		Map<Integer, Integer> valuesUseCount = null;
		if (mHighlightCompletedValues || mShowNumberTotals)
			valuesUseCount = mGame.getCells().getValuesUseCount();

		if (mHighlightCompletedValues) {
			for (Map.Entry<Integer, Integer> entry : valuesUseCount.entrySet()) {
				boolean highlightValue = entry.getValue() >= CellCollection.SUDOKU_SIZE;
				Button b = mNumberButtons.get(entry.getKey());
				if (highlightValue) {
                    b.getBackground().setColorFilter(0xFF1B5E20, PorterDuff.Mode.MULTIPLY);
				} else {
                    b.getBackground().setColorFilter(null);
				}
			}
		}

		if (mShowNumberTotals) {
			for (Map.Entry<Integer, Integer> entry : valuesUseCount.entrySet()) {
				Button b = mNumberButtons.get(entry.getKey());
				b.setText(entry.getKey() + " (" + entry.getValue() + ")");
			}
		}
	}

	@Override
	protected void onSaveState(IMControlPanelStatePersister.StateBundle outState) {
		outState.putInt("editMode", mEditMode);
	}

	@Override
	protected void onRestoreState(IMControlPanelStatePersister.StateBundle savedState) {
		mEditMode = savedState.getInt("editMode", MODE_EDIT_VALUE);
		if (isInputMethodViewCreated()) {
			update();
		}
	}
}
