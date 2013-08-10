package co.acjs.cricdecode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
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
		if (_selection != null && which < _selection.length) {
			_selection[which] = isChecked;

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
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
		_items = items;
		_selection = new boolean[_items.length];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the options for this spinner.
	 * 
	 * @param items
	 */
	public void setItems(List<String> items) {
		_items = items.toArray(new String[items.size()]);
		_selection = new boolean[_items.length];

		Arrays.fill(_selection, false);
	}

	/**
	 * Sets the selected options based on an array of string.
	 * 
	 * @param selection
	 */
	public void setSelection(String[] selection) {
		for (String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					_selection[j] = true;
				}
			}
		}
	}

	/**
	 * Sets the selected options based on a list of string.
	 * 
	 * @param selection
	 */
	public void setSelection(List<String> selection) {
		for (String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					_selection[j] = true;
				}
			}
		}
	}

	/**
	 * Sets the selected options based on an array of positions.
	 * 
	 * @param selectedIndicies
	 */
	public void setSelection(int[] selectedIndicies) {
		for (int index : selectedIndicies) {
			if (index >= 0 && index < _selection.length) {
				_selection[index] = true;
			} else {
				throw new IllegalArgumentException("Index " + index
						+ " is out of bounds.");
			}
		}
	}

	/**
	 * Returns a list of strings, one for each selected item.
	 * 
	 * @return
	 */
	public List<String> getSelectedStrings() {
		List<String> selection = new LinkedList<String>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
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
	public List<Integer> getSelectedIndicies() {
		List<Integer> selection = new LinkedList<Integer>();
		for (int i = 0; i < _items.length; ++i) {
			if (_selection[i]) {
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