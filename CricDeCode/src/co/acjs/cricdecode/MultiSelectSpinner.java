package co.acjs.cricdecode;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * A Spinner view that does not dismiss the dialog displayed when the control is
 * "dropped down" and the user presses it. This allows for the selection of more
 * than one option.
 */
public class MultiSelectSpinner extends Spinner implements
		OnMultiChoiceClickListener {
	String[] _items = null;
	boolean[] _selection = null;

	AlertDialog.Builder builder;

	ArrayAdapter<String> _proxyAdapter;

	/**
	 * Constructor for use when instantiating directly.
	 * 
	 * @param context
	 */
	public MultiSelectSpinner(Context context) {
		super(context);

		_proxyAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		super.setAdapter(_proxyAdapter);
	}

	/**
	 * Constructor used by the layout inflater.
	 * 
	 * @param context
	 * @param attrs
	 */
	public MultiSelectSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);

		_proxyAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		super.setAdapter(_proxyAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		Log.d("Debug", "OnClick which=" + which + " isChecked=" + isChecked);
		if (_selection != null && which < _selection.length) {
			if (which == 0) {
				for (int i = 0; i < _selection.length; i++) {
					_selection[i] = isChecked;
					((AlertDialog) dialog).getListView().setItemChecked(i,
							isChecked);
				}
			} else {
				_selection[which] = isChecked;
				((AlertDialog) dialog).getListView().setItemChecked(which,
						isChecked);
				if (isChecked) {
					int i = 1;
					for (; i < _selection.length; i++) {
						if (!_selection[i]) {
							break;
						}
					}
					if (i == _selection.length) {
						_selection[0] = true;
						((AlertDialog) dialog).getListView().setItemChecked(0,
								true);
					}
				} else {
					_selection[0] = false;
					((AlertDialog) dialog).getListView().setItemChecked(0,
							false);
				}
			}
			Log.d("Debug", Arrays.toString(_selection));
			_proxyAdapter.clear();
			_proxyAdapter.add(buildSelectedItemString());
			setSelection(0);
		} else {
			throw new IllegalArgumentException(
					"Argument 'which' is out of bounds.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performClick() {
		Log.d("Debug", "On Perform Click");
		builder = new AlertDialog.Builder(getContext());
		builder.setMultiChoiceItems(_items, _selection, this);
		builder.show();
		return true;
	}

	/**
	 * MultiSelectSpinner does not support setting an adapter. This will throw
	 * an exception.
	 * 
	 * @param adapter
	 */
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		throw new RuntimeException(
				"setAdapter is not supported by MultiSelectSpinner.");
	}

	/**
	 * Sets the options for this spinner.
	 * 
	 * @param items
	 */
	public void setItems(String[] items) {
		_items = new String[items.length + 1];
		_items[0] = "All";
		for (int i = 0; i < items.length; i++) {
			_items[i + 1] = items[i];
		}
		_selection = new boolean[_items.length];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the options for this spinner.
	 * 
	 * @param items
	 */
	public void setItems(ArrayList<String> items) {
		String[] temp = items.toArray(new String[items.size()]);
		_items = new String[temp.length + 1];
		_items[0] = "All";
		for (int i = 0; i < temp.length; i++) {
			_items[i + 1] = temp[i];
		}
		_selection = new boolean[_items.length];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the selected options based on an array of string.
	 * 
	 * @param selection
	 */
	public void setSelection(String[] selection) {
		int truecount = 0;
		for (String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					if (j == 0) {
						for (int k = 0; k < _items.length; k++) {
							_selection[k] = true;
						}
						return;
					}
					_selection[j] = true;
					truecount++;
				}
			}
		}
		if (truecount == _items.length - 1) {
			_selection[0] = true;
		}
	}

	/**
	 * Sets the selected options based on a list of string.
	 * 
	 * @param selection
	 */
	public void setSelection(ArrayList<String> selection) {
		int truecount = 0;
		for (String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					if (j == 0) {
						for (int k = 0; k < _items.length; k++) {
							_selection[k] = true;
						}
						return;
					}
					_selection[j] = true;
					truecount++;
				}
			}
		}
		if (truecount == _items.length - 1) {
			_selection[0] = true;
		}

	}

	/**
	 * Sets the selected options based on an array of positions.
	 * 
	 * @param selectedIndicies
	 */
	public void setSelection(int[] selectedIndicies) {
		int truecount = 0;
		for (int index : selectedIndicies) {
			if (index + 1 >= 0 && index + 1 < _selection.length) {
				_selection[index + 1] = true;
				truecount++;
			} else {
				throw new IllegalArgumentException("Index " + (index + 1)
						+ " is out of bounds.");
			}
		}
		if (truecount == _items.length - 1) {
			_selection[0] = true;
		}
	}

	/**
	 * Returns a list of strings, one for each selected item.
	 * 
	 * @return
	 */
	public ArrayList<String> getSelectedStrings() {
		ArrayList<String> selection = new ArrayList<String>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				if (i == 0) {
					for (i = 1; i < _items.length; i++) {
						selection.add(_items[i]);
					}
					break;
				}
				selection.add(_items[i]);
			}
		}
		return selection;
	}

	/**
	 * Returns a list of positions, one for each selected item.
	 * 
	 * @return
	 */
	public ArrayList<Integer> getSelectedIndicies() {
		ArrayList<Integer> selection = new ArrayList<Integer>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				if (i == 0) {
					for (i = 1; i < _items.length; i++) {
						selection.add(i);
					}
					break;
				}
				selection.add(i);
			}
		}
		return selection;
	}

	/**
	 * Builds the string for display in the spinner.
	 * 
	 * @return comma-separated list of selected items
	 */
	String buildSelectedItemString() {
		StringBuilder sb = new StringBuilder();
		boolean foundOne = false;

		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
				if (i == 0) {
					sb.append(_items[i]);
					break;
				}
				if (foundOne) {
					sb.append(", ");
				}
				foundOne = true;

				sb.append(_items[i]);

			}
		}

		return sb.toString();
	}
}